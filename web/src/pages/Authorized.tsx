import commonStyles from './SolarThing.module.css';
import {useAuthorizedQuery, useDeleteAuthorizedSenderMutation} from "../generated/graphql";
import {graphQLClient} from "../client";
import Layout from "../layout/Layout";
import styles from './Authorized.module.css';
import {useNavigate} from "react-router-dom";
import {toDatabaseAuthorizationInput, useDatabaseAuth} from "../authUtil";

export default function Authorized() {
  const deleteSenderMutation = useDeleteAuthorizedSenderMutation(graphQLClient);
  const { status, data, error, isLoading, isSuccess, refetch } = useAuthorizedQuery(graphQLClient);
  const navigate = useNavigate();
  const [databaseAuth] = useDatabaseAuth();

  function deleteSender(sender: string) {
    if (databaseAuth === undefined) {
      navigate("/login")
    } else {
      if (deleteSenderMutation.isLoading) {
        alert("Currently in process of deleting...");
        return;
      }
      if (!confirm("Really delete " + sender + "?")) {
        return;
      }
      deleteSenderMutation.mutate(
        {sender, authorization: toDatabaseAuthorizationInput(databaseAuth)},
        {
          onSuccess: () => {
            // noinspection JSIgnoredPromiseFromCall
            refetch();
          }
        }
      );
    }
  }
  return <>
    <Layout>
      <div className={commonStyles.contentDiv}>
        <div>
          <h3 style={{textAlign: "center"}}>Authorized Senders</h3>
          <table style={{width: "100%"}}>
            <colgroup>
              <col span={1} className={styles.emptyDeleteSenderTdWidth}/>
              <col span={1} style={{width: "15em"}}/>
              <col span={1} />
            </colgroup>
            <tr>
              <th className={styles.emptyDeleteSenderTd}>.</th>
              <th>Sender</th>
              <th>Public Key</th>
            </tr>
            { !isSuccess ? <>
              <tr>
                <td className={styles.emptyDeleteSenderTd}>.</td>
                <td>Loading</td>
                <td>Loading</td>
              </tr>
            </> : data.authorizedSenders.map((sender) => <>
              <tr>
                <th className={styles.deleteSenderTd}>
                  <div className={styles.deleteSenderTd} style={{width: "100%", height: "100%", "display": "flex", flexDirection: "column", alignItems: "center", justifyContent: "center", textAlign: "center"}}>
                    <div className={styles.deleteSenderButtonDiv} onClick={() => deleteSender(sender.sender)}>
                      <p style={{margin: 0}}>X</p>
                    </div>
                  </div>
                </th>
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
