#!/bin/sh
# A 'u' character means that the program shouldn't check that character.
# usually, in an actual mate packet, that unused character will always be a 0
# By using a 'u' character, we basically add an assertion to our "unit test" that the program will not read it because it is unused.
# If the program, for some reason does try and read the 'u' character, it will crash after it tries to parse a base-10 digit from it.
sleep 4
while true
do
#    printf "\n1,00,13,16,132,133,00,03,00        ,008,000,046\r" this is an example packet that was "garbled"
    printf "\n1,10,10,10,100,100,10,03,000,02,281,129,000,999\r"  # this is a FX Packet
    printf "\n2,10,10,10,100,100,10,03,000,02,282,129,000,999\r"  # this is a FX Packet
    printf "\nD,uu,10,99,999,000,u7,73,000,04,283,000,000,999\r"  # this is a MX Packet  # 73 = 9 | 64
#    printf "\nE,uu,10,99,999,000,u7,73,000,04,282,000,000,999\r"  # this is a MX Packet  # 73 = 9 | 64
    sleep 6
done

