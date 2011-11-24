package us.troutwine.barkety

import akka.actor.ActorRef
import akka.event.{EventHandler => log}
import jid.JID
import org.jivesoftware.smack.{ChatManagerListener,Chat}


private class ChatListener(parent:ActorRef) extends ChatManagerListener {
  override def chatCreated(chat:Chat, createdLocally:Boolean) = {
    val jid:JID = JID(chat.getParticipant)
    if (createdLocally)
      log.info(this,"A local chat with %s was created.".format(jid))
    else {
      log.info(this,"%s has begun to chat with us.".format(jid))
      parent ! RemoteChatCreated(jid, chat)
    }
  }
}









