package de.htwg.se.uno.controller.controllerComponent.controllerBaseImpl

import com.google.inject.{Guice, Inject, Injector}
import com.google.inject.name.Names
import de.htwg.se.uno.Uno.gui
import de.htwg.se.uno.UnoModule
import de.htwg.se.uno.controller.controllerComponent._
import net.codingwell.scalaguice.InjectorExtensions._
import de.htwg.se.uno.model.gameComponent.GameInterface
import de.htwg.se.uno.util.UndoManager

import scala.swing.{Color, Publisher}


class Controller @Inject() (var game: GameInterface) extends ControllerInterface with Publisher {
  private var undoManager = new UndoManager
  val injector: Injector = Guice.createInjector(new UnoModule)
  private var hs = "Du bist dran. Mögliche Befehle: q, n, t, s [Karte], g, u, r"
  private var hs2 = ""
  private var color = 0

  def createGame(size: Int):Unit = {
    size match {
      case 2 => game = injector.instance[GameInterface](Names.named("2 Players"))
      case 3 => game = injector.instance[GameInterface](Names.named("3 Players"))
      case 4 => game = injector.instance[GameInterface](Names.named("4 Players"))
      case _ =>
    }
    game = game.createGame()
    hs2 = ""
    color = 0
    undoManager = new UndoManager
    gameStatus("yourTurn")
    publish(new GameSizeChanged)
  }

  def createTestGame():Unit = {
    game = injector.instance[GameInterface](Names.named("2 Players"))
    game = game.createTestGame()
    hs2 = ""
    undoManager = new UndoManager
    gameStatus("yourTurn")
    publish(new GameSizeChanged)
  }

  def gameToString: String = game.toString

  def set(string:String, color : Int = 0): Unit = {
    if (string.charAt(0) != 'S' || color != 0) {
      if (game.nextTurn()) {
        val s = gameToString
        game = game.setActivePlayer()
        undoManager.doStep(new PushCommand(string, color, this))
        if (!s.equals(gameToString)) {
          gameStatus("enemyTurn")
          publish(new GameChanged)
          won
        } else {
          game = game.setDirection()
          game = game.setActivePlayer()
          game = game.setDirection()
          gameStatus("pushCardNotAllowed")
          publish(new GameNotChanged)
        }
      } else {
        gameStatus("enemyTurn")
        publish(new GameNotChanged)
      }
    } else {
      hs2 = string
      gameStatus("chooseColor")
      publish(new ChooseColor)
    }
  }

  def get(): Unit = {
    if (game.nextTurn()) {
      val s = gameToString
      game = game.setActivePlayer()
      undoManager.doStep(new PullCommand(this))
      if (!s.equals(gameToString)) {
        gameStatus("enemyTurn")
        publish(new GameChanged)
        won
      } else {
        game = game.setDirection()
        game = game.setActivePlayer()
        game = game.setDirection()
        gameStatus("pullCardNotAllowed")
        publish(new GameNotChanged)
      }
    } else {
      gameStatus("enemyTurn")
      publish(new GameNotChanged)
    }
  }

  def enemy(): Unit = {
    if (game.nextEnemy() == 1) {
      game = game.setActivePlayer()
      undoManager.doStep(new EnemyCommand(this))
      if (game.nextTurn()) {
        gameStatus("yourTurn")
      } else {
        gameStatus("enemyTurn")
      }
    } else if (game.nextEnemy() == 2) {
      game = game.setActivePlayer()
      undoManager.doStep(new EnemyCommand2(this))
      if (game.nextTurn()) {
        gameStatus("yourTurn")
      } else {
        gameStatus("enemyTurn")
      }
    } else {
      game = game.setActivePlayer()
      undoManager.doStep(new EnemyCommand3(this))
      if (game.nextTurn()) {
        gameStatus("yourTurn")
      } else {
        gameStatus("enemyTurn")
      }
    }
    publish(new GameChanged)
    won
  }

  def undo: Unit = {
    do {
      undoManager.undoStep
      game.setDirection()
      game.setActivePlayer()
      game.setDirection()
    }
    while(!game.nextTurn())
    gameStatus("undo")
    publish(new GameChanged)
    won
  }

  def redo: Unit = {
    val a = game.getActivePlayer()
    undoManager.redoStep
    if (a == game.getActivePlayer())
      game = game.setActivePlayer()
    gameStatus("redo")
    publish(new GameChanged)
    won
  }

  def won: Unit = {
    if(game.getLength(4) == 0) {
      gameStatus("won")
      publish(new GameEnded)
    } else if(game.getLength(0) == 0) {
      gameStatus("lost")
      publish(new GameEnded)
    } else if (game.getNumOfPlayers() >= 3 &&game.getLength(1) == 0) {
      gameStatus("lost")
      publish(new GameEnded)
    } else if (game.getNumOfPlayers() >= 4 &&game.getLength(2) == 0) {
      gameStatus("lost")
      publish(new GameEnded)
    } else {
      gameStatus("idle")
    }
  }

  def notPush : Unit = {
    gameStatus("pushCardNotAllowed")
    publish(new GameNotChanged)
  }

  def getCardText(list : Int, index : Int) : String = game.getCardText(list, index)
  def getGuiCardText(list : Int, index : Int) : String = game.getGuiCardText(list, index)
  def getLength(list : Int) : Int = game.getLength(list)
  def getNumOfPlayers() : Int = game.getNumOfPlayers()
  def nextTurn() : Boolean = game.nextTurn()
  def getHs2() : String = hs2
  def getColor() : Color = game.getColor()

  def gameStatus(string : String) : String = {
    string match {
      case "pushCardNotAllowed" => {
        hs = "Du kannst diese Karte nicht legen"
        hs
      }
      case "enemyTurn" => {
        hs = "Gegner ist an der Reihe"
        hs
      }
      case "pullCardNotAllowed" => {
        hs = "Du kannst keine Karte ziehen"
        hs
      }
      case "unknownCommand" => {
        hs = "Befehl nicht bekannt"
        hs
      }
      case "yourTurn" => {
        hs = "Du bist dran. Mögliche Befehle: q, n [2 | 3 | 4], t, s [Karte], g, u, r, d"
        hs
      }
      case "won" => {
        hs = "Glückwunsch du hast gewonnen"
        hs
      }
      case "lost" => {
        hs = "Du hast leider verloren"
        hs
      }
      case "undo" => {
        hs = "Zug rückgängig gemacht"
        hs
      }
      case "redo" => {
        hs = "Zug wiederhergestellt"
        hs
      }
      case "chooseColor" => {
        hs = "Wähle eine Farbe"
        hs
      }
      case "idle" => hs
    }
  }
}
