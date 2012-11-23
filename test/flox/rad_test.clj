(ns flox.rad-test
  (:use clojure.test)
  (:use flox.rad)
  (:use midje.sweet)
  (:use clojure.math.numeric-tower))



(fact "audio format sample rate is 48000"
      (int  (.getSampleRate (:audio-format line-data))) => 48000)

(fact "audio format size is 16 bits"
      (.getSampleSizeInBits (:audio-format line-data)) => 16)

(fact "audio format is stereo"
  (.getChannels (:audio-format line-data)) => 2)

(fact "audio format frame size is four"
      (.getFrameSize (:audio-format line-data)) => 4)

(fact "audio format is little endian"
      (not (.isBigEndian (:audio-format line-data))) => true)

(fact "the twelth root is 1.059...."
      (:12th-root line-data) => 1.0594630943592953)


(defn nth-sig-fig
  "Working with frequencies, it's useful to see how accurate we can be. This helps"
  [n number]
  (let
      [scaler (clojure.math.numeric-tower/expt 10 n)]
      (/ (round (* scaler number)) (float scaler))))

(defn two-sig-fig
  [x]
  (nth-sig-fig 2 x))


(fact "default frequency is A"
      (frequency) => 440.0)

(fact "frequency offset one up is A#"
      (two-sig-fig (frequency 1)) => 466.16)

(fact "the first value in the lazy freq stream is A"
      (nth-sig-fig 5 (nth (freq-freq-lazy-seq 440.0) 0)) => 440.0)

(fact "the 48001th value in the lazy freq stream is A#"
      (nth-sig-fig 8 (nth (freq-freq-lazy-seq 440.0) 48000)) => (nth-sig-fig 8 (frequency 1)))

(fact "frequency offset one up and then one down is A"
      (frequency -1 (frequency 1)) => 440.0)

(fact "frequency offset one down is G#"
      (two-sig-fig (frequency -1)) => 415.30)

(fact "frequency offset twelve down is A below middle A"
      (two-sig-fig (frequency -12)) => 220.000)

(fact "frequency offset twelve up is A above middle A"
      (two-sig-fig (frequency 12)) => 880.000)

