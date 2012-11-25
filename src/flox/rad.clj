(ns flox.rad
  (:require clojure.algo.generic.math-functions)
  (:require clojure.math.numeric-tower))

(import '(javax.sound.sampled AudioSystem DataLine$Info
                              SourceDataLine
                              AudioFormat AudioFormat$Encoding))

(def line-data
  {:sample-rate 44100
   :audio-format (AudioFormat. 44100 16 1 true false)
   :write-size (/ 44100 10)
   :12th-root (clojure.math.numeric-tower/expt 2 (/ 1 12))})


(defn frequency
  "Calculate the frequency of a note offset from a base frequency."
  ([offset base]
     (* base (clojure.math.numeric-tower/expt (:12th-root line-data) offset))))

(defn freq-freq-lazy-seq 
  "Create a lazy sequence of incrementing looping frequencies at one note per second."
  ([base] (freq-freq-lazy-seq base base (frequency 12 base)))
  ([base min max]
     (lazy-seq
      (cons base
            (if (> base max)
              (freq-freq-lazy-seq (frequency (/ 1 (:sample-rate line-data)) min) min max)
              (freq-freq-lazy-seq (frequency (/ 1 (:sample-rate line-data)) base) min max))))))


(defn sine-seq
  "Lazy seq for sine wave values"
  ([freqs] (sine-seq freqs 0))
  ([freqs angle]
     (let [increment (* 2 (. Math PI) (/ (first freqs) (:sample-rate line-data)))
           this-sine (clojure.algo.generic.math-functions/sin angle)]
       (lazy-seq
        (cons this-sine
              ;; The increment is divided by four to correct the pitch. Why though?
              (sine-seq (rest freqs) (+ angle (/ increment 4)))))))) 
(defn byte-my-sine
  "Transforms a floating point sine value into a byte"
  ([sine]
     (byte (* (. Byte MAX_VALUE) sine))))

(defn create-line
  []
  (let [line (. AudioSystem getSourceDataLine (:audio-format line-data))]
    (doto line
      (.open (:audio-format line-data))
      (.start))))

(defn -merge-values
  [& values]
  (/ (apply + values) (count values)))

(defn -write-audio2
  [line data]
  (.write ^SourceDataLine line (byte-array data) 0 (:write-size line-data)))

(defn emit-audio
  [line data]
  (do
    (-write-audio2 line (take (:write-size line-data) data)))
  (recur line (drop  (:write-size line-data) data)))



