package us.troutwine.barkety.jid

/**
 * @author eiennohito
 * @since 24.11.11 
 */

case class MucJID (room: String, server: String,  nickname: String) {
}

object MucJID {
  def apply(jid: String) = {
    val smt = JID(jid)
    new MucJID(smt.username, smt.domain, smt.resource.get)
  }

  implicit def mjid2String(mjid: MucJID) = "%s@%s/%s".format(mjid.room, mjid.server, mjid.nickname)
}