package reboot.as.lift.stream

import reboot.stream.StringsByLine
import net.liftweb.json.{ JsonParser, JValue }

object Json {
  def apply[T](f: JValue => T) =
    new StringsByLine[Unit] {
      def onStringBy(string: String) {
        f(JsonParser.parse(string))
      }
      def onCompleted = ()
    }
}
