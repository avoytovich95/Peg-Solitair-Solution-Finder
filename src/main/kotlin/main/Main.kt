package main

import gui.PegBoardFrame
import javax.swing.JFrame
import javax.swing.SwingUtilities

object Main {

  lateinit var frame: PegBoardFrame

  @JvmStatic
  fun main(args: Array<String>) {
    SwingUtilities.invokeLater {
      frame = PegBoardFrame()
      frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE

      Thread(Controller(frame)).start()

      frame.isVisible = true
    }

  }
}

