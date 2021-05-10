
Packets are stored usually once a minute or once every two minutes. This is to keep as much data as
possible while still having good precision when doing calculations on historical data. However most of
the time, much of this data is unnecessary.

It would be good if we could keep at least one packet every 5 to 10 minutes, but increase the number of
retained packets if the previous packet and current packet have an "event" change. This has the advantage
of using regular packets (no event packets that are only applied to new day), so calculations can take
advantage of regular packets. New data with more precise packets will have better precision with these
calculations.

So what is an "event" exactly? It's when something like the mode of a device changes, (especially an FX AC Mode change).
