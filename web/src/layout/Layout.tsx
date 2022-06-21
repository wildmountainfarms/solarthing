import React from "react";
import styles from './Layout.module.css';
import {Link, useLocation, useNavigate} from "react-router-dom";
import {useLoginInfoQuery} from "../generated/graphql";
import {graphQLClient} from "../client";
import {toDatabaseAuthorizationInput, useDatabaseAuth} from "../authUtil";
import {useSourceId} from "../sourceUtil";

interface PageLinkProps {
  to: string;
  name: string
}

function PageLink({to, name}: PageLinkProps) {
  const location = useLocation();
  const isSelected = location.pathname == to;
  return <>
    <Link to={to} style={{textDecoration: "none"}}>
      <div className={isSelected ? styles.navLinkCurrent : styles.navLink}>
        <div className={styles.navLinkContents}>
          {name}
        </div>
      </div>
    </Link>
  </>
}

function LoginButton() {
  const [databaseAuth, setDatabaseAuth] = useDatabaseAuth();
  const authorization = databaseAuth === undefined ? undefined : toDatabaseAuthorizationInput(databaseAuth);
  const { status, data, error, isLoading, isSuccess } = useLoginInfoQuery(graphQLClient, { authorization }, { refetchOnWindowFocus: false });
  const navigate = useNavigate();

  function logout() {
    setDatabaseAuth(undefined);
  }
  function goToLogin() {
    navigate("/login");
  }
  return <>
    <div className={styles.navFooterLogin} onClick={authorization === undefined ? goToLogin : logout}>
      <div className={styles.navFooterLoginContents}>
        { authorization !== undefined ? <>
          Logout
          <br/>
          { data?.username ?? "..."}
        </> : <>
          Login
        </> }
      </div>
    </div>
  </>;
}
function SourceIdSelector() {
  const [sourceId, setSourceId] = useSourceId();
  const inputRef = React.createRef<HTMLInputElement>();
  function updateValue() {
    const newValue = inputRef.current!.value;
    if (newValue !== sourceId) {
      setSourceId(newValue);
    }
  }
  return <>
    <div className={styles.sourceIdSelector}>
      <div>
        <p style={{textAlign: "center"}}>Source ID</p>
        <input ref={inputRef} defaultValue={sourceId} onBlur={updateValue}/>
      </div>
    </div>
  </>;
}

interface LayoutProps {
  children: React.ReactNode
}
/*
If we want to use bootstrap in the future, this is a good tutorial:
https://codeburst.io/how-to-create-a-navigation-bar-and-sidebar-using-react-348243ccd93
 */

export default function Layout({children}: LayoutProps) {
  return <>
    <div className={styles.page}>
      <div className={styles.sideNav}>
        <PageLink to={"/"} name={"Home"}/>
        <PageLink to={"/authorized"} name={"Authorized"}/>
        <PageLink to={"/classic"} name={"Classic"}/>

        <div className={styles.navFooter}>
          <SourceIdSelector/>
          <LoginButton/>
        </div>
      </div>
      <div className={styles.main}>
        {children}
      </div>
    </div>
  </>
}
