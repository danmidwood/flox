(ns flox.rad
  (:require clojure.algo.generic.math-functions)
  (:require clojure.math.numeric-tower))

(import '(javax.sound.sampled AudioSystem DataLine$Info
                              SourceDataLine
                              AudioFormat AudioFormat$Encoding))

(def line-data
  {:sample-rate 48000
   :audio-format (AudioFormat. 48000 16 2 true false)
   :12th-root (clojure.math.numeric-tower/expt 2 (/ 1 12))})


(defn frequency
  "Calculate the frequency of a note offset from a base frequency."
  ([] (frequency 0))
  ([offset] (frequency offset 440.0) )
  ([offset base]
     (* base (clojure.math.numeric-tower/expt (:12th-root line-data) offset))))

(defn find-sine
  [distance]
  (clojure.algo.generic.math-functions/sin (* 2 distance (. Math PI))))


(defn freq-freq-lazy-seq 
  "Create a lazy sequence of incrementing frequencies at one note per second with the specified sample-rate. If no sample-rate is supplied then 48000 is chosen"
  ([base] (freq-freq-lazy-seq base 48000))
  ([base sample-rate]
     (lazy-seq
      (cons base
            (freq-freq-lazy-seq (frequency (/ 1 sample-rate) base) sample-rate)))))




