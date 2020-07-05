package de.htwg.se.uno.model.gameComponent

import de.htwg.se.uno.model.gameComponent.gameBaseImpl.Game

trait GameInterface {
  def toString: String
  def createTestGame() : Game
  def enemy() : Game
  def enemyUndo() : Game
  def pullMove() : Game
  def playerUndo() : Game
  def pushMove(string : String) : Game
  def getLength(list:Integer) : Int
  def getCardText(list : Int, index : Int) : String
  def getGuiCardText(list : Int, index : Int) : String
  def getNumOfPlayers() : Int
  def createGame() : Game
  def enemyUndo2() : Game
  def enemyUndo3() : Game
  def enemy2() : Game
  def enemy3() : Game
  def nextTurn() : Boolean
  def nextEnemy() : Int
  def setActivePlayer() : Game
  def setDirection() : Game
  def getActivePlayer() : Int
  def getDirection() : Boolean
}