package gui

import peg.Move
import peg.PegBoard
import java.awt.event.ActionEvent
import java.awt.event.ActionListener

class FrameController(board: PegBoardFrame): Runnable, ActionListener {

  override fun run() {
  }


  override fun actionPerformed(e: ActionEvent?) {

    val moves = Thing.frame.solutionsPanel.selectedValue as Array<Move>
//    frame.move(moves)
    val board = PegBoard()

    for (move in moves) {
      board.move(move)
      Thing.frame.redrawBoard(board)
//      Thread.sleep(500)
    }
  }

}