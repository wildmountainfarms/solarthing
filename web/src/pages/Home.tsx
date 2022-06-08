import {useHomeQuery} from "../generated/graphql";
import {graphQLClient} from "../client";
import React from "react";

function BatteryVoltage() {
  // TODO don't hardcode default sourceId
  const currentTimeMillis = Date.now();
  // We have to use a rounded up value to make sure that useQuery doesn't keep reloading the data
  const timeMillisRounded = Math.ceil(currentTimeMillis / 10_000) * 10_000;
  const {data, error, isLoading, isSuccess} = useHomeQuery(graphQLClient, { sourceId: "default", currentTimeMillis: "" + timeMillisRounded});
  return <>
    {!isSuccess
      ? <p>Loading Data</p>
      : <p>Battery: {isSuccess && data!.queryStatusLast.batteryVoltageAverage[0].data} V</p>}
  </>
}

export default function Home() {
  return <>
    <div>
      <div className="App">
        <header className="App-header">
          <h1>SolarThing</h1>
          <BatteryVoltage/>

         </header>
       </div>
    </div>
  </>
}
