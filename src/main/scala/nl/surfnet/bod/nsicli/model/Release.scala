package nl.surfnet.bod.nsicli.model

import nl.surfnet.bod.nsicli.Config

case class Release(config: Config) extends NsiRequest {

  def envelope = ???

}