package reboot.oauth

import com.ning.http.client.oauth._

import reboot._

class SigningVerbs(val subject: Req) extends RequestVerbs {
  val emptyToken = new RequestToken("", "")

  def sign(consumer: ConsumerKey, token: RequestToken = emptyToken) = {
    val calc = new OAuthSignatureCalculator(consumer, token)
    val req = subject.build
    val baseurl = req.getUrl().takeWhile { _ != '?' }.mkString("")
    calc.calculateAndAddSignature(baseurl, req, subject)
    subject
  }

  def <@(consumer: ConsumerKey, token: RequestToken = emptyToken) =
    sign(consumer, token)

}
