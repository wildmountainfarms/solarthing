import styles from './Legacy.module.css'
import {LegacyQuery, useLegacyQuery} from "../generated/graphql";
import {graphQLClient} from "../client";
import {getTimeMillisRounded} from "../timeUtil";
import {useState} from "react";


export default function Legacy() {
  // TODO don't use default source ID
  const timeMillisRounded = getTimeMillisRounded();
  const {data, error, isLoading } = useLegacyQuery(
    graphQLClient,
    { sourceId: "default", currentTimeMillis: "" + timeMillisRounded},
    { refetchInterval: 10_000, keepPreviousData: true }
  );
  const nestedData = data?.queryStatusLast.flatData[0]?.data;
  return <>
    <div className={styles.page}>
      <h1 className={styles.header}>Wild Mountain Farms Power Information</h1>
      <section style={{width:"100%"}}>
        <hr/>
        <div className={styles.number_heading}>Current Battery Voltage</div>
        <div className={styles.large_info}>
          {nestedData?.batteryVoltage ?? (error ? "ERROR" : isLoading ? "Loading" : "No data")}
          V
        </div>

        <hr/>
        <div className={styles.number_heading}>Solar Panel Status</div>
        <div className={styles.large_info}><span id="panel_watts">ERROR</span> W</div>
        <div className={styles.number_heading}>Solar Charging</div>
        <div className={styles.large_info}><span id="charger">ERROR</span> W</div>

        <hr/>
        <div className={styles.number_heading}>Load</div>
        <div className={styles.large_info}><span id="load">ERROR</span> W</div>

        <hr/>
        <div className={styles.number_heading}>Generator Status</div>
        <div className={styles.large_info}><span id="generator_status">ERROR</span></div>
        <div className={styles.medium_info}>Total: <span id="generator_total_watts">ERROR</span> W</div>
        <div className={styles.medium_info}>Charge: <span id="generator_charge_watts">ERROR</span> W</div>

        <hr/>
        {/*<div id="chart_div"></div>*/}

        <hr/>
        <div className={styles.nerd_info}>
          <h3 className={styles.nerd_header}>Nerd Info</h3>
          <p>
            Devices Info: <span id="packets_info">unknown</span>
            <br/>
            Operating Mode: <span id="operating_mode">unknown</span>
            <br/>
            AC Mode: <span id="ac_mode">unknown</span>
            <br/>
            Aux Mode: <span id="aux_mode">unknown</span>
            <br/>
            Charger Mode: <span id="charger_mode">unknown</span>
            <br/>
            Misc Modes: <span id="misc_mode"></span>
            <br/>
            Warnings: <span id="warnings"></span>
            <br/>
            FX Errors: <span id="errors_fx"></span>
            <br/>
            MXFM Errors: <span id="errors_mx"></span>
            <br/>
            Rover Errors: <span id="errors_rover"></span>
          </p>
        </div>
      </section>
      <h6 className={styles.header}>Programmed by Josh and Dave</h6>
    </div>
  </>
}
