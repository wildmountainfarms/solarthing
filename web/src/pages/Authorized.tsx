import commonStyles from './SolarThing.module.css';
import {useAuthorizedQuery} from "../generated/graphql";
import {graphQLClient} from "../client";
import Layout from "../layout/Layout";

export default function Authorized() {
  const { status, data, error, isLoading, isSuccess, refetch } = useAuthorizedQuery(graphQLClient);
  return <>
    <Layout>
      <div className={commonStyles.contentDiv}>
        <div>
          <h3>Authorized Senders</h3>
          <table>
            <tr>
              <th style={{width: "15em"}}>Sender</th>
              <th>Public Key</th>
            </tr>
            { !isSuccess ? <>
              <tr>
                <td>Loading</td>
                <td>Loading</td>
              </tr>
            </> : data.authorizedSenders.map((sender) => <>
              <tr>
                <td>{sender.sender}</td>
                <td>{sender.data.publicKey}</td>
              </tr>
            </>)}
          </table>
        </div>
      </div>
    </Layout>
  </>;
}
