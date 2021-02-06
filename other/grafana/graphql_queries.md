## Grafana GraphQL Queries

Note in the below queries random `from` and `to` values may be used. In Grafana, replace these with `$__from` and `$__to`.

#### Battery Voltage
```graphql
query {
  data: queryStatus(sourceId: "default", from: "1612418400000", to: "1612504800000") {
    data: batteryVoltage {
      Time: dateMillis
      packet {
        batteryVoltage
        identifier {
          representation
        }
        identityInfo {
          displayName
        }
      }
    }
  }
}
```
* data path: `data.data`
* Group by: `packet.identifier.representation`
* Alias by: `$field_packet.identityInfo.displayName`

In the above example, group by is important if your system has multiple devices that report battery voltage.

#### Daily kWh Sum (all charge controllers combined)
```graphql
query {
  data: queryFullDay(sourceId: "default", from: "1612418400000", to: "1612504800000") {
    data: dailyKWHSum {
      Time: dateMillis
      data
    }  
  }
}
```
* Data path: `data.data`
* Group by: ``
* Alias by: `Daily kWh`

In the above example, group by and alias by are not important. Group by is left blank because each entry is grouped into
the same key. Alias by is used to give a meaningful name to the data, but can really be anything you'd like.

## More Examples
#### Rover specific query
You can get creative with your queries. The roverStatus packet has many options
```graphql
query {
  data: queryStatus(sourceId: "default", from: "1612418400000", to: "1612504800000") {
    roverStatus {
      Time: dateMillis
      packet {
        productModelString
        chargingStateName
        controllerTemperatureCelsius
      }
    }
  }
}
```
