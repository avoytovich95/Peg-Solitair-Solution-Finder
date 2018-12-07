package gui

import peg.Direction
import peg.Move
import peg.PegBoard
import java.awt.event.ActionEvent
import java.awt.event.ActionListener

class GridController(val frame: PegBoardFrame, val moves: Array<Move>): Runnable {

  override fun run() {
    frame.run.isEnabled = false

    val board = PegBoard()
    frame.redrawBoard(board)
    Thread.sleep(800)

    for (move in moves) {
      board.move(move)
      frame.redrawBoard(board)
      Thread.sleep(800)
    }
    frame.run.isEnabled = true
  }

}