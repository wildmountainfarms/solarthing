# A 'u' character means that the program shouldn't check that character.
# usually, in an actual mate packet, that unused character will always be a 0
sleep 3
printf "\n1,10,10,10,100,100,10,00,255,00,262,255,255,999\r"
printf "\n2,10,10,10,100,100,10,00,255,00,262,255,255,999\r"
printf "\nD,uu,10,99,999,888,u7,09,255,04,264,000,000,999\r"
printf "\nF,uu,10,99,999,888,u7,09,255,04,264,000,000,999\r"
sleep 1
printf "\n1,10,10,10,100,100,10,00,255,00,262,255,255,999\r"
printf "\n2,10,10,10,100,100,10,00,255,00,262,255,255,999\r"
printf "\nD,uu,10,99,999,888,u7,09,255,04,264,000,000,999\r"
printf "\nF,uu,10,99,999,888,u7,09,255,04,264,000,000,999\r"

printf "\004"
