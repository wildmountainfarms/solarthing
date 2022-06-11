import styles from './Legacy.module.css'
import {AcMode, useLegacyQuery} from "../generated/graphql";
import {graphQLClient} from "../client";
import {getTimeMillisRounded} from "../timeUtil";


export default function Legacy() {
  // TODO don't use default source ID
  const timeMillisRounded = getTimeMillisRounded();
  const {data, error, isLoading, isLoadingError } = useLegacyQuery(
    graphQLClient,
    { sourceId: "default", currentTimeMillis: "" + timeMillisRounded},
    { refetchInterval: 10_000, keepPreviousData: true }
  );
  const actualData = isLoadingError ? undefined : data;
  const nestedData = actualData?.queryStatusLast.flatData[0]?.data;
  const fallbackMessage = error ? "ERROR" : isLoading ? "Loading" : "No data";
  let generatorStatus: string | undefined;
  switch (nestedData?.fx?.acMode) {
    case AcMode.AcUse:
      generatorStatus = "Using generator power";
      break;
    case AcMode.NoAc:
      generatorStatus = "Generator off";
      break;
    case AcMode.AcDrop:
      generatorStatus = "Generator on, not using";
      break;
    default:
      generatorStatus = undefined;
      break;
  }
  return <>
    <div className={styles.page}>
      <h1 className={styles.header}>Wild Mountain Farms Power Information</h1>
      <section style={{width:"100%"}}>
        <hr/>
        <div className={styles.number_heading}>Current Battery Voltage</div>
        <div className={styles.large_info}>{nestedData?.batteryVoltage ?? fallbackMessage} V</div>

        <hr/>
        <div className={styles.number_heading}>Solar Panel Status</div>
        <div className={styles.large_info}>{nestedData?.chargeController?.pvWattage?.toFixed(1)} W</div>
        <div className={styles.number_heading}>Solar Charging</div>
        <div className={styles.large_info}>{nestedData?.chargeController?.chargerWattage?.toFixed(1)} W</div>

        <hr/>
        <div className={styles.number_heading}>Load</div>
        <div className={styles.large_info}>{nestedData?.fx?.loadWattage} W</div>

        <hr/>
        {generatorStatus && <>
          <div className={styles.number_heading}>Generator Status</div>
          <div className={styles.large_info}>{generatorStatus}</div>
          <div className={styles.medium_info}>Total: {nestedData!.fx!.acBuyWattage} W</div>
          <div className={styles.medium_info}>Charge: {nestedData!.fx!.acChargeWattage} W</div>
          <hr/>
        </>}

        {/*<div id="chart_div"></div>*/}
        {/*<hr/>*/}

        <div className={styles.nerd_info}>
          <h3 className={styles.nerd_header}>Nerd Info</h3>
          <p>
            Devices Info: {nestedData?.deviceInfoString ?? fallbackMessage}
            <br/>
            Operating Mode: {nestedData?.operatingModeString ?? fallbackMessage}
            <br/>
            Misc Modes: {nestedData?.fx?.miscModesString ?? fallbackMessage}
            <br/>
            Warnings: {nestedData?.fx?.warningsString ?? fallbackMessage}
            <br/>
            Errors: <span id="errors">{nestedData?.errorsString ?? fallbackMessage}</span>
          </p>
        </div>
      </section>
      <h6 className={styles.header}>Programmed by Josh and Dave</h6>
    </div>
  </>
}
