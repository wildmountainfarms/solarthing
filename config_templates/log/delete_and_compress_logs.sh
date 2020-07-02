#!/bin/sh

find /opt/solarthing/logs -type f -daystart -mtime +8 -delete
find /opt/solarthing/logs -type f -daystart -mtime +1 -exec gzip {} \;
