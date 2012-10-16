(ns flox.core
  (:gen-class)
  (:require clojure.algo.generic.math-functions)
  (:require clojure.math.numeric-tower))

(import '(javax.sound.sampled AudioSystem DataLine$Info
                              SourceDataLine
                              AudioFormat AudioFormat$Encoding))


(def sample-rate 48000)
(def audio-format (AudioFormat. sample-rate, 16, 2, true, false))
(def a12th-root (clojure.math.numeric-tower/expt 2 (/ 1 12)))
(def audio-data-queue (ref '()))

(defn create-line
  []
  (let [line (. AudioSystem getSourceDataLine audio-format)]
    (doto line
      (.open audio-format, sample-rate)
      (.start))))

(def line (agent (create-line)))

(defn play-to-line
  [line data]
  (.write ^SourceDataLine line (byte-array data) 0 (count data))
  line)



(defn write-to-audio-loop
  []
  (let [space-for-writing (.available @line)
        size-of-data-available (count @audio-data-queue)
        proposed-amount-to-write 24000]
    (do
      (if (and
           (>= space-for-writing proposed-amount-to-write)
           (>= size-of-data-available proposed-amount-to-write))
        (dosync
         (println "writing")
         (send line #(play-to-line % (take proposed-amount-to-write @audio-data-queue)))
         (alter audio-data-queue #(drop proposed-amount-to-write %)))))))

(def running (ref false))

(defn run-audio-loop
  []
  (while @running
    (write-to-audio-loop)))

(defn start
  []
  (dosync
   (ref-set running true)
   (future (run-audio-loop))))

(defn stop
  []
  (dosync
   (ref-set running false)))

(defn queue-data
  [data]
  (dosync
   (alter audio-data-queue concat data))
  nil)



(defn frequency-int
  "Calculate the frequency of a note offset from a base frequency."
  ([] (frequency-int 0))
  ([offset] (frequency-int offset 444.0) )
  ([offset base]
     (* base (clojure.math.numeric-tower/expt a12th-root offset))))

(def frequency (memoize frequency-int))

(defn get-note-int
  ([offset] (get-note-int offset 1000))
  ([offset ms]
     (let
         [freq (frequency offset)
          period (/ sample-rate freq)]
       (for [i (range (* sample-rate (/ ms 1000)))]
         (let [angle (/ (* 2 i (. Math PI)) period)]
           (byte (* 127  (clojure.algo.generic.math-functions/sin angle))))))))

(def get-note (memoize get-note-int))

(defn harmonize
  [& waves]
  (apply map (fn [& notes] (byte (/ (apply + notes) (count waves)))) waves))


(defn rise
  ([start end note-provider] (rise (range start end) note-provider))
  ([note-offsets note-provider]
     (loop [distance 0 line @line]
       (play-to-line line (get-note (nth note-offsets distance)))
       (recur (mod (+ 1 distance) (count note-offsets)) line))))

(defn offsets
  [start end steps]
  (let [step-distance (/ (clojure.math.numeric-tower/abs (- start end)) steps)]
    (if (< start end)
      (range start end step-distance)
      (reverse (range end start step-distance)))))


(defn play
  [start end steps]
  (map #(play-to-line @line (get-note %1 100))
       (offsets start end steps)))

(defn play-to-queue
  [start end steps]
  (map #(queue-data (get-note % 250))
       (offsets start end steps)))





(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (do
    (map #(queue-data (get-note % 250))
         (offsets -48 -24 24))
    (println "Starting...")
    (start))
)





