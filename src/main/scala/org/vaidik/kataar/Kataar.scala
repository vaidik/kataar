package org.vaidik.kataar

object Kataar {
    var conf = Map(
      "buffer" -> 10
    )

    def configure(conf: Map[String, Int]) {
      // TODO: check how to refer to object variable of the same name
      this.conf = conf
    }

    def create (name: String): KataarQueue = {
      new KataarQueue(name)
    }
}

// Exception to be thrown whenever queue is empty
case class EmptyQueueException(message: String = null) extends Exception(message)

// Exception to be thrown when buffer overflows
case class QueueBufferOverflowException(message: String = null) extends Exception(message)

class KataarQueue (name: String) {
  // The actual queue
  private var q: List[String] = List()
  private var size: Int = 0

  def push(item: Any) {
    // TODO: this is probably error prone. Check better ways of fixing this.
    this.synchronized {
      if (this.size + 1 > Kataar.conf{"buffer"}) {
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
