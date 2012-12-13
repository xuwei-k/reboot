package reboot.as.jsoup

import org.jsoup.Jsoup
import org.jsoup.nodes
import com.ning.http.client.Response

object Document extends (Response => nodes.Document) {
  def apply(r: Response) =
    (reboot.as.String andThen Jsoup.parse)(r)
}

object Query {
  import org.jsoup.select.Elements
  def apply(query: String): Response => Elements =
    Document(_).select(query)
} 

object Clean {
  import org.jsoup.safety.Whitelist
  def apply(wl: Whitelist): Response => String =
    { r => Jsoup.clean(reboot.as.String(r), wl) }
}
