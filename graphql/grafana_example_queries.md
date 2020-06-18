
# Status
TODO

# Annotations (events)

---
Operational Mode Change
```graphql
{
  data:queryEventIdentifier(from:"$__from", to:"$__to", sourceId:"default", fragmentId:1, identifier:"SupplementaryIdentifier(identifier=OutbackIdentifier(address=1), supplementaryType=FX_OPERATIONAL_MODE_CHANGE)") {
    fxOperationalModeChange {
      Time:dateMillis
      packet {
        operationalModeName
        previousOperationalModeName
      }
    }
  }
}
```
data path: `data.fxOperationalModeChange` <br/>
title: `FX 1 $field_packet.operationalModeName` <br/>
text: `(was $field_packet.previousOperationalModeName)` <br/>
tags: `FX 1`

---

Mate Commands
```graphql
{
  data:queryEvent(from:"$__from", to:"$__to", sourceId:"default") {
    mateCommand {
      Time:dateMillis
      packet {
        commandName
      }
    }
  }
}
```
data path: `data.mateCommand` <br/>
title: `Command $field_packet.commandName`
