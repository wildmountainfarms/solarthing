import React from 'react';
import './App.css';
import {graphQLClient} from "./client";
import {useHomeQuery} from "./generated/graphql";

function App() {
  // TODO don't hardcode default sourceId
  const currentTimeMillis = Date.now();
  // We have to use a rounded up value to make sure that useQuery doesn't keep reloading the data
  const timeMillisRounded = Math.ceil(currentTimeMillis / 10_000) * 10_000;
  const {data, error, isLoading, isSuccess} = useHomeQuery(graphQLClient, { sourceId: "default", currentTimeMillis: "" + timeMillisRounded});
  return (
    <div className="App">
      <header className="App-header">
        <h1>SolarThing</h1>
        {!isSuccess
          ? <p>Loading Data</p>
          : <p>Battery: {isSuccess && data!.queryStatusLast!.batteryVoltageAverage[0].data} V</p>}

      </header>
    </div>
  );
}

export default App;
