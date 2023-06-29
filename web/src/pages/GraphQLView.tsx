import Layout from "../layout/Layout";
import styles from "./SolarThing.module.css";
import {Voyager} from "graphql-voyager";
import {API_URL, graphQLClient} from "../client";
import 'graphql-voyager/dist/voyager.css';

function introspectionProvider(query: string) {
  return fetch(API_URL, {
    method: 'post',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ query: query }),
  }).then((response) => response.json());
}

export default function GraphQLView() {

  /*
  Note Voyager does not have visualization for enums: https://github.com/IvanGoncharov/graphql-voyager/issues/78
   */
  return (
    <Layout>
      <div className={styles.contentDiv}>
        <Voyager
          introspection={introspectionProvider}
        />
      </div>
    </Layout>
  );
}
