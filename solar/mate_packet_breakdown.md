
FX Packet:
```
\n1,10,10,10,100,100,10,03,000,02,282,129,000,999\r
\n - The start character
  1 - The address of the FX. 0 means it is plugged directly into the Mate.
   ,
    10 - The inverter current
      ,
       10 - The charger current
         ,
          10 - The buy current
            ,
             100 - The input voltage
                ,
                 100 - The output voltage
                    ,
                     10 - The sell current
                       ,
                        03 - The operating mode
                          ,
                           000 - The error mode
                              ,
                               02 - The AC Mode
                                 ,
                                  282 - The battery voltage. 282 -> 28.2
                                     ,
                                      129 - The misc mode
                                         ,
                                          000 - The warning mode
                                             ,
                                              999 - The check sum (incorrect on this particular packet)
\n1,10,10,10,100,100,10,03,000,02,282,129,000,999\r
```

