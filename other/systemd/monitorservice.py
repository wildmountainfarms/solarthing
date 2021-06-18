#!/usr/bin/env python3
import sys
from typing import List
import subprocess
import time
from pathlib import Path
import json
from slack_sdk import WebClient
from slack_sdk.errors import SlackApiError
import traceback

"""
sudo python3 -m pip install slack_sdk
"""

class SlackSender:
    def __init__(self, prefix: str, suffix: str):
        self.prefix = prefix
        self.suffix = suffix
        with Path("config.json").open() as file:
            config = json.load(file)

        slack_bot_token = config["slack_bot_token"]  # xoxb-***
        self.slack_channel = config["slack_channel"]
        self.web_client = WebClient(token=slack_bot_token)

    def send(self, message: str):
        message = self.prefix + message + self.suffix
        try:
            self.web_client.chat_postMessage(channel=self.slack_channel, text=message)
        except SlackApiError as e:
            traceback.print_exc()


def is_running(service_name):
    status = subprocess.call(["systemctl", "is-active", "--quiet", service_name])
    return status == 0

def get_start_message(service_name):
    return subprocess.check_output(["systemctl", "show", "--property=ActiveEnterTimestamp", service_name])


def monitor(service_name: str, slack: SlackSender):
    was_running = None
    last_start_message = None
    while True:
        running = is_running(service_name)
        start_message = get_start_message(service_name)
        if was_running is None:
            slack.send(f"Started monitoring {service_name}. It is {'' if running else 'not '}running.")
        elif running != was_running:
            if running:
                slack.send(f"Started {service_name}")
            else:
                slack.send(f"Stopped {service_name}")
        elif last_start_message is not None and running and last_start_message != start_message:
            slack.send(f"Restarted {service_name}")


        was_running = running
        if running:
            last_start_message = start_message
        else:
            last_start_message = None
        time.sleep(0.3)



def main(args: List[str]):
    service_name = args[0]
    prefix = args[1] + " " if len(args) >= 2 else ""
    suffix = " " + args[2] if len(args) >= 3 else ""

    slack = SlackSender(prefix, suffix)
    try:
        monitor(service_name, slack)
    except KeyboardInterrupt:
        slack.send(f"Stopped monitoring {service_name}")



if __name__ == '__main__':
    main(sys.argv[1:])
