#!/bin/sh
# This is from a real system
sleep 4
while true
do
    printf "\n1,00,00,02,123,123,00,10,000,02,252,008,000,035\r" # GVFX
    printf "\nb,0004,0003,0000,12,00000,251,100,001,26,35,086\r" # FlexNET DC
    printf "\nD,00,00,00,008,128,00,10,000,00,251,0480,00,060\r" # FM-80
    printf "\nE,00,00,00,009,091,00,10,000,00,252,0342,00,059\r" # FM-60
    sleep 6
done

