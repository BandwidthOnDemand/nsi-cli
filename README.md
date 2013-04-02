# NSI Command Line Client

The client is currently under development and only the NSI reserve request is implemented.
Also command line options may change at any time.

## Running the client

    git clone git://github.com/BandwidthOnDemand/nsi-cli.git
    cd nsi-cli
    mvn package

Now there should be an executable jar in `target/nsi-cli.jar`.
In `src/main/scripts` is a `nsi-cli` script that will call the jar file.
Example:

    src/main/scripts/nsi-cli --start 2013-04-03T12:00 --end 2013-04-03T13:00 \\
    -s urn:ogf:network:stp:surfnet.nl:19 -t urn:ogf:network:stp:surfnet.nl:22 \\
    --endpoint http://localhost:8082/bod/nsi/v1_sc/provider --token 1f5bb411-71ad-406b-aaaa-5889f7840df2 \\
    -b 100 -p urn:ogf:network:nsa:surfnet.nl --description "NSI CLI reserve request" --reply http://localhost:9999
