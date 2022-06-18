import {Dispatch} from "react";
import {useCookies} from "react-cookie";

export interface DatabaseAuth {
  cookie: string
  url: string
  expires: Date
}


export function useDatabaseAuth(): [DatabaseAuth | undefined, Dispatch<DatabaseAuth | undefined>] {
  const [currentValue, setCookie, removeCookie] = useCookies<"databaseAuthCookie", DatabaseAuth>(["databaseAuthCookie"])
  return [
    Object.keys(currentValue).length === 0 ? undefined : currentValue,
    databaseAuth => {
      if (databaseAuth === undefined) {
        removeCookie("databaseAuthCookie");
      } else {
        setCookie("databaseAuthCookie", databaseAuth, {expires: databaseAuth.expires});
      }
    }
  ];
}

