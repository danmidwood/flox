(ns flox.core-test
  (:use clojure.test
        flox.core))

(deftest audio-format-sample-rate
  (testing "is 48000"
    (is (= 48000 (int  (.getSampleRate audio-format))))))

(deftest audio-format-sample-size-in-bits
  (testing "is 16"
    (is (= 16 (.getSampleSizeInBits audio-format)))))

(deftest audio-format-is-stereo
  (testing "expected two channels"
    (is (= 2 (.getChannels audio-format)))))

(deftest audio-format-frame-size
  (testing "is four"
    (is (= 4 (.getFrameSize audio-format)))))

(deftest audio-format-is-little-endian
  (testing "expected isBigEndian to be false"
    (is (not (.isBigEndian audio-format)))))

(deftest twelth-root
  (testing "is 1.059..."
    (is (=  1.0594630943592953 a12th-root))))

(deftest audio-data-queue-starts
  (testing "off as empty list"
    (is (= '() @audio-data-queue))))

(deftest queue-data-with-two-items-on-empty-list
  (testing "leaves queue with two items"
    (dosync
     (ref-set audio-data-queue (list)))
    (queue-data '(123 456))
    (is (= 2 (count @audio-data-queue)))
    (dosync
     (ref-set audio-data-queue (list)))))

