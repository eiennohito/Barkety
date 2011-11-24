package us.troutwine.barkety

import akka.actor.{Actor,ActorRef}
import jid.MucJID
import org.jivesoftware.smack.PacketListener
import org.jivesoftware.smack.packet.{Message,Packet}
import org.jivesoftware.smackx.muc.{MultiUserChat,DiscussionHistory}
import java.util.Date


class RoomChatter(muc: MultiUserChat, nickname: String, password: Option[String] = None) extends Actor {
  muc.addMessageListener(new PacketListener() {
    def processPacket(packet: Packet) {
      packet match {
        case msg: Message =>
          if (msg.getBody != null)
            self ! MucMessage(MucJID(msg.getFrom), msg.getBody, new Date)
      }
    }
  })
  var parent:Option[ActorRef] = None
  
  case object Join
  
  override def preStart() = self ! Join
  
  override def postStop() = muc.leave() // TODO: If receiving Join blows up, will this call blow up too?
  
  def receive = {
    case Join =>
      val history = new DiscussionHistory()
      history.setMaxChars(0) // Don't get anything when joining
      muc.join(nickname, password.getOrElse(null), history, 5000)
    case RegisterParent(ref) =>
      parent = Some(ref)
    case msg: MucMessage =>
      parent map { _ ! msg }
    case msg: String => 
      muc.sendMessage(msg)
  }
}

