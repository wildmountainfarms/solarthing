import React from "react";
import styles from './SolarThing.module.css';
import Layout from "../layout/Layout";


export default function Status() {
  return (
    <Layout>
      <div className={styles.contentDiv}>
        <h1>CouchDB Status</h1>

      </div>
    </Layout>
  );
}
