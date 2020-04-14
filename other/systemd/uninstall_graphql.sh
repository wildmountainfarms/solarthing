#!/usr/bin/env sh

systemctl stop solarthing-graphql.service
rm /etc/systemd/system/solarthing-graphql.service
