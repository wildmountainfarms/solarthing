import React from "react";
import styles from './SolarThing.module.css';
import Layout from "../layout/Layout";
import {graphQLClient} from "../client";
import {useDatabaseStatusQuery} from "../generated/graphql";


export default function Status() {

  const {data, error, isLoading, isSuccess} = useDatabaseStatusQuery(graphQLClient);
  return (
    <Layout>
      <div className={styles.contentDiv}>
        <h1>CouchDB Status</h1>
        {
          isLoading
            ? <p>Loading</p>
            : !isSuccess
              ? <p>Error Loading Status</p>
              : <>
                Status DB: {data!.systemStatus?.status ?? "Loading Error"}
                <br/>
                Event DB: {data!.systemStatus?.event ?? "Loading Error"}
                <br/>
                Open DB: {data!.systemStatus?.open ?? "Loading Error"}
                <br/>
                Closed DB: {data!.systemStatus?.closed ?? "Loading Error"}
                <br/>
                Alter DB: {data!.systemStatus?.alter ?? "Loading Error"}
                <br/>
                Cache DB: {data!.systemStatus?.cache ?? "Loading Error"}
                <br/>
              </>
        }

      </div>
    </Layout>
  );
}
