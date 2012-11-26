(defproject flox "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [org.clojure/algo.generic "0.1.0"]
                 [org.clojure/math.numeric-tower "0.0.1"]
                 [midje "1.5-alpha2"]]
  :profiles {:dev {:plugins [[lein-midje "2.0.1"]]}}
  :main flox.main)
