package us.troutwine.barkety

import akka.event.{EventHandler => log}
import org.jivesoftware.smack.Chat
import org.jivesoftware.smack.MessageListener
import org.jivesoftware.smack.packet.Message


private class MsgLogger extends MessageListener {
  override def processMessage(chat:Chat,msg:Message) = {
    log.info(this, "INBOUND %s --> %s : %s".format(chat.getParticipant,
                                                   chat.getThreadID,
                                                   msg.getBody))
  }
}





