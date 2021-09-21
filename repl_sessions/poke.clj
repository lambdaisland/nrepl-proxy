(ns poke
  (:require [lambdaisland.classpath :as licp]))

(licp/update-classpath! {})
(licp/classpath-chain)
