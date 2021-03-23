#!/usr/bin/env sh
echo Starting
# -i specifies the key
# 9022 is the open port on the remote machine that can already be used to ssh to
# 9023 is the port on the remote machine to open
# 22 is the open port on the local machine that will be forwarded
# -4 Force iPv4 https://www.linuxquestions.org/questions/linux-networking-3/problem-with-ssh-local-port-forwarding-timeout-transmission-daemon-4175681713/
# -n Makes sure nothing is read from stdin (No password prompts)
# The ServerAlive configuration makes sure the connection stays alive
# "ping localhost -i 20" makes that the command being run in an attempt to keep the connection open
ssh -4 -g -n -i /opt/ssh-forward/id_rsa -o "ServerAliveInterval 15" -o "ServerAliveCountMax 2" -o "GatewayPorts yes" -R 9023:*:22 -p 9022 joshua@your_url "ping localhost -i 20"
# Note, you may want to change localhost to 192.168.X.X if it's not working
# Note, in your sshd_config, you may want to add "UseDNS no" - https://serverfault.com/a/1028903
# We could also look into using autossh instead
echo Failed
