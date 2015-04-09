package org.vaidik.kataar

object Kataar {
    var conf = Map("path" -> "/var/lib/kataar")

    def configure(conf: Map[String, String]) {
      // TODO: check how to refer to object variable of the same name
      this.conf = conf
    }

    def create (name: String): KataarQueue = {
      new KataarQueue(name)
    }
}

// Exception to be thrown whenever queue is empty
case class EmptyQueueException(message: String = null) extends Exception(message)

class KataarQueue (name: String) {
  // The actual queue
  private var q: List[String] = List()

  def push(item: Any) {
    // TODO: this is probably error prone. Check better ways of fixing this.
    this.synchronized {
      val newQ = List(this.q, List(item.toString)).flatten
      this.q = newQ
    }
  }

  def pop(): String = {
    this.synchronized {
      try {
        val item = this.q.head
        this.q = this.q.drop(1)
        item
      } catch {
        case e: java.util.NoSuchElementException  => {
          throw new EmptyQueueException(this.name + " queue is empty.")
        }
      }
    }
  }
}
