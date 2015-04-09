package org.vaidik.kataar

import com.typesafe.config._

object Kataar {
    var config: Config = null

    def configure(config: Config) {
      config.checkValid(ConfigFactory.defaultReference(), "kataar")
      this.config = config
    }

    def create (name: String): KataarQueue = {
      new KataarQueue(name)
    }
}

class KataarQueue (name: String) {
  // The actual queue
  private var q: List[String] = List()
  private var size: Int = 0

  def push(item: Any) {
    // TODO: this is probably error prone. Check better ways of fixing this.
    this.synchronized {
      if (this.size + 1 > Kataar.config.getInt("kataar.buffer")) {
        throw new QueueBufferOverflowException("Buffer overflow for queue " + this.name + ".")
      }

      val newQ = List(this.q, List(item.toString)).flatten
      this.q = newQ
      size = size + 1
    }
  }

  def pop(): String = {
    this.synchronized {
      try {
        val item = this.q.head
        this.q = this.q.drop(1)
        size = size - 1
        item
      } catch {
        case e: java.util.NoSuchElementException  => {
          throw new EmptyQueueException(this.name + " queue is empty.")
        }
      }
    }
  }
}
