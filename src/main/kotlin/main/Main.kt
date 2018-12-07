package main

import peg.Direction
import peg.Move
import peg.PegBoard
import solver.PegSolver
import java.util.*
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.Phaser
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import kotlin.collections.ArrayList

object Main {

  @JvmStatic
  fun main(args: Array<String>) {
    val board = PegBoard()
    val solved = LinkedBlockingQueue<Array<Move>>()
    val phaser = Phaser()

    val ps1 = PegSolver(board.movesPlayed.clone() as ArrayList<Move>, Move(3, 5, Direction.NORTH), solved, phaser)
    ps1.fork()


    println("Starting to listen for solutions")

    print("${phaser.registeredParties}: ")
    while (true) {
      try {
        println(Arrays.toString(solved.poll(10000, TimeUnit.MILLISECONDS)))
        print("${phaser.registeredParties}: ")
      } catch (e: TimeoutException) {}

      if (phaser.registeredParties < 50) {
        println("Ending")
        break
      }
    }

    ps1.join()
    println(solved.remove())
  }


}