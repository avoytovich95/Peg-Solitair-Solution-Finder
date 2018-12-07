package gui

import peg.Direction
import peg.Move
import peg.PegBoard
import peg.SpaceType.*

import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.GridLayout
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.util.concurrent.locks.ReentrantLock

import javax.swing.*
import javax.swing.JList
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.util.*


class PegBoardFrame: JFrame(){

  val solutions = ArrayList<Array<Move>>()
//  val board = PegBoard()

  lateinit var currentSelectedSolution: Array<Move>

  var solutionsPanel = JList(solutions.toArray())
  val gridPanel = JPanel()
  val textArea = JTextArea()
  val run = JButton("Run")

  val lock = ReentrantLock()
  val gridLock = ReentrantLock()

  init {
    setSize(800, 700)
    layout = GridBagLayout()
    title = "Peg Solitaire Deep Search"
    gridPanel.layout = GridLayout(PegBoard.X, PegBoard.Y)
    setGridBag()
//    setListClick()
//    run.addActionListener(this)
    redrawBoard(PegBoard())
  }

  private fun setGridBag() {
    GridBagConstraints().run {
      fill = GridBagConstraints.BOTH
      ipadx = 1
      ipady = 1

      gridx = 0
      gridy = 0
      gridheight = 3
      weightx = 1.0
      weighty = 3.0
      add(solutionsPanel, this)

      gridx = 1
      gridheight = 3
      gridwidth = 2
      weightx = 3.0
      weighty = 2.0
      add(gridPanel, this)

      gridx = 1
      gridy = 4
      gridheight = 1
      gridwidth = 2
      weighty = 0.5
      add(textArea, this)
    }
  }

//  private fun setListClick() {
//    solutionsPanel.addMouseListener(object : MouseAdapter() {
//      override fun mouseClicked(evt: MouseEvent) {
//        val list = evt.source as JList<Array<Move>>
//        if (evt.clickCount == 1) {
//          val index = list.locationToIndex(evt.point)
//          currentSelectedSolution = list.model.getElementAt(index)
//        }
////        else if (evt.clickCount == 2) {
////          val index = list.locationToIndex(evt.point)
////          val n = list.model.getElementAt(index)
////          move(n)
////        }
//      }
//    })
//  }

  fun addSolution(moves: Array<Move>) {
    SwingUtilities.invokeLater {
      lock.lock()
      try {
        solutions.add(moves)
        solutionsPanel.setListData(solutions.toTypedArray())
        revalidate()
        repaint()
      } finally {
        lock.unlock()
      }
    }
  }

//  fun move(moves: Array<Move>) {
//    println("Solving")
//    for (move in moves) {
//      Thread.sleep(300)
//      board.move(move)
//      redrawBoard()
//    }
//    Thread.sleep(1000)
////    board.reset()
////    redrawBoard()
//  }

  fun redrawBoard(board: PegBoard) {
    gridLock.lock()
    try {
      println(board)
      gridPanel.removeAll()
      for (y in 0 until PegBoard.Y) {
        for (x in 0 until PegBoard.X) {
          if (x == PegBoard.X - 1 && y == PegBoard.Y - 1)
            gridPanel.add(run)
          else {
            when (board.board[x][y].type) {
              PEG -> gridPanel.add(Circle(true))
              HOLE -> gridPanel.add(Circle(false))
              BLOCK -> gridPanel.add(JPanel())
            }
          }
        }
      }
      revalidate()
      repaint()
      Thread.sleep(500)
    } finally {
      gridLock.unlock()
    }
  }

//  fun redrawBoard(board: PegBoard) {
//    gridLock.lock()
//    try {
//
//    } finally {
//      gridLock.unlock()
//    }
//  }

}
object Thing {

  lateinit var frame :PegBoardFrame

  @JvmStatic
  fun main(args: Array<String>) {
    SwingUtilities.invokeLater {
      frame = PegBoardFrame()
      frame.isVisible = true

      val moves = arrayOf(
        Move(3, 1, Direction.SOUTH),
        Move(5, 2, Direction.WEST),
        Move(2, 2, Direction.EAST)
      )

      frame.addSolution(moves)
      val controller = FrameController(frame)
      frame.run.addActionListener(controller)

      Thread(controller).start()
    }
  }
}