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
    def __init__(self, prefix: str, suffix: str, slack_bot_token: str, slack_channel: str):
        self.prefix = prefix
        self.suffix = suffix
        self.slack_channel = slack_channel
        self.web_client = WebClient(token=slack_bot_token)

    def send(self, message: str):
        message = self.prefix + message + self.suffix
        try:
            self.web_client.chat_postMessage(channel=self.slack_channel, text=message)
        except SlackApiError as e:
            traceback.print_exc()


class ServiceMonitor:
    def __init__(self, service_name: str, slack: SlackSender):
        self.service_name = service_name
        self.slack = slack
        self.was_running = None
        self.last_start_message = None

    def update(self):
        running = is_running(self.service_name)
        start_message = get_start_message(self.service_name)
        if self.was_running is None:
            self.slack.send(f"Started monitoring {self.service_name}. It is {'' if running else 'not '}running.")
        elif running != self.was_running:
            if running:
                self.slack.send(f"Started {self.service_name}")
            else:
                self.slack.send(f"Stopped {self.service_name}")
        elif self.last_start_message is not None and running and self.last_start_message != start_message:
            self.slack.send(f"Restarted {self.service_name}")


        self.was_running = running
        if running:
            self.last_start_message = start_message
        else:
            self.last_start_message = None



def is_running(service_name):
    status = subprocess.call(["systemctl", "is-active", "--quiet", service_name])
    return status == 0

def get_start_message(service_name):
    return subprocess.check_output(["systemctl", "show", "--property=ActiveEnterTimestamp", service_name])


def monitor(service_names: List[str], slack: SlackSender):
    services: List[ServiceMonitor] = [ServiceMonitor(name, slack) for name in service_names]
    while True:
        for service in services:
            service.update()
        time.sleep(0.3)



def main(args: List[str]):
    with Path("config.json").open() as file:
        config = json.load(file)

    service_names = config["service_names"]
    try:
        prefix = config["prefix"] + " "
    except KeyError:
        prefix = ""
    try:
        suffix = " " + config["suffix"]
    except KeyError:
        suffix = ""

    slack_bot_token = config["slack_bot_token"]  # xoxb-***
    slack_channel = config["slack_channel"]
    slack = SlackSender(prefix, suffix, slack_bot_token, slack_channel)
    try:
        monitor(service_names, slack)
    except KeyboardInterrupt:
        slack.send(f"Stopped monitoring {service_name}")



if __name__ == '__main__':
    main(sys.argv[1:])
