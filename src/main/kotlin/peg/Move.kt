package peg

import java.io.Serializable

class Move(val x: Int, val y: Int, val dir: Direction): Serializable {

  fun toLongString() = "{$x, $y, $dir}"

  override fun toString() = "$x:$y:${dir.toString().first()}"
}