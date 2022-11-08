import styles from "./SolarThing.module.css";
import Layout from "../layout/Layout";
import {createGraphiQLFetcher} from '@graphiql/toolkit';

import 'graphiql/graphiql.css';
import {GraphiQL} from "graphiql";
import {API_URL} from "../client";

const fetcher = createGraphiQLFetcher({
  url: API_URL
});

export default function GraphQLQuery() {

  // Decent demo of what this is similar to: https://graphql.org/swapi-graphql
  return (
    <Layout>
      <div className={styles.contentDiv}>
        <GraphiQL fetcher={fetcher}/>
      </div>
    </Layout>
  );
}
