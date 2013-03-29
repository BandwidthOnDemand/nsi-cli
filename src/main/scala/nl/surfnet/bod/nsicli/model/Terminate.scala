package nl.surfnet.bod.nsicli.model

import nl.surfnet.bod.nsicli.Config

case class Terminate(config: Config) extends NsiRequest {

  def envelope = ???
}