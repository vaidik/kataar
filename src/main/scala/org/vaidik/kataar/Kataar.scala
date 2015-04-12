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

  private val datadirFile = new File(Kataar.config.getString("kataar.datadir"))
  private var dataFiles: List[String] = datadirFile.list.toList.filter((fileName: String) => {
    fileName.startsWith(this.name + "-")
  })

  this.loadBackup()

  private def backup() {
    var fileName = this.name + "-" + System.currentTimeMillis + ".dump"
    var q = this.pushQ

    new Thread(new Runnable {
      def run() {
        var filePath = Kataar.config.getString("kataar.datadir") + "/" + fileName
        var pw = new java.io.PrintWriter(new File(filePath))
        pw.write(q.pickle.value)
        pw.close()

        dataFiles = List(dataFiles, List(fileName)).flatten
      }
    }).start
  }

  def push(item: Any) {
    this.synchronized {
      if (this.pushQ.length + 1 > Kataar.config.getInt("kataar.buffer")) {
        this.backup
        this.pushQ = List()
      }

      // TODO: this is probably error prone. Check better ways of fixing this.
      val newQ = List(this.pushQ, List(item.toString)).flatten
      this.pushQ = newQ
    }
  }

  private def loadBackup() {
    try {
      val backupFile = this.dataFiles.head
      this.popQ =
        scala.io.Source.fromFile(Kataar.config.getString("kataar.datadir") +
        "/" + backupFile).mkString.unpickle[List[String]]
      dataFiles = dataFiles.drop(1)
    } catch {
      case e: java.util.NoSuchElementException => this.popQ = this.pushQ
    }
  }

  def pop(): String = {
    this.synchronized {
      if (this.popQ.length == 0) {
          this.loadBackup()
      }

      try {
        val item = this.popQ.head
        val newQ = this.popQ.drop(1)

        // When pushQ and popQ are the same
        if (this.popQ == this.pushQ) {
          this.pushQ = newQ
        }
        this.popQ = newQ

        item
      } catch {
        case e: java.util.NoSuchElementException => {
          throw new EmptyQueueException(this.name + " queue is empty.")
        }
      }
    }
  }
}
