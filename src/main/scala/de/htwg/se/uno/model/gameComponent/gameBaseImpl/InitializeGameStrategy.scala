package de.htwg.se.uno.model.gameComponent.gameBaseImpl

import de.htwg.se.uno.model.cardComponent.cardBaseImpl.Card

import scala.collection.mutable.ListBuffer

trait InitializeGameStrategy {
  var cardsCovered = new ListBuffer[Card]()
  var cardsRevealed = new ListBuffer[Card]()
  var player = new Player
  var enemy = new Enemy

  def initializeGame(numOfPlayerCards: Int = 7): InitializeGameStrategy
}

object InitializeGameStrategy {
  def apply(kind: Int = 0): InitializeGameStrategy = kind match{
    case 0 => new InitializeRandomGameStrategy
    case 1 => new InitializeTestGameStrategy
  }
}
