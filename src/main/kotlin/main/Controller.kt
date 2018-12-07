package main

import gui.PegBoardFrame
import peg.Direction
import peg.Move
import peg.PegBoard
import solver.PegSolver
import solver.PegSolverSingle
import java.util.*
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.Phaser
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import java.util.concurrent.atomic.AtomicBoolean

class Controller(val frame: PegBoardFrame): Runnable {

  val solved = LinkedBlockingQueue<Array<Move>>()
  val phaser = Phaser()
  val board = PegBoard()
  val stop = AtomicBoolean(false)

  var solver: PegSolverSingle

  init {

    solver = PegSolverSingle(
      board.movesPlayed.clone() as ArrayList<Move>,
      Move(3, 5, Direction.NORTH),
      solved,
      phaser,
      stop
    )
  }


  override fun run() {
    solver.fork()
//    print("${phaser.registeredParties}: ")
    while (phaser.registeredParties != 0) {
      try {
        val moves = solved.poll(5, TimeUnit.SECONDS)
//        println(Arrays.toString(moves))
//        print("${phaser.registeredParties}: ")
        if (moves != null) {
//          println("Solution found")
          frame.addSolution(moves)
        }
      } catch (e: TimeoutException) { continue }
    }
    solver.join()
  }

}