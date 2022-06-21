import { GraphQLClient } from 'graphql-request';
import { RequestInit } from 'graphql-request/dist/types.dom';
import { useQuery, useMutation, UseQueryOptions, UseMutationOptions } from 'react-query';
export type Maybe<T> = T | null;
export type InputMaybe<T> = Maybe<T>;
export type Exact<T extends { [key: string]: unknown }> = { [K in keyof T]: T[K] };
export type MakeOptional<T, K extends keyof T> = Omit<T, K> & { [SubKey in K]?: Maybe<T[SubKey]> };
export type MakeMaybe<T, K extends keyof T> = Omit<T, K> & { [SubKey in K]: Maybe<T[SubKey]> };

function fetcher<TData, TVariables>(client: GraphQLClient, query: string, variables?: TVariables, headers?: RequestInit['headers']) {
  return async (): Promise<TData> => client.request<TData, TVariables>(query, variables, headers);
}
/** All built-in and custom scalars, mapped to their actual values */
export type Scalars = {
  ID: string;
  String: string;
  Boolean: boolean;
  Int: number;
  Float: number;
  /** Base64-encoded binary */
  Base64String: any;
  /** Built-in java.math.BigDecimal */
  BigDecimal: any;
  /** Built-in scalar representing an amount of time */
  Duration: any;
  /** Built-in scalar representing an instant in time */
  Instant: any;
  /** Built-in scalar representing a local date-time */
  LocalDateTime: any;
  /** Built-in scalar representing a local time */
  LocalTime: any;
  /** Long type */
  Long: any;
  /** Built-in scalar for dynamic values */
  ObjectScalar: any;
  /** Use SPQR's SchemaPrinter to remove this from SDL */
  UNREPRESENTABLE: any;
};

export enum ACMode {
  AC_DROP = 'AC_DROP',
  AC_USE = 'AC_USE',
  NO_AC = 'NO_AC'
}

export type ActivePeriod = {
  __typename?: 'ActivePeriod';
  active: Scalars['Boolean'];
  activeNow: Scalars['Boolean'];
  packetType: ActivePeriodType;
  uniqueString: Scalars['String'];
};


export type ActivePeriodactiveArgs = {
  dateMillis: Scalars['Long'];
};

export enum ActivePeriodType {
  TIME_RANGE = 'TIME_RANGE'
}

export enum AlterPacketType {
  FLAG = 'FLAG',
  SCHEDULED_COMMAND = 'SCHEDULED_COMMAND'
}

export type AuthorizedSender = {
  __typename?: 'AuthorizedSender';
  data: PermissionObject;
  sender: Scalars['String'];
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
  batteryVoltage: Scalars['Float'];
  chargingCurrent: Scalars['BigDecimal'];
  chargingMode: SolarMode;
  chargingPower: Scalars['BigDecimal'];
  identifier: Identifier;
  identityInfo: IdentityInfo;
  pvCurrent: Scalars['BigDecimal'];
  pvVoltage: Scalars['BigDecimal'];
  pvWattage: Scalars['BigDecimal'];
  solarMode: SolarMode;
  solarModeName: Scalars['String'];
  solarModeType: SolarModeType;
  solarModeTypeDisplayName: Scalars['String'];
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

export type BatteryVoltage = {
  __typename?: 'BatteryVoltage';
  /** The battery voltage */
  batteryVoltage: Scalars['Float'];
  identifier: Identifier;
  identityInfo: IdentityInfo;
};

export type ChargeControllerAccumulationDataCache = {
  __typename?: 'ChargeControllerAccumulationDataCache';
  firstDateMillis?: Maybe<Scalars['Long']>;
  generationKWH: Scalars['Float'];
  identifier: Identifier;
  lastDateMillis?: Maybe<Scalars['Long']>;
  unknownGenerationKWH: Scalars['Float'];
  unknownStartDateMillis?: Maybe<Scalars['Long']>;
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

export type CpuTemperaturePacket = {
  __typename?: 'CpuTemperaturePacket';
  cpuTemperatureCelsius: Scalars['Float'];
  cpuTemperatureFahrenheit: Scalars['Float'];
  identifier: Identifier;
  identityInfo: IdentityInfo;
  packetType: DevicePacketType;
};

export type DailyChargeController = {
  __typename?: 'DailyChargeController';
  dailyAH: Scalars['Int'];
  dailyAHSupport: Support;
  dailyKWH: Scalars['Float'];
  identifier: Identifier;
  identityInfo: IdentityInfo;
  startDateMillis?: Maybe<Scalars['Long']>;
};

export type DailyEnergy = {
  __typename?: 'DailyEnergy';
  dailyKWH: Scalars['Float'];
  dayStart: Scalars['Long'];
};

export type DailyFXPacket = {
  __typename?: 'DailyFXPacket';
  acModeValues: Array<Scalars['Int']>;
  acModes: Array<ACMode>;
  /** The address of the port that this device is plugged in to. If 0, this is plugged in to the mate */
  address: Scalars['Int'];
  buyKWH: Scalars['Float'];
  chargerKWH: Scalars['Float'];
  /** The maximum battery voltage for the day. Note this may reset at a different time compared to min battery voltage for the day. */
  dailyMaxBatteryVoltage: Scalars['Float'];
  /** The minimum battery voltage for the day. Note this may reset at a different time compared to max battery voltage for the day. */
  dailyMinBatteryVoltage: Scalars['Float'];
  /** @deprecated Field no longer supported */
  errorModeValue: Scalars['Int'];
  errorModes: Array<FXErrorMode>;
  hasError: Scalars['Boolean'];
  identifier: KnownSupplementaryIdentifier_OutbackIdentifier;
  identityInfo: IdentityInfo;
  inverterKWH: Scalars['Float'];
  is230V: Scalars['Boolean'];
  isAuxOn: Scalars['Boolean'];
  miscModes: Array<MiscMode>;
  miscValue: Scalars['Int'];
  operationalModeValues: Array<Scalars['Int']>;
  operationalModes: Array<OperationalMode>;
  packetType: SolarExtraPacketType;
  sellKWH: Scalars['Float'];
  startDateMillis?: Maybe<Scalars['Long']>;
  warningModeValue: Scalars['Int'];
  warningModes: Array<WarningMode>;
};

export type DataIdentifier = {
  __typename?: 'DataIdentifier';
  dataId: Scalars['Int'];
  /** A string representation of this identifier */
  representation: Scalars['String'];
};

export type DataMetaPacket = {
  __typename?: 'DataMetaPacket';
  dataId: Scalars['Int'];
  dataIdString: Scalars['String'];
  description: Scalars['String'];
  identifier: DataIdentifier;
  identityInfo: IdentityInfo;
  location: Scalars['String'];
  name: Scalars['String'];
  packetType: TargetedMetaPacketType;
};

export type DataNode_Double = {
  __typename?: 'DataNode_Double';
  data: Scalars['Float'];
  dateMillis: Scalars['Long'];
  fragmentId: Scalars['Int'];
  fragmentIdString?: Maybe<Scalars['String']>;
  identifiable: Identifiable;
  sourceId: Scalars['String'];
};

export type DataNode_FXChargingPacket = {
  __typename?: 'DataNode_FXChargingPacket';
  data: FXChargingPacket;
  dateMillis: Scalars['Long'];
  fragmentId: Scalars['Int'];
  fragmentIdString?: Maybe<Scalars['String']>;
  identifiable: Identifiable;
  sourceId: Scalars['String'];
};

export type DataNode_Float = {
  __typename?: 'DataNode_Float';
  data: Scalars['Float'];
  dateMillis: Scalars['Long'];
  fragmentId: Scalars['Int'];
  fragmentIdString?: Maybe<Scalars['String']>;
  identifiable: Identifiable;
  sourceId: Scalars['String'];
};

export type DatabaseAuthorization = {
  __typename?: 'DatabaseAuthorization';
  cookie: Scalars['String'];
  expiresAt: Scalars['Long'];
  url: Scalars['String'];
};

export type DatabaseAuthorizationInput = {
  cookie: Scalars['String'];
  url: Scalars['String'];
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
  deviceDescription: Scalars['String'];
  deviceLocation: Scalars['String'];
  deviceName: Scalars['String'];
  packetType: TargetedMetaPacketType;
};

export enum DevicePacketType {
  DEVICE_CPU_TEMPERATURE = 'DEVICE_CPU_TEMPERATURE'
}

export type DeviceSource = {
  __typename?: 'DeviceSource';
  name?: Maybe<Scalars['String']>;
};

export type DualTemperature = {
  __typename?: 'DualTemperature';
  batteryTemperatureCelsius: Scalars['BigDecimal'];
  batteryTemperatureFahrenheit: Scalars['Float'];
  controllerTemperatureCelsius: Scalars['BigDecimal'];
  controllerTemperatureFahrenheit: Scalars['Float'];
  identifier: Identifier;
  identityInfo: IdentityInfo;
};

export type ExecutionReason = {
  __typename?: 'ExecutionReason';
  packetType: ExecutionReasonType;
};

export enum ExecutionReasonType {
  SOURCE = 'SOURCE'
}

export type FXACModeChangePacket = {
  __typename?: 'FXACModeChangePacket';
  acMode: ACMode;
  acModeName: Scalars['String'];
  acModeValue: Scalars['Int'];
  /** The address of the port that this device is plugged in to. If 0, this is plugged in to the mate */
  address: Scalars['Int'];
  identifier: KnownSupplementaryIdentifier_OutbackIdentifier;
  identityInfo: IdentityInfo;
  packetType: SolarEventPacketType;
  previousACMode?: Maybe<ACMode>;
  previousACModeName?: Maybe<Scalars['String']>;
  previousACModeValue?: Maybe<Scalars['Int']>;
};

export type FXAuxStateChangePacket = {
  __typename?: 'FXAuxStateChangePacket';
  /** The address of the port that this device is plugged in to. If 0, this is plugged in to the mate */
  address: Scalars['Int'];
  identifier: KnownSupplementaryIdentifier_OutbackIdentifier;
  identityInfo: IdentityInfo;
  isAuxActive: Scalars['Boolean'];
  packetType: SolarEventPacketType;
  wasAuxActive?: Maybe<Scalars['Boolean']>;
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
  address: Scalars['Int'];
  fxChargingMode?: Maybe<FXChargingMode>;
  remainingAbsorbTimeMillis: Scalars['Long'];
  remainingEqualizeTimeMillis: Scalars['Long'];
  remainingFloatTimeMillis: Scalars['Long'];
  totalAbsorbTimeMillis: Scalars['Long'];
  totalEqualizeTimeMillis: Scalars['Long'];
  totalFloatTimeMillis: Scalars['Long'];
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
  address: Scalars['Int'];
  identifier: KnownSupplementaryIdentifier_OutbackIdentifier;
  identityInfo: IdentityInfo;
  operationalMode?: Maybe<OperationalMode>;
  operationalModeName: Scalars['String'];
  operationalModeValue: Scalars['Int'];
  packetType: SolarEventPacketType;
  previousOperationalMode?: Maybe<OperationalMode>;
  previousOperationalModeName?: Maybe<Scalars['String']>;
  previousOperationalModeValue?: Maybe<Scalars['Int']>;
};

/** Status packet for FX devices */
export type FXStatusPacket = {
  __typename?: 'FXStatusPacket';
  acMode: Scalars['Int'];
  acModeName: Scalars['String'];
  /** The address of the port that this device is plugged in to. If 0, this is plugged in to the mate */
  address: Scalars['Int'];
  /** The battery voltage */
  batteryVoltage: Scalars['Float'];
  buyCurrent: Scalars['Float'];
  buyCurrentRaw: Scalars['Int'];
  buyWattage: Scalars['Int'];
  chargerCurrent: Scalars['Float'];
  chargerCurrentRaw: Scalars['Int'];
  chargerWattage: Scalars['Int'];
  chksum: Scalars['Int'];
  errorMode: Scalars['Int'];
  errorModes: Array<FXErrorMode>;
  errorsString: Scalars['String'];
  hasError: Scalars['Boolean'];
  identifier: OutbackIdentifier;
  identityInfo: IdentityInfo;
  inputVoltage: Scalars['Int'];
  inputVoltageRaw: Scalars['Int'];
  inverterCurrent: Scalars['Float'];
  inverterCurrentRaw: Scalars['Int'];
  inverterWattage: Scalars['Int'];
  is230V: Scalars['Boolean'];
  isAuxOn: Scalars['Boolean'];
  misc: Scalars['Int'];
  miscModes: Array<MiscMode>;
  miscModesString: Scalars['String'];
  operatingMode: Scalars['Int'];
  operatingModeName: Scalars['String'];
  operationalMode: OperationalMode;
  outputVoltage: Scalars['Int'];
  outputVoltageRaw: Scalars['Int'];
  packetType: SolarStatusPacketType;
  packetVersion?: Maybe<Scalars['Int']>;
  passThruWattage: Scalars['Int'];
  powerUsageWattage: Scalars['Int'];
  sellCurrent: Scalars['Float'];
  sellCurrentRaw: Scalars['Int'];
  sellWattage: Scalars['Int'];
  solarModeName: Scalars['String'];
  solarModeType: SolarModeType;
  solarModeTypeDisplayName: Scalars['String'];
  warningMode: Scalars['Int'];
  warningModes: Array<WarningMode>;
  warnings: Scalars['String'];
};

export type FlagData = {
  __typename?: 'FlagData';
  activePeriod?: Maybe<ActivePeriod>;
  flagName?: Maybe<Scalars['String']>;
};

export type FlagPacket = {
  __typename?: 'FlagPacket';
  executionReason: ExecutionReason;
  flagData: FlagData;
  packetType: AlterPacketType;
};

export type FlatData = {
  __typename?: 'FlatData';
  batteryVoltage?: Maybe<Scalars['Float']>;
  chargeController?: Maybe<FlatDataChargeController>;
  /** Returns a comma separated string of the names of connected devices */
  deviceInfoString: Scalars['String'];
  /** Returns the errors if there are any, empty string if no errors. Errors are formatted with device first, then error description */
  errorsString: Scalars['String'];
  fx?: Maybe<FlatDataFX>;
  /** Returns a string representing the operating modes of all the devices */
  operatingModeString: Scalars['String'];
};

export type FlatDataChargeController = {
  __typename?: 'FlatDataChargeController';
  chargerWattage: Scalars['Float'];
  pvWattage: Scalars['Float'];
};

export type FlatDataFX = {
  __typename?: 'FlatDataFX';
  /** The buy wattage from the AC being used by the FX(s) */
  acBuyWattage: Scalars['Float'];
  /** The charging wattage from the AC being used by the FX(s) */
  acChargeWattage: Scalars['Float'];
  acMode: ACMode;
  loadWattage: Scalars['Float'];
  miscModesString: Scalars['String'];
  /** Returns the warnings if there are any, empty string if no errors. Errors are formatted with device first, then error description */
  warningsString: Scalars['String'];
};

export type Forecast = {
  __typename?: 'Forecast';
  energyGenerationEstimate: Scalars['Float'];
  period?: Maybe<Scalars['Duration']>;
  period_end?: Maybe<Scalars['Instant']>;
  period_midpoint?: Maybe<Scalars['Instant']>;
  period_start?: Maybe<Scalars['Instant']>;
  pv_estimate: Scalars['Float'];
  pv_estimate10: Scalars['Float'];
  pv_estimate10_watts: Scalars['Float'];
  pv_estimate90: Scalars['Float'];
  pv_estimate90_watts: Scalars['Float'];
  pv_estimate_watts: Scalars['Float'];
};

export type Identifiable = {
  __typename?: 'Identifiable';
  identifier: Identifier;
  identityInfo: IdentityInfo;
};

export type IdentificationCacheDataPacket_ChargeControllerAccumulationDataCache = {
  __typename?: 'IdentificationCacheDataPacket_ChargeControllerAccumulationDataCache';
  _id?: Maybe<Scalars['String']>;
  cacheName: Scalars['String'];
  nodes?: Maybe<Array<Maybe<IdentificationCacheNode_ChargeControllerAccumulationDataCache>>>;
  periodDurationMillis: Scalars['Long'];
  periodStartDateMillis: Scalars['Long'];
  sourceId: Scalars['String'];
};

export type IdentificationCacheNode_ChargeControllerAccumulationDataCache = {
  __typename?: 'IdentificationCacheNode_ChargeControllerAccumulationDataCache';
  data: ChargeControllerAccumulationDataCache;
  fragmentId: Scalars['Int'];
};

/** Contains a representation that is unique across all packets in a particular fragment */
export type Identifier = {
  __typename?: 'Identifier';
  /** A string representation of this identifier */
  representation: Scalars['String'];
};

/** Contains info used to show human readable identifiers */
export type IdentityInfo = {
  __typename?: 'IdentityInfo';
  displayName?: Maybe<Scalars['String']>;
  name: Scalars['String'];
  shortName: Scalars['String'];
  stripExtra?: Maybe<IdentityInfo>;
  suffix: Scalars['String'];
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
  representation: Scalars['String'];
  supplementaryTo: OutbackIdentifier;
};

export type KnownSupplementaryIdentifier_RoverIdentifier = {
  __typename?: 'KnownSupplementaryIdentifier_RoverIdentifier';
  /** A string representation of this identifier */
  representation: Scalars['String'];
  supplementaryTo: RoverIdentifier;
};

export enum LoadTimingControlSelection {
  ONE_TIMER = 'ONE_TIMER',
  TWO_TIMER = 'TWO_TIMER'
}

export type MXAuxModeChangePacket = {
  __typename?: 'MXAuxModeChangePacket';
  /** The address of the port that this device is plugged in to. If 0, this is plugged in to the mate */
  address: Scalars['Int'];
  identifier: KnownSupplementaryIdentifier_OutbackIdentifier;
  identityInfo: IdentityInfo;
  packetType: SolarEventPacketType;
  previousRawAuxModeValue?: Maybe<Scalars['Int']>;
  rawAuxModeValue: Scalars['Int'];
};

export type MXChargerModeChangePacket = {
  __typename?: 'MXChargerModeChangePacket';
  /** The address of the port that this device is plugged in to. If 0, this is plugged in to the mate */
  address: Scalars['Int'];
  chargerModeValue: Scalars['Int'];
  chargingModeName: Scalars['String'];
  identifier: KnownSupplementaryIdentifier_OutbackIdentifier;
  identityInfo: IdentityInfo;
  packetType: SolarEventPacketType;
  previousChargerModeValue?: Maybe<Scalars['Int']>;
  previousChargingModeName?: Maybe<Scalars['String']>;
};

export enum MXErrorMode {
  HIGH_VOC = 'HIGH_VOC',
  SHORTED_BATTERY_SENSOR = 'SHORTED_BATTERY_SENSOR',
  TOO_HOT = 'TOO_HOT'
}

export type MXRawDayEndPacket = {
  __typename?: 'MXRawDayEndPacket';
  /** The address of the port that this device is plugged in to. If 0, this is plugged in to the mate */
  address: Scalars['Int'];
  dailyAH: Scalars['Int'];
  dailyAHSupport: Support;
  dailyKWH: Scalars['Float'];
  identifier: KnownSupplementaryIdentifier_OutbackIdentifier;
  identityInfo: IdentityInfo;
  packetType: SolarEventPacketType;
};

export type MXStatusPacket = {
  __typename?: 'MXStatusPacket';
  /** The address of the port that this device is plugged in to. If 0, this is plugged in to the mate */
  address: Scalars['Int'];
  /** @deprecated Field no longer supported */
  ampChargerCurrent: Scalars['Float'];
  auxBitActive: Scalars['Boolean'];
  auxMode: AuxMode;
  auxModeValue: Scalars['Int'];
  auxModeValueRaw: Scalars['Int'];
  /** The battery voltage */
  batteryVoltage: Scalars['Float'];
  /** @deprecated Field no longer supported */
  chargerCurrent: Scalars['Int'];
  chargerMode: Scalars['Int'];
  chargingCurrent: Scalars['BigDecimal'];
  chargingMode: ChargerMode;
  chargingPower: Scalars['Float'];
  chksum: Scalars['Int'];
  dailyAH: Scalars['Int'];
  dailyAHSupport: Support;
  dailyKWH: Scalars['Float'];
  errorMode: Scalars['Int'];
  errorModes: Array<MXErrorMode>;
  hasError: Scalars['Boolean'];
  identifier: OutbackIdentifier;
  identityInfo: IdentityInfo;
  packetType: SolarStatusPacketType;
  packetVersion?: Maybe<Scalars['Int']>;
  pvCurrent: Scalars['Int'];
  pvVoltage: Scalars['Int'];
  pvWattage: Scalars['Int'];
  solarModeName: Scalars['String'];
  solarModeType: SolarModeType;
  solarModeTypeDisplayName: Scalars['String'];
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
  dayOfMonth: Scalars['Int'];
  month?: Maybe<Month>;
  monthValue: Scalars['Int'];
};

/** Mutation root */
export type Mutation = {
  __typename?: 'Mutation';
  removeAuthorizedSender: Scalars['Boolean'];
};


/** Mutation root */
export type MutationremoveAuthorizedSenderArgs = {
  authorization: DatabaseAuthorizationInput;
  sender: Scalars['String'];
};

export type OperatingSettingBundle = {
  __typename?: 'OperatingSettingBundle';
  durationHours: Scalars['Int'];
  operatingPowerPercentage: Scalars['Int'];
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
  address: Scalars['Int'];
  /** A string representation of this identifier */
  representation: Scalars['String'];
};

export type PVCurrentAndVoltage = {
  __typename?: 'PVCurrentAndVoltage';
  identifier: Identifier;
  identityInfo: IdentityInfo;
  pvCurrent: Scalars['BigDecimal'];
  pvVoltage: Scalars['BigDecimal'];
  pvWattage: Scalars['BigDecimal'];
};

export type PacketNode_BasicChargeController = {
  __typename?: 'PacketNode_BasicChargeController';
  dateMillis: Scalars['Long'];
  fragmentDeviceInfo?: Maybe<DeviceInfoPacket>;
  /** The fragment the packet was from */
  fragmentId: Scalars['Int'];
  fragmentIdString: Scalars['String'];
  packet: BasicChargeController;
  /** The Source ID the packet was from */
  sourceId: Scalars['String'];
};

export type PacketNode_BatteryVoltage = {
  __typename?: 'PacketNode_BatteryVoltage';
  dateMillis: Scalars['Long'];
  fragmentDeviceInfo?: Maybe<DeviceInfoPacket>;
  /** The fragment the packet was from */
  fragmentId: Scalars['Int'];
  fragmentIdString: Scalars['String'];
  packet: BatteryVoltage;
  /** The Source ID the packet was from */
  sourceId: Scalars['String'];
};

export type PacketNode_CpuTemperaturePacket = {
  __typename?: 'PacketNode_CpuTemperaturePacket';
  dateMillis: Scalars['Long'];
  fragmentDeviceInfo?: Maybe<DeviceInfoPacket>;
  /** The fragment the packet was from */
  fragmentId: Scalars['Int'];
  fragmentIdString: Scalars['String'];
  packet: CpuTemperaturePacket;
  /** The Source ID the packet was from */
  sourceId: Scalars['String'];
};

export type PacketNode_DailyChargeController = {
  __typename?: 'PacketNode_DailyChargeController';
  dateMillis: Scalars['Long'];
  fragmentDeviceInfo?: Maybe<DeviceInfoPacket>;
  /** The fragment the packet was from */
  fragmentId: Scalars['Int'];
  fragmentIdString: Scalars['String'];
  packet: DailyChargeController;
  /** The Source ID the packet was from */
  sourceId: Scalars['String'];
};

export type PacketNode_DailyFXPacket = {
  __typename?: 'PacketNode_DailyFXPacket';
  dateMillis: Scalars['Long'];
  fragmentDeviceInfo?: Maybe<DeviceInfoPacket>;
  /** The fragment the packet was from */
  fragmentId: Scalars['Int'];
  fragmentIdString: Scalars['String'];
  packet: DailyFXPacket;
  /** The Source ID the packet was from */
  sourceId: Scalars['String'];
};

export type PacketNode_DualTemperature = {
  __typename?: 'PacketNode_DualTemperature';
  dateMillis: Scalars['Long'];
  fragmentDeviceInfo?: Maybe<DeviceInfoPacket>;
  /** The fragment the packet was from */
  fragmentId: Scalars['Int'];
  fragmentIdString: Scalars['String'];
  packet: DualTemperature;
  /** The Source ID the packet was from */
  sourceId: Scalars['String'];
};

export type PacketNode_FXACModeChangePacket = {
  __typename?: 'PacketNode_FXACModeChangePacket';
  dateMillis: Scalars['Long'];
  fragmentDeviceInfo?: Maybe<DeviceInfoPacket>;
  /** The fragment the packet was from */
  fragmentId: Scalars['Int'];
  fragmentIdString: Scalars['String'];
  packet: FXACModeChangePacket;
  /** The Source ID the packet was from */
  sourceId: Scalars['String'];
};

export type PacketNode_FXAuxStateChangePacket = {
  __typename?: 'PacketNode_FXAuxStateChangePacket';
  dateMillis: Scalars['Long'];
  fragmentDeviceInfo?: Maybe<DeviceInfoPacket>;
  /** The fragment the packet was from */
  fragmentId: Scalars['Int'];
  fragmentIdString: Scalars['String'];
  packet: FXAuxStateChangePacket;
  /** The Source ID the packet was from */
  sourceId: Scalars['String'];
};

export type PacketNode_FXOperationalModeChangePacket = {
  __typename?: 'PacketNode_FXOperationalModeChangePacket';
  dateMillis: Scalars['Long'];
  fragmentDeviceInfo?: Maybe<DeviceInfoPacket>;
  /** The fragment the packet was from */
  fragmentId: Scalars['Int'];
  fragmentIdString: Scalars['String'];
  packet: FXOperationalModeChangePacket;
  /** The Source ID the packet was from */
  sourceId: Scalars['String'];
};

export type PacketNode_FXStatusPacket = {
  __typename?: 'PacketNode_FXStatusPacket';
  dateMillis: Scalars['Long'];
  fragmentDeviceInfo?: Maybe<DeviceInfoPacket>;
  /** The fragment the packet was from */
  fragmentId: Scalars['Int'];
  fragmentIdString: Scalars['String'];
  packet: FXStatusPacket;
  /** The Source ID the packet was from */
  sourceId: Scalars['String'];
};

export type PacketNode_MXAuxModeChangePacket = {
  __typename?: 'PacketNode_MXAuxModeChangePacket';
  dateMillis: Scalars['Long'];
  fragmentDeviceInfo?: Maybe<DeviceInfoPacket>;
  /** The fragment the packet was from */
  fragmentId: Scalars['Int'];
  fragmentIdString: Scalars['String'];
  packet: MXAuxModeChangePacket;
  /** The Source ID the packet was from */
  sourceId: Scalars['String'];
};

export type PacketNode_MXChargerModeChangePacket = {
  __typename?: 'PacketNode_MXChargerModeChangePacket';
  dateMillis: Scalars['Long'];
  fragmentDeviceInfo?: Maybe<DeviceInfoPacket>;
  /** The fragment the packet was from */
  fragmentId: Scalars['Int'];
  fragmentIdString: Scalars['String'];
  packet: MXChargerModeChangePacket;
  /** The Source ID the packet was from */
  sourceId: Scalars['String'];
};

export type PacketNode_MXRawDayEndPacket = {
  __typename?: 'PacketNode_MXRawDayEndPacket';
  dateMillis: Scalars['Long'];
  fragmentDeviceInfo?: Maybe<DeviceInfoPacket>;
  /** The fragment the packet was from */
  fragmentId: Scalars['Int'];
  fragmentIdString: Scalars['String'];
  packet: MXRawDayEndPacket;
  /** The Source ID the packet was from */
  sourceId: Scalars['String'];
};

export type PacketNode_MXStatusPacket = {
  __typename?: 'PacketNode_MXStatusPacket';
  dateMillis: Scalars['Long'];
  fragmentDeviceInfo?: Maybe<DeviceInfoPacket>;
  /** The fragment the packet was from */
  fragmentId: Scalars['Int'];
  fragmentIdString: Scalars['String'];
  packet: MXStatusPacket;
  /** The Source ID the packet was from */
  sourceId: Scalars['String'];
};

export type PacketNode_PVCurrentAndVoltage = {
  __typename?: 'PacketNode_PVCurrentAndVoltage';
  dateMillis: Scalars['Long'];
  fragmentDeviceInfo?: Maybe<DeviceInfoPacket>;
  /** The fragment the packet was from */
  fragmentId: Scalars['Int'];
  fragmentIdString: Scalars['String'];
  packet: PVCurrentAndVoltage;
  /** The Source ID the packet was from */
  sourceId: Scalars['String'];
};

export type PacketNode_PzemShuntStatusPacket = {
  __typename?: 'PacketNode_PzemShuntStatusPacket';
  dateMillis: Scalars['Long'];
  fragmentDeviceInfo?: Maybe<DeviceInfoPacket>;
  /** The fragment the packet was from */
  fragmentId: Scalars['Int'];
  fragmentIdString: Scalars['String'];
  packet: PzemShuntStatusPacket;
  /** The Source ID the packet was from */
  sourceId: Scalars['String'];
};

export type PacketNode_RoverChargingStateChangePacket = {
  __typename?: 'PacketNode_RoverChargingStateChangePacket';
  dateMillis: Scalars['Long'];
  fragmentDeviceInfo?: Maybe<DeviceInfoPacket>;
  /** The fragment the packet was from */
  fragmentId: Scalars['Int'];
  fragmentIdString: Scalars['String'];
  packet: RoverChargingStateChangePacket;
  /** The Source ID the packet was from */
  sourceId: Scalars['String'];
};

export type PacketNode_RoverStatusPacket = {
  __typename?: 'PacketNode_RoverStatusPacket';
  dateMillis: Scalars['Long'];
  fragmentDeviceInfo?: Maybe<DeviceInfoPacket>;
  /** The fragment the packet was from */
  fragmentId: Scalars['Int'];
  fragmentIdString: Scalars['String'];
  packet: RoverStatusPacket;
  /** The Source ID the packet was from */
  sourceId: Scalars['String'];
};

export type PacketNode_SolarDevice = {
  __typename?: 'PacketNode_SolarDevice';
  dateMillis: Scalars['Long'];
  fragmentDeviceInfo?: Maybe<DeviceInfoPacket>;
  /** The fragment the packet was from */
  fragmentId: Scalars['Int'];
  fragmentIdString: Scalars['String'];
  packet: SolarDevice;
  /** The Source ID the packet was from */
  sourceId: Scalars['String'];
};

export type PacketNode_SuccessMateCommandPacket = {
  __typename?: 'PacketNode_SuccessMateCommandPacket';
  dateMillis: Scalars['Long'];
  fragmentDeviceInfo?: Maybe<DeviceInfoPacket>;
  /** The fragment the packet was from */
  fragmentId: Scalars['Int'];
  fragmentIdString: Scalars['String'];
  packet: SuccessMateCommandPacket;
  /** The Source ID the packet was from */
  sourceId: Scalars['String'];
};

export type PacketNode_TemperaturePacket = {
  __typename?: 'PacketNode_TemperaturePacket';
  dateMillis: Scalars['Long'];
  fragmentDeviceInfo?: Maybe<DeviceInfoPacket>;
  /** The fragment the packet was from */
  fragmentId: Scalars['Int'];
  fragmentIdString: Scalars['String'];
  meta?: Maybe<DataMetaPacket>;
  packet: TemperaturePacket;
  /** The Source ID the packet was from */
  sourceId: Scalars['String'];
};

export type PacketNode_TracerStatusPacket = {
  __typename?: 'PacketNode_TracerStatusPacket';
  dateMillis: Scalars['Long'];
  fragmentDeviceInfo?: Maybe<DeviceInfoPacket>;
  /** The fragment the packet was from */
  fragmentId: Scalars['Int'];
  fragmentIdString: Scalars['String'];
  packet: TracerStatusPacket;
  /** The Source ID the packet was from */
  sourceId: Scalars['String'];
};

export type PermissionObject = {
  __typename?: 'PermissionObject';
  /** @deprecated Field no longer supported */
  fragments?: Maybe<Scalars['ObjectScalar']>;
  publicKey?: Maybe<Scalars['String']>;
};

export type PzemShuntStatusPacket = {
  __typename?: 'PzemShuntStatusPacket';
  currentAmps: Scalars['Float'];
  currentValueRaw: Scalars['Int'];
  dataId: Scalars['Int'];
  dataIdString: Scalars['String'];
  energyKWH: Scalars['Float'];
  energyValueRaw: Scalars['Int'];
  energyWattHours: Scalars['Int'];
  highVoltageAlarmStatus: Scalars['Int'];
  identifier: DataIdentifier;
  identityInfo: IdentityInfo;
  isHighVoltageAlarm: Scalars['Boolean'];
  isLowVoltageAlarm: Scalars['Boolean'];
  lowVoltageAlarmStatus: Scalars['Int'];
  modbusAddress: Scalars['Int'];
  packetType: SolarStatusPacketType;
  powerValueRaw: Scalars['Int'];
  powerWatts: Scalars['Float'];
  voltage: Scalars['Float'];
  voltageValueRaw: Scalars['Int'];
};

/** Query root */
export type Query = {
  __typename?: 'Query';
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
  username?: Maybe<Scalars['String']>;
};


/** Query root */
export type QuerydatabaseAuthorizeArgs = {
  password: Scalars['String'];
  username: Scalars['String'];
};


/** Query root */
export type QueryqueryAlterArgs = {
  sourceId: Scalars['String'];
};


/** Query root */
export type QueryqueryBatteryEstimateArgs = {
  duration?: InputMaybe<Scalars['Duration']>;
  sourceId?: InputMaybe<Scalars['String']>;
  to: Scalars['Long'];
};


/** Query root */
export type QueryqueryBatteryRecordArgs = {
  from: Scalars['Long'];
  sourceId?: InputMaybe<Scalars['String']>;
  to: Scalars['Long'];
};


/** Query root */
export type QueryqueryEventArgs = {
  from: Scalars['Long'];
  includeUnknownChangePackets?: InputMaybe<Scalars['Boolean']>;
  sourceId?: InputMaybe<Scalars['String']>;
  to: Scalars['Long'];
};


/** Query root */
export type QueryqueryEventFragmentArgs = {
  fragmentId: Scalars['Int'];
  from: Scalars['Long'];
  includeUnknownChangePackets?: InputMaybe<Scalars['Boolean']>;
  to: Scalars['Long'];
};


/** Query root */
export type QueryqueryEventIdentifierArgs = {
  acceptSupplementary?: InputMaybe<Scalars['Boolean']>;
  fragmentId: Scalars['Int'];
  from: Scalars['Long'];
  identifier: Scalars['String'];
  includeUnknownChangePackets?: InputMaybe<Scalars['Boolean']>;
  to: Scalars['Long'];
};


/** Query root */
export type QueryqueryFXChargingArgs = {
  fragmentId: Scalars['Int'];
  from: Scalars['Long'];
  to: Scalars['Long'];
};


/** Query root */
export type QueryqueryFullDayArgs = {
  from: Scalars['Long'];
  sourceId?: InputMaybe<Scalars['String']>;
  to: Scalars['Long'];
  useCache?: InputMaybe<Scalars['Boolean']>;
};


/** Query root */
export type QueryqueryLongTermMillisArgs = {
  from: Scalars['Long'];
  sourceId?: InputMaybe<Scalars['String']>;
  to: Scalars['Long'];
};


/** Query root */
export type QueryqueryLongTermMonthArgs = {
  month?: InputMaybe<Month>;
  sourceId?: InputMaybe<Scalars['String']>;
  year: Scalars['Int'];
};


/** Query root */
export type QueryquerySolcastArgs = {
  from: Scalars['Long'];
  sourceId: Scalars['String'];
  to: Scalars['Long'];
};


/** Query root */
export type QueryquerySolcastDayArgs = {
  sourceId: Scalars['String'];
  to: Scalars['Long'];
};


/** Query root */
export type QueryqueryStatusArgs = {
  from: Scalars['Long'];
  sourceId?: InputMaybe<Scalars['String']>;
  to: Scalars['Long'];
};


/** Query root */
export type QueryqueryStatusLastArgs = {
  reversed?: InputMaybe<Scalars['Boolean']>;
  sourceId?: InputMaybe<Scalars['String']>;
  to: Scalars['Long'];
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
  chargingStateValue: Scalars['Int'];
  identifier: KnownSupplementaryIdentifier_RoverIdentifier;
  identityInfo: IdentityInfo;
  number: Scalars['Int'];
  packetType: SolarEventPacketType;
  previousChargingMode?: Maybe<ChargingState>;
  previousChargingStateValue?: Maybe<Scalars['Int']>;
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
  number: Scalars['Int'];
  /** A string representation of this identifier */
  representation: Scalars['String'];
};

/** Status packet for Rover and Rover-like devices */
export type RoverStatusPacket = {
  __typename?: 'RoverStatusPacket';
  /** The state of charge of the battery. (A number from 0 to 100). Note this is not usually accurate. */
  batteryCapacitySOC: Scalars['Int'];
  batteryFullChargesCount: Scalars['Int'];
  batteryOverDischargesCount: Scalars['Int'];
  batteryTemperatureCelsius: Scalars['Int'];
  batteryTemperatureFahrenheit: Scalars['Float'];
  batteryTemperatureRaw: Scalars['Int'];
  batteryType?: Maybe<RoverBatteryType>;
  batteryTypeValue: Scalars['Int'];
  /** The battery voltage */
  batteryVoltage: Scalars['Float'];
  boostChargingRecoveryVoltageRaw: Scalars['Int'];
  boostChargingTimeRaw: Scalars['Int'];
  boostChargingVoltageRaw: Scalars['Int'];
  chargingAmpHoursOfBatteryCount: Scalars['Int'];
  chargingCurrent: Scalars['Float'];
  chargingMode: ChargingState;
  chargingPower: Scalars['Int'];
  chargingState: Scalars['Int'];
  chargingVoltageLimitRaw: Scalars['Int'];
  /** The modbus address of the device */
  controllerDeviceAddress: Scalars['Int'];
  controllerTemperatureCelsius: Scalars['Int'];
  controllerTemperatureFahrenheit: Scalars['Float'];
  controllerTemperatureRaw: Scalars['Int'];
  cumulativeKWH: Scalars['Float'];
  cumulativeKWHConsumption: Scalars['Float'];
  dailyAH: Scalars['Int'];
  dailyAHDischarging: Scalars['Int'];
  dailyKWH: Scalars['Float'];
  dailyKWHConsumption: Scalars['Float'];
  /** The maximum battery voltage for the day. Note this may reset at a different time compared to min battery voltage for the day. */
  dailyMaxBatteryVoltage: Scalars['Float'];
  dailyMaxChargingCurrent: Scalars['Float'];
  dailyMaxChargingPower: Scalars['Int'];
  dailyMaxDischargingCurrent: Scalars['Float'];
  dailyMaxDischargingPower: Scalars['Int'];
  /** The minimum battery voltage for the day. Note this may reset at a different time compared to max battery voltage for the day. */
  dailyMinBatteryVoltage: Scalars['Float'];
  dcdcErrorModes: Array<DcdcErrorMode>;
  /** The DcdcErrorMode or an empty list if this is not a DCDC charge controller */
  dcdcErrorModesOrEmpty?: Maybe<Array<Maybe<DcdcErrorMode>>>;
  dischargingAmpHoursOfBatteryCount: Scalars['Int'];
  dischargingLimitVoltageRaw: Scalars['Int'];
  endOfChargeSOC: Scalars['Int'];
  endOfDischargeSOC: Scalars['Int'];
  equalizingChargingIntervalRaw: Scalars['Int'];
  equalizingChargingTimeRaw: Scalars['Int'];
  equalizingChargingVoltageRaw: Scalars['Int'];
  errorMode: Scalars['Int'];
  errorModes: Array<SimpleRoverErrorMode>;
  floatingChargingVoltageRaw: Scalars['Int'];
  generatorCurrent: Scalars['Float'];
  generatorPower: Scalars['Int'];
  generatorVoltage: Scalars['Float'];
  hardwareVersion?: Maybe<Version>;
  hardwareVersionValue: Scalars['Int'];
  hasError: Scalars['Boolean'];
  hasLoad: Scalars['Boolean'];
  identifier: RoverIdentifier;
  identityInfo: IdentityInfo;
  isDcdc: Scalars['Boolean'];
  ledLoadCurrentRaw?: Maybe<Scalars['Int']>;
  ledLoadCurrentSettingRaw: Scalars['Int'];
  /** Usually does not represent any meaningful value */
  lightControlDelayMinutes: Scalars['Int'];
  /** Usually does not represent any meaningful value */
  lightControlVoltage: Scalars['Int'];
  loadCurrent: Scalars['Float'];
  loadPower: Scalars['Int'];
  loadVoltage: Scalars['Float'];
  loadWorkingMode: Scalars['Int'];
  maxVoltage: Scalars['Int'];
  /** Nominal battery capacity in AH. Usually this is not accurate. */
  nominalBatteryCapacity: Scalars['Int'];
  number: Scalars['Int'];
  operatingDaysCount: Scalars['Int'];
  operatingMorningOn?: Maybe<OperatingSettingBundle>;
  operatingStage1?: Maybe<OperatingSettingBundle>;
  operatingStage2?: Maybe<OperatingSettingBundle>;
  operatingStage3?: Maybe<OperatingSettingBundle>;
  overDischargeRecoveryVoltageRaw: Scalars['Int'];
  overDischargeTimeDelaySeconds: Scalars['Int'];
  overDischargeVoltageRaw: Scalars['Int'];
  overVoltageThresholdRaw: Scalars['Int'];
  packetType: SolarStatusPacketType;
  packetVersion?: Maybe<Scalars['Int']>;
  productModelEncoded?: Maybe<Scalars['Base64String']>;
  productModelString: Scalars['String'];
  /** The product serial number. Note that is not always unique as devices' serial numbers can accidentally be reset. */
  productSerialNumber: Scalars['Int'];
  productType: Scalars['Int'];
  pvCurrent: Scalars['Float'];
  pvVoltage: Scalars['Float'];
  pvWattage: Scalars['BigDecimal'];
  ratedChargingCurrent: Scalars['Int'];
  ratedDischargingCurrent: Scalars['Int'];
  recognizedVoltage: Scalars['Int'];
  roverErrorModes: Array<RoverErrorMode>;
  /** The RoverErrorModes or an empty list if this is a DCDC charge controller */
  roverErrorModesOrEmpty?: Maybe<Array<Maybe<RoverErrorMode>>>;
  sensed1?: Maybe<SensingBundle>;
  sensed2?: Maybe<SensingBundle>;
  sensed3?: Maybe<SensingBundle>;
  sensingTimeDelayRaw?: Maybe<Scalars['Int']>;
  softwareVersion: Version;
  softwareVersionValue: Scalars['Int'];
  solarModeName: Scalars['String'];
  solarModeType: SolarModeType;
  solarModeTypeDisplayName: Scalars['String'];
  specialPowerControlE02DRaw?: Maybe<Scalars['Int']>;
  specialPowerControlE021Raw: Scalars['Int'];
  streetLightValue: Scalars['Int'];
  systemVoltageSetting: Scalars['Int'];
  temperatureCompensationFactorRaw: Scalars['Int'];
  underVoltageWarningLevelRaw: Scalars['Int'];
};

export type ScheduledCommandData = {
  __typename?: 'ScheduledCommandData';
  commandName: Scalars['String'];
  scheduledTimeMillis: Scalars['Long'];
  targetFragmentIds: Array<Maybe<Scalars['Int']>>;
};

export type ScheduledCommandPacket = {
  __typename?: 'ScheduledCommandPacket';
  data: ScheduledCommandData;
  executionReason: ExecutionReason;
  packetType: AlterPacketType;
};

export type SensingBundle = {
  __typename?: 'SensingBundle';
  powerWithNoPeopleSensedRaw: Scalars['Int'];
  powerWithPeopleSensedRaw: Scalars['Int'];
  workingHoursRaw: Scalars['Int'];
};

export type SimpleEstimatedActual = {
  __typename?: 'SimpleEstimatedActual';
  energyGenerationEstimate: Scalars['Float'];
  period?: Maybe<Scalars['Duration']>;
  period_end?: Maybe<Scalars['Instant']>;
  period_midpoint?: Maybe<Scalars['Instant']>;
  period_start?: Maybe<Scalars['Instant']>;
  pv_estimate: Scalars['Float'];
  pv_estimate_watts: Scalars['Float'];
};

export type SimpleNode_FlatData = {
  __typename?: 'SimpleNode_FlatData';
  data: FlatData;
  dateMillis: Scalars['Long'];
};

export type SimpleNode_Float = {
  __typename?: 'SimpleNode_Float';
  data: Scalars['Float'];
  dateMillis: Scalars['Long'];
};

export type SimpleRoverErrorMode = {
  __typename?: 'SimpleRoverErrorMode';
  maskValue: Scalars['Int'];
  modeName: Scalars['String'];
};

export type SolarDevice = {
  __typename?: 'SolarDevice';
  identifier: Identifier;
  identityInfo: IdentityInfo;
  solarMode: SolarMode;
  solarModeName: Scalars['String'];
  solarModeType: SolarModeType;
  solarModeTypeDisplayName: Scalars['String'];
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
  modeName: Scalars['String'];
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
  activeFlagStrings: Array<Scalars['String']>;
  activeFlags: Array<FlagPacket>;
  flags: Array<FlagPacket>;
  scheduledCommands: Array<ScheduledCommandPacket>;
};


export type SolarThingAlterQueryactiveFlagsArgs = {
  dateMillis: Scalars['Long'];
};


export type SolarThingAlterQueryflagsArgs = {
  mustBeActive?: InputMaybe<Scalars['Boolean']>;
};

export type SolarThingBatteryEstimate = {
  __typename?: 'SolarThingBatteryEstimate';
  queryEstimate: Array<DataNode_Double>;
};


export type SolarThingBatteryEstimatequeryEstimateArgs = {
  ratio: Scalars['Float'];
};

export type SolarThingBatteryRecordQuery = {
  __typename?: 'SolarThingBatteryRecordQuery';
  averageBatteryVoltage: Array<DataNode_Double>;
};

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
  offset?: InputMaybe<Scalars['Int']>;
};

export type SolarThingSolcastQuery = {
  __typename?: 'SolarThingSolcastQuery';
  queryDailyEnergyEstimates: Array<DailyEnergy>;
  queryEstimateActuals: Array<SimpleEstimatedActual>;
  queryForecasts: Array<Forecast>;
};


export type SolarThingSolcastQueryqueryForecastsArgs = {
  includePast?: InputMaybe<Scalars['Boolean']>;
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

export type SuccessMateCommandPacket = {
  __typename?: 'SuccessMateCommandPacket';
  command: MateCommand;
  commandName: Scalars['String'];
  executionReason?: Maybe<ExecutionReason>;
  packetType: MateCommandFeedbackPacketType;
  packetVersion?: Maybe<Scalars['Int']>;
  source: Scalars['String'];
};

export enum Support {
  FULLY_SUPPORTED = 'FULLY_SUPPORTED',
  NOT_SUPPORTED = 'NOT_SUPPORTED',
  UNKNOWN = 'UNKNOWN'
}

export enum TargetedMetaPacketType {
  DATA_INFO = 'DATA_INFO',
  DEVICE_INFO = 'DEVICE_INFO',
  FX_CHARGING_SETTINGS = 'FX_CHARGING_SETTINGS',
  FX_CHARGING_TEMPERATURE_ADJUST = 'FX_CHARGING_TEMPERATURE_ADJUST'
}

export type TemperaturePacket = {
  __typename?: 'TemperaturePacket';
  dataId: Scalars['Int'];
  dataIdString: Scalars['String'];
  identifier: DataIdentifier;
  identityInfo: IdentityInfo;
  packetType: WeatherPacketType;
  source: DeviceSource;
  temperatureCelsius: Scalars['Float'];
  temperatureFahrenheit: Scalars['Float'];
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
  number: Scalars['Int'];
  /** A string representation of this identifier */
  representation: Scalars['String'];
};

export type TracerStatusPacket = {
  __typename?: 'TracerStatusPacket';
  ambientTemperatureCelsius: Scalars['Float'];
  batteryCapacityAmpHours: Scalars['Int'];
  batteryDetection: BatteryDetection;
  batteryManagementMode: BatteryManagementMode;
  batteryManagementModeValue: Scalars['Int'];
  batteryRatedVoltageCode: Scalars['Int'];
  batterySOC: Scalars['Int'];
  batteryStatusValue: Scalars['Int'];
  batteryTemperatureCelsius: Scalars['Float'];
  batteryTemperatureCelsius331D: Scalars['Float'];
  batteryTemperatureFahrenheit: Scalars['Float'];
  batteryTemperatureStatus: TracerBatteryTemperatureStatus;
  batteryTemperatureWarningLowerLimit: Scalars['Float'];
  batteryTemperatureWarningUpperLimit: Scalars['Float'];
  batteryType: TracerBatteryType;
  batteryTypeValue: Scalars['Int'];
  /** The battery voltage */
  batteryVoltage: Scalars['Float'];
  batteryVoltageStatus: TracerBatteryVoltageStatus;
  boostDurationMinutes: Scalars['Int'];
  boostReconnectVoltage: Scalars['Float'];
  boostVoltage: Scalars['Float'];
  carbonDioxideReductionTons: Scalars['Float'];
  chargingCurrent: Scalars['Float'];
  chargingEquipmentStatusValue: Scalars['Int'];
  chargingLimitVoltage: Scalars['Float'];
  chargingMode: ChargingStatus;
  chargingPercentage: Scalars['Int'];
  chargingPower: Scalars['Float'];
  chargingStatus: ChargingStatus;
  chargingStatusName: Scalars['String'];
  chargingType: TracerChargingType;
  chargingTypeValue: Scalars['Int'];
  clockMonthDay: MonthDay;
  clockSolarThing: Scalars['LocalDateTime'];
  clockTime: Scalars['LocalTime'];
  clockYearNumber: Scalars['Int'];
  controllerTemperatureCelsius: Scalars['Float'];
  controllerTemperatureFahrenheit: Scalars['Float'];
  cumulativeKWH: Scalars['Float'];
  cumulativeKWHConsumption: Scalars['Float'];
  dailyKWH: Scalars['Float'];
  dailyKWHConsumption: Scalars['Float'];
  /** The maximum battery voltage for the day. Note this may reset at a different time compared to min battery voltage for the day. */
  dailyMaxBatteryVoltage: Scalars['Float'];
  dailyMaxInputVoltage: Scalars['Float'];
  /** The minimum battery voltage for the day. Note this may reset at a different time compared to max battery voltage for the day. */
  dailyMinBatteryVoltage: Scalars['Float'];
  dailyMinInputVoltage: Scalars['Float'];
  dayPVVoltageThreshold: Scalars['Float'];
  dischargingLimitVoltage: Scalars['Float'];
  dischargingPercentage: Scalars['Int'];
  equalizationChargingCycleDays: Scalars['Int'];
  equalizationVoltage: Scalars['Float'];
  equalizeDurationMinutes: Scalars['Int'];
  errorModes: Array<ChargingEquipmentError>;
  floatVoltage: Scalars['Float'];
  hasError: Scalars['Boolean'];
  highVoltageDisconnect: Scalars['Float'];
  identifier: TracerIdentifier;
  identityInfo: IdentityInfo;
  inputVoltageStatus: InputVoltageStatus;
  insideControllerTemperatureCelsius: Scalars['Float'];
  insideControllerTemperatureFahrenheit: Scalars['Float'];
  insideControllerTemperatureWarningUpperLimit: Scalars['Float'];
  insideControllerTemperatureWarningUpperLimitRecover: Scalars['Float'];
  isBatteryInternalResistanceAbnormal: Scalars['Boolean'];
  isBatteryWrongIdentificationForRatedVoltage: Scalars['Boolean'];
  isInsideControllerOverTemperature: Scalars['Boolean'];
  isLoadForcedOn: Scalars['Boolean'];
  isLoadOn: Scalars['Boolean'];
  isLoadOnByDefaultInManualMode: Scalars['Boolean'];
  isLoadTestModeEnabled: Scalars['Boolean'];
  isManualLoadControlOn: Scalars['Boolean'];
  isNight: Scalars['Boolean'];
  isRunning: Scalars['Boolean'];
  lengthOfNight: Scalars['Duration'];
  lengthOfNightRaw: Scalars['Int'];
  lightSignalStartupDelayTime: Scalars['Int'];
  lightSignalTurnOffDelayTime: Scalars['Int'];
  lineImpedance: Scalars['Float'];
  loadControlModeValue: Scalars['Int'];
  loadCurrent: Scalars['Float'];
  loadPower: Scalars['Float'];
  loadTimingControlSelection: LoadTimingControlSelection;
  loadTimingControlSelectionValue: Scalars['Int'];
  loadVoltage: Scalars['Float'];
  lowVoltageDisconnect: Scalars['Float'];
  lowVoltageReconnect: Scalars['Float'];
  monthlyKWH: Scalars['Float'];
  monthlyKWHConsumption: Scalars['Float'];
  netBatteryCurrent: Scalars['Float'];
  nightPVVoltageThreshold: Scalars['Float'];
  number: Scalars['Int'];
  overVoltageReconnect: Scalars['Float'];
  packetType: SolarStatusPacketType;
  packetVersion?: Maybe<Scalars['Int']>;
  powerComponentTemperatureCelsius: Scalars['Float'];
  powerComponentTemperatureWarningUpperLimit: Scalars['Float'];
  powerComponentTemperatureWarningUpperLimitRecover: Scalars['Float'];
  pvCurrent: Scalars['Float'];
  pvVoltage: Scalars['Float'];
  pvWattage: Scalars['Float'];
  ratedInputCurrent: Scalars['Int'];
  ratedInputPower: Scalars['Int'];
  ratedInputVoltage: Scalars['Int'];
  ratedLoadOutputCurrent: Scalars['Int'];
  ratedOutputCurrent: Scalars['Int'];
  ratedOutputPower: Scalars['Int'];
  ratedOutputVoltage: Scalars['Int'];
  realBatteryRatedVoltageValue: Scalars['Int'];
  remoteBatteryTemperatureCelsius: Scalars['Float'];
  secondMinuteHourDayMonthYearRaw: Scalars['Long'];
  solarModeName: Scalars['String'];
  solarModeType: SolarModeType;
  solarModeTypeDisplayName: Scalars['String'];
  temperatureCompensationCoefficient: Scalars['Int'];
  turnOffTiming1?: Maybe<Scalars['LocalTime']>;
  turnOffTiming1Raw: Scalars['Long'];
  turnOffTiming2?: Maybe<Scalars['LocalTime']>;
  turnOffTiming2Raw: Scalars['Long'];
  turnOnTiming1?: Maybe<Scalars['LocalTime']>;
  turnOnTiming1Raw: Scalars['Long'];
  turnOnTiming2?: Maybe<Scalars['LocalTime']>;
  turnOnTiming2Raw: Scalars['Long'];
  underVoltageRecover: Scalars['Float'];
  underVoltageWarning: Scalars['Float'];
  workingTimeLength1Raw: Scalars['Int'];
  workingTimeLength2Raw: Scalars['Int'];
  yearlyKWH: Scalars['Float'];
  yearlyKWHConsumption: Scalars['Float'];
};

export type Version = {
  __typename?: 'Version';
  major: Scalars['Int'];
  minor: Scalars['Int'];
  patch: Scalars['Int'];
  representation?: Maybe<Scalars['String']>;
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


export type AuthorizedQuery = { __typename?: 'Query', authorizedSenders: Array<{ __typename?: 'AuthorizedSender', sender: string, data: { __typename?: 'PermissionObject', publicKey?: string | null } }> };

export type ClassicQueryVariables = Exact<{
  sourceId: Scalars['String'];
  currentTimeMillis: Scalars['Long'];
}>;


export type ClassicQuery = { __typename?: 'Query', queryStatusLast: { __typename?: 'SolarThingStatusQuery', flatData: Array<{ __typename?: 'SimpleNode_FlatData', data: { __typename?: 'FlatData', deviceInfoString: string, batteryVoltage?: number | null, operatingModeString: string, errorsString: string, fx?: { __typename?: 'FlatDataFX', loadWattage: number, acBuyWattage: number, acChargeWattage: number, acMode: ACMode, miscModesString: string, warningsString: string } | null, chargeController?: { __typename?: 'FlatDataChargeController', pvWattage: number, chargerWattage: number } | null } }> } };

export type HomeQueryVariables = Exact<{
  sourceId: Scalars['String'];
  currentTimeMillis: Scalars['Long'];
}>;


export type HomeQuery = { __typename?: 'Query', queryStatusLast: { __typename?: 'SolarThingStatusQuery', batteryVoltageAverage: Array<{ __typename?: 'SimpleNode_Float', data: number, dateMillis: any }> } };

export type LoginQueryVariables = Exact<{
  username: Scalars['String'];
  password: Scalars['String'];
}>;


export type LoginQuery = { __typename?: 'Query', databaseAuthorize: { __typename?: 'DatabaseAuthorization', cookie: string, url: string, expiresAt: any } };

export type LoginInfoQueryVariables = Exact<{
  authorization?: InputMaybe<DatabaseAuthorizationInput>;
}>;


export type LoginInfoQuery = { __typename?: 'Query', username?: string | null };

export type DeleteAuthorizedSenderMutationVariables = Exact<{
  sender: Scalars['String'];
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