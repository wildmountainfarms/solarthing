import {Dispatch} from "react";
import {useCookies} from "react-cookie";
import {DatabaseAuthorizationInput} from "./generated/graphql";

export interface DatabaseAuth {
  cookie: string;
  url: string;
  expires: Date;

}

export function toDatabaseAuthorizationInput(databaseAuth: DatabaseAuth): DatabaseAuthorizationInput {
  return { cookie: databaseAuth.cookie, url: databaseAuth.url };
}

interface StoredCookies {
  databaseAuthCookie: DatabaseAuth
}

export function useDatabaseAuth(): [DatabaseAuth | undefined, Dispatch<DatabaseAuth | undefined>] {
  const [currentValue, setCookie, removeCookie] = useCookies<"databaseAuthCookie", StoredCookies>(["databaseAuthCookie"]);
  return [
    currentValue.databaseAuthCookie,
    databaseAuth => {
      if (databaseAuth === undefined) {
        removeCookie("databaseAuthCookie");
      } else {
        setCookie("databaseAuthCookie", databaseAuth, {expires: databaseAuth.expires});
      }
    }
  ];
}

