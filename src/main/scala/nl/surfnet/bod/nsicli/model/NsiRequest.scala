package nl.surfnet.bod.nsicli.model

trait NsiRequest {

  def envelope: scala.xml.Elem
}