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
         (alter audio-data-queue #(drop proposed-amount-to-write %)))))
    nil))

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



(defn frequency
  "Calculate the frequency of a note offset from a base frequency."
  ([] (frequency 0))
  ([offset] (frequency offset 440.0) )
  ([offset base]
     (* base (clojure.math.numeric-tower/expt a12th-root offset))))

(defn find-sine
  [distance]
  (clojure.algo.generic.math-functions/sin (* 2 distance (. Math PI))))


(defn get-note
  ([offset] (get-note offset 1000))
  ([offset ms]
     (let
         [freq (frequency offset)
          period (/ sample-rate freq)]
       (for [i (range (* sample-rate (/ ms 1000)))]
         (let [angle (/ (* 2 i (. Math PI)) period)]
           (byte (* 127  (clojure.algo.generic.math-functions/sin angle))))))))
         
(defn get-note-replacement
  ([offset] (get-note-replacement offset 1000))
  ([offset ms]
     (let
         [freq (frequency offset) ; 440.0
          cycles-per-sample (/ sample-rate freq) ; 109.0909090909
          requested-no-of-samples (* sample-rate (/ ms 1000)) ; 48 * ms
          actual-no-of-samples (* cycles-per-sample (clojure.math.numeric-tower/round (+ 0.5 (/ requested-no-of-samples cycles-per-sample))))]
       (for [i (range actual-no-of-samples)]
         (byte (* 127 (find-sine (/ i cycles-per-sample))))))))

(defn get-notes
  [& offsets]
  (let [frame-size (* sample-rate (/ 1000 1000))
        all-frames-total-size (* frame-size (count offsets))]
    (for [i (range (-  all-frames-total-size 1))]
      (let [offset (nth offsets (if (> i 0) (mod frame-size i) 0) 0)
            freq (frequency offset)
            period (/ sample-rate freq)
            angle (/ (* 2 i (. Math PI)) period)]
        (byte (* 127  (clojure.algo.generic.math-functions/sin angle)))))))
  


(defn harmonize
  [& waves]
  (apply map (fn [& notes] (float (/ (apply + notes) (count waves)))) waves))


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
  (map #(play-to-line @line (get-note %1 (/ 10000 steps)))
       (offsets start end steps)))


(defn play-to-queue
  [start end steps]
  (map #(queue-data (get-note % 250))
       (offsets start end steps)))

(defn play-notes-to-queue
  [start end steps]
  (play-to-line @line (apply get-notes (offsets start end steps))))




(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (do
    (map #(queue-data (get-note % 250))
         (offsets -48 -24 24))
    (println "Starting...")
    (start))
)





