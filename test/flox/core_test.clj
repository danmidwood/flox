(ns flox.core-test
  (:use clojure.test)
  (:use flox.core)
  (:use midje.sweet)
  (:use clojure.math.numeric-tower))



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

(fact "the twelth root is 1.059...."
      a12th-root => 1.0594630943592953)

(fact "audio queue is empty"
      @audio-data-queue => '())

(fact "adding two items onto queue gives queue with two items"
      (queue-data '(123 456))
      (dosync
       (count @audio-data-queue) => 2
       (ref-set audio-data-queue (list))))

(fact "queueing two items on a list of two gives list with four items"
      (queue-data '(1 2))
      (queue-data '(3 4))
      (count @audio-data-queue) => 4
      (dosync
       (ref-set audio-data-queue (list))))

(fact "queueing data on non-empty list adds new items to the end"
      (queue-data '(1 2))
      (queue-data '(3 4))
      (=  '(1 2 3 4) @audio-data-queue) => true
      (dosync
       (ref-set audio-data-queue (list))))


(fact "a new line can accept data"
      (< 0 (.available (create-line))) => true)

(fact "an agent to a new line can accept data"
      (< 0 (.available @(agent (create-line)))) => true)

(fact "app begins with audio output not started"
      @running => false)

(fact "calling start sets running to true"
      (start)
      @running => true)

(fact "calling stop sets running to false"
      (stop)
      @running => false)

(fact "calling stop after start leaves running false"
      (start)
      (stop)
      @running => false)

(fact "calling start stop start leaves running true"
      (start)
      (stop)
      (start)
      @running => true)

(defn two-sig-fig
  [x]
  (/ (round (* 100 x)) 100.0))

(fact "default frequency is A"
      (frequency) => 440.0)

(fact "frequency offset one up is A#"
      (two-sig-fig (frequency 1)) => 466.16)

(fact "frequency offset one down is G#"
      (two-sig-fig (frequency -1)) => 415.30)

(fact "frequency offset twelve down is A below middle A"
      (two-sig-fig (frequency -12)) => 220.000)

(fact "frequency offset twelve up is A above middle A"
      (two-sig-fig (frequency 12)) => 880.000)


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

    