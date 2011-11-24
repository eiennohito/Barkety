package us.troutwine.barkety

import jid.{MucJID, JID}
import org.jivesoftware.smack.Chat
import akka.actor.ActorRef
import java.util.Date

sealed abstract class Memo
case class CreateChat(jid:JID) extends Memo
case class InboundMessage(msg:String) extends Memo
case class OutboundMessage(msg:String) extends Memo
case class JoinRoom(room: JID, nickname: Option[String] = None, roomPassword: Option[String] = None) extends Memo
case class RegisterParent(ref:ActorRef) extends Memo

private sealed abstract class InternalMessage
private case class RemoteChatCreated(jid:JID,chat:Chat) extends InternalMessage
private case class ReceivedMessage(msg:String) extends InternalMessage

case class MucMessage(jid: MucJID, msg: String,  time: Date)