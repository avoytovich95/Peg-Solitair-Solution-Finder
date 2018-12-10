package cluster

import peg.Move
import java.io.Serializable

class Task(val move: Move, val history: ArrayList<Move>): Serializable {

  override fun toString() =
      "Next Move: $move\n" +
          "History: $history"

}