package reboot.as.xml

import reboot._
import scala.xml._

object Elem extends (Res => scala.xml.Elem) {
  def apply(res: Res) =
    XML.withSAXParser(factory.newSAXParser).loadString(res.getResponseBody)

  private lazy val factory = {
    val spf = javax.xml.parsers.SAXParserFactory.newInstance()
    spf.setNamespaceAware(false)
    spf
  }
}
