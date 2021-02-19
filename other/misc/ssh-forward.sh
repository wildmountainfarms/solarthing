#!/usr/bin/env sh
echo Starting
# -N makes it so it doesn't take input (doesn't run bash)
# -i specifies the key
# 9022 is the open port on the remote machine that can already be used to ssh to
# 9023 is the port on the remote machine to open
# 22 is the open port on the local machine that will be forwarded
ssh -N -i /opt/ssh-forward/id_rsa -p 9022 -R 9023:localhost:22 joshua@your_url
echo Failed
