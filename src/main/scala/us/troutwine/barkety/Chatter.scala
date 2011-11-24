package us.troutwine.barkety

import akka.actor.{Actor,ActorRef}
import org.jivesoftware.smack.Chat
import org.jivesoftware.smack.Roster


private class Chatter(chat:Chat, roster:Roster) extends Actor {
  chat.addMessageListener(new MsgListener(self))
  var parent:Option[ActorRef] = None

  def receive = {
    case RegisterParent(ref) =>
      parent = Some(ref)
    case OutboundMessage(msg) =>
      if ( roster.contains(chat.getParticipant) )
        chat.sendMessage(msg)
    case msg:String =>
      chat.sendMessage(msg)
    case msg:ReceivedMessage =>
      parent map { _ ! InboundMessage(msg.msg) }
  }
}



