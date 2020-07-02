package de.htwg.se.uno.controller.controllerComponent

import de.htwg.se.uno.model.Card

import scala.swing.Publisher

trait ControllerInterface extends Publisher {

  def createGame(size: Int = 7):Unit
  def createTestGame():Unit
  def gameToString: String
  def set(string:String): Unit
  def get(): Unit
  def enemy(): Unit
  def undo: Unit
  def redo: Unit
  def won: Unit
  def getIndex(string: String): Int
  def notPush : Unit
  def getCard(list : Int, index : Int) : Card
  def getCardText(list : Int, index : Int) : String
  def getGuiCardText(list : Int, index : Int) : String
  def getLength(list : Int) : Int

}


import scala.swing.event.Event

class GameChanged extends Event
class GameNotChanged extends Event
class GameSizeChanged extends Event
class GameEnded extends Event