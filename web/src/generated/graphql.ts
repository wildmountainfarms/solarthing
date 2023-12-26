import { GraphQLClient } from 'graphql-request';
import { RequestInit } from 'graphql-request/dist/types.dom';
import { useQuery, useMutation, UseQueryOptions, UseMutationOptions } from 'react-query';
export type Maybe<T> = T | null;
export type InputMaybe<T> = Maybe<T>;
export type Exact<T extends { [key: string]: unknown }> = { [K in keyof T]: T[K] };
export type MakeOptional<T, K extends keyof T> = Omit<T, K> & { [SubKey in K]?: Maybe<T[SubKey]> };
export type MakeMaybe<T, K extends keyof T> = Omit<T, K> & { [SubKey in K]: Maybe<T[SubKey]> };
export type MakeEmpty<T extends { [key: string]: unknown }, K extends keyof T> = { [_ in K]?: never };
export type Incremental<T> = T | { [P in keyof T]?: P extends ' $fragmentName' | '__typename' ? T[P] : never };

function fetcher<TData, TVariables>(client: GraphQLClient, query: string, variables?: TVariables, headers?: RequestInit['headers']) {
  return async (): Promise<TData> => client.request<TData, TVariables>(query, variables, headers);
}
/** All built-in and custom scalars, mapped to their actual values */
export type Scalars = {
  ID: { input: string; output: string; }
  String: { input: string; output: string; }
  Boolean: { input: boolean; output: boolean; }
  Int: { input: number; output: number; }
  Float: { input: number; output: number; }
  /** Base64-encoded binary */
  Base64String: { input: any; output: any; }
  /** An arbitrary precision signed decimal */
  BigDecimal: { input: any; output: any; }
  /** Built-in scalar representing an amount of time */
  Duration: { input: any; output: any; }
  /** Built-in scalar representing an instant in time */
  Instant: { input: any; output: any; }
  /** Built-in scalar representing a local date-time */
  LocalDateTime: { input: any; output: any; }
  /** Built-in scalar representing a local time */
  LocalTime: { input: any; output: any; }
  /** A 64-bit signed integer */
  Long: { input: any; output: any; }
  /** Built-in scalar for dynamic values */
  ObjectScalar: { input: any; output: any; }
  /** A 16-bit signed integer */
  Short: { input: any; output: any; }
};

export enum ACMode {
  AC_DROP = 'AC_DROP',
  AC_USE = 'AC_USE',
  NO_AC = 'NO_AC'
}

export type ActivePeriod = {
  __typename?: 'ActivePeriod';
  active: Scalars['Boolean']['output'];
  activeNow: Scalars['Boolean']['output'];
  packetType: ActivePeriodType;
  uniqueString: Scalars['String']['output'];
};


export type ActivePeriodactiveArgs = {
  dateMillis: Scalars['Long']['input'];
};

export enum ActivePeriodType {
  TIME_RANGE = 'TIME_RANGE'
}

export enum AlterPacketType {
  FLAG = 'FLAG',
  FLAG_ALIAS = 'FLAG_ALIAS',
  SCHEDULED_COMMAND = 'SCHEDULED_COMMAND'
}

export type AuthNewSenderPacket = {
  __typename?: 'AuthNewSenderPacket';
  packetType: SecurityPacketType;
  publicKey: Scalars['String']['output'];
  sender: Scalars['String']['output'];
};

export type AuthorizedSender = {
  __typename?: 'AuthorizedSender';
  data: PermissionObject;
  sender: Scalars['String']['output'];
};

export enum AuxMode {
  DISABLED = 'DISABLED',
  DIVERSION = 'DIVERSION',
  ERROR_OUTPUT = 'ERROR_OUTPUT',
  FLOAT = 'FLOAT',
  LOW_BATTERY = 'LOW_BATTERY',
  MANUAL = 'MANUAL',
  NIGHT_LIGHT = 'NIGHT_LIGHT',
  PV_TRIGGER = 'PV_TRIGGER',
  PWM_DIVERSION = 'PWM_DIVERSION',
  REMOTE = 'REMOTE',
  VENT_FAN = 'VENT_FAN'
}

/** Represents a charge controller that charges the battery and has input from PV */
export type BasicChargeController = {
  __typename?: 'BasicChargeController';
  /** The battery voltage */
  batteryVoltage: Scalars['Float']['output'];
  chargingCurrent: Scalars['BigDecimal']['output'];
  chargingMode: SolarMode;
  chargingPower: Scalars['BigDecimal']['output'];
  identifier: Identifier;
  identityInfo: IdentityInfo;
  pvCurrent: Scalars['BigDecimal']['output'];
  pvVoltage: Scalars['BigDecimal']['output'];
  pvWattage: Scalars['BigDecimal']['output'];
  solarMode: SolarMode;
  solarModeName: Scalars['String']['output'];
  solarModeType: SolarModeType;
  solarModeTypeDisplayName: Scalars['String']['output'];
};

export enum BatteryDetection {
  AUTO = 'AUTO',
  V12 = 'V12',
  V24 = 'V24'
}

export enum BatteryManagementMode {
  SOC = 'SOC',
  VOLTAGE_COMPENSATION = 'VOLTAGE_COMPENSATION'
}

export enum BatteryType {
  LEAD_ACID = 'LEAD_ACID',
  LITHIUM = 'LITHIUM'
}

export type BatteryVoltage = {
  __typename?: 'BatteryVoltage';
  /** The battery voltage */
  batteryVoltage: Scalars['Float']['output'];
  identifier: Identifier;
  identityInfo: IdentityInfo;
};

export type ChargeControllerAccumulationDataCache = {
  __typename?: 'ChargeControllerAccumulationDataCache';
  firstDateMillis?: Maybe<Scalars['Long']['output']>;
  generationKWH: Scalars['Float']['output'];
  identifier: Identifier;
  lastDateMillis?: Maybe<Scalars['Long']['output']>;
  unknownGenerationKWH: Scalars['Float']['output'];
  unknownStartDateMillis?: Maybe<Scalars['Long']['output']>;
};

export enum ChargerMode {
  ABSORB = 'ABSORB',
  BULK = 'BULK',
  EQ = 'EQ',
  FLOAT = 'FLOAT',
  SILENT = 'SILENT'
}

export enum ChargingEquipmentError {
  ANTI_REVERSE_MOSFET_SHORT = 'ANTI_REVERSE_MOSFET_SHORT',
  CHARGING_MOSFET_SHORT = 'CHARGING_MOSFET_SHORT',
  CHARGING_OR_ANTI_REVERSE_MOSFET_SHORT = 'CHARGING_OR_ANTI_REVERSE_MOSFET_SHORT',
  FAULT = 'FAULT',
  INPUT_OVER_CURRENT = 'INPUT_OVER_CURRENT',
  LOAD_MOSFET_SHORT = 'LOAD_MOSFET_SHORT',
  LOAD_OVER_CURRENT = 'LOAD_OVER_CURRENT',
  LOAD_SHORT = 'LOAD_SHORT',
  PV_INPUT_SHORT = 'PV_INPUT_SHORT'
}

export enum ChargingMethod_E02D {
  DIRECT = 'DIRECT',
  PWM = 'PWM'
}

export enum ChargingMethod_E021 {
  DIRECT = 'DIRECT',
  PWM = 'PWM'
}

export enum ChargingState {
  ACTIVATED = 'ACTIVATED',
  BOOST = 'BOOST',
  CURRENT_LIMITING = 'CURRENT_LIMITING',
  DEACTIVATED = 'DEACTIVATED',
  DIRECT_CHARGE = 'DIRECT_CHARGE',
  EQ = 'EQ',
  FLOAT = 'FLOAT',
  MPPT = 'MPPT'
}

export enum ChargingStatus {
  BOOST = 'BOOST',
  EQUALIZATION = 'EQUALIZATION',
  FLOAT = 'FLOAT',
  NO_CHARGING = 'NO_CHARGING'
}

export type Core = {
  __typename?: 'Core';
  number: Scalars['Int']['output'];
  temperatureCelsius: Scalars['Float']['output'];
  temperatureFahrenheit: Scalars['Float']['output'];
};

export type CpuTemperaturePacket = {
  __typename?: 'CpuTemperaturePacket';
  cores: Array<Maybe<Core>>;
  cpuTemperatureCelsius: Scalars['Float']['output'];
  cpuTemperatureFahrenheit: Scalars['Float']['output'];
  identifier: Identifier;
  identityInfo: IdentityInfo;
  packetType: DevicePacketType;
  packetVersion?: Maybe<Scalars['Int']['output']>;
};

export type DailyChargeController = {
  __typename?: 'DailyChargeController';
  dailyAH: Scalars['Int']['output'];
  dailyAHSupport: Support;
  dailyKWH: Scalars['Float']['output'];
  identifier: Identifier;
  identityInfo: IdentityInfo;
  startDateMillis?: Maybe<Scalars['Long']['output']>;
};

export type DailyEnergy = {
  __typename?: 'DailyEnergy';
  dailyKWH: Scalars['Float']['output'];
  dayStart: Scalars['Long']['output'];
};

export type DailyFXPacket = {
  __typename?: 'DailyFXPacket';
  acModeValues: Array<Scalars['Int']['output']>;
  acModes: Array<ACMode>;
  /** The address of the port that this device is plugged in to. If 0, this is plugged in to the mate */
  address: Scalars['Int']['output'];
  buyKWH: Scalars['Float']['output'];
  chargerKWH: Scalars['Float']['output'];
  /** The maximum battery voltage for the day. Note this may reset at a different time compared to min battery voltage for the day. */
  dailyMaxBatteryVoltage: Scalars['Float']['output'];
  /** The minimum battery voltage for the day. Note this may reset at a different time compared to max battery voltage for the day. */
  dailyMinBatteryVoltage: Scalars['Float']['output'];
  /** @deprecated Field no longer supported */
  errorModeValue: Scalars['Int']['output'];
  errorModes: Array<FXErrorMode>;
  hasError: Scalars['Boolean']['output'];
  identifier: KnownSupplementaryIdentifier_OutbackIdentifier;
  identityInfo: IdentityInfo;
  inverterKWH: Scalars['Float']['output'];
  is230V: Scalars['Boolean']['output'];
  isAuxOn: Scalars['Boolean']['output'];
  miscModes: Array<MiscMode>;
  miscValue: Scalars['Int']['output'];
  operationalModeValues: Array<Scalars['Int']['output']>;
  operationalModes: Array<OperationalMode>;
  packetType: SolarExtraPacketType;
  sellKWH: Scalars['Float']['output'];
  startDateMillis?: Maybe<Scalars['Long']['output']>;
  warningModeValue: Scalars['Int']['output'];
  warningModes: Array<WarningMode>;
};

export type DataIdentifier = {
  __typename?: 'DataIdentifier';
  dataId: Scalars['Int']['output'];
  /** A string representation of this identifier */
  representation: Scalars['String']['output'];
};

export type DataMetaPacket = {
  __typename?: 'DataMetaPacket';
  dataId: Scalars['Int']['output'];
  dataIdString: Scalars['String']['output'];
  description: Scalars['String']['output'];
  identifier: DataIdentifier;
  identityInfo: IdentityInfo;
  location: Scalars['String']['output'];
  name: Scalars['String']['output'];
  packetType: TargetedMetaPacketType;
};

export type DataNode_Double = {
  __typename?: 'DataNode_Double';
  data: Scalars['Float']['output'];
  dateMillis: Scalars['Long']['output'];
  fragmentId: Scalars['Int']['output'];
  fragmentIdString?: Maybe<Scalars['String']['output']>;
  identifiable: Identifiable;
  sourceId: Scalars['String']['output'];
};

export type DataNode_FXChargingPacket = {
  __typename?: 'DataNode_FXChargingPacket';
  data: FXChargingPacket;
  dateMillis: Scalars['Long']['output'];
  fragmentId: Scalars['Int']['output'];
  fragmentIdString?: Maybe<Scalars['String']['output']>;
  identifiable: Identifiable;
  sourceId: Scalars['String']['output'];
};

export type DataNode_Float = {
  __typename?: 'DataNode_Float';
  data: Scalars['Float']['output'];
  dateMillis: Scalars['Long']['output'];
  fragmentId: Scalars['Int']['output'];
  fragmentIdString?: Maybe<Scalars['String']['output']>;
  identifiable: Identifiable;
  sourceId: Scalars['String']['output'];
};

export type DatabaseAuthorization = {
  __typename?: 'DatabaseAuthorization';
  cookie: Scalars['String']['output'];
  expiresAt: Scalars['Long']['output'];
  url: Scalars['String']['output'];
};

export type DatabaseAuthorizationInput = {
  cookie: Scalars['String']['input'];
  url: Scalars['String']['input'];
};

export enum DatabaseStatus {
  BAD_PERMISSIONS = 'BAD_PERMISSIONS',
  COMPLETE = 'COMPLETE',
  ERROR = 'ERROR',
  INCOMPLETE = 'INCOMPLETE',
  NOT_PRESENT = 'NOT_PRESENT'
}

export type DatabaseSystemStatus = {
  __typename?: 'DatabaseSystemStatus';
  getStatus: DatabaseStatus;
};


export type DatabaseSystemStatusgetStatusArgs = {
  type: SolarThingDatabaseType;
};

export enum DcdcErrorMode {
  BACKUP_BATTERY_OVER_DISCHARGE = 'BACKUP_BATTERY_OVER_DISCHARGE',
  BACKUP_BATTERY_OVER_VOLTAGE = 'BACKUP_BATTERY_OVER_VOLTAGE',
  BACKUP_BATTERY_UNDER_VOLTAGE = 'BACKUP_BATTERY_UNDER_VOLTAGE',
  BATTERY_OVER_TEMPERATURE = 'BATTERY_OVER_TEMPERATURE',
  BATTERY_TEMPERATURE_HALT_CHARGING = 'BATTERY_TEMPERATURE_HALT_CHARGING',
  BMS_OVERCHARGE = 'BMS_OVERCHARGE',
  CONTROLLER_OVER_TEMPERATURE = 'CONTROLLER_OVER_TEMPERATURE',
  CONTROLLER_WARM = 'CONTROLLER_WARM',
  FAN_ALARM = 'FAN_ALARM',
  GENERATOR_OVER_VOLTAGE = 'GENERATOR_OVER_VOLTAGE',
  OVER_CURRENT = 'OVER_CURRENT',
  PV_OVER_VOLTAGE = 'PV_OVER_VOLTAGE',
  PV_POWER_OVERLOAD = 'PV_POWER_OVERLOAD',
  SOLAR_PANEL_REVERSELY_CONNECTED = 'SOLAR_PANEL_REVERSELY_CONNECTED',
  START_BATTERY_BACKUP = 'START_BATTERY_BACKUP',
  UNABLE_TO_TRANSLATE_B22 = 'UNABLE_TO_TRANSLATE_B22',
  UNABLE_TO_TRANSLATE_B23 = 'UNABLE_TO_TRANSLATE_B23'
}

export type DeviceInfoPacket = {
  __typename?: 'DeviceInfoPacket';
  deviceDescription: Scalars['String']['output'];
  deviceLocation: Scalars['String']['output'];
  deviceName: Scalars['String']['output'];
  packetType: TargetedMetaPacketType;
};

export enum DevicePacketType {
  DEVICE_CPU_TEMPERATURE = 'DEVICE_CPU_TEMPERATURE'
}

export type DeviceSource = {
  __typename?: 'DeviceSource';
  name?: Maybe<Scalars['String']['output']>;
};

export type DualTemperature = {
  __typename?: 'DualTemperature';
  batteryTemperatureCelsius: Scalars['BigDecimal']['output'];
  batteryTemperatureFahrenheit: Scalars['Float']['output'];
  controllerTemperatureCelsius: Scalars['BigDecimal']['output'];
  controllerTemperatureFahrenheit: Scalars['Float']['output'];
  identifier: Identifier;
  identityInfo: IdentityInfo;
};

export type ExecutionReason = {
  __typename?: 'ExecutionReason';
  packetType: ExecutionReasonType;
};

export enum ExecutionReasonType {
  PACKET_COLLECTION = 'PACKET_COLLECTION',
  SOURCE = 'SOURCE'
}

export type FXACModeChangePacket = {
  __typename?: 'FXACModeChangePacket';
  acMode: ACMode;
  acModeName: Scalars['String']['output'];
  acModeValue: Scalars['Int']['output'];
  /** The address of the port that this device is plugged in to. If 0, this is plugged in to the mate */
  address: Scalars['Int']['output'];
  identifier: KnownSupplementaryIdentifier_OutbackIdentifier;
  identityInfo: IdentityInfo;
  packetType: SolarEventPacketType;
  previousACMode?: Maybe<ACMode>;
  previousACModeName?: Maybe<Scalars['String']['output']>;
  previousACModeValue?: Maybe<Scalars['Int']['output']>;
};

export type FXAuxStateChangePacket = {
  __typename?: 'FXAuxStateChangePacket';
  /** The address of the port that this device is plugged in to. If 0, this is plugged in to the mate */
  address: Scalars['Int']['output'];
  identifier: KnownSupplementaryIdentifier_OutbackIdentifier;
  identityInfo: IdentityInfo;
  isAuxActive: Scalars['Boolean']['output'];
  packetType: SolarEventPacketType;
  wasAuxActive?: Maybe<Scalars['Boolean']['output']>;
};

export enum FXChargingMode {
  ABSORB = 'ABSORB',
  BULK_TO_ABSORB = 'BULK_TO_ABSORB',
  BULK_TO_EQ = 'BULK_TO_EQ',
  EQ = 'EQ',
  FLOAT = 'FLOAT',
  REFLOAT = 'REFLOAT',
  SILENT = 'SILENT'
}

export type FXChargingPacket = {
  __typename?: 'FXChargingPacket';
  /** The address of the port that this device is plugged in to. If 0, this is plugged in to the mate */
  address: Scalars['Int']['output'];
  fxChargingMode?: Maybe<FXChargingMode>;
  remainingAbsorbTimeMillis: Scalars['Long']['output'];
  remainingEqualizeTimeMillis: Scalars['Long']['output'];
  remainingFloatTimeMillis: Scalars['Long']['output'];
  totalAbsorbTimeMillis: Scalars['Long']['output'];
  totalEqualizeTimeMillis: Scalars['Long']['output'];
  totalFloatTimeMillis: Scalars['Long']['output'];
};

export enum FXErrorMode {
  BACK_FEED = 'BACK_FEED',
  HIGH_BATTERY = 'HIGH_BATTERY',
  LOW_BATTERY = 'LOW_BATTERY',
  LOW_VAC_OUTPUT = 'LOW_VAC_OUTPUT',
  OVER_TEMP = 'OVER_TEMP',
  PHASE_LOSS = 'PHASE_LOSS',
  SHORTED_OUTPUT = 'SHORTED_OUTPUT',
  STACKING_ERROR = 'STACKING_ERROR'
}

export type FXOperationalModeChangePacket = {
  __typename?: 'FXOperationalModeChangePacket';
  /** The address of the port that this device is plugged in to. If 0, this is plugged in to the mate */
  address: Scalars['Int']['output'];
  identifier: KnownSupplementaryIdentifier_OutbackIdentifier;
  identityInfo: IdentityInfo;
  operationalMode?: Maybe<OperationalMode>;
  operationalModeName: Scalars['String']['output'];
  operationalModeValue: Scalars['Int']['output'];
  packetType: SolarEventPacketType;
  previousOperationalMode?: Maybe<OperationalMode>;
  previousOperationalModeName?: Maybe<Scalars['String']['output']>;
  previousOperationalModeValue?: Maybe<Scalars['Int']['output']>;
};

/** Status packet for FX devices */
export type FXStatusPacket = {
  __typename?: 'FXStatusPacket';
  acMode: Scalars['Int']['output'];
  acModeName: Scalars['String']['output'];
  /** The address of the port that this device is plugged in to. If 0, this is plugged in to the mate */
  address: Scalars['Int']['output'];
  /** The battery voltage */
  batteryVoltage: Scalars['Float']['output'];
  buyCurrent: Scalars['Float']['output'];
  buyCurrentRaw: Scalars['Int']['output'];
  buyWattage: Scalars['Int']['output'];
  chargerCurrent: Scalars['Float']['output'];
  chargerCurrentRaw: Scalars['Int']['output'];
  chargerWattage: Scalars['Int']['output'];
  chksum: Scalars['Int']['output'];
  errorMode: Scalars['Int']['output'];
  errorModes: Array<FXErrorMode>;
  errorsString: Scalars['String']['output'];
  hasError: Scalars['Boolean']['output'];
  identifier: OutbackIdentifier;
  identityInfo: IdentityInfo;
  inputVoltage: Scalars['Int']['output'];
  inputVoltageRaw: Scalars['Int']['output'];
  inverterCurrent: Scalars['Float']['output'];
  inverterCurrentRaw: Scalars['Int']['output'];
  inverterWattage: Scalars['Int']['output'];
  is230V: Scalars['Boolean']['output'];
  isAuxOn: Scalars['Boolean']['output'];
  misc: Scalars['Int']['output'];
  miscModes: Array<MiscMode>;
  miscModesString: Scalars['String']['output'];
  operatingMode: Scalars['Int']['output'];
  operatingModeName: Scalars['String']['output'];
  operationalMode: OperationalMode;
  outputVoltage: Scalars['Int']['output'];
  outputVoltageRaw: Scalars['Int']['output'];
  packetType: SolarStatusPacketType;
  packetVersion?: Maybe<Scalars['Int']['output']>;
  passThruWattage: Scalars['Int']['output'];
  powerUsageWattage: Scalars['Int']['output'];
  sellCurrent: Scalars['Float']['output'];
  sellCurrentRaw: Scalars['Int']['output'];
  sellWattage: Scalars['Int']['output'];
  solarModeName: Scalars['String']['output'];
  solarModeType: SolarModeType;
  solarModeTypeDisplayName: Scalars['String']['output'];
  warningMode: Scalars['Int']['output'];
  warningModes: Array<WarningMode>;
  warnings: Scalars['String']['output'];
};

export type FlagData = {
  __typename?: 'FlagData';
  activePeriod?: Maybe<ActivePeriod>;
  flagName?: Maybe<Scalars['String']['output']>;
};

export type FlagPacket = {
  __typename?: 'FlagPacket';
  executionReason: ExecutionReason;
  flagData: FlagData;
  packetType: AlterPacketType;
};

export type FlatData = {
  __typename?: 'FlatData';
  batteryVoltage?: Maybe<Scalars['Float']['output']>;
  chargeController?: Maybe<FlatDataChargeController>;
  /** Returns a comma separated string of the names of connected devices */
  deviceInfoString: Scalars['String']['output'];
  /** Returns the errors if there are any, empty string if no errors. Errors are formatted with device first, then error description */
  errorsString: Scalars['String']['output'];
  fx?: Maybe<FlatDataFX>;
  /** Returns a string representing the operating modes of all the devices */
  operatingModeString: Scalars['String']['output'];
};

export type FlatDataChargeController = {
  __typename?: 'FlatDataChargeController';
  chargerWattage: Scalars['Float']['output'];
  pvWattage: Scalars['Float']['output'];
};

export type FlatDataFX = {
  __typename?: 'FlatDataFX';
  /** The buy wattage from the AC being used by the FX(s) */
  acBuyWattage: Scalars['Float']['output'];
  /** The charging wattage from the AC being used by the FX(s) */
  acChargeWattage: Scalars['Float']['output'];
  acMode: ACMode;
  loadWattage: Scalars['Float']['output'];
  miscModesString: Scalars['String']['output'];
  /** Returns the warnings if there are any, empty string if no errors. Errors are formatted with device first, then error description */
  warningsString: Scalars['String']['output'];
};

export type Forecast = {
  __typename?: 'Forecast';
  energyGenerationEstimate: Scalars['Float']['output'];
  period?: Maybe<Scalars['Duration']['output']>;
  period_end?: Maybe<Scalars['Instant']['output']>;
  period_midpoint?: Maybe<Scalars['Instant']['output']>;
  period_start?: Maybe<Scalars['Instant']['output']>;
  pv_estimate: Scalars['Float']['output'];
  pv_estimate10: Scalars['Float']['output'];
  pv_estimate10_watts: Scalars['Float']['output'];
  pv_estimate90: Scalars['Float']['output'];
  pv_estimate90_watts: Scalars['Float']['output'];
  pv_estimate_watts: Scalars['Float']['output'];
};

export type Identifiable = {
  __typename?: 'Identifiable';
  identifier: Identifier;
  identityInfo: IdentityInfo;
};

export type IdentificationCacheDataPacket_ChargeControllerAccumulationDataCache = {
  __typename?: 'IdentificationCacheDataPacket_ChargeControllerAccumulationDataCache';
  _id?: Maybe<Scalars['String']['output']>;
  cacheName: Scalars['String']['output'];
  nodes?: Maybe<Array<Maybe<IdentificationCacheNode_ChargeControllerAccumulationDataCache>>>;
  periodDurationMillis: Scalars['Long']['output'];
  periodStartDateMillis: Scalars['Long']['output'];
  sourceId: Scalars['String']['output'];
};

export type IdentificationCacheNode_ChargeControllerAccumulationDataCache = {
  __typename?: 'IdentificationCacheNode_ChargeControllerAccumulationDataCache';
  data: ChargeControllerAccumulationDataCache;
  fragmentId: Scalars['Int']['output'];
};

/** Contains a representation that is unique across all packets in a particular fragment */
export type Identifier = {
  __typename?: 'Identifier';
  /** A string representation of this identifier */
  representation: Scalars['String']['output'];
};

/** Contains info used to show human readable identifiers */
export type IdentityInfo = {
  __typename?: 'IdentityInfo';
  displayName?: Maybe<Scalars['String']['output']>;
  name: Scalars['String']['output'];
  shortName: Scalars['String']['output'];
  stripExtra?: Maybe<IdentityInfo>;
  suffix: Scalars['String']['output'];
};

export enum InputVoltageStatus {
  HIGH_VOLTAGE = 'HIGH_VOLTAGE',
  INPUT_ERROR = 'INPUT_ERROR',
  NORMAL = 'NORMAL',
  NOT_CONNECTED = 'NOT_CONNECTED'
}

export type KnownSupplementaryIdentifier_OutbackIdentifier = {
  __typename?: 'KnownSupplementaryIdentifier_OutbackIdentifier';
  /** A string representation of this identifier */
  representation: Scalars['String']['output'];
  supplementaryTo: OutbackIdentifier;
};

export type KnownSupplementaryIdentifier_RoverIdentifier = {
  __typename?: 'KnownSupplementaryIdentifier_RoverIdentifier';
  /** A string representation of this identifier */
  representation: Scalars['String']['output'];
  supplementaryTo: RoverIdentifier;
};

export enum LoadTimingControlSelection {
  ONE_TIMER = 'ONE_TIMER',
  TWO_TIMER = 'TWO_TIMER'
}

export type MXAuxModeChangePacket = {
  __typename?: 'MXAuxModeChangePacket';
  /** The address of the port that this device is plugged in to. If 0, this is plugged in to the mate */
  address: Scalars['Int']['output'];
  identifier: KnownSupplementaryIdentifier_OutbackIdentifier;
  identityInfo: IdentityInfo;
  packetType: SolarEventPacketType;
  previousRawAuxModeValue?: Maybe<Scalars['Int']['output']>;
  rawAuxModeValue: Scalars['Int']['output'];
};

export type MXChargerModeChangePacket = {
  __typename?: 'MXChargerModeChangePacket';
  /** The address of the port that this device is plugged in to. If 0, this is plugged in to the mate */
  address: Scalars['Int']['output'];
  chargerModeValue: Scalars['Int']['output'];
  chargingModeName: Scalars['String']['output'];
  identifier: KnownSupplementaryIdentifier_OutbackIdentifier;
  identityInfo: IdentityInfo;
  packetType: SolarEventPacketType;
  previousChargerModeValue?: Maybe<Scalars['Int']['output']>;
  previousChargingModeName?: Maybe<Scalars['String']['output']>;
};

export enum MXErrorMode {
  HIGH_VOC = 'HIGH_VOC',
  SHORTED_BATTERY_SENSOR = 'SHORTED_BATTERY_SENSOR',
  TOO_HOT = 'TOO_HOT'
}

export type MXRawDayEndPacket = {
  __typename?: 'MXRawDayEndPacket';
  /** The address of the port that this device is plugged in to. If 0, this is plugged in to the mate */
  address: Scalars['Int']['output'];
  dailyAH: Scalars['Int']['output'];
  dailyAHSupport: Support;
  dailyKWH: Scalars['Float']['output'];
  identifier: KnownSupplementaryIdentifier_OutbackIdentifier;
  identityInfo: IdentityInfo;
  packetType: SolarEventPacketType;
};

export type MXStatusPacket = {
  __typename?: 'MXStatusPacket';
  /** The address of the port that this device is plugged in to. If 0, this is plugged in to the mate */
  address: Scalars['Int']['output'];
  /** @deprecated Field no longer supported */
  ampChargerCurrent: Scalars['Float']['output'];
  auxBitActive: Scalars['Boolean']['output'];
  auxMode?: Maybe<AuxMode>;
  auxModeValue: Scalars['Int']['output'];
  auxModeValueRaw: Scalars['Int']['output'];
  /** The battery voltage */
  batteryVoltage: Scalars['Float']['output'];
  /** @deprecated Field no longer supported */
  chargerCurrent: Scalars['Int']['output'];
  chargerMode: Scalars['Int']['output'];
  chargingCurrent: Scalars['BigDecimal']['output'];
  chargingMode: ChargerMode;
  chargingPower: Scalars['Float']['output'];
  chksum: Scalars['Int']['output'];
  dailyAH: Scalars['Int']['output'];
  dailyAHSupport: Support;
  dailyKWH: Scalars['Float']['output'];
  errorMode: Scalars['Int']['output'];
  errorModes: Array<MXErrorMode>;
  hasError: Scalars['Boolean']['output'];
  identifier: OutbackIdentifier;
  identityInfo: IdentityInfo;
  packetType: SolarStatusPacketType;
  packetVersion?: Maybe<Scalars['Int']['output']>;
  pvCurrent: Scalars['Int']['output'];
  pvVoltage: Scalars['Int']['output'];
  pvWattage: Scalars['Int']['output'];
  solarModeName: Scalars['String']['output'];
  solarModeType: SolarModeType;
  solarModeTypeDisplayName: Scalars['String']['output'];
};

export enum MateCommand {
  AUX_OFF = 'AUX_OFF',
  AUX_ON = 'AUX_ON',
  DROP = 'DROP',
  OFF = 'OFF',
  ON = 'ON',
  SEARCH = 'SEARCH',
  USE = 'USE'
}

export enum MateCommandFeedbackPacketType {
  MATE_COMMAND_SUCCESS = 'MATE_COMMAND_SUCCESS'
}

export enum MiscMode {
  AUX_OUTPUT_ON = 'AUX_OUTPUT_ON',
  FX_230V_UNIT = 'FX_230V_UNIT'
}

export enum Month {
  APRIL = 'APRIL',
  AUGUST = 'AUGUST',
  DECEMBER = 'DECEMBER',
  FEBRUARY = 'FEBRUARY',
  JANUARY = 'JANUARY',
  JULY = 'JULY',
  JUNE = 'JUNE',
  MARCH = 'MARCH',
  MAY = 'MAY',
  NOVEMBER = 'NOVEMBER',
  OCTOBER = 'OCTOBER',
  SEPTEMBER = 'SEPTEMBER'
}

export type MonthDay = {
  __typename?: 'MonthDay';
  dayOfMonth: Scalars['Int']['output'];
  month?: Maybe<Month>;
  monthValue: Scalars['Int']['output'];
};

/** Mutation root */
export type Mutation = {
  __typename?: 'Mutation';
  addAuthorizedSender: Scalars['Boolean']['output'];
  removeAuthorizedSender: Scalars['Boolean']['output'];
};


/** Mutation root */
export type MutationaddAuthorizedSenderArgs = {
  allowReplace?: InputMaybe<Scalars['Boolean']['input']>;
  authorization: DatabaseAuthorizationInput;
  publicKey: Scalars['String']['input'];
  sender: Scalars['String']['input'];
};


/** Mutation root */
export type MutationremoveAuthorizedSenderArgs = {
  authorization: DatabaseAuthorizationInput;
  sender: Scalars['String']['input'];
};

export type OperatingSettingBundle = {
  __typename?: 'OperatingSettingBundle';
  durationHours: Scalars['Int']['output'];
  operatingPowerPercentage: Scalars['Int']['output'];
};

export enum OperationalMode {
  AGS_ERROR = 'AGS_ERROR',
  CHARGE = 'CHARGE',
  CHARGER_OFF = 'CHARGER_OFF',
  COM_ERROR = 'COM_ERROR',
  EQ = 'EQ',
  FLOAT = 'FLOAT',
  FX_ERROR = 'FX_ERROR',
  INV_OFF = 'INV_OFF',
  INV_ON = 'INV_ON',
  PASS_THRU = 'PASS_THRU',
  SEARCH = 'SEARCH',
  SELL_ENABLED = 'SELL_ENABLED',
  SILENT = 'SILENT',
  SUPPORT = 'SUPPORT'
}

export type OutbackIdentifier = {
  __typename?: 'OutbackIdentifier';
  /** The address of the port that this device is plugged in to. If 0, this is plugged in to the mate */
  address: Scalars['Int']['output'];
  /** A string representation of this identifier */
  representation: Scalars['String']['output'];
};

export type PVCurrentAndVoltage = {
  __typename?: 'PVCurrentAndVoltage';
  identifier: Identifier;
  identityInfo: IdentityInfo;
  pvCurrent: Scalars['BigDecimal']['output'];
  pvVoltage: Scalars['BigDecimal']['output'];
  pvWattage: Scalars['BigDecimal']['output'];
};

export type PacketNode_BasicChargeController = {
  __typename?: 'PacketNode_BasicChargeController';
  dateMillis: Scalars['Long']['output'];
  fragmentDeviceInfo?: Maybe<DeviceInfoPacket>;
  /** The fragment the packet was from */
  fragmentId: Scalars['Int']['output'];
  fragmentIdString: Scalars['String']['output'];
  packet: BasicChargeController;
  /** The Source ID the packet was from */
  sourceId: Scalars['String']['output'];
};

export type PacketNode_BatteryVoltage = {
  __typename?: 'PacketNode_BatteryVoltage';
  dateMillis: Scalars['Long']['output'];
  fragmentDeviceInfo?: Maybe<DeviceInfoPacket>;
  /** The fragment the packet was from */
  fragmentId: Scalars['Int']['output'];
  fragmentIdString: Scalars['String']['output'];
  packet: BatteryVoltage;
  /** The Source ID the packet was from */
  sourceId: Scalars['String']['output'];
};

export type PacketNode_CpuTemperaturePacket = {
  __typename?: 'PacketNode_CpuTemperaturePacket';
  dateMillis: Scalars['Long']['output'];
  fragmentDeviceInfo?: Maybe<DeviceInfoPacket>;
  /** The fragment the packet was from */
  fragmentId: Scalars['Int']['output'];
  fragmentIdString: Scalars['String']['output'];
  packet: CpuTemperaturePacket;
  /** The Source ID the packet was from */
  sourceId: Scalars['String']['output'];
};

export type PacketNode_DailyChargeController = {
  __typename?: 'PacketNode_DailyChargeController';
  dateMillis: Scalars['Long']['output'];
  fragmentDeviceInfo?: Maybe<DeviceInfoPacket>;
  /** The fragment the packet was from */
  fragmentId: Scalars['Int']['output'];
  fragmentIdString: Scalars['String']['output'];
  packet: DailyChargeController;
  /** The Source ID the packet was from */
  sourceId: Scalars['String']['output'];
};

export type PacketNode_DailyFXPacket = {
  __typename?: 'PacketNode_DailyFXPacket';
  dateMillis: Scalars['Long']['output'];
  fragmentDeviceInfo?: Maybe<DeviceInfoPacket>;
  /** The fragment the packet was from */
  fragmentId: Scalars['Int']['output'];
  fragmentIdString: Scalars['String']['output'];
  packet: DailyFXPacket;
  /** The Source ID the packet was from */
  sourceId: Scalars['String']['output'];
};

export type PacketNode_DualTemperature = {
  __typename?: 'PacketNode_DualTemperature';
  dateMillis: Scalars['Long']['output'];
  fragmentDeviceInfo?: Maybe<DeviceInfoPacket>;
  /** The fragment the packet was from */
  fragmentId: Scalars['Int']['output'];
  fragmentIdString: Scalars['String']['output'];
  packet: DualTemperature;
  /** The Source ID the packet was from */
  sourceId: Scalars['String']['output'];
};

export type PacketNode_FXACModeChangePacket = {
  __typename?: 'PacketNode_FXACModeChangePacket';
  dateMillis: Scalars['Long']['output'];
  fragmentDeviceInfo?: Maybe<DeviceInfoPacket>;
  /** The fragment the packet was from */
  fragmentId: Scalars['Int']['output'];
  fragmentIdString: Scalars['String']['output'];
  packet: FXACModeChangePacket;
  /** The Source ID the packet was from */
  sourceId: Scalars['String']['output'];
};

export type PacketNode_FXAuxStateChangePacket = {
  __typename?: 'PacketNode_FXAuxStateChangePacket';
  dateMillis: Scalars['Long']['output'];
  fragmentDeviceInfo?: Maybe<DeviceInfoPacket>;
  /** The fragment the packet was from */
  fragmentId: Scalars['Int']['output'];
  fragmentIdString: Scalars['String']['output'];
  packet: FXAuxStateChangePacket;
  /** The Source ID the packet was from */
  sourceId: Scalars['String']['output'];
};

export type PacketNode_FXOperationalModeChangePacket = {
  __typename?: 'PacketNode_FXOperationalModeChangePacket';
  dateMillis: Scalars['Long']['output'];
  fragmentDeviceInfo?: Maybe<DeviceInfoPacket>;
  /** The fragment the packet was from */
  fragmentId: Scalars['Int']['output'];
  fragmentIdString: Scalars['String']['output'];
  packet: FXOperationalModeChangePacket;
  /** The Source ID the packet was from */
  sourceId: Scalars['String']['output'];
};

export type PacketNode_FXStatusPacket = {
  __typename?: 'PacketNode_FXStatusPacket';
  dateMillis: Scalars['Long']['output'];
  fragmentDeviceInfo?: Maybe<DeviceInfoPacket>;
  /** The fragment the packet was from */
  fragmentId: Scalars['Int']['output'];
  fragmentIdString: Scalars['String']['output'];
  packet: FXStatusPacket;
  /** The Source ID the packet was from */
  sourceId: Scalars['String']['output'];
};

export type PacketNode_MXAuxModeChangePacket = {
  __typename?: 'PacketNode_MXAuxModeChangePacket';
  dateMillis: Scalars['Long']['output'];
  fragmentDeviceInfo?: Maybe<DeviceInfoPacket>;
  /** The fragment the packet was from */
  fragmentId: Scalars['Int']['output'];
  fragmentIdString: Scalars['String']['output'];
  packet: MXAuxModeChangePacket;
  /** The Source ID the packet was from */
  sourceId: Scalars['String']['output'];
};

export type PacketNode_MXChargerModeChangePacket = {
  __typename?: 'PacketNode_MXChargerModeChangePacket';
  dateMillis: Scalars['Long']['output'];
  fragmentDeviceInfo?: Maybe<DeviceInfoPacket>;
  /** The fragment the packet was from */
  fragmentId: Scalars['Int']['output'];
  fragmentIdString: Scalars['String']['output'];
  packet: MXChargerModeChangePacket;
  /** The Source ID the packet was from */
  sourceId: Scalars['String']['output'];
};

export type PacketNode_MXRawDayEndPacket = {
  __typename?: 'PacketNode_MXRawDayEndPacket';
  dateMillis: Scalars['Long']['output'];
  fragmentDeviceInfo?: Maybe<DeviceInfoPacket>;
  /** The fragment the packet was from */
  fragmentId: Scalars['Int']['output'];
  fragmentIdString: Scalars['String']['output'];
  packet: MXRawDayEndPacket;
  /** The Source ID the packet was from */
  sourceId: Scalars['String']['output'];
};

export type PacketNode_MXStatusPacket = {
  __typename?: 'PacketNode_MXStatusPacket';
  dateMillis: Scalars['Long']['output'];
  fragmentDeviceInfo?: Maybe<DeviceInfoPacket>;
  /** The fragment the packet was from */
  fragmentId: Scalars['Int']['output'];
  fragmentIdString: Scalars['String']['output'];
  packet: MXStatusPacket;
  /** The Source ID the packet was from */
  sourceId: Scalars['String']['output'];
};

export type PacketNode_PVCurrentAndVoltage = {
  __typename?: 'PacketNode_PVCurrentAndVoltage';
  dateMillis: Scalars['Long']['output'];
  fragmentDeviceInfo?: Maybe<DeviceInfoPacket>;
  /** The fragment the packet was from */
  fragmentId: Scalars['Int']['output'];
  fragmentIdString: Scalars['String']['output'];
  packet: PVCurrentAndVoltage;
  /** The Source ID the packet was from */
  sourceId: Scalars['String']['output'];
};

export type PacketNode_PzemShuntStatusPacket = {
  __typename?: 'PacketNode_PzemShuntStatusPacket';
  dateMillis: Scalars['Long']['output'];
  fragmentDeviceInfo?: Maybe<DeviceInfoPacket>;
  /** The fragment the packet was from */
  fragmentId: Scalars['Int']['output'];
  fragmentIdString: Scalars['String']['output'];
  packet: PzemShuntStatusPacket;
  /** The Source ID the packet was from */
  sourceId: Scalars['String']['output'];
};

export type PacketNode_RoverChargingStateChangePacket = {
  __typename?: 'PacketNode_RoverChargingStateChangePacket';
  dateMillis: Scalars['Long']['output'];
  fragmentDeviceInfo?: Maybe<DeviceInfoPacket>;
  /** The fragment the packet was from */
  fragmentId: Scalars['Int']['output'];
  fragmentIdString: Scalars['String']['output'];
  packet: RoverChargingStateChangePacket;
  /** The Source ID the packet was from */
  sourceId: Scalars['String']['output'];
};

export type PacketNode_RoverStatusPacket = {
  __typename?: 'PacketNode_RoverStatusPacket';
  dateMillis: Scalars['Long']['output'];
  fragmentDeviceInfo?: Maybe<DeviceInfoPacket>;
  /** The fragment the packet was from */
  fragmentId: Scalars['Int']['output'];
  fragmentIdString: Scalars['String']['output'];
  packet: RoverStatusPacket;
  /** The Source ID the packet was from */
  sourceId: Scalars['String']['output'];
};

export type PacketNode_SolarDevice = {
  __typename?: 'PacketNode_SolarDevice';
  dateMillis: Scalars['Long']['output'];
  fragmentDeviceInfo?: Maybe<DeviceInfoPacket>;
  /** The fragment the packet was from */
  fragmentId: Scalars['Int']['output'];
  fragmentIdString: Scalars['String']['output'];
  packet: SolarDevice;
  /** The Source ID the packet was from */
  sourceId: Scalars['String']['output'];
};

export type PacketNode_SuccessMateCommandPacket = {
  __typename?: 'PacketNode_SuccessMateCommandPacket';
  dateMillis: Scalars['Long']['output'];
  fragmentDeviceInfo?: Maybe<DeviceInfoPacket>;
  /** The fragment the packet was from */
  fragmentId: Scalars['Int']['output'];
  fragmentIdString: Scalars['String']['output'];
  packet: SuccessMateCommandPacket;
  /** The Source ID the packet was from */
  sourceId: Scalars['String']['output'];
};

export type PacketNode_TemperaturePacket = {
  __typename?: 'PacketNode_TemperaturePacket';
  dateMillis: Scalars['Long']['output'];
  fragmentDeviceInfo?: Maybe<DeviceInfoPacket>;
  /** The fragment the packet was from */
  fragmentId: Scalars['Int']['output'];
  fragmentIdString: Scalars['String']['output'];
  meta?: Maybe<DataMetaPacket>;
  packet: TemperaturePacket;
  /** The Source ID the packet was from */
  sourceId: Scalars['String']['output'];
};

export type PacketNode_TracerStatusPacket = {
  __typename?: 'PacketNode_TracerStatusPacket';
  dateMillis: Scalars['Long']['output'];
  fragmentDeviceInfo?: Maybe<DeviceInfoPacket>;
  /** The fragment the packet was from */
  fragmentId: Scalars['Int']['output'];
  fragmentIdString: Scalars['String']['output'];
  packet: TracerStatusPacket;
  /** The Source ID the packet was from */
  sourceId: Scalars['String']['output'];
};

export type PermissionObject = {
  __typename?: 'PermissionObject';
  /** @deprecated Field no longer supported */
  fragments?: Maybe<Scalars['ObjectScalar']['output']>;
  publicKey?: Maybe<Scalars['String']['output']>;
};

export type PzemShuntStatusPacket = {
  __typename?: 'PzemShuntStatusPacket';
  currentAmps: Scalars['Float']['output'];
  currentValueRaw: Scalars['Int']['output'];
  dataId: Scalars['Int']['output'];
  dataIdString: Scalars['String']['output'];
  energyKWH: Scalars['Float']['output'];
  energyValueRaw: Scalars['Int']['output'];
  energyWattHours: Scalars['Int']['output'];
  highVoltageAlarmStatus: Scalars['Int']['output'];
  identifier: DataIdentifier;
  identityInfo: IdentityInfo;
  isHighVoltageAlarm: Scalars['Boolean']['output'];
  isLowVoltageAlarm: Scalars['Boolean']['output'];
  lowVoltageAlarmStatus: Scalars['Int']['output'];
  modbusAddress: Scalars['Int']['output'];
  packetType: SolarStatusPacketType;
  powerValueRaw: Scalars['Int']['output'];
  powerWatts: Scalars['Float']['output'];
  voltage: Scalars['Float']['output'];
  voltageValueRaw: Scalars['Int']['output'];
};

/** Query root */
export type Query = {
  __typename?: 'Query';
  authRequests: Array<SimpleNode_AuthNewSenderPacket>;
  authorizedSenders: Array<AuthorizedSender>;
  databaseAuthorize: DatabaseAuthorization;
  queryAlter?: Maybe<SolarThingAlterQuery>;
  queryBatteryEstimate: SolarThingBatteryEstimate;
  queryBatteryRecord: SolarThingBatteryRecordQuery;
  queryEvent: SolarThingEventQuery;
  /** Queries events in the specified time range while only including the specified fragment */
  queryEventFragment: SolarThingEventQuery;
  /** Queries events in the specified time range while only including the specified identifier in the specified fragment */
  queryEventIdentifier: SolarThingEventQuery;
  /** Gives the timer values for the master FX of a single fragment over a time range */
  queryFXCharging?: Maybe<Array<Maybe<DataNode_FXChargingPacket>>>;
  queryFullDay?: Maybe<SolarThingFullDayStatusQuery>;
  queryLongTermMillis?: Maybe<SolarThingLongTermQuery>;
  queryLongTermMonth?: Maybe<SolarThingLongTermQuery>;
  querySolcast?: Maybe<SolarThingSolcastQuery>;
  querySolcastDay?: Maybe<SolarThingSolcastDayQuery>;
  /** Query status packets in the specified time range. */
  queryStatus: SolarThingStatusQuery;
  /** Query the latest collection of status packets on or before the 'to' timestamp. */
  queryStatusLast: SolarThingStatusQuery;
  systemStatus?: Maybe<DatabaseSystemStatus>;
  username?: Maybe<Scalars['String']['output']>;
};


/** Query root */
export type QuerydatabaseAuthorizeArgs = {
  password: Scalars['String']['input'];
  username: Scalars['String']['input'];
};


/** Query root */
export type QueryqueryAlterArgs = {
  sourceId: Scalars['String']['input'];
};


/** Query root */
export type QueryqueryBatteryEstimateArgs = {
  duration?: InputMaybe<Scalars['Duration']['input']>;
  sourceId?: InputMaybe<Scalars['String']['input']>;
  to: Scalars['Long']['input'];
};


/** Query root */
export type QueryqueryBatteryRecordArgs = {
  from: Scalars['Long']['input'];
  sourceId?: InputMaybe<Scalars['String']['input']>;
  to: Scalars['Long']['input'];
};


/** Query root */
export type QueryqueryEventArgs = {
  from: Scalars['Long']['input'];
  includeUnknownChangePackets?: InputMaybe<Scalars['Boolean']['input']>;
  sourceId?: InputMaybe<Scalars['String']['input']>;
  to: Scalars['Long']['input'];
};


/** Query root */
export type QueryqueryEventFragmentArgs = {
  fragmentId: Scalars['Int']['input'];
  from: Scalars['Long']['input'];
  includeUnknownChangePackets?: InputMaybe<Scalars['Boolean']['input']>;
  to: Scalars['Long']['input'];
};


/** Query root */
export type QueryqueryEventIdentifierArgs = {
  acceptSupplementary?: InputMaybe<Scalars['Boolean']['input']>;
  fragmentId: Scalars['Int']['input'];
  from: Scalars['Long']['input'];
  identifier: Scalars['String']['input'];
  includeUnknownChangePackets?: InputMaybe<Scalars['Boolean']['input']>;
  to: Scalars['Long']['input'];
};


/** Query root */
export type QueryqueryFXChargingArgs = {
  fragmentId: Scalars['Int']['input'];
  from: Scalars['Long']['input'];
  to: Scalars['Long']['input'];
};


/** Query root */
export type QueryqueryFullDayArgs = {
  from: Scalars['Long']['input'];
  sourceId?: InputMaybe<Scalars['String']['input']>;
  to: Scalars['Long']['input'];
  useCache?: InputMaybe<Scalars['Boolean']['input']>;
};


/** Query root */
export type QueryqueryLongTermMillisArgs = {
  from: Scalars['Long']['input'];
  sourceId?: InputMaybe<Scalars['String']['input']>;
  to: Scalars['Long']['input'];
};


/** Query root */
export type QueryqueryLongTermMonthArgs = {
  month?: InputMaybe<Month>;
  sourceId?: InputMaybe<Scalars['String']['input']>;
  year: Scalars['Int']['input'];
};


/** Query root */
export type QueryquerySolcastArgs = {
  from: Scalars['Long']['input'];
  sourceId: Scalars['String']['input'];
  to: Scalars['Long']['input'];
};


/** Query root */
export type QueryquerySolcastDayArgs = {
  sourceId: Scalars['String']['input'];
  to: Scalars['Long']['input'];
};


/** Query root */
export type QueryqueryStatusArgs = {
  from: Scalars['Long']['input'];
  sourceId?: InputMaybe<Scalars['String']['input']>;
  to: Scalars['Long']['input'];
};


/** Query root */
export type QueryqueryStatusLastArgs = {
  reversed?: InputMaybe<Scalars['Boolean']['input']>;
  sourceId?: InputMaybe<Scalars['String']['input']>;
  to: Scalars['Long']['input'];
};


/** Query root */
export type QueryusernameArgs = {
  authorization?: InputMaybe<DatabaseAuthorizationInput>;
};

export enum RoverBatteryType {
  GEL = 'GEL',
  LITHIUM = 'LITHIUM',
  LITHIUM_48V = 'LITHIUM_48V',
  OPEN = 'OPEN',
  SEALED = 'SEALED',
  USER_LOCKED = 'USER_LOCKED',
  USER_UNLOCKED = 'USER_UNLOCKED'
}

export type RoverChargingStateChangePacket = {
  __typename?: 'RoverChargingStateChangePacket';
  chargingMode: ChargingState;
  chargingStateValue: Scalars['Int']['output'];
  identifier: KnownSupplementaryIdentifier_RoverIdentifier;
  identityInfo: IdentityInfo;
  number: Scalars['Int']['output'];
  packetType: SolarEventPacketType;
  previousChargingMode?: Maybe<ChargingState>;
  previousChargingStateValue?: Maybe<Scalars['Int']['output']>;
};

export enum RoverErrorMode {
  AMBIENT_TEMP_HIGH = 'AMBIENT_TEMP_HIGH',
  ANTI_REVERSE_SHORT = 'ANTI_REVERSE_SHORT',
  BATTERY_OVER_DISCHARGE = 'BATTERY_OVER_DISCHARGE',
  BATTERY_OVER_VOLTAGE = 'BATTERY_OVER_VOLTAGE',
  BATTERY_UNDER_VOLTAGE = 'BATTERY_UNDER_VOLTAGE',
  CHARGE_SHORT_CIRCUIT = 'CHARGE_SHORT_CIRCUIT',
  CONTROLLER_TEMP_HIGH = 'CONTROLLER_TEMP_HIGH',
  LOAD_OVER = 'LOAD_OVER',
  LOAD_SHORT_CIRCUIT = 'LOAD_SHORT_CIRCUIT',
  PV_INPUT_OVERPOWER = 'PV_INPUT_OVERPOWER',
  PV_INPUT_SIDE_OVER_VOLTAGE = 'PV_INPUT_SIDE_OVER_VOLTAGE',
  PV_INPUT_SIDE_SHORT_CIRCUIT = 'PV_INPUT_SIDE_SHORT_CIRCUIT',
  SOLAR_PANEL_COUNTER_CURRENT = 'SOLAR_PANEL_COUNTER_CURRENT',
  SOLAR_PANEL_REVERSELY_CONNECTED = 'SOLAR_PANEL_REVERSELY_CONNECTED',
  SOLAR_PANEL_WORKING_POINT_OVER_VOLTAGE = 'SOLAR_PANEL_WORKING_POINT_OVER_VOLTAGE'
}

export type RoverIdentifier = {
  __typename?: 'RoverIdentifier';
  number: Scalars['Int']['output'];
  /** A string representation of this identifier */
  representation: Scalars['String']['output'];
};

/** Status packet for Rover and Rover-like devices */
export type RoverStatusPacket = {
  __typename?: 'RoverStatusPacket';
  /** The state of charge of the battery. (A number from 0 to 100). Note this is not usually accurate. */
  batteryCapacitySOC: Scalars['Int']['output'];
  batteryFullChargesCount: Scalars['Int']['output'];
  batteryOverDischargesCount: Scalars['Int']['output'];
  batteryTemperatureCelsius: Scalars['Int']['output'];
  batteryTemperatureFahrenheit: Scalars['Float']['output'];
  batteryTemperatureRaw: Scalars['Int']['output'];
  batteryType: RoverBatteryType;
  batteryTypeValue: Scalars['Int']['output'];
  /** The battery voltage */
  batteryVoltage: Scalars['Float']['output'];
  boostChargingRecoveryVoltageRaw: Scalars['Int']['output'];
  boostChargingTimeRaw: Scalars['Int']['output'];
  boostChargingVoltageRaw: Scalars['Int']['output'];
  chargingAmpHoursOfBatteryCount: Scalars['Int']['output'];
  chargingCurrent: Scalars['Float']['output'];
  chargingMode: ChargingState;
  chargingPower: Scalars['Int']['output'];
  chargingState: Scalars['Int']['output'];
  chargingVoltageLimitRaw: Scalars['Int']['output'];
  /** The modbus address of the device */
  controllerDeviceAddress: Scalars['Int']['output'];
  controllerTemperatureCelsius: Scalars['Int']['output'];
  controllerTemperatureFahrenheit: Scalars['Float']['output'];
  controllerTemperatureRaw: Scalars['Int']['output'];
  cumulativeKWH: Scalars['Float']['output'];
  cumulativeKWHConsumption: Scalars['Float']['output'];
  dailyAH: Scalars['Int']['output'];
  dailyAHDischarging: Scalars['Int']['output'];
  dailyKWH: Scalars['Float']['output'];
  dailyKWHConsumption: Scalars['Float']['output'];
  /** The maximum battery voltage for the day. Note this may reset at a different time compared to min battery voltage for the day. */
  dailyMaxBatteryVoltage: Scalars['Float']['output'];
  dailyMaxChargingCurrent: Scalars['Float']['output'];
  dailyMaxChargingPower: Scalars['Int']['output'];
  dailyMaxDischargingCurrent: Scalars['Float']['output'];
  dailyMaxDischargingPower: Scalars['Int']['output'];
  /** The minimum battery voltage for the day. Note this may reset at a different time compared to max battery voltage for the day. */
  dailyMinBatteryVoltage: Scalars['Float']['output'];
  dcdcErrorModes: Array<DcdcErrorMode>;
  /** The DcdcErrorMode or an empty list if this is not a DCDC charge controller */
  dcdcErrorModesOrEmpty: Array<DcdcErrorMode>;
  dischargingAmpHoursOfBatteryCount: Scalars['Int']['output'];
  dischargingLimitVoltageRaw: Scalars['Int']['output'];
  endOfChargeSOC: Scalars['Int']['output'];
  endOfDischargeSOC: Scalars['Int']['output'];
  equalizingChargingIntervalRaw: Scalars['Int']['output'];
  equalizingChargingTimeRaw: Scalars['Int']['output'];
  equalizingChargingVoltageRaw: Scalars['Int']['output'];
  errorMode: Scalars['Int']['output'];
  errorModes: Array<SimpleRoverErrorMode>;
  floatingChargingVoltageRaw: Scalars['Int']['output'];
  generatorCurrent: Scalars['Float']['output'];
  generatorPower: Scalars['Int']['output'];
  generatorVoltage: Scalars['Float']['output'];
  hardwareVersion?: Maybe<Version>;
  hardwareVersionValue: Scalars['Int']['output'];
  hasError: Scalars['Boolean']['output'];
  hasLoad: Scalars['Boolean']['output'];
  identifier: RoverIdentifier;
  identityInfo: IdentityInfo;
  isDcdc: Scalars['Boolean']['output'];
  ledLoadCurrentRaw?: Maybe<Scalars['Int']['output']>;
  ledLoadCurrentSettingRaw: Scalars['Int']['output'];
  /** Usually does not represent any meaningful value */
  lightControlDelayMinutes: Scalars['Int']['output'];
  /** Usually does not represent any meaningful value */
  lightControlVoltage: Scalars['Int']['output'];
  loadCurrent: Scalars['Float']['output'];
  loadPower: Scalars['Int']['output'];
  loadVoltage: Scalars['Float']['output'];
  loadWorkingMode: Scalars['Int']['output'];
  maxVoltage: Scalars['Int']['output'];
  /** Nominal battery capacity in AH. Usually this is not accurate. */
  nominalBatteryCapacity: Scalars['Int']['output'];
  number: Scalars['Int']['output'];
  operatingDaysCount: Scalars['Int']['output'];
  operatingMorningOn?: Maybe<OperatingSettingBundle>;
  operatingStage1?: Maybe<OperatingSettingBundle>;
  operatingStage2?: Maybe<OperatingSettingBundle>;
  operatingStage3?: Maybe<OperatingSettingBundle>;
  overDischargeRecoveryVoltageRaw: Scalars['Int']['output'];
  overDischargeTimeDelaySeconds: Scalars['Int']['output'];
  overDischargeVoltageRaw: Scalars['Int']['output'];
  overVoltageThresholdRaw: Scalars['Int']['output'];
  packetType: SolarStatusPacketType;
  packetVersion?: Maybe<Scalars['Int']['output']>;
  productModelEncoded?: Maybe<Scalars['Base64String']['output']>;
  productModelString: Scalars['String']['output'];
  /** The product serial number. Note that is not always unique as devices' serial numbers can accidentally be reset. */
  productSerialNumber: Scalars['Int']['output'];
  productType: Scalars['Int']['output'];
  pvCurrent: Scalars['Float']['output'];
  pvVoltage: Scalars['Float']['output'];
  pvWattage: Scalars['BigDecimal']['output'];
  ratedChargingCurrent: Scalars['Int']['output'];
  ratedDischargingCurrent: Scalars['Int']['output'];
  recognizedVoltage: Scalars['Int']['output'];
  roverErrorModes: Array<RoverErrorMode>;
  /** The RoverErrorModes or an empty list if this is a DCDC charge controller */
  roverErrorModesOrEmpty: Array<RoverErrorMode>;
  sensed1?: Maybe<SensingBundle>;
  sensed2?: Maybe<SensingBundle>;
  sensed3?: Maybe<SensingBundle>;
  sensingTimeDelayRaw?: Maybe<Scalars['Int']['output']>;
  softwareVersion: Version;
  softwareVersionValue: Scalars['Int']['output'];
  solarModeName: Scalars['String']['output'];
  solarModeType: SolarModeType;
  solarModeTypeDisplayName: Scalars['String']['output'];
  specialPowerControlE02D?: Maybe<SpecialPowerControl_E02D>;
  specialPowerControlE02DRaw?: Maybe<Scalars['Int']['output']>;
  specialPowerControlE021?: Maybe<SpecialPowerControl_E021>;
  specialPowerControlE021Raw: Scalars['Int']['output'];
  streetLightValue: Scalars['Int']['output'];
  systemVoltageSetting: Scalars['Int']['output'];
  temperatureCompensationFactorRaw: Scalars['Int']['output'];
  underVoltageWarningLevelRaw: Scalars['Int']['output'];
};

export type ScheduledCommandData = {
  __typename?: 'ScheduledCommandData';
  commandName: Scalars['String']['output'];
  scheduledTimeMillis: Scalars['Long']['output'];
  targetFragmentIds: Array<Maybe<Scalars['Int']['output']>>;
};

export type ScheduledCommandPacket = {
  __typename?: 'ScheduledCommandPacket';
  data: ScheduledCommandData;
  executionReason: ExecutionReason;
  packetType: AlterPacketType;
};

export enum SecurityPacketType {
  AUTH_NEW_SENDER = 'AUTH_NEW_SENDER',
  INTEGRITY_PACKET = 'INTEGRITY_PACKET',
  LARGE_INTEGRITY_PACKET = 'LARGE_INTEGRITY_PACKET'
}

export type SensingBundle = {
  __typename?: 'SensingBundle';
  powerWithNoPeopleSensedRaw: Scalars['Int']['output'];
  powerWithPeopleSensedRaw: Scalars['Int']['output'];
  workingHoursRaw: Scalars['Int']['output'];
};

export type SimpleEstimatedActual = {
  __typename?: 'SimpleEstimatedActual';
  energyGenerationEstimate: Scalars['Float']['output'];
  period?: Maybe<Scalars['Duration']['output']>;
  period_end?: Maybe<Scalars['Instant']['output']>;
  period_midpoint?: Maybe<Scalars['Instant']['output']>;
  period_start?: Maybe<Scalars['Instant']['output']>;
  pv_estimate: Scalars['Float']['output'];
  pv_estimate_watts: Scalars['Float']['output'];
};

export type SimpleNode_AuthNewSenderPacket = {
  __typename?: 'SimpleNode_AuthNewSenderPacket';
  data: AuthNewSenderPacket;
  dateMillis: Scalars['Long']['output'];
};

export type SimpleNode_FlatData = {
  __typename?: 'SimpleNode_FlatData';
  data: FlatData;
  dateMillis: Scalars['Long']['output'];
};

export type SimpleNode_Float = {
  __typename?: 'SimpleNode_Float';
  data: Scalars['Float']['output'];
  dateMillis: Scalars['Long']['output'];
};

export type SimpleRoverErrorMode = {
  __typename?: 'SimpleRoverErrorMode';
  maskValue: Scalars['Int']['output'];
  modeName: Scalars['String']['output'];
};

export type SolarDevice = {
  __typename?: 'SolarDevice';
  identifier: Identifier;
  identityInfo: IdentityInfo;
  solarMode: SolarMode;
  solarModeName: Scalars['String']['output'];
  solarModeType: SolarModeType;
  solarModeTypeDisplayName: Scalars['String']['output'];
};

export enum SolarEventPacketType {
  FX_AC_MODE_CHANGE = 'FX_AC_MODE_CHANGE',
  FX_AUX_STATE_CHANGE = 'FX_AUX_STATE_CHANGE',
  /** @deprecated Field no longer supported */
  FX_DAILY_DAY_END = 'FX_DAILY_DAY_END',
  FX_ERROR_MODE_CHANGE = 'FX_ERROR_MODE_CHANGE',
  FX_OPERATIONAL_MODE_CHANGE = 'FX_OPERATIONAL_MODE_CHANGE',
  FX_WARNING_MODE_CHANGE = 'FX_WARNING_MODE_CHANGE',
  MXFM_AUX_MODE_CHANGE = 'MXFM_AUX_MODE_CHANGE',
  MXFM_CHARGER_MODE_CHANGE = 'MXFM_CHARGER_MODE_CHANGE',
  /** @deprecated Field no longer supported */
  MXFM_DAILY_DAY_END = 'MXFM_DAILY_DAY_END',
  MXFM_ERROR_MODE_CHANGE = 'MXFM_ERROR_MODE_CHANGE',
  MXFM_RAW_DAY_END = 'MXFM_RAW_DAY_END',
  ROVER_CHARGING_STATE_CHANGE = 'ROVER_CHARGING_STATE_CHANGE',
  ROVER_ERROR_MODE_CHANGE = 'ROVER_ERROR_MODE_CHANGE',
  TRACER_CHARGING_EQUIPMENT_STATUS_CHANGE = 'TRACER_CHARGING_EQUIPMENT_STATUS_CHANGE'
}

export enum SolarExtraPacketType {
  /** @deprecated Field no longer supported */
  FX_CHARGING = 'FX_CHARGING',
  FX_DAILY = 'FX_DAILY',
  /** @deprecated Field no longer supported */
  MXFM_DAILY = 'MXFM_DAILY'
}

export type SolarMode = {
  __typename?: 'SolarMode';
  modeName: Scalars['String']['output'];
  solarModeType: SolarModeType;
};

export enum SolarModeType {
  ABSORB = 'ABSORB',
  BULK = 'BULK',
  BULK_ABSORB = 'BULK_ABSORB',
  BULK_EQUALIZE = 'BULK_EQUALIZE',
  BULK_FLOAT = 'BULK_FLOAT',
  CHARGE_CONTROLLER_OFF = 'CHARGE_CONTROLLER_OFF',
  CURRENT_LIMITING = 'CURRENT_LIMITING',
  DIRECT_CHARGE = 'DIRECT_CHARGE',
  EQUALIZE = 'EQUALIZE',
  FLOAT = 'FLOAT',
  INVERTER_CHARGER_OFF = 'INVERTER_CHARGER_OFF',
  INVERTER_OFF = 'INVERTER_OFF',
  INVERTER_ON = 'INVERTER_ON',
  INVERTER_PASS_THRU = 'INVERTER_PASS_THRU',
  INVERTER_SEARCH = 'INVERTER_SEARCH',
  INVERTER_SELL = 'INVERTER_SELL',
  INVERTER_SILENT = 'INVERTER_SILENT',
  INVERTER_SUPPORT = 'INVERTER_SUPPORT',
  INVERTER_UNKNOWN = 'INVERTER_UNKNOWN'
}

export enum SolarStatusPacketType {
  BATTERY_VOLTAGE_ONLY = 'BATTERY_VOLTAGE_ONLY',
  FLEXNET_DC_STATUS = 'FLEXNET_DC_STATUS',
  FX_STATUS = 'FX_STATUS',
  MXFM_STATUS = 'MXFM_STATUS',
  PZEM_SHUNT = 'PZEM_SHUNT',
  RENOGY_ROVER_STATUS = 'RENOGY_ROVER_STATUS',
  TRACER_STATUS = 'TRACER_STATUS'
}

export type SolarThingAlterQuery = {
  __typename?: 'SolarThingAlterQuery';
  activeFlagStrings: Array<Scalars['String']['output']>;
  activeFlags: Array<FlagPacket>;
  flags: Array<FlagPacket>;
  scheduledCommands: Array<ScheduledCommandPacket>;
};


export type SolarThingAlterQueryactiveFlagsArgs = {
  dateMillis: Scalars['Long']['input'];
};


export type SolarThingAlterQueryflagsArgs = {
  mustBeActive?: InputMaybe<Scalars['Boolean']['input']>;
};

export type SolarThingBatteryEstimate = {
  __typename?: 'SolarThingBatteryEstimate';
  queryEstimate: Array<DataNode_Double>;
};


export type SolarThingBatteryEstimatequeryEstimateArgs = {
  ratio: Scalars['Float']['input'];
};

export type SolarThingBatteryRecordQuery = {
  __typename?: 'SolarThingBatteryRecordQuery';
  averageBatteryVoltage: Array<DataNode_Double>;
};

export enum SolarThingDatabaseType {
  ALTER = 'ALTER',
  CACHE = 'CACHE',
  CLOSED = 'CLOSED',
  EVENT = 'EVENT',
  OPEN = 'OPEN',
  STATUS = 'STATUS'
}

export type SolarThingEventQuery = {
  __typename?: 'SolarThingEventQuery';
  fxACModeChange: Array<PacketNode_FXACModeChangePacket>;
  fxAuxStateChange: Array<PacketNode_FXAuxStateChangePacket>;
  fxOperationalModeChange: Array<PacketNode_FXOperationalModeChangePacket>;
  mateCommand: Array<PacketNode_SuccessMateCommandPacket>;
  mxAuxModeChange: Array<PacketNode_MXAuxModeChangePacket>;
  mxChargerModeChange: Array<PacketNode_MXChargerModeChangePacket>;
  mxRawDayEnd: Array<PacketNode_MXRawDayEndPacket>;
  roverChargingStateChange: Array<PacketNode_RoverChargingStateChangePacket>;
};


export type SolarThingEventQueryfxOperationalModeChangeArgs = {
  exclude?: InputMaybe<Array<OperationalMode>>;
  include?: InputMaybe<Array<OperationalMode>>;
};


export type SolarThingEventQuerymateCommandArgs = {
  exclude?: InputMaybe<Array<MateCommand>>;
  include?: InputMaybe<Array<MateCommand>>;
};

export type SolarThingFullDayStatusQuery = {
  __typename?: 'SolarThingFullDayStatusQuery';
  /** Gives a list of a list entries. Each entry can be grouped by their identifier as entries may represent different devices */
  dailyKWH: Array<DataNode_Float>;
  /** Gives a list of entries where each entry is a sum of the daily kWh at that instant in time for the day at that time. */
  dailyKWHSum: Array<SimpleNode_Float>;
  /** Gives entries where each entry is timestamped at the start of a certain day and its value represents the daily kWh of that device for that day. (Results can be grouped by their identifiers as there may be different devices) */
  singleDailyKWH: Array<DataNode_Float>;
};

export type SolarThingLongTermQuery = {
  __typename?: 'SolarThingLongTermQuery';
  chargeControllerAccumulation?: Maybe<IdentificationCacheDataPacket_ChargeControllerAccumulationDataCache>;
  chargeControllerAccumulationRaw: Array<IdentificationCacheDataPacket_ChargeControllerAccumulationDataCache>;
};

export type SolarThingSolcastDayQuery = {
  __typename?: 'SolarThingSolcastDayQuery';
  /** Queries the kWh generation estimate for a certain day. offset of 0 is today, 1 is tomorrow, -1 is yesterday */
  queryEnergyEstimate: DailyEnergy;
};


export type SolarThingSolcastDayQueryqueryEnergyEstimateArgs = {
  offset?: InputMaybe<Scalars['Int']['input']>;
};

export type SolarThingSolcastQuery = {
  __typename?: 'SolarThingSolcastQuery';
  queryDailyEnergyEstimates: Array<DailyEnergy>;
  queryEstimateActuals: Array<SimpleEstimatedActual>;
  queryForecasts: Array<Forecast>;
};


export type SolarThingSolcastQueryqueryForecastsArgs = {
  includePast?: InputMaybe<Scalars['Boolean']['input']>;
};

export type SolarThingStatusQuery = {
  __typename?: 'SolarThingStatusQuery';
  batteryVoltage: Array<PacketNode_BatteryVoltage>;
  batteryVoltageAverage: Array<SimpleNode_Float>;
  batteryVoltageTemperatureCompensated: Array<DataNode_Float>;
  chargeController: Array<PacketNode_BasicChargeController>;
  cpuTemperature: Array<PacketNode_CpuTemperaturePacket>;
  dailyChargeController: Array<PacketNode_DailyChargeController>;
  dualTemperature: Array<PacketNode_DualTemperature>;
  flatData: Array<SimpleNode_FlatData>;
  fxDaily: Array<PacketNode_DailyFXPacket>;
  fxStatus: Array<PacketNode_FXStatusPacket>;
  mxStatus: Array<PacketNode_MXStatusPacket>;
  pzemShuntStatus: Array<PacketNode_PzemShuntStatusPacket>;
  roverStatus: Array<PacketNode_RoverStatusPacket>;
  solar: Array<PacketNode_PVCurrentAndVoltage>;
  solarDevice: Array<PacketNode_SolarDevice>;
  temperature: Array<PacketNode_TemperaturePacket>;
  tracerStatus: Array<PacketNode_TracerStatusPacket>;
};

export type SpecialPowerControl_E02D = {
  __typename?: 'SpecialPowerControl_E02D';
  batteryType?: Maybe<BatteryType>;
  batteryTypeValueCode: Scalars['Int']['output'];
  chargingMethod?: Maybe<ChargingMethod_E02D>;
  chargingMethodValueCode: Scalars['Int']['output'];
  combined: Scalars['Int']['output'];
  combinedShort: Scalars['Short']['output'];
  formattedInfo?: Maybe<Scalars['String']['output']>;
  is24VSystem: Scalars['Boolean']['output'];
  isEachNightOnEnabled: Scalars['Boolean']['output'];
  isIntelligentPowerEnabled: Scalars['Boolean']['output'];
  isLithiumBattery: Scalars['Boolean']['output'];
  isNoChargingBelow0CEnabled: Scalars['Boolean']['output'];
  lower: Scalars['Int']['output'];
  rawChargingMethodValueCode: Scalars['Int']['output'];
  systemVoltage?: Maybe<SystemVoltage>;
  systemVoltageValueCode: Scalars['Int']['output'];
  upper: Scalars['Int']['output'];
};

export type SpecialPowerControl_E021 = {
  __typename?: 'SpecialPowerControl_E021';
  chargingMethod?: Maybe<ChargingMethod_E021>;
  chargingMethodValueCode: Scalars['Int']['output'];
  combined: Scalars['Int']['output'];
  combinedShort: Scalars['Short']['output'];
  formattedInfo?: Maybe<Scalars['String']['output']>;
  isChargingModeControlledByVoltage: Scalars['Boolean']['output'];
  isEachNightOnEnabled: Scalars['Boolean']['output'];
  isNoChargingBelow0CEnabled: Scalars['Boolean']['output'];
  isSpecialPowerControlEnabled: Scalars['Boolean']['output'];
  lower: Scalars['Int']['output'];
  upper: Scalars['Int']['output'];
};

export type SuccessMateCommandPacket = {
  __typename?: 'SuccessMateCommandPacket';
  command: MateCommand;
  commandName: Scalars['String']['output'];
  executionReason?: Maybe<ExecutionReason>;
  packetType: MateCommandFeedbackPacketType;
  packetVersion?: Maybe<Scalars['Int']['output']>;
  source: Scalars['String']['output'];
};

export enum Support {
  FULLY_SUPPORTED = 'FULLY_SUPPORTED',
  NOT_SUPPORTED = 'NOT_SUPPORTED',
  UNKNOWN = 'UNKNOWN'
}

export enum SystemVoltage {
  V12 = 'V12',
  V24 = 'V24'
}

export enum TargetedMetaPacketType {
  DATA_INFO = 'DATA_INFO',
  DEVICE_INFO = 'DEVICE_INFO',
  FX_CHARGING_SETTINGS = 'FX_CHARGING_SETTINGS',
  FX_CHARGING_TEMPERATURE_ADJUST = 'FX_CHARGING_TEMPERATURE_ADJUST'
}

export type TemperaturePacket = {
  __typename?: 'TemperaturePacket';
  dataId: Scalars['Int']['output'];
  dataIdString: Scalars['String']['output'];
  identifier: DataIdentifier;
  identityInfo: IdentityInfo;
  packetType: WeatherPacketType;
  source: DeviceSource;
  temperatureCelsius: Scalars['Float']['output'];
  temperatureFahrenheit: Scalars['Float']['output'];
};

export enum TracerBatteryTemperatureStatus {
  LOW_TEMPERATURE = 'LOW_TEMPERATURE',
  NORMAL = 'NORMAL',
  OVER_TEMPERATURE = 'OVER_TEMPERATURE'
}

export enum TracerBatteryType {
  FLOODED = 'FLOODED',
  GEL = 'GEL',
  SEALED = 'SEALED',
  USER = 'USER'
}

export enum TracerBatteryVoltageStatus {
  FAULT = 'FAULT',
  LOW_VOLTAGE_DISCONNECT = 'LOW_VOLTAGE_DISCONNECT',
  NORMAL = 'NORMAL',
  OVER_VOLTAGE = 'OVER_VOLTAGE',
  UNDER_VOLTAGE = 'UNDER_VOLTAGE'
}

export enum TracerChargingType {
  MPPT = 'MPPT',
  PWM = 'PWM'
}

export type TracerIdentifier = {
  __typename?: 'TracerIdentifier';
  number: Scalars['Int']['output'];
  /** A string representation of this identifier */
  representation: Scalars['String']['output'];
};

export type TracerStatusPacket = {
  __typename?: 'TracerStatusPacket';
  ambientTemperatureCelsius: Scalars['Float']['output'];
  batteryCapacityAmpHours: Scalars['Int']['output'];
  batteryDetection: BatteryDetection;
  batteryManagementMode: BatteryManagementMode;
  batteryManagementModeValue: Scalars['Int']['output'];
  batteryRatedVoltageCode: Scalars['Int']['output'];
  batterySOC: Scalars['Int']['output'];
  batteryStatusValue: Scalars['Int']['output'];
  batteryTemperatureCelsius: Scalars['Float']['output'];
  batteryTemperatureCelsius331D: Scalars['Float']['output'];
  batteryTemperatureFahrenheit: Scalars['Float']['output'];
  batteryTemperatureStatus: TracerBatteryTemperatureStatus;
  batteryTemperatureWarningLowerLimit: Scalars['Float']['output'];
  batteryTemperatureWarningUpperLimit: Scalars['Float']['output'];
  batteryType: TracerBatteryType;
  batteryTypeValue: Scalars['Int']['output'];
  /** The battery voltage */
  batteryVoltage: Scalars['Float']['output'];
  batteryVoltageStatus: TracerBatteryVoltageStatus;
  boostDurationMinutes: Scalars['Int']['output'];
  boostReconnectVoltage: Scalars['Float']['output'];
  boostVoltage: Scalars['Float']['output'];
  carbonDioxideReductionTons: Scalars['Float']['output'];
  chargingCurrent: Scalars['Float']['output'];
  chargingEquipmentStatusValue: Scalars['Int']['output'];
  chargingLimitVoltage: Scalars['Float']['output'];
  chargingMode: ChargingStatus;
  chargingPercentage: Scalars['Int']['output'];
  chargingPower: Scalars['Float']['output'];
  chargingStatus: ChargingStatus;
  chargingStatusName: Scalars['String']['output'];
  chargingType: TracerChargingType;
  chargingTypeValue: Scalars['Int']['output'];
  clockMonthDay: MonthDay;
  clockSolarThing: Scalars['LocalDateTime']['output'];
  clockTime: Scalars['LocalTime']['output'];
  clockYearNumber: Scalars['Int']['output'];
  controllerTemperatureCelsius: Scalars['Float']['output'];
  controllerTemperatureFahrenheit: Scalars['Float']['output'];
  cumulativeKWH: Scalars['Float']['output'];
  cumulativeKWHConsumption: Scalars['Float']['output'];
  dailyKWH: Scalars['Float']['output'];
  dailyKWHConsumption: Scalars['Float']['output'];
  /** The maximum battery voltage for the day. Note this may reset at a different time compared to min battery voltage for the day. */
  dailyMaxBatteryVoltage: Scalars['Float']['output'];
  dailyMaxInputVoltage: Scalars['Float']['output'];
  /** The minimum battery voltage for the day. Note this may reset at a different time compared to max battery voltage for the day. */
  dailyMinBatteryVoltage: Scalars['Float']['output'];
  dailyMinInputVoltage: Scalars['Float']['output'];
  dayPVVoltageThreshold: Scalars['Float']['output'];
  dischargingLimitVoltage: Scalars['Float']['output'];
  dischargingPercentage: Scalars['Int']['output'];
  equalizationChargingCycleDays: Scalars['Int']['output'];
  equalizationVoltage: Scalars['Float']['output'];
  equalizeDurationMinutes: Scalars['Int']['output'];
  errorModes: Array<ChargingEquipmentError>;
  floatVoltage: Scalars['Float']['output'];
  hasError: Scalars['Boolean']['output'];
  highVoltageDisconnect: Scalars['Float']['output'];
  identifier: TracerIdentifier;
  identityInfo: IdentityInfo;
  inputVoltageStatus: InputVoltageStatus;
  insideControllerTemperatureCelsius: Scalars['Float']['output'];
  insideControllerTemperatureFahrenheit: Scalars['Float']['output'];
  insideControllerTemperatureWarningUpperLimit: Scalars['Float']['output'];
  insideControllerTemperatureWarningUpperLimitRecover: Scalars['Float']['output'];
  isBatteryInternalResistanceAbnormal: Scalars['Boolean']['output'];
  isBatteryWrongIdentificationForRatedVoltage: Scalars['Boolean']['output'];
  isInsideControllerOverTemperature: Scalars['Boolean']['output'];
  isLoadForcedOn: Scalars['Boolean']['output'];
  isLoadOn: Scalars['Boolean']['output'];
  isLoadOnByDefaultInManualMode: Scalars['Boolean']['output'];
  isLoadTestModeEnabled: Scalars['Boolean']['output'];
  isManualLoadControlOn: Scalars['Boolean']['output'];
  isNight: Scalars['Boolean']['output'];
  isRunning: Scalars['Boolean']['output'];
  lengthOfNight: Scalars['Duration']['output'];
  lengthOfNightRaw: Scalars['Int']['output'];
  lightSignalStartupDelayTime: Scalars['Int']['output'];
  lightSignalTurnOffDelayTime: Scalars['Int']['output'];
  lineImpedance: Scalars['Float']['output'];
  loadControlModeValue: Scalars['Int']['output'];
  loadCurrent: Scalars['Float']['output'];
  loadPower: Scalars['Float']['output'];
  loadTimingControlSelection: LoadTimingControlSelection;
  loadTimingControlSelectionValue: Scalars['Int']['output'];
  loadVoltage: Scalars['Float']['output'];
  lowVoltageDisconnect: Scalars['Float']['output'];
  lowVoltageReconnect: Scalars['Float']['output'];
  monthlyKWH: Scalars['Float']['output'];
  monthlyKWHConsumption: Scalars['Float']['output'];
  netBatteryCurrent: Scalars['Float']['output'];
  nightPVVoltageThreshold: Scalars['Float']['output'];
  number: Scalars['Int']['output'];
  overVoltageReconnect: Scalars['Float']['output'];
  packetType: SolarStatusPacketType;
  packetVersion?: Maybe<Scalars['Int']['output']>;
  powerComponentTemperatureCelsius: Scalars['Float']['output'];
  powerComponentTemperatureWarningUpperLimit: Scalars['Float']['output'];
  powerComponentTemperatureWarningUpperLimitRecover: Scalars['Float']['output'];
  pvCurrent: Scalars['Float']['output'];
  pvVoltage: Scalars['Float']['output'];
  pvWattage: Scalars['Float']['output'];
  ratedInputCurrent: Scalars['Int']['output'];
  ratedInputPower: Scalars['Int']['output'];
  ratedInputVoltage: Scalars['Int']['output'];
  ratedLoadOutputCurrent: Scalars['Int']['output'];
  ratedOutputCurrent: Scalars['Int']['output'];
  ratedOutputPower: Scalars['Int']['output'];
  ratedOutputVoltage: Scalars['Int']['output'];
  realBatteryRatedVoltageValue: Scalars['Int']['output'];
  remoteBatteryTemperatureCelsius: Scalars['Float']['output'];
  secondMinuteHourDayMonthYearRaw: Scalars['Long']['output'];
  solarModeName: Scalars['String']['output'];
  solarModeType: SolarModeType;
  solarModeTypeDisplayName: Scalars['String']['output'];
  temperatureCompensationCoefficient: Scalars['Int']['output'];
  turnOffTiming1?: Maybe<Scalars['LocalTime']['output']>;
  turnOffTiming1Raw: Scalars['Long']['output'];
  turnOffTiming2?: Maybe<Scalars['LocalTime']['output']>;
  turnOffTiming2Raw: Scalars['Long']['output'];
  turnOnTiming1?: Maybe<Scalars['LocalTime']['output']>;
  turnOnTiming1Raw: Scalars['Long']['output'];
  turnOnTiming2?: Maybe<Scalars['LocalTime']['output']>;
  turnOnTiming2Raw: Scalars['Long']['output'];
  underVoltageRecover: Scalars['Float']['output'];
  underVoltageWarning: Scalars['Float']['output'];
  workingTimeLength1Raw: Scalars['Int']['output'];
  workingTimeLength2Raw: Scalars['Int']['output'];
  yearlyKWH: Scalars['Float']['output'];
  yearlyKWHConsumption: Scalars['Float']['output'];
};

export type Version = {
  __typename?: 'Version';
  major: Scalars['Int']['output'];
  minor: Scalars['Int']['output'];
  patch: Scalars['Int']['output'];
  representation?: Maybe<Scalars['String']['output']>;
};

export enum WarningMode {
  AC_INPUT_FREQ_HIGH = 'AC_INPUT_FREQ_HIGH',
  AC_INPUT_FREQ_LOW = 'AC_INPUT_FREQ_LOW',
  BUY_AMPS_GT_INPUT_SIZE = 'BUY_AMPS_GT_INPUT_SIZE',
  COMM_ERROR = 'COMM_ERROR',
  FAN_FAILURE = 'FAN_FAILURE',
  INPUT_VAC_HIGH = 'INPUT_VAC_HIGH',
  INPUT_VAC_LOW = 'INPUT_VAC_LOW',
  TEMP_SENSOR_FAILED = 'TEMP_SENSOR_FAILED'
}

export enum WeatherPacketType {
  TEMPERATURE = 'TEMPERATURE'
}

export type AuthorizedQueryVariables = Exact<{ [key: string]: never; }>;


export type AuthorizedQuery = { __typename?: 'Query', authorizedSenders: Array<{ __typename?: 'AuthorizedSender', sender: string, data: { __typename?: 'PermissionObject', publicKey?: string | null } }>, authRequests: Array<{ __typename?: 'SimpleNode_AuthNewSenderPacket', dateMillis: any, data: { __typename?: 'AuthNewSenderPacket', sender: string, publicKey: string } }> };

export type ClassicQueryVariables = Exact<{
  sourceId: Scalars['String']['input'];
  currentTimeMillis: Scalars['Long']['input'];
}>;


export type ClassicQuery = { __typename?: 'Query', queryStatusLast: { __typename?: 'SolarThingStatusQuery', flatData: Array<{ __typename?: 'SimpleNode_FlatData', data: { __typename?: 'FlatData', deviceInfoString: string, batteryVoltage?: number | null, operatingModeString: string, errorsString: string, fx?: { __typename?: 'FlatDataFX', loadWattage: number, acBuyWattage: number, acChargeWattage: number, acMode: ACMode, miscModesString: string, warningsString: string } | null, chargeController?: { __typename?: 'FlatDataChargeController', pvWattage: number, chargerWattage: number } | null } }> } };

export type DatabaseStatusQueryVariables = Exact<{ [key: string]: never; }>;


export type DatabaseStatusQuery = { __typename?: 'Query', systemStatus?: { __typename?: 'DatabaseSystemStatus', status: DatabaseStatus, event: DatabaseStatus, open: DatabaseStatus, closed: DatabaseStatus, alter: DatabaseStatus, cache: DatabaseStatus } | null };

export type HomeQueryVariables = Exact<{
  sourceId: Scalars['String']['input'];
  currentTimeMillis: Scalars['Long']['input'];
}>;


export type HomeQuery = { __typename?: 'Query', queryStatusLast: { __typename?: 'SolarThingStatusQuery', batteryVoltageAverage: Array<{ __typename?: 'SimpleNode_Float', data: number, dateMillis: any }> } };

export type LoginQueryVariables = Exact<{
  username: Scalars['String']['input'];
  password: Scalars['String']['input'];
}>;


export type LoginQuery = { __typename?: 'Query', databaseAuthorize: { __typename?: 'DatabaseAuthorization', cookie: string, url: string, expiresAt: any } };

export type LoginInfoQueryVariables = Exact<{
  authorization?: InputMaybe<DatabaseAuthorizationInput>;
}>;


export type LoginInfoQuery = { __typename?: 'Query', username?: string | null };

export type AddAuthorizedSenderMutationVariables = Exact<{
  sender: Scalars['String']['input'];
  publicKey: Scalars['String']['input'];
  authorization: DatabaseAuthorizationInput;
}>;


export type AddAuthorizedSenderMutation = { __typename?: 'Mutation', addAuthorizedSender: boolean };

export type DeleteAuthorizedSenderMutationVariables = Exact<{
  sender: Scalars['String']['input'];
  authorization: DatabaseAuthorizationInput;
}>;


export type DeleteAuthorizedSenderMutation = { __typename?: 'Mutation', removeAuthorizedSender: boolean };


export const AuthorizedDocument = `
    query Authorized {
  authorizedSenders {
    sender
    data {
      publicKey
    }
  }
  authRequests {
    dateMillis
    data {
      sender
      publicKey
    }
  }
}
    `;
export const useAuthorizedQuery = <
      TData = AuthorizedQuery,
      TError = unknown
    >(
      client: GraphQLClient,
      variables?: AuthorizedQueryVariables,
      options?: UseQueryOptions<AuthorizedQuery, TError, TData>,
      headers?: RequestInit['headers']
    ) =>
    useQuery<AuthorizedQuery, TError, TData>(
      variables === undefined ? ['Authorized'] : ['Authorized', variables],
      fetcher<AuthorizedQuery, AuthorizedQueryVariables>(client, AuthorizedDocument, variables, headers),
      options
    );
export const ClassicDocument = `
    query Classic($sourceId: String!, $currentTimeMillis: Long!) {
  queryStatusLast(sourceId: $sourceId, to: $currentTimeMillis) {
    flatData {
      data {
        deviceInfoString
        batteryVoltage
        operatingModeString
        errorsString
        fx {
          loadWattage
          acBuyWattage
          acChargeWattage
          acMode
          miscModesString
          warningsString
        }
        chargeController {
          pvWattage
          chargerWattage
        }
      }
    }
  }
}
    `;
export const useClassicQuery = <
      TData = ClassicQuery,
      TError = unknown
    >(
      client: GraphQLClient,
      variables: ClassicQueryVariables,
      options?: UseQueryOptions<ClassicQuery, TError, TData>,
      headers?: RequestInit['headers']
    ) =>
    useQuery<ClassicQuery, TError, TData>(
      ['Classic', variables],
      fetcher<ClassicQuery, ClassicQueryVariables>(client, ClassicDocument, variables, headers),
      options
    );
export const DatabaseStatusDocument = `
    query DatabaseStatus {
  systemStatus {
    status: getStatus(type: STATUS)
    event: getStatus(type: EVENT)
    open: getStatus(type: OPEN)
    closed: getStatus(type: CLOSED)
    alter: getStatus(type: ALTER)
    cache: getStatus(type: CACHE)
  }
}
    `;
export const useDatabaseStatusQuery = <
      TData = DatabaseStatusQuery,
      TError = unknown
    >(
      client: GraphQLClient,
      variables?: DatabaseStatusQueryVariables,
      options?: UseQueryOptions<DatabaseStatusQuery, TError, TData>,
      headers?: RequestInit['headers']
    ) =>
    useQuery<DatabaseStatusQuery, TError, TData>(
      variables === undefined ? ['DatabaseStatus'] : ['DatabaseStatus', variables],
      fetcher<DatabaseStatusQuery, DatabaseStatusQueryVariables>(client, DatabaseStatusDocument, variables, headers),
      options
    );
export const HomeDocument = `
    query Home($sourceId: String!, $currentTimeMillis: Long!) {
  queryStatusLast(sourceId: $sourceId, to: $currentTimeMillis) {
    batteryVoltageAverage {
      data
      dateMillis
    }
  }
}
    `;
export const useHomeQuery = <
      TData = HomeQuery,
      TError = unknown
    >(
      client: GraphQLClient,
      variables: HomeQueryVariables,
      options?: UseQueryOptions<HomeQuery, TError, TData>,
      headers?: RequestInit['headers']
    ) =>
    useQuery<HomeQuery, TError, TData>(
      ['Home', variables],
      fetcher<HomeQuery, HomeQueryVariables>(client, HomeDocument, variables, headers),
      options
    );
export const LoginDocument = `
    query Login($username: String!, $password: String!) {
  databaseAuthorize(username: $username, password: $password) {
    cookie
    url
    expiresAt
  }
}
    `;
export const useLoginQuery = <
      TData = LoginQuery,
      TError = unknown
    >(
      client: GraphQLClient,
      variables: LoginQueryVariables,
      options?: UseQueryOptions<LoginQuery, TError, TData>,
      headers?: RequestInit['headers']
    ) =>
    useQuery<LoginQuery, TError, TData>(
      ['Login', variables],
      fetcher<LoginQuery, LoginQueryVariables>(client, LoginDocument, variables, headers),
      options
    );
export const LoginInfoDocument = `
    query LoginInfo($authorization: DatabaseAuthorizationInput) {
  username(authorization: $authorization)
}
    `;
export const useLoginInfoQuery = <
      TData = LoginInfoQuery,
      TError = unknown
    >(
      client: GraphQLClient,
      variables?: LoginInfoQueryVariables,
      options?: UseQueryOptions<LoginInfoQuery, TError, TData>,
      headers?: RequestInit['headers']
    ) =>
    useQuery<LoginInfoQuery, TError, TData>(
      variables === undefined ? ['LoginInfo'] : ['LoginInfo', variables],
      fetcher<LoginInfoQuery, LoginInfoQueryVariables>(client, LoginInfoDocument, variables, headers),
      options
    );
export const AddAuthorizedSenderDocument = `
    mutation AddAuthorizedSender($sender: String!, $publicKey: String!, $authorization: DatabaseAuthorizationInput!) {
  addAuthorizedSender(
    sender: $sender
    publicKey: $publicKey
    authorization: $authorization
  )
}
    `;
export const useAddAuthorizedSenderMutation = <
      TError = unknown,
      TContext = unknown
    >(
      client: GraphQLClient,
      options?: UseMutationOptions<AddAuthorizedSenderMutation, TError, AddAuthorizedSenderMutationVariables, TContext>,
      headers?: RequestInit['headers']
    ) =>
    useMutation<AddAuthorizedSenderMutation, TError, AddAuthorizedSenderMutationVariables, TContext>(
      ['AddAuthorizedSender'],
      (variables?: AddAuthorizedSenderMutationVariables) => fetcher<AddAuthorizedSenderMutation, AddAuthorizedSenderMutationVariables>(client, AddAuthorizedSenderDocument, variables, headers)(),
      options
    );
export const DeleteAuthorizedSenderDocument = `
    mutation DeleteAuthorizedSender($sender: String!, $authorization: DatabaseAuthorizationInput!) {
  removeAuthorizedSender(sender: $sender, authorization: $authorization)
}
    `;
export const useDeleteAuthorizedSenderMutation = <
      TError = unknown,
      TContext = unknown
    >(
      client: GraphQLClient,
      options?: UseMutationOptions<DeleteAuthorizedSenderMutation, TError, DeleteAuthorizedSenderMutationVariables, TContext>,
      headers?: RequestInit['headers']
    ) =>
    useMutation<DeleteAuthorizedSenderMutation, TError, DeleteAuthorizedSenderMutationVariables, TContext>(
      ['DeleteAuthorizedSender'],
      (variables?: DeleteAuthorizedSenderMutationVariables) => fetcher<DeleteAuthorizedSenderMutation, DeleteAuthorizedSenderMutationVariables>(client, DeleteAuthorizedSenderDocument, variables, headers)(),
      options
    );