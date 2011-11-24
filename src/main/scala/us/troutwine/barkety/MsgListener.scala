package us.troutwine.barkety

import akka.actor.ActorRef
import org.jivesoftware.smack.Chat
import org.jivesoftware.smack.MessageListener
import org.jivesoftware.smack.packet.Message


private class MsgListener(parent:ActorRef) extends MessageListener {
  override def processMessage(chat:Chat,msg:Message) = {
    if (msg.getBody != null)
      parent ! ReceivedMessage(msg.getBody)
  }
}







