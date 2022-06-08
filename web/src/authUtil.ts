import {DatabaseAuthorizationInput} from "./generated/graphql";
import {getCookie, removeCookie, setCookie} from "typescript-cookie";
import {Dispatch, useState} from "react";

export interface Auth {
  databaseAuthorization?: DatabaseAuthorizationInput
}


export function useAuth(): [Auth, Dispatch<Auth>] {
  const [count, setCount] = useState<number>(0);
  const authorizationCookieString = getCookie("authorizationCookie");
  const authorizationDomain = getCookie("authorizationDomain");
  const result = authorizationCookieString === undefined || authorizationDomain === undefined
    ? undefined
    : {
      cookie: authorizationCookieString, url: authorizationDomain
    };
  return [
    { databaseAuthorization: result },
    auth => {
      setCount(c => c + 1);
      saveDatabaseAuthorization(auth);
    }
  ];
}

function saveDatabaseAuthorization(auth: Auth) {
  const authorization = auth.databaseAuthorization;
  if (authorization === undefined) {
    removeCookie("authorizationCookie");
    removeCookie("authorizationDomain");
  } else {
    setCookie("authorizationCookie", authorization.cookie);
    setCookie("authorizationDomain", authorization.url);
  }
}
