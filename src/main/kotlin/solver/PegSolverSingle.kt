package solver

import peg.Move
import peg.PegBoard
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.Phaser
import java.util.concurrent.RecursiveTask
import java.util.concurrent.atomic.AtomicBoolean

class PegSolverSingle(
  val history: ArrayList<Move>,
  move: Move,
  val solved: LinkedBlockingQueue<Array<Move>>,
  val phaser: Phaser,
  val stop: AtomicBoolean
): RecursiveTask<Unit>() {

  var board: PegBoard

  init {
    phaser.register()
    board = PegBoard(history)
    board.move(move.x, move.y, move.dir)
  }

  override fun compute() {
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
      tasks += PegSolverSingle(board.movesPlayed.clone() as ArrayList<Move>, m, solved, phaser, stop)
    }
    tasks.forEach { t -> pool.execute(t) }
    tasks.forEach { t -> t.join() }
  }

  private fun solve() {
    val iter = board.moveList.clone() as ArrayList<Move>
    for (m in iter) {
      if (!stop.get())
        recurse(m)
    }
  }

  private fun recurse(move: Move) {
    board.move(move)
    if (board.countPegs() == 1) {
      solved.add(board.movesPlayed.toTypedArray())
      stop.compareAndSet(false, true)
      return
    }
    val iter = board.moveList.clone() as ArrayList<Move>
    for (m in iter) {
      if (!stop.get())
        recurse(m)
    }
    board.undoMove()
  }
}