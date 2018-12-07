package gui

import java.awt.Graphics
import javax.swing.JPanel

class Circle(val filled: Boolean): JPanel() {

  override fun paint(g: Graphics) {
    val circleSide = (width * 0.8).toInt()
    val location = (width - circleSide) / 2

    if (filled) {
      g.fillOval(location, location, circleSide, circleSide)
    } else {
      g.drawOval(location, location, circleSide, circleSide)
    }
  }

}