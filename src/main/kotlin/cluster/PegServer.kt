package cluster

import peg.Move
import solver.PegSolver
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.Phaser
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class PegServer: Runnable {

  private val serverSocket = ServerSocket(ServerData.port)
  private var socket: Socket? = null

  var inStream: ObjectInputStream? = null
  var outStream: ObjectOutputStream? = null
  lateinit var task: Task

  private val solved = LinkedBlockingQueue<Array<Move>>()
  private val phaser = Phaser()

  override fun run() {
    println("Starting connection")
    socket = serverSocket.accept()
    println("Connected")

    inStream = ObjectInputStream(socket!!.getInputStream())
    task = inStream!!.readObject() as Task
    println("Task received")
    println("$task")

    solve()
  }

  private fun solve() {
    val solver = PegSolver(task.history, task.move, solved, phaser)
    outStream = ObjectOutputStream(socket!!.getOutputStream())
    println("Starting peg solver")

    solver.fork()
    while (phaser.registeredParties != 0) {
      try {
        val moves = solved.poll(5, TimeUnit.SECONDS)
        if (moves != null) {
          println("Solution found")
          outStream!!.writeObject(moves)
        }
      }catch (e: TimeoutException) { continue }
    }
    solver.join()
  }
}

