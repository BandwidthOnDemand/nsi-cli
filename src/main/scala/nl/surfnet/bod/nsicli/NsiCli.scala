package nl.surfnet.bod.nsicli

import scala.xml.PrettyPrinter
import scala.concurrent.Await
import scala.concurrent.duration._
import scopt.immutable.OptionParser
import org.joda.time.LocalDateTime
import org.joda.time.format.DateTimeFormat
import dispatch._
import Defaults._
import model._

object NsiCli {

  val parser = new OptionParser[Config]("nsi-cli", "0.1") {
    lazy val dateTimeParser = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm")

    def options = Seq(
      opt("o", "operation", "the NSI operation") { (o, c) => c.copy(operation = o) },
      opt("s", "source", "the source STP id") { (s, c) => c.copy(source = Some(s)) },
      opt("t", "target", "the target STP id") { (t, c) => c.copy(target = Some(t)) },
      opt("start", "reservation start time") { (start, c) => c.copy(start = Some(dateTimeParser.parseLocalDateTime(start))) },
      opt("end", "reservation end time") { (end, c) => c.copy(end = Some(dateTimeParser.parseLocalDateTime(end))) },
      intOpt("b", "bandwidth", "bandwidth of reservation") { (b, c) => c.copy(bandwidth = Some(b)) },
      opt("e", "endpoint", "URL of the NSI provider") { (url, c) => c.copy(endPoint = url) },
      opt("r", "reply", "NSI reply to address") { (r, c) => c.copy(reply = r) },
      opt("description", "Description of reservation") { (d, c) => c.copy(description = Some(d)) },
      opt("p", "provider", "NSA provider") { (p, c) => c.copy(provider = p) },
      opt("token", "OAuth2 token") { (t, c) => c.copy(oAuthToken = Some(t)) },
      opt("u", "user", "basic authentication username") { (u, c) => c.copy(username = Some(u)) },
      opt("password", "basic authentication password") { (p, c) => c.copy(password = Some(p)) },
      opt("c", "connection", "connection id") { (con, c) => c.copy(connection = Some(con)) },
      flag("v", "verbose", "makes the nsi-cli verbose") { c => c.copy(verbose = true) }
    )
  }

  def main(args: Array[String]) {
    parser.parse(args, Config()) map { config =>
      if (config.valid) {
        sendRequest(config)
      } else {
        parser.showUsage
      }
    }
  }

  private def sendRequest(config: Config) {
    val nsiRequest = config.operation match {
      case "reserve" => Reserve(config)
      case "provision" => Provision(config)
      case "terminate" => Terminate(config)
      case "release" => Release(config)
      case "query" => Query(config)
    }

    val result = Await.result(send(nsiRequest, config), 10 seconds)
    Console.print(new PrettyPrinter(90, 2).format(result))
  }

  private def send(nsiRequest: NsiRequest, config: Config) = {
    val request = url(config.endPoint).POST << nsiRequest.envelope.toString
    config.oAuthToken.foreach(t => request.addHeader("Authorization", s"bearer $t"))
    Http(request OK as.xml.Elem)
  }
}

case class Config(
    operation: String = "reserve",
    source: Option[String] = None, target: Option[String] = None,
    start: Option[LocalDateTime] = None, end: Option[LocalDateTime] = None,
    bandwidth: Option[Long] = None, description: Option[String] = None,
    endPoint: String = "", provider: String = "", requester: String = "urn:ogf:network:nsa:surfnet.nl",
    reply: String = "", connection: Option[String] = None,
    oAuthToken: Option[String] = None, username: Option[String] = None, password: Option[String] = None,
    verbose: Boolean = false) {

  def valid: Boolean = {

    val operationValid = operation match {
      case "reserve" =>
        source.isDefined && target.isDefined && start.isDefined && end.isDefined && bandwidth.isDefined && description.isDefined
      case "provision" => connection.isDefined
      case "terminate" => connection.isDefined
      case "release" => connection.isDefined
      case "query" => true
      case _ => false
    }
    val basicAuthValid = if (username.isDefined) password.isDefined else !password.isDefined
    val genericValid = endPoint.nonBlank && provider.nonBlank && reply.nonBlank

    operationValid && basicAuthValid && genericValid
  }

  implicit class RicherString(s: String) {
    def isBlank = s == null || s.trim.isEmpty
    def nonBlank = !isBlank
  }
}