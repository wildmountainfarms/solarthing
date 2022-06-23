import commonStyles from './SolarThing.module.css';
import {
  useAddAuthorizedSenderMutation,
  useAuthorizedQuery,
  useDeleteAuthorizedSenderMutation
} from "../generated/graphql";
import {graphQLClient} from "../client";
import Layout from "../layout/Layout";
import styles from './Authorized.module.css';
import {useNavigate} from "react-router-dom";
import {toDatabaseAuthorizationInput, useDatabaseAuth} from "../authUtil";
import React from "react";

interface SenderTableProps {
  children: React.ReactNode;
}

function SenderTable({children}: SenderTableProps) {

  return <>
    <table style={{width: "100%"}}>
      <colgroup>
        <col span={1} className={styles.optionSenderTdWidth}/>
        <col span={1} style={{width: "15em"}}/>
        <col span={1} />
      </colgroup>
      <tr>
        <th className={styles.optionSenderTd}>.</th>
        <th>Sender</th>
        <th>Public Key</th>
      </tr>
      {children}
    </table>
  </>;
}

interface ButtonProps {
  onClick: () => void;
}
function DeleteButton({onClick}: ButtonProps) {
  return <>
    <div className={styles.divButtonContainer}>
      <div className={styles.deleteSenderButtonDiv} onClick={onClick}>
        <p style={{margin: 0}}>X</p>
      </div>
    </div>
  </>;
}
function AddButton({onClick}: ButtonProps) {
  return <>
    <div className={styles.divButtonContainer}>
      <div className={styles.addSenderButtonDiv} onClick={onClick}>
        <p style={{margin: 0}}>+</p>
      </div>
    </div>
  </>;
}


export default function Authorized() {
  const deleteSenderMutation = useDeleteAuthorizedSenderMutation(graphQLClient);
  const addSenderMutation = useAddAuthorizedSenderMutation(graphQLClient);
  const { status, data, error, isLoading, isSuccess, refetch } = useAuthorizedQuery(graphQLClient);
  const navigate = useNavigate();
  const [databaseAuth] = useDatabaseAuth();

  function deleteSender(sender: string) {
    if (databaseAuth === undefined) {
      navigate("/login");
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
  function addSender(sender: string, publicKey: string) {
    if (databaseAuth === undefined) {
      navigate("/login");
    } else {
      if (addSenderMutation.isLoading) {
        alert("Currently adding another...");
        return;
      }
      if (!confirm("Really authorize " + sender + "?")) {
        return;
      }
      addSenderMutation.mutate(
        {sender, publicKey, authorization: toDatabaseAuthorizationInput(databaseAuth)},
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
          <SenderTable>
            { !isSuccess ? <>
              <tr>
                <td className={styles.optionSenderTd}>.</td>
                <td>Loading</td>
                <td>Loading</td>
              </tr>
            </> : data.authorizedSenders.map((sender) => <>
              <tr>
                <td className={styles.optionSenderTd}>
                  <DeleteButton onClick={() => deleteSender(sender.sender)}/>
                </td>
                <td>{sender.sender}</td>
                <td>{sender.data.publicKey}</td>
              </tr>
            </>)}
          </SenderTable>

          <hr/>
          <h3 style={{textAlign: "center"}}>Authorize Requests</h3>
          <SenderTable>
            { !isSuccess ? <>
              <tr>
                <td className={styles.optionSenderTd}>.</td>
                <td>Loading</td>
                <td>Loading</td>
              </tr>
            </> : data.authRequests.map((authRequest) => <>
              <tr>
                <td className={styles.optionSenderTd}>
                  <AddButton onClick={() => addSender(authRequest.data.sender, authRequest.data.publicKey)}/>
                </td>
                <td>{authRequest.data.sender}</td>
                <td>{authRequest.data.publicKey}</td>
              </tr>
            </>)}
          </SenderTable>


        </div>
      </div>
    </Layout>
  </>;
}
