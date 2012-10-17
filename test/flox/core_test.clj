(ns flox.core-test
  (:use clojure.test)
  (:use flox.core)
  (:use midje.sweet))



(fact "audio format sample rate is 48000"
      (int  (.getSampleRate audio-format)) => 48000)

(fact "audio format size is 16 bits"
      (.getSampleSizeInBits audio-format) => 16)

(fact "audio format is stereo"
  (.getChannels audio-format) => 2)

(fact "audio format frame size is four"
      (.getFrameSize audio-format) => 4)

(fact "audio format is little endian"
      (not (.isBigEndian audio-format)) => true)

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

(deftest queue-data-with-two-items-on-list-of-two-items
  (testing "add second items after first items"
    (queue-data '(1 2))
    (queue-data '(3 4))
    (is (=  '(1 2 3 4) @audio-data-queue))
    (dosync
     (ref-set audio-data-queue (list)))))


(deftest create-line-starts-line
  (testing "expected line to be able to accept data"
    (is (< 0 (.available (create-line))))))

(deftest create-line-wrapped-in-agent-starts-available
  (testing "expected line to be able to accept data"
    (is (< 0 (.available @(agent (create-line)))))))

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


;; (deftest write-to-loop-reduces-queue-size-by-24000
;;   (testing "expected queue size of 24000"
;;     (do
;;       (dosync
;;        (queue-data (get-note 0))
;;        (is (= 48000 (count @audio-data-queue)))
;;        (write-to-audio-loop))
;;       (await line)
;;       (is (= 24000 (count @audio-data-queue)))
;;       (dosync
;;        (ref-set audio-data-queue (list))))))

    