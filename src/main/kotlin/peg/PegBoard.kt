package peg

class PegBoard(private val holeX: Int, private val holeY: Int): Cloneable {

  val board = Array(Y) { Array(X) { Space(SpaceType.PEG) } }
  private var copy = false
  private var check = true
  val moveList = ArrayList<Move>()
  var movesPlayed = ArrayList<Move>()
  var win = false
  var count = 32

  constructor() : this(holeX = 3, holeY = 3)

  constructor(board: PegBoard) : this(board.holeX, board.holeY) {
    copy = true
    for (x in 0 until PegBoard.X) {
      for (y in 0 until PegBoard.Y) {
        this.board[x][y] = board.board[x][y]
      }
    }
    win = board.win
    check = board.check
    count = board.count
    this.movesPlayed = ArrayList(board.movesPlayed)
    findAllMoves()
  }

  constructor(history: ArrayList<Move>) : this() {
    val list = history.toTypedArray()
    for (move in list)
      move(move.x, move.y, move.dir)
  }

  init {
    setRestricted()
    if (!copy) {
      reset()
    }
  }

  fun reset() {
    for (x in 0 until X) {
      for (y in 0 until Y) {
        if (board[x][y].type != SpaceType.BLOCK)
        board[x][y].type = SpaceType.PEG
      }
    }
    board[holeX][holeY].type = SpaceType.HOLE
    check = true
    count = TOTAL
    findAllMoves()
  }

  private fun setRestricted() {
    board[0][0].type = SpaceType.BLOCK; board[0][1].type = SpaceType.BLOCK
    board[0][5].type = SpaceType.BLOCK; board[0][6].type = SpaceType.BLOCK
    board[1][0].type = SpaceType.BLOCK; board[1][6].type = SpaceType.BLOCK
    board[5][0].type = SpaceType.BLOCK; board[5][6].type = SpaceType.BLOCK
    board[6][0].type = SpaceType.BLOCK; board[6][1].type = SpaceType.BLOCK
    board[6][5].type = SpaceType.BLOCK; board[6][6].type = SpaceType.BLOCK
    // English board setup
    board[1][1].type = SpaceType.BLOCK; board[5][1].type = SpaceType.BLOCK
    board[1][5].type = SpaceType.BLOCK; board[5][5].type = SpaceType.BLOCK
  }

  fun move(x: Int, y: Int, dir: Direction) {
    move(Move(x, y, dir))
  }

  fun move(move: Move): Boolean {
    return if (move in moveList && !win) {
      when (move.dir) {
        Direction.NORTH -> {
          board[move.x][move.y].type = SpaceType.HOLE
          board[move.x][move.y - 1].type = SpaceType.HOLE
          board[move.x][move.y - 2].type = SpaceType.PEG
        }
        Direction.WEST -> {
          board[move.x][move.y].type = SpaceType.HOLE
          board[move.x - 1][move.y].type = SpaceType.HOLE
          board[move.x - 2][move.y].type = SpaceType.PEG
        }
        Direction.SOUTH -> {
          board[move.x][move.y].type = SpaceType.HOLE
          board[move.x][move.y + 1].type = SpaceType.HOLE
          board[move.x][move.y + 2].type = SpaceType.PEG
        }
        Direction.EAST -> {
          board[move.x][move.y].type = SpaceType.HOLE
          board[move.x + 1][move.y].type = SpaceType.HOLE
          board[move.x + 2][move.y].type = SpaceType.PEG
        }
      }
      movesPlayed.add(move)
      findAllMoves()

      true
    } else
      false
  }

  fun undoMove() {
    val move = movesPlayed.removeAt(movesPlayed.size - 1)
    when (move.dir) {
      Direction.NORTH -> {
        board[move.x][move.y].type = SpaceType.PEG
        board[move.x][move.y - 1].type = SpaceType.PEG
        board[move.x][move.y - 2].type = SpaceType.HOLE
      }
      Direction.WEST -> {
        board[move.x][move.y].type = SpaceType.PEG
        board[move.x - 1][move.y].type = SpaceType.PEG
        board[move.x - 2][move.y].type = SpaceType.HOLE
      }
      Direction.SOUTH -> {
        board[move.x][move.y].type = SpaceType.PEG
        board[move.x][move.y + 1].type = SpaceType.PEG
        board[move.x][move.y + 2].type = SpaceType.HOLE
      }
      Direction.EAST -> {
        board[move.x][move.y].type = SpaceType.PEG
        board[move.x + 1][move.y].type = SpaceType.PEG
        board[move.x + 2][move.y].type = SpaceType.HOLE
      }
    }
    findAllMoves()
  }

  private fun findAllMoves() {
    var count = 0
    moveList.clear()
    for (x in 0 until X) {
      for (y in 0 until Y) {
        if (board[x][y].type != SpaceType.BLOCK) {
          if (board[x][y].type == SpaceType.PEG)
            count++
//            resourceCount += board[x][y].x
          if (check)
            findMovesHole(x, y)
          else
            findMovesPeg(x, y)
        }
      }
    }
    if (count == 1)
      win = true
  }

  private fun findMovesHole(x: Int, y: Int) {
    if (board[x][y].type == SpaceType.HOLE) {
      //NORTH
      var next = y + 1
      var over = y + 2
      if (next < Y && over < Y && board[x][next].type == SpaceType.PEG && board[x][over].type == SpaceType.PEG)
        moveList += Move(x, over, Direction.NORTH)
      //SOUTH
      next = y - 1
      over = y - 2
      if (next >= 0 && over >= 0 && board[x][next].type == SpaceType.PEG && board[x][over].type == SpaceType.PEG)
        moveList += Move(x, over, Direction.SOUTH)
      //EAST
      next = x - 1
      over = x - 2
      if (next >= 0 && over >= 0 && board[next][y].type == SpaceType.PEG && board[over][y].type == SpaceType.PEG)
        moveList += Move(over, y, Direction.EAST)
      //WEST
      next = x + 1
      over = x + 2
      if (next < X && over < X && board[next][y].type == SpaceType.PEG && board[over][y].type == SpaceType.PEG)
        moveList += Move(over, y, Direction.WEST)
    }
  }

  private fun findMovesPeg(x: Int, y: Int) {
    if (board[x][y].type == SpaceType.PEG) {
      //NORTH
      var next = y - 1
      var over = y - 2
      if (next >= 0 && over >= 0 && board[x][next].type == SpaceType.PEG && board[x][over].type == SpaceType.HOLE)
        moveList += Move(x, y, Direction.NORTH)
      //SOUTH
      next = y + 1
      over = y + 2
      if (next < Y && over < Y && board[x][next].type == SpaceType.PEG && board[x][over].type == SpaceType.HOLE)
        moveList += Move(x, y, Direction.SOUTH)
      //WEST
      next = x - 1
      over = x - 2
      if (next >= 0 && over >= 0 && board[next][y].type == SpaceType.PEG && board[over][y].type == SpaceType.HOLE)
        moveList += Move(x, y, Direction.WEST)
      //EAST
      next = x + 1
      over = x + 2
      if (next < X && over < X && board[next][y].type == SpaceType.PEG && board[over][y].type == SpaceType.HOLE)
        moveList += Move(x, y, Direction.EAST)
    }
  }

  fun countPegs(): Int {
    var c = 0
    for (x in 0 until X) {
      for (y in 0 until Y) {
        if (board[x][y].type == SpaceType.PEG)
          c++
      }
    }
    return c
  }

  override fun toString(): String {
    val str = StringBuilder()
    str.append("  ")
    for (i in 0..6)
      str.append(" $i ")
    str.append("\n")
    for (y in 0 until Y) {
      str.append("$y ")
      for (x in 0 until X)
        str.append(board[x][y])
      if (y < Y - 1)
        str.append("\n")
    }
    return str.toString()
  }

  companion object {
    const val X = 7
    const val Y = 7
    const val TOTAL = 32
  }
}