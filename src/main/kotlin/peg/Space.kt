package peg

class Space(var type: SpaceType) {

  override fun toString(): String {
    return when(type) {
      SpaceType.PEG -> " · "
      SpaceType.HOLE -> " O "
      SpaceType.BLOCK -> "   "
    }
  }
}