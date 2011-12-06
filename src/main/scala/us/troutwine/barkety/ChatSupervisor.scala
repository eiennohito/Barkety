package us.troutwine.barkety

import akka.actor.Actor
import akka.config.Supervision.AllForOneStrategy
import jid.JID
import scala.collection.mutable
import org.jivesoftware.smack.{XMPPConnection,Chat}
import org.jivesoftware.smack.{Roster,ConnectionConfiguration}
import org.jivesoftware.smack.packet.Presence
import org.jivesoftware.smackx.muc.MultiUserChat


class ChatSupervisor(jid:JID, password:String,
                     domain:Option[String] = None,
                     port:Option[Int] = None) extends Actor
{
  self.faultHandler = AllForOneStrategy(List(classOf[Throwable]), 5, 5000)
  self.id = "chatsupervisor:%s".format(jid)

  private val conf = new ConnectionConfiguration(domain.getOrElse(jid.domain),
                          port.getOrElse(5222), jid.domain)
  private val conn = new XMPPConnection(conf)
  conn.connect()
  domain match {
    case Some("talk.google.com") => conn.login(jid, password, "eien-bot")
    case _ => conn.login(jid.username, password, "eien-bot")
  }
  private val roster:Roster = conn.getRoster()
  roster.setSubscriptionMode(Roster.SubscriptionMode.accept_all)
  conn.sendPacket( new Presence(Presence.Type.available) )

  private val chats:mutable.Map[JID,Chat] = new mutable.HashMap
  private val msglog:MsgLogger = new MsgLogger

  def receive = {
    case CreateChat(partnerJID) => {
      val chat = conn.getChatManager().createChat(partnerJID, msglog)
      if ( !roster.contains(partnerJID) )
        roster.createEntry(partnerJID, partnerJID, null)
      val chatter = Actor.actorOf(new Chatter(chat, roster)).start
      self.link(chatter)
      self.tryReply(chatter)
    }
    case RemoteChatCreated(partnerJID,chat) =>
      chats.put(partnerJID,chat)
    case JoinRoom(roomId, nickname, roomPwd) =>
      val roomChatter = Actor.actorOf(new RoomChatter(new MultiUserChat(conn, roomId), nickname.getOrElse(jid.username), roomPwd)).start()
      self.link(roomChatter)
      self.tryReply(roomChatter)
  }

  override def postStop = {
    conn.disconnect()
  }

  override def preRestart(reason: Throwable, message: Option[Any]) {
    wait(100)
  }
}