package cluster

import gui.PegBoardFrame
import peg.Direction
import peg.Move
import java.io.EOFException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.Socket
import java.util.*

class PegClient(val ip: String, val task: Task, val frame: PegBoardFrame): Runnable {

  private var socket: Socket? = null

  var outStream: ObjectOutputStream? = null
  var inStream: ObjectInputStream? = null

  override fun run() {
    socket = Socket(ip, ServerData.port)

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