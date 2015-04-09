package org.vaidik.kataar

// Exception to be thrown whenever queue is empty
case class EmptyQueueException(message: String = null) extends Exception(message)

// Exception to be thrown when buffer overflows
case class QueueBufferOverflowException(message: String = null) extends Exception(message)
