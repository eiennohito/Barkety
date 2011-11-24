import akka.testkit.TestKit
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.mock.MockitoSugar
import org.scalatest.Spec
import us.troutwine.barkety.jid.MucJID

/**
 * @author eiennohito
 * @since 24.11.11 
 */

class MucJIDSpec extends Spec with ShouldMatchers with TestKit with MockitoSugar {
  describe("MucJID") {
    it("should constructs") {
      val m1 = MucJID("test_conf@conference.jabber.org/whoever")
      m1.nickname should be === "whoever"
      m1.server should be === "conference.jabber.org"
      m1.room should be === "test_conf"
    }
  }
}