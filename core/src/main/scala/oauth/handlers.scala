package dispatch.oauth

import dispatch._
import com.ning.http.client.oauth._

@deprecated("use reboot.as.oauth.Token") object AsOAuth {
  val token = as.oauth.Token
}
