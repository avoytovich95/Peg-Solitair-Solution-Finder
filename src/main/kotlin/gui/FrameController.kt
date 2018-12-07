package gui

import peg.Move
import peg.PegBoard
import java.awt.event.ActionEvent
import java.awt.event.ActionListener

class FrameController(val frame: PegBoardFrame): Runnable, ActionListener {

  override fun run() {
    while (true) {}
  }


  override fun actionPerformed(e: ActionEvent?) {

    val moves = frame.solutionsPanel.selectedValue as Array<Move>
//    frame.move(moves)
    val board = PegBoard()

    for (move in moves) {
      board.move(move)
      frame.redrawBoard(board)
      Thread.sleep(500)
    }
  }

}