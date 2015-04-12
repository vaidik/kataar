# kataar

**Note:** this is a work in progress

An almost useless queueing library with certain useless features to learn some
Scala.

## Features

* Fixed buffer size for in-memory queueing, to prevent OOMs
* Persist to disk when crosses buffer limit
* Persist on regular intervals
* Persistence work done in a separate thread

## Configuration

* Copy `application.default.conf` to
  `./target/scala-2.11/classes/application.conf` with the actual values
  substituted in the conf file.
