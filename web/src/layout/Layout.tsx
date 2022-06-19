import React from "react";
import styles from './Layout.module.css';
import {Link} from "react-router-dom";
import { useLocation } from "react-router-dom"

interface PageLinkProps {
  to: string;
  name: string
}

function PageLink({to, name}: PageLinkProps) {
  const location = useLocation();
  console.log(location.pathname);
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
  return <>
    <div className={styles.page}>
      <div className={styles.sideNav}>
        <PageLink to={"/"} name={"Home"}/>
        <PageLink to={"/legacy"} name={"Legacy"}/>
      </div>
      <div className={styles.main}>
        {children}
      </div>
    </div>
  </>
}
