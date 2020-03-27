### Individual documentation
[Solar readme](solar/README.md)

### How packets are stored
Each packet in the database holds other packets that were saved at the same time. This makes
it simple to link FX1 FX2 and MX3 packets to one single packet. By default the program links packets together by saving
packets when it's been 250 ms after the first data received from a packet. 

Example:

* We receive 10% of Packet 1 so we start the 250ms timer
* We receive 90% of Packet 1 and 80% of Packet 2
* We receive 20% of Packet 2 and 99% of Packet 3
* 250ms is up so Packet 1 and 2 are saved together, Packet 3 will be saved next time

Usually packets aren't cut off like this, but sometimes it happens

### Fragmented Packets
Sometimes, an instance needs multiple packets to come from different sub-sources (different computers or different programs).
When this happens, you can set up packets to be stored in the database fragmented. The way this works is simple. One
program is the "master fragment", indicated by the lowest fragment-id. Other programs have higher fragment IDs. Each program
has its own fragment ID, allowing you to distinguish between them in the database.

When you read from the database, you iterate through master packets and find the nearest fragment for all the other
fragment IDs.

Example: <br/>
Fragment 1: FX1, FX2, MX3, MX4 <br/>
Fragment 2: Renogy Rover

### Duplicate Packets
As of 2019.12.20, duplicate packets are no longer something we have to worry about.

In a "mate" type program, it is possible that packets will pile up. When this happens, the `instant` boolean is usually set to false.
Even if this fails, [OutbackDuplicatePacketRemover](../../../core/src/main/java/me/retrodaredevil/solarthing/solar/outback/OutbackDuplicatePacketRemover.java) will
remove duplicate packets.

For old solarthing versions, you can expect that a PacketCollection may have one or two other identical packets from almost the same time. 
This is where [Identifiers](../../../core/src/main/java/me/retrodaredevil/solarthing/packets/identification/Identifier.java) comes in. By
adding packets to a Map, you can make sure that there's only one packet for each unique Identifier

#### Logging
This uses slf4j to log and uses log4j2 as the main implementation. https://logging.apache.org/log4j/2.x/manual/appenders.html
