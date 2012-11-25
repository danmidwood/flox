# flox

A Shepard Tone Generator written in Clojure

Work in progress. Here be no Shepards, yet.

## What is a Shepard Tone?

A Shepard tone is an auditory illusion that gives the impression of an ever increasing or decresing pitch, but without a global pitch change.

What?

It is best described as the audio equivalent of M. C. Escher's Ascending and Descending, wherein walking either up or down the steps results in no change to altitude.

![An optical illusion showing steps that raise up and then joins itself at the bottom to complete a circuit](http://upload.wikimedia.org/wikipedia/en/6/66/Ascending_and_Descending.jpg "M.C. Escher's Ascending and Descending")

## Get

`git clone git://github.com/danmidwood/flox.git`

## Usage

To run inside the repl
```shell
cd flox
lein repl
```

Inside the repl
```clojure
user> (ns flox.rad)
flox.rad> (emit-audio (create-line) (map byte-my-sine (sine-seq (freq-freq-lazy-seq 55.0))))
```
This will loop forever. Hit C-c C-b to interrupt.

## Examples

`freq-freq-lazy-seq` takes a start, a maximum and a minumum and produce a lazy seq beginning at start and incrememting up to maximum before restarting at minumum. A one parameter variant also exists where the start and min are set to the one parameter and the max set to one octave above.

This gives a nice bassy hum
```clojure
(emit-audio (create-line) (map byte-my-sine (sine-seq (freq-freq-lazy-seq 27.5 27.5 55.0))))
```

Multiple sounds can be produced together by combining their wave values
```clojure
(emit-audio 
	   (create-line) 
	    (map byte-my-sine (sine-seq 
			       (map -merge-values 
				    (freq-freq-lazy-seq 440 440 (frequency 0 440.0))
				    (freq-freq-lazy-seq 466 466 (frequency 0.5 466.0))))))
```


### Bugs

This is still WIP and doesn't actually produce Shepard Tones yet. What it does produce is the illusion of a Shepard Tone but with an actual increase in pitch,

## License

Copyright © 2012 Dan Midwood

Distributed under the Eclipse Public License, the same as Clojure.
