package us.troutwine.barkety

import akka.actor.{Actor,ActorRef}
import jid.{JID, MucJID}
import org.jivesoftware.smack.PacketListener
import org.jivesoftware.smackx.muc.{MultiUserChat,DiscussionHistory}
import org.jivesoftware.smack.packet.{Presence, Message, Packet}
import java.util.Date

/**
 * If you register something here, it should process MucBaseMessage messages
 */
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
  
  private val userNS = "http://jabber.org/protocol/muc#user"

  muc.addParticipantListener(new PacketListener {
    def processPacket(packet: Packet) {
      packet match {
        case p: Presence => {
          self ! MucPresence(MucJID(p.getFrom), p.getType, new Date)
        }
      }
    }
  })

  var parent:Option[ActorRef] = None
  
  case object Join
  
  override def preStart() = self ! Join
  
  override def postStop() = muc.leave() // TODO: If receiving Join blows up, will this call blow up too?

  def forward[A](msg : A) = parent map {_ ! msg}

  def findUserInfo(mjid: MucJID): Option[ExtendedUserInfo] = {
    val ui = muc.getOccupant(mjid)
    if (ui == null) {
      None
    } else {
      Some(ExtendedUserInfo(
        mjid,
        if (ui.getJid == null) None else Some(JID(ui.getJid)),
        ui.getAffiliation,
        ui.getRole
      ))
    }
  }


  def receive = {
    case Join =>
      val history = new DiscussionHistory()
      history.setMaxChars(0) // Don't get anything when joining
      muc.join(nickname, password.getOrElse(null), history, 5000)
    case RegisterParent(ref) =>
      parent = Some(ref)
    case msg: MucMessage => forward(msg)
    case msg: MucPresence => forward(msg)
    case msg: String => 
      muc.sendMessage(msg)
    case UserInfoRequest(mjid) =>
      self.tryReply(findUserInfo(mjid))
  }
}

