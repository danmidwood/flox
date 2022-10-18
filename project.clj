(defproject flox "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [org.clojure/algo.generic "0.1.3"]
                 [org.clojure/math.numeric-tower "0.0.5"]
                 [midje "1.10.6"]]
  :profiles {:dev {:plugins [[lein-midje "2.0.1"]]}}
  :main flox.main)
