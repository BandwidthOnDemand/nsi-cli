package nl.surfnet.bod.nsicli.model

import nl.surfnet.bod.nsicli.Config
import java.util.UUID

case class Reserve(config: Config) extends NsiRequest {

  def envelope =
    <soapenv:Envelope
      xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
      xmlns:type="http://schemas.ogf.org/nsi/2011/10/connection/types"
      xmlns:int="http://schemas.ogf.org/nsi/2011/10/connection/interface">
      <soapenv:Header/>
      <soapenv:Body>
        <int:reserveRequest>
          <int:correlationId>{ UUID.randomUUID }</int:correlationId>
          <int:replyTo>{ config.reply }</int:replyTo>
          <type:reserve>
            <requesterNSA>{ config.requester }</requesterNSA>
            <providerNSA>{ config.provider }</providerNSA>
            <reservation>
              <globalReservationId/>
              <description>{ config.description.get }</description>
              <connectionId>{ UUID.randomUUID }</connectionId>
              <serviceParameters>
                <schedule>
                  <startTime>{ config.start.get }</startTime>
                  <endTime>{ config.end.get }</endTime>
                </schedule>
                <bandwidth>
                  <desired>{ config.bandwidth.get }</desired>
                </bandwidth>
              </serviceParameters>
              <path>
                <directionality>Bidirectional</directionality>
                <sourceSTP>
                  <stpId>{ config.source.get }</stpId>
                </sourceSTP>
                <destSTP>
                  <stpId>{ config.target.get }</stpId>
                </destSTP>
              </path>
            </reservation>
          </type:reserve>
        </int:reserveRequest>
      </soapenv:Body>
    </soapenv:Envelope>

}