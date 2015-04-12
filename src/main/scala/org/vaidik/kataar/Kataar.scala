package org.vaidik.kataar

import com.typesafe.config._
import java.io.File
import scala.pickling.Defaults._
import scala.pickling.json._

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
  private var pushQ: List[String] = List()
  private var popQ: List[String] = List()
  private var size: Int = 0

  private val datadirFile = new File(Kataar.config.getString("kataar.datadir"))
  private var dataFiles: List[String] = datadirFile.list.toList.filter((fileName: String) => {
    fileName.startsWith(this.name + "-")
  })

  private def backup(q: List[String]) {
    var fileName = this.name + "-" + System.currentTimeMillis + ".dump"

    new Thread(new Runnable {
      def run() {
        var filePath = Kataar.config.getString("kataar.datadir") + "/" + fileName
        var pw = new java.io.PrintWriter(new File(filePath))
        pw.write(q.pickle.toString)
        pw.close()

        dataFiles = List(dataFiles, List(fileName)).flatten
      }
    }).start
  }

  def push(item: Any) {
    this.synchronized {
      if (this.size + 1 > Kataar.config.getInt("kataar.buffer")) {
        this.backup(this.pushQ)
        this.pushQ = List()
        this.size = 0
        // throw new QueueBufferOverflowException("Buffer overflow for queue " + this.name + ".")
      }

      // TODO: this is probably error prone. Check better ways of fixing this.
      val newQ = List(this.pushQ, List(item.toString)).flatten
      this.pushQ = newQ
      size = size + 1
    }
  }

  def pop(): String = {
    this.synchronized {
      try {
        val item = this.popQ.head
        this.popQ = this.popQ.drop(1)
        size = size - 1
        item
      } catch {
        case e: java.util.NoSuchElementException => {
          throw new EmptyQueueException(this.name + " queue is empty.")
        }
      }
    }
  }
}
