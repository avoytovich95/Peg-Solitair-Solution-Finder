package solver

import peg.Move
import peg.PegBoard
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.Phaser
import java.util.concurrent.RecursiveTask

const val STEPS = 5

class PegSolver(
  val history: ArrayList<Move>,
  move: Move,
  val solved: LinkedBlockingQueue<Array<Move>>,
  val phaser: Phaser
): RecursiveTask<Unit>() {

  var board: PegBoard
  var found = false

  init {
    phaser.register()
    board = PegBoard(history)
    board.move(move.x, move.y, move.dir)
  }

  override fun compute() {
//    if (board.win) {
//      solved.add(board.movesPlayed.toTypedArray())
//    } else if (board.moveList.size > 0) {
//      if (board.movesPlayed.size < STEPS) {
//        playNextMoves()
//      } else
//        solve()
//    }
    if (board.movesPlayed.size < STEPS) {
      playNextMoves()
    } else
      solve()
    phaser.arriveAndDeregister()
  }

  private fun playNextMoves() {
    val tasks = ArrayList<RecursiveTask<Unit>>()
    val pool = ForkJoinPool()
    board.moveList.forEach{ m ->
      tasks += PegSolver(board.movesPlayed.clone() as ArrayList<Move>, m, solved, phaser)
    }
    tasks.forEach { t -> pool.execute(t) }
    tasks.forEach { t -> t.join() }
  }

  private fun solve() {
    val iter = board.moveList.clone() as ArrayList<Move>
    for (m in iter) {
      if (!found)
        recurse(m)
    }
  }

  private fun recurse(move: Move) {
    board.move(move)
    if (board.countPegs() == 1) {
      solved.add(board.movesPlayed.toTypedArray())
      found = true
      return
    }
    val iter = board.moveList.clone() as ArrayList<Move>
    for (m in iter) {
      if (!found)
        recurse(m)
    }
    board.undoMove()
  }
}