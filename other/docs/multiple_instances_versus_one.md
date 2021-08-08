## Multiple SolarThing Instances Running Versus One
If you're reading this, you should be aware that pretty much anything related to storing data in the database from devices
can be done with either the `request` program alone, or with the `mate` program if you need data from a MATE.

A request program can upload data from multiple devices. A setup could look like this:
* Rover 40A
* Rover 40A
* Tracer 30A
* Temperature sensor

That can all be configured to be done in a single program, which is pretty convenient. However, currently when
SolarThing gets data from these devices, it uploads a single packet to the database, and uses a single ID to only keep
one packet every few minutes. The downside of this is that if your Tracer errors out frequently, data for the other
devices will be uploaded, but data for the Tracer will likely go in and out on the current ID being uploaded to, and
it likely won't be on previous IDs.

This makes it look like one of your devices is disconnecting when in reality, it just has an unstable connection.
Unstable connections are not bad, they just make stuff a little more difficult to deal with. This is one reason why
you may want to have a separate instance of SolarThing running for your different devices. If you have one program
for your Tracer, then when it disconnects it won't even upload a packet for it. This makes its data much more stable
as it won't overwrite the packet with no data.

### Better Solution?
There's not really a better solution for this yet. I don't want to upload cached data, because uploaded SolarThing data
is real time, and each packet's timestamp should be as accurate as possible. One thing I might consider doing is allowing
a single program to behave like multiple programs. A single program could upload data as multiple packet collections on different
fragment IDs.

Another possible solution would be to make sure uploaded data doesn't get overwritten with no data from a particular device.
This could be done by switching to a new document ID when a device disconnect is detected. Only downside to this is that
many disconnects could cause many packets to be persisted in the database... Unless, we could hash a packet collection
for the included devices. That hash could determine what document ID is used. At the moment, to all SolarThing clients
this still looks like a device disconnecting, but these clients could be updated to still use a device's slightly older
data from a packet collection where it is not disconnected.
