(ns flox.main
  (:gen-class :main true)
  (:use flox.rad))


(defn -main
    "Start the Shepard.
This takes three parameters
Base: The lower point of the scale.
Height: The number of notes to rise. Fractions of notes are allowed.
Threads: How many concurrent streams of sound there should be"
    [& args]
    (apply start (map #(. Float valueOf %) args)))
