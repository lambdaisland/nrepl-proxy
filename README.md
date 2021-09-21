# lambdaisland/nrepl-proxy

<!-- badges -->
[![cljdoc badge](https://cljdoc.org/badge/com.lambdaisland/nrepl-proxy)](https://cljdoc.org/d/com.lambdaisland/nrepl-proxy) [![Clojars Project](https://img.shields.io/clojars/v/com.lambdaisland/nrepl-proxy.svg)](https://clojars.org/com.lambdaisland/nrepl-proxy)
<!-- /badges -->

Proxy server for debugging nREPL

## Usage

```
clojure -X lambdaisland.nrepl-proxy/start :port 1234 :attach 5678
```

This will listen for incoming connections on port 1234, and will connect through
to an nREPL server listening on port 5678. When it gets a connection it prints
out something like this:

```
nil ---> 1 clone {}
J <=== 1 #{:done} {:new-session "002914a8-db79-408d-807a-c5b3955ab6f9"}
nil ---> 2 clone {}
X <=== 2 #{:done} {:new-session "6a7e7b99-1b8e-4008-bbe5-ddddf46672a9"}
Y ---> 3 describe {}
Y <=== 3 #{:done} {:aux {:current-ns "user"}, :ops {:stdin {}, :add-middleware {}, :lookup {}, :swap-middleware {}, :sideloader-start {}, :ls-middleware {}, :close {}, :sideloader-provide {}, :load-file {}, :ls-sessions {}, :clone {}, :describe {}, :interrupt {}, :completions {}, :eval {}}, :versions {:clojure {:incremental 3, :major 1, :minor 10, :version-string "1.10.3"}, :java {:version-string "17"}, :nrepl {:incremental 0, :major 0, :minor 9, :version-string "0.9.0"}}}
Y ---> 4 eval {:nrepl.middleware.print/buffer-size 4096, :file "*cider-repl lambdaisland/nrepl-proxy:localhost:5424(clj)*", :nrepl.middleware.print/quota 1048576, :nrepl.middleware.print/print "cider.nrepl.pprint/pprint", :column 1, :line 10, :code "(clojure.core/apply clojure.core/require clojure.main/repl-requires)", :inhibit-cider-middleware "true", :nrepl.middleware.print/stream? "1", :nrepl.middleware.print/options {:right-margin 80}}
G ---> 5 eval {:code "(seq (.split (System/getProperty \"java.class.path\") \":\"))"}
Y <--- 4 #{:nrepl.middleware.print/error} #:nrepl.middleware.print{:error "Couldn't resolve print-var cider.nrepl.pprint/pprint"}
Y <--- 4 #{} {:value "nil"}
Y <--- 4 #{} {:ns "user"}
Y <=== 4 #{:done} {}
```

The initial letter indicates the session, arrows to the right are messages sent
to the server, arrows coming back are responses. Responses marked as `:done` get
a thick arrow, `:error` responses get a `<***` arrow.

Then you get the message id, the `:op` for sent messages, or the `:status` flags
for returning messages, and then the map with other keys.

This is meant as a PoC for tooling that wants to make nREPL traffic
introspectable.

<!-- opencollective -->
## Lambda Island Open Source

<img align="left" src="https://github.com/lambdaisland/open-source/raw/master/artwork/lighthouse_readme.png">

&nbsp;

nrepl-proxy is part of a growing collection of quality Clojure libraries created and maintained
by the fine folks at [Gaiwan](https://gaiwan.co).

Pay it forward by [becoming a backer on our Open Collective](http://opencollective.com/lambda-island),
so that we may continue to enjoy a thriving Clojure ecosystem.

You can find an overview of our projects at [lambdaisland/open-source](https://github.com/lambdaisland/open-source).

&nbsp;

&nbsp;
<!-- /opencollective -->

<!-- contributing -->
## Contributing

Everyone has a right to submit patches to nrepl-proxy, and thus become a contributor.

Contributors MUST

- adhere to the [LambdaIsland Clojure Style Guide](https://nextjournal.com/lambdaisland/clojure-style-guide)
- write patches that solve a problem. Start by stating the problem, then supply a minimal solution. `*`
- agree to license their contributions as MPL 2.0.
- not break the contract with downstream consumers. `**`
- not break the tests.

Contributors SHOULD

- update the CHANGELOG and README.
- add tests for new functionality.

If you submit a pull request that adheres to these rules, then it will almost
certainly be merged immediately. However some things may require more
consideration. If you add new dependencies, or significantly increase the API
surface, then we need to decide if these changes are in line with the project's
goals. In this case you can start by [writing a pitch](https://nextjournal.com/lambdaisland/pitch-template),
and collecting feedback on it.

`*` This goes for features too, a feature needs to solve a problem. State the problem it solves, then supply a minimal solution.

`**` As long as this project has not seen a public release (i.e. is not on Clojars)
we may still consider making breaking changes, if there is consensus that the
changes are justified.
<!-- /contributing -->

<!-- license -->
## License

Copyright &copy; 2021 Arne Brasseur and Contributors

Licensed under the term of the Mozilla Public License 2.0, see LICENSE.
<!-- /license -->
