import React from "react";
import styles from './Layout.module.css';
import {Link, useLocation, useNavigate} from "react-router-dom";
import {useLoginInfoQuery} from "../generated/graphql";
import {graphQLClient} from "../client";
import {toDatabaseAuthorizationInput, useDatabaseAuth} from "../authUtil";

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

interface LayoutProps {
  children: React.ReactNode
}
/*
If we want to use bootstrap in the future, this is a good tutorial:
https://codeburst.io/how-to-create-a-navigation-bar-and-sidebar-using-react-348243ccd93
 */

export default function Layout({children}: LayoutProps) {
  const [databaseAuth, setDatabaseAuth] = useDatabaseAuth();
  const authorization = databaseAuth === undefined ? undefined : toDatabaseAuthorizationInput(databaseAuth);
  console.log(authorization);
  const { status, data, error, isLoading, isSuccess } = useLoginInfoQuery(graphQLClient, { authorization }, { refetchOnWindowFocus: false });
  const navigate = useNavigate();

  function logout() {
    setDatabaseAuth(undefined);
  }
  function goToLogin() {
    navigate("/login");
  }
  return <>
    <div className={styles.pageWrapper}>
      <div className={styles.page}>
        <div className={styles.sideNav}>
          <PageLink to={"/"} name={"Home"}/>
          <PageLink to={"/legacy"} name={"Legacy"}/>

          <div className={styles.navFooter} onClick={authorization === undefined ? goToLogin : logout}>
            <div className={styles.navFooterContents}>
              { authorization !== undefined ? <>
                Logout
                <br/>
                { data?.username ?? "..."}
              </> : <>
                Login
              </> }
            </div>
          </div>
        </div>
        <div className={styles.main}>
          {children}
        </div>
      </div>
    </div>
  </>
}
