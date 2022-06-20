import {Dispatch} from "react";
import {useCookies} from "react-cookie";


export function useSourceId(): [string, Dispatch<string>] {
  const [cookies, setCookie, removeCookie] = useCookies(["sourceId"]);

  return [
    cookies.sourceId ?? "default",
    sourceId => {
      setCookie("sourceId", sourceId);
    }
  ];
}
