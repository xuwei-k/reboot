package reboot.as.lift

import net.liftweb.json.{ JsonParser, JValue }
import com.ning.http.client.Response

object Json extends (Response => JValue) {
  def apply(r: Response) =
    (reboot.as.String andThen JsonParser.parse)(r)
}
