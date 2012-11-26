# flox

A Shepard Tone Generator written in Clojure

## What is a Shepard Tone?

A Shepard tone is an auditory illusion that gives the impression of an ever increasing or decreasing pitch, but without a global pitch change.

#### What?

A visual equivalent would be M. C. Escher's Ascending and Descending, wherein walking either up or down the steps results in no change to altitude.

![An optical illusion showing steps that raise up and then joins itself at the bottom to complete a circuit](http://upload.wikimedia.org/wikipedia/en/6/66/Ascending_and_Descending.jpg "M.C. Escher's Ascending and Descending")

## Get

Source:
`git clone git://github.com/danmidwood/flox.git`

Runnable jar:
https://github.com/downloads/danmidwood/flox/flox-0.1.0-SNAPSHOT-standalone.jar

## Usage

### With Leiningen

Run through leiningen with `lein run` and passing in a starting pitch, size of increase and the number of concurrent audio tracks.

e.g. To start at 27.5 (A), rise 96 tones (8 octaves (96 / 12)) and with 8 audio tracks
```shell
lein run 27.5 96 8
```

Alternatively, to run inside the repl
```shell
cd flox
lein repl
```

Then, inside the repl
```clojure
user> (ns flox.rad)
flox.rad> (start 27.5 96 8)
```
This will loop forever. Hit `C-c C-b` to interrupt.

### With the downloaded jar file

This takes the same parameters as described above in the Leiningen section.

```shell
java -jar flox-0.1.0-SNAPSHOT-standalone.jar 27.5 96 8
```


### How to choose values

For the pitch value anything can be chosen. A's are easy to deal with because they're often whole numbers.
Some values of A are:

* 27.5
* 55
* 110
* 220
* 440 (A above middle C, often used as "Pitch Standard" for tuning instruments in an orchestra)
* 880

The increase is the number of tones that the scale should cover. Use full octaves (multiples of 12 tones) for Shepard tones. Four or more octaves gives a good sound.
The number of audio tracks should be equal to the number of octaves to keep the notes in sync between octaves.
Some suggestions:

* 36 3
* 48 4
* 60 5
* 72 6
* 84 7
* 96 8

Of course, there's a great potential to make some strange noises instead by throwing in other numbers. Experimentation is always recommended.

### Bugs

All audio wave values are calculated just in time, and sometimes cannot be processed fast enough to prevent the audio buffer from underrunning. To work around this the sample rate has been set at a low 4410.

If you experience stuttering, then dropping the number of audio tracks should help.
Conversely, if you have a powerful machine the try upping the sample rate and see how it goes.

## License

Copyright © 2012 Dan Midwood

Distributed under the Eclipse Public License, the same as Clojure.
