import React from "react";
import styles from './SolarThing.module.css';
import Layout from "../layout/Layout";
import {graphQLClient} from "../client";


export default function Status() {

  // const {data, error, isLoading, isSuccess} = useDatabaseStatus(graphQLClient);
  return (
    <Layout>
      <div className={styles.contentDiv}>
        <h1>CouchDB Status</h1>

      </div>
    </Layout>
  );
}
