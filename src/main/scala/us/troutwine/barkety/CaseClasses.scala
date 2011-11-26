package us.troutwine.barkety

import jid.{MucJID, JID}
import org.jivesoftware.smack.Chat
import akka.actor.ActorRef
import java.util.Date
import org.jivesoftware.smack.packet.Presence

sealed abstract class Memo
case class CreateChat(jid:JID) extends Memo
case class InboundMessage(msg:String) extends Memo
case class OutboundMessage(msg:String) extends Memo
case class JoinRoom(room: JID, nickname: Option[String] = None, roomPassword: Option[String] = None) extends Memo
case class RegisterParent(ref:ActorRef) extends Memo

private sealed abstract class InternalMessage
private case class RemoteChatCreated(jid:JID,chat:Chat) extends InternalMessage
private case class ReceivedMessage(msg:String) extends InternalMessage

abstract class MucBaseMessage
case class MucMessage(jid: MucJID, msg: String, time: Date) extends MucBaseMessage
case class MucPresence(jid: MucJID, status: Presence.Type, time: Date) extends MucBaseMessage

case class UserInfoRequest(jid: MucJID)
case class ExtendedUserInfo(mjid: MucJID, jid: Option[JID], affinity: String, role: String)