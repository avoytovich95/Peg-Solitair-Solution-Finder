package cluster

import gui.PegBoardFrame
import main.Controller
import main.Main
import peg.Direction
import peg.Move
import java.util.ArrayList
import javax.swing.JFrame
import javax.swing.SwingUtilities

object Main {

  lateinit var frame: PegBoardFrame
  lateinit var wolfTask: Task
  lateinit var rhoTask: Task
  lateinit var altairTask: Task

  @JvmStatic
  fun main(args: Array<String>) {
    SwingUtilities.invokeLater {
      frame = PegBoardFrame()
      frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE

      createTasks()
      Thread(PegClient(ServerData.wolf, "Wolf", wolfTask, frame)).start()
      Thread(PegClient(ServerData.rho, "Rho", rhoTask,  frame)).start()
      Thread(PegClient(ServerData.altair, "Altair", altairTask, frame)).start()

      frame.isVisible = true
    }
  }

  private fun createTasks() {
    wolfTask = Task(Move(3, 5, Direction.NORTH), ArrayList())
    rhoTask = Task(Move(3, 1, Direction.SOUTH), ArrayList())
    altairTask = Task(Move(1, 3, Direction.EAST), ArrayList())
  }

}