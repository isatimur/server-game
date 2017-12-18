package xyz.isatimur.actor

import akka.actor.{Actor, ActorRef}

/**
  * ${CLASS_NAME}.
  * Created at 12/14/2017 2:01 AM by
  *
  * @author Timur Isachenko
  *
  */
trait GameEvent

case class PlayerJoined(player: Player, actor: ActorRef) extends GameEvent

case class PlayerLeft(playerName: String) extends GameEvent

case class PlayerMoveRequest(playerName: String, direction: String) extends GameEvent

case class Player(name: String, position: Position)

case class PlayerWithActor(player: Player, actor: ActorRef)

case class PlayerChanged(players: Iterable[Player]) extends GameEvent

case class Position(x: Int, y: Int) {
  def +(other: Position): Position = {
    Position(x + other.x, y + other.y)
  }

}

class GameAreaActor extends Actor {
  val players = collection.mutable.LinkedHashMap[String, PlayerWithActor]()

  override def receive: Receive = {
    case PlayerJoined(player, actor) => {
      players += (player.name -> PlayerWithActor(player, actor))
      notifyPlayerChange()
    }
    case PlayerLeft(playerName) => {
      players -= playerName
      notifyPlayerChange()
    }
    case PlayerChanged(players: Iterable[Player]) => notifyPlayerChange()
    case PlayerMoveRequest(playerName, direction) => {
      val offset = direction match {
        case "up" => Position(0, 1)
        case "down" => Position(0, -1)
        case "right" => Position(1, 0)
        case "left" => Position(-1, 0)
      }
      val oldPlayerWithActor = players(playerName)
      val oldPlayer = oldPlayerWithActor.player
      val actorRef = oldPlayerWithActor.actor
      players(playerName) = PlayerWithActor(Player(playerName, oldPlayer.position + offset), actorRef)
      notifyPlayerChange()
    }
  }

  def notifyPlayerChange(): Unit = {
    players.values.foreach(_.actor ! PlayerChanged(players.values.map(_.player)))
  }
}
