#!/bin/sh
sleep 3
while true
do
    sleep 2
    printf "\n1,07,00,00,000,125,00,03,000,00,282,000,000,999\r"  # This tests to make sure that this packet is removed (it's a 'duplicate')
    printf "\n1,08,00,00,000,125,00,03,000,00,282,000,000,999\r"  # This is the packet we should see
done

