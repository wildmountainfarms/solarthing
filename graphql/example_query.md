
```graphql
{
  data:queryAll(from:"100", to:"5000", sourceId:"default") {
    fxStatus {
      packet {
        inverterCurrentRaw
        outputVoltageRaw
        inverterWattage
        warningModes
        address
        miscModes
        batteryVoltage
        acMode
        acModeName
        packetType
      }
    }
  }
}
```
