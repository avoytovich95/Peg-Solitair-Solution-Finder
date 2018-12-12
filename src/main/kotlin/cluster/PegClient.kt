package cluster

import gui.PegBoardFrame
import peg.Direction
import peg.Move
import java.io.EOFException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.ConnectException
import java.net.Socket
import java.util.*

class PegClient(val ip: String, val name: String, val task: Task, val frame: PegBoardFrame): Runnable {

  private var socket: Socket? = null

  var outStream: ObjectOutputStream? = null
  var inStream: ObjectInputStream? = null

  var tries = 0

  override fun run() {

    while (true) {
      try {
        socket = Socket(ip, ServerData.port)
        break
      } catch (e: ConnectException) {
        if (tries++ >= 5) {
          println("Connection to $name failed")
          return
        }
        Thread.sleep(2000)
        println("Retrying connection to $name")
      }
    }
//    socket = Socket(ip, ServerData.port)

    outStream = ObjectOutputStream(socket!!.getOutputStream())
    outStream!!.writeObject(task)

    getSolved()
  }

  private fun getSolved() {
    inStream = ObjectInputStream(socket!!.getInputStream())

    while (true) {
      try {
        val moves = inStream!!.readObject() as Array<Move>
        frame.addSolution(moves)
      } catch (e: EOFException) {
        println("Server disconnected")
        break
      }
    }
    socket!!.close()
  }
}