package scala

import aview.Tui
import controller.Controller

import model.Game
import scala.io.StdIn.readLine

object Uno {
  val controller = new Controller(Game())
  val tui = new Tui(controller)
  controller.notifyObservers

  def main(args: Array[String]): Unit = {
    var input: String = ""

    do {
      println("Mögliche Befehle: q, n, s [Karte], g")
      input = readLine()
      tui.processInputLine(input)
    } while (input != "q")
  }
}
