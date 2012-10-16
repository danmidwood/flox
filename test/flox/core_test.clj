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
    (queue-data '(123 456))
    (is (= 2 (count @audio-data-queue)))
    (dosync
     (ref-set audio-data-queue (list)))))

(deftest queue-data-with-two-items-on-list-of-two-items
  (testing "leaves queue with four items"
    (queue-data '(1 2))
    (queue-data '(3 4))
    (is (= 4 (count @audio-data-queue)))
    (dosync
     (ref-set audio-data-queue (list)))))


(deftest create-line-starts-line
  (testing "expected line to be able to accept data"
    (is (< 0 (.available (create-line))))))


(deftest not-running-to-begin
  (testing "expected not to be running loop"
    (is (not @running))))

(deftest start-makes-us-running
  (testing "expected to be running"
    (start)
    (is @running)))

(deftest stop-stops-us-running
  (testing "expected to not be running"
    (stop)
    (is (not @running))))

(deftest stop-after-start-leaves-us-not-running
  (testing "expected to not be running"
    (start)
    (stop)
    (is (not @running))))

(deftest start-after-stop-after-start-leaves-us-running
  (testing "expected to be running"
    (start)
    (stop)
    (start)
    (is @running)))
