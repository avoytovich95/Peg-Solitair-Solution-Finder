package gui

import peg.Move
import peg.PegBoard
import peg.SpaceType.*

import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.GridLayout
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.util.concurrent.locks.ReentrantLock

import javax.swing.*
import javax.swing.JList
import java.util.*


class PegBoardFrame : JFrame(), ActionListener {

  val solutions = ArrayList<Array<Move>>()

  var solutionsPanel = JList(solutions.toArray())
  val gridPanel = JPanel()
  val textArea = JTextArea()
  val run = JButton("Run")

  val lock = ReentrantLock()
  val gridLock = ReentrantLock()

  init {
    setSize(800, 800)
    layout = GridBagLayout()
    title = "Peg Solitaire Deep Search"
    gridPanel.layout = GridLayout(PegBoard.X, PegBoard.Y)
    textArea.isEditable = false
    textArea.lineWrap = true
    setGridBag()
    setListClick()

    run.addActionListener(this)
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
      gridheight = 2
      gridwidth = 2
      weightx = 3.0
      weighty = 2.0
      add(gridPanel, this)

      gridx = 0
      gridy = 3
      gridheight = 1
      gridwidth = 3
      weighty = 0.5
      add(textArea, this)
    }
  }

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

  private fun setListClick() {
    solutionsPanel.addMouseListener(object : MouseAdapter() {
      override fun mouseClicked(evt: MouseEvent) {
        val list = evt.source as JList<Array<Move>>
        if (evt.clickCount == 1) {
          val index = list.locationToIndex(evt.point)
          val moves = list.model.getElementAt(index) as Array<Move>

          val str = StringBuilder()
          for (i in 0 until moves.size) {
            str.append("${moves[i].toLongString()} ")
            if (i != moves.size - 1)
              str.append("-> ")
            if ((i + 1) % 7 == 0)
              str.append("\n")
          }

          textArea.text = str.toString()
        }
      }
    })
  }

  override fun actionPerformed(e: ActionEvent?) {
    val action = GridController(this, solutionsPanel.selectedValue as Array<Move>)
    Thread(action).start()
  }

  fun redrawBoard(board: PegBoard) {
    SwingUtilities.invokeLater {
      gridLock.lock()
      try {
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
        gridPanel.updateUI()
        repaint()
      } finally {
        gridLock.unlock()
      }
    }
  }

}