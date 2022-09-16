(ns lambdaisland.nrepl-proxy
  (:require [nrepl.transport :as transport]
            [nrepl.socket :as socket]
            [nrepl.bencode :as bencode]
            #_[lambdaisland.funnel-client :as funnel]
            [clojure.java.io :as io]
            [clojure.walk :as walk])
  (:import (org.apache.commons.io.input TeeInputStream)
           (java.net Socket SocketException)
           (java.io ByteArrayOutputStream
                    Closeable
                    EOFException
                    Flushable
                    PushbackInputStream
                    PushbackReader
                    OutputStream)))

(defmacro rethrow-on-disconnection
  [s & body]
  `(try
     ~@body
     (catch RuntimeException e#
       (if (= "EOF while reading" (.getMessage e#))
         (throw (SocketException. "The transport's socket appears to have lost its connection to the nREPL server"))
         (throw e#)))
     (catch EOFException e#
       (if (= "Invalid netstring. Unexpected end of input." (.getMessage e#))
         (throw (SocketException. "The transport's socket appears to have lost its connection to the nREPL server"))
         (throw e#)))
     (catch Throwable e#
       (if (and ~s (not (let [^Socket s# ~s] (.isConnected s#))))
         (throw (SocketException. "The transport's socket appears to have lost its connection to the nREPL server"))
         (throw e#)))))

(defn read-bencode [socket in]
  (walk/keywordize-keys
   (rethrow-on-disconnection socket (#'transport/<bytes
                                     (bencode/read-bencode in)))))

(def input-stream (memfn ^Socket getInputStream))
(def output-stream (memfn ^Socket getOutputStream))

(defn on-send [msg]
  (locking *out*
    (println
     (when (:session msg)
       (char (+ (long \A) (mod (hash (str (:session msg))) 26))))
     '--->
     (:id msg)
     (:op msg)
     (pr-str (cond-> (dissoc msg :id :op :session)
               (:content msg)
               (update :content (fn [c] (str "<" (count c) " chars>"))))))))

(defn on-receive [msg]
  (let [msg (update msg :status #(into #{} (map keyword) %))]
    (locking *out*
      (println (when (:session msg)
                 (char (+ (long \A) (mod (hash (str (:session msg))) 26))))
               (cond
                 (:done (:status msg))
                 '<===
                 (:error (:status msg))
                 '<***
                 :else
                 '<---) (:id msg) (:status msg) (pr-str (cond-> (dissoc msg :id :status :session)
                                                          (:changed-namespaces msg)
                                                          (update :changed-namespaces keys)))))))

(defn start [{:keys [port attach]}]
  (let [proxy (socket/inet-socket "0.0.0.0" port)]
    (while true
      (let [downlink (socket/accept proxy)
            uplink (Socket. "localhost" attach)
            down-in (input-stream downlink)
            down-out (output-stream downlink)
            up-in (input-stream uplink)
            up-out (output-stream uplink)
            down-in-tee (PushbackInputStream. (TeeInputStream. down-in up-out))
            up-in-tee (PushbackInputStream. (TeeInputStream. up-in down-out))]
        (future
          (try
            (while true
              (on-send (read-bencode downlink down-in-tee)))
            (catch SocketException e
              (println (.getMessage e)))
            (catch Throwable t
              (println t)
              (.printStackTrace t))))
        (future
          (try
            (while true
              (on-receive (read-bencode uplink up-in-tee)))
            (catch SocketException e
              (println (.getMessage e)))
            (catch Throwable t
              (println t)
              (.printStackTrace t))))))))
