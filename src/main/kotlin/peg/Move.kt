package peg

class Move(val x: Int, val y: Int, val dir: Direction) {

  override operator fun equals(other: Any?): Boolean =
    hashCode() == other.hashCode()

  override fun hashCode(): Int {
    var result = x
    result = 31 * result + y
    result = 31 * result + dir.hashCode()
    return result
  }

  fun toLongString() = "{$x, $y, $dir}"

  override fun toString() = "$x:$y:${dir.toString().first()}"
}