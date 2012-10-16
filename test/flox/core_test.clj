(ns flox.core-test
  (:use clojure.test
        flox.core))

(deftest a-test
  (testing "Sample rate is 48000"
    (is (= 48000 sample-rate))))