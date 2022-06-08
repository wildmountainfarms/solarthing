import { GraphQLClient } from 'graphql-request';
import { RequestInit } from 'graphql-request/dist/types.dom';
import { useQuery, UseQueryOptions } from 'react-query';
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

export enum AcMode {
  AcDrop = 'AC_DROP',
  AcUse = 'AC_USE',
  NoAc = 'NO_AC'
}

export type ActivePeriod = {
  __typename?: 'ActivePeriod';
  active: Scalars['Boolean'];
  activeNow: Scalars['Boolean'];
  packetType: ActivePeriodType;
  uniqueString: Scalars['String'];
};


export type ActivePeriodActiveArgs = {
  dateMillis: Scalars['Long'];
};

export enum ActivePeriodType {
  TimeRange = 'TIME_RANGE'
}

export enum AlterPacketType {
  Flag = 'FLAG',
  ScheduledCommand = 'SCHEDULED_COMMAND'
}

export type AuthorizedSender = {
  __typename?: 'AuthorizedSender';
  data: PermissionObject;
  sender: Scalars['String'];
};

export enum AuxMode {
  Disabled = 'DISABLED',
  Diversion = 'DIVERSION',
  ErrorOutput = 'ERROR_OUTPUT',
  Float = 'FLOAT',
  LowBattery = 'LOW_BATTERY',
  Manual = 'MANUAL',
  NightLight = 'NIGHT_LIGHT',
  PvTrigger = 'PV_TRIGGER',
  PwmDiversion = 'PWM_DIVERSION',
  Remote = 'REMOTE',
  VentFan = 'VENT_FAN'
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
  Auto = 'AUTO',
  V12 = 'V12',
  V24 = 'V24'
}

export enum BatteryManagementMode {
  Soc = 'SOC',
  VoltageCompensation = 'VOLTAGE_COMPENSATION'
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
  Absorb = 'ABSORB',
  Bulk = 'BULK',
  Eq = 'EQ',
  Float = 'FLOAT',
  Silent = 'SILENT'
}

export enum ChargingEquipmentError {
  AntiReverseMosfetShort = 'ANTI_REVERSE_MOSFET_SHORT',
  ChargingMosfetShort = 'CHARGING_MOSFET_SHORT',
  ChargingOrAntiReverseMosfetShort = 'CHARGING_OR_ANTI_REVERSE_MOSFET_SHORT',
  Fault = 'FAULT',
  InputOverCurrent = 'INPUT_OVER_CURRENT',
  LoadMosfetShort = 'LOAD_MOSFET_SHORT',
  LoadOverCurrent = 'LOAD_OVER_CURRENT',
  LoadShort = 'LOAD_SHORT',
  PvInputShort = 'PV_INPUT_SHORT'
}

export enum ChargingState {
  Activated = 'ACTIVATED',
  Boost = 'BOOST',
  CurrentLimiting = 'CURRENT_LIMITING',
  Deactivated = 'DEACTIVATED',
  DirectCharge = 'DIRECT_CHARGE',
  Eq = 'EQ',
  Float = 'FLOAT',
  Mppt = 'MPPT'
}

export enum ChargingStatus {
  Boost = 'BOOST',
  Equalization = 'EQUALIZATION',
  Float = 'FLOAT',
  NoCharging = 'NO_CHARGING'
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

export type DailyFxPacket = {
  __typename?: 'DailyFXPacket';
  acModeValues: Array<Scalars['Int']>;
  acModes: Array<AcMode>;
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
  errorModes: Array<FxErrorMode>;
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

export type DataNode_FxChargingPacket = {
  __typename?: 'DataNode_FXChargingPacket';
  data: FxChargingPacket;
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
  url: Scalars['String'];
};

export type DatabaseAuthorizationInput = {
  cookie: Scalars['String'];
  url: Scalars['String'];
};

export enum DcdcErrorMode {
  BackupBatteryOverDischarge = 'BACKUP_BATTERY_OVER_DISCHARGE',
  BackupBatteryOverVoltage = 'BACKUP_BATTERY_OVER_VOLTAGE',
  BackupBatteryUnderVoltage = 'BACKUP_BATTERY_UNDER_VOLTAGE',
  BatteryOverTemperature = 'BATTERY_OVER_TEMPERATURE',
  BatteryTemperatureHaltCharging = 'BATTERY_TEMPERATURE_HALT_CHARGING',
  BmsOvercharge = 'BMS_OVERCHARGE',
  ControllerOverTemperature = 'CONTROLLER_OVER_TEMPERATURE',
  ControllerWarm = 'CONTROLLER_WARM',
  FanAlarm = 'FAN_ALARM',
  GeneratorOverVoltage = 'GENERATOR_OVER_VOLTAGE',
  OverCurrent = 'OVER_CURRENT',
  PvOverVoltage = 'PV_OVER_VOLTAGE',
  PvPowerOverload = 'PV_POWER_OVERLOAD',
  SolarPanelReverselyConnected = 'SOLAR_PANEL_REVERSELY_CONNECTED',
  StartBatteryBackup = 'START_BATTERY_BACKUP',
  UnableToTranslateB22 = 'UNABLE_TO_TRANSLATE_B22',
  UnableToTranslateB23 = 'UNABLE_TO_TRANSLATE_B23'
}

export type DeviceInfoPacket = {
  __typename?: 'DeviceInfoPacket';
  deviceDescription: Scalars['String'];
  deviceLocation: Scalars['String'];
  deviceName: Scalars['String'];
  packetType: TargetedMetaPacketType;
};

export enum DevicePacketType {
  DeviceCpuTemperature = 'DEVICE_CPU_TEMPERATURE'
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
  Source = 'SOURCE'
}

export type FxacModeChangePacket = {
  __typename?: 'FXACModeChangePacket';
  acMode: AcMode;
  acModeName: Scalars['String'];
  acModeValue: Scalars['Int'];
  /** The address of the port that this device is plugged in to. If 0, this is plugged in to the mate */
  address: Scalars['Int'];
  identifier: KnownSupplementaryIdentifier_OutbackIdentifier;
  identityInfo: IdentityInfo;
  packetType: SolarEventPacketType;
  previousACMode?: Maybe<AcMode>;
  previousACModeName?: Maybe<Scalars['String']>;
  previousACModeValue?: Maybe<Scalars['Int']>;
};

export type FxAuxStateChangePacket = {
  __typename?: 'FXAuxStateChangePacket';
  /** The address of the port that this device is plugged in to. If 0, this is plugged in to the mate */
  address: Scalars['Int'];
  identifier: KnownSupplementaryIdentifier_OutbackIdentifier;
  identityInfo: IdentityInfo;
  isAuxActive: Scalars['Boolean'];
  packetType: SolarEventPacketType;
  wasAuxActive?: Maybe<Scalars['Boolean']>;
};

export enum FxChargingMode {
  Absorb = 'ABSORB',
  BulkToAbsorb = 'BULK_TO_ABSORB',
  BulkToEq = 'BULK_TO_EQ',
  Eq = 'EQ',
  Float = 'FLOAT',
  Refloat = 'REFLOAT',
  Silent = 'SILENT'
}

export type FxChargingPacket = {
  __typename?: 'FXChargingPacket';
  /** The address of the port that this device is plugged in to. If 0, this is plugged in to the mate */
  address: Scalars['Int'];
  fxChargingMode?: Maybe<FxChargingMode>;
  remainingAbsorbTimeMillis: Scalars['Long'];
  remainingEqualizeTimeMillis: Scalars['Long'];
  remainingFloatTimeMillis: Scalars['Long'];
  totalAbsorbTimeMillis: Scalars['Long'];
  totalEqualizeTimeMillis: Scalars['Long'];
  totalFloatTimeMillis: Scalars['Long'];
};

export enum FxErrorMode {
  BackFeed = 'BACK_FEED',
  HighBattery = 'HIGH_BATTERY',
  LowBattery = 'LOW_BATTERY',
  LowVacOutput = 'LOW_VAC_OUTPUT',
  OverTemp = 'OVER_TEMP',
  PhaseLoss = 'PHASE_LOSS',
  ShortedOutput = 'SHORTED_OUTPUT',
  StackingError = 'STACKING_ERROR'
}

export type FxOperationalModeChangePacket = {
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
export type FxStatusPacket = {
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
  errorModes: Array<FxErrorMode>;
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
  HighVoltage = 'HIGH_VOLTAGE',
  InputError = 'INPUT_ERROR',
  Normal = 'NORMAL',
  NotConnected = 'NOT_CONNECTED'
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
  OneTimer = 'ONE_TIMER',
  TwoTimer = 'TWO_TIMER'
}

export type MxAuxModeChangePacket = {
  __typename?: 'MXAuxModeChangePacket';
  /** The address of the port that this device is plugged in to. If 0, this is plugged in to the mate */
  address: Scalars['Int'];
  identifier: KnownSupplementaryIdentifier_OutbackIdentifier;
  identityInfo: IdentityInfo;
  packetType: SolarEventPacketType;
  previousRawAuxModeValue?: Maybe<Scalars['Int']>;
  rawAuxModeValue: Scalars['Int'];
};

export type MxChargerModeChangePacket = {
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

export enum MxErrorMode {
  HighVoc = 'HIGH_VOC',
  ShortedBatterySensor = 'SHORTED_BATTERY_SENSOR',
  TooHot = 'TOO_HOT'
}

export type MxRawDayEndPacket = {
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

export type MxStatusPacket = {
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
  errorModes: Array<MxErrorMode>;
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
  AuxOff = 'AUX_OFF',
  AuxOn = 'AUX_ON',
  Drop = 'DROP',
  Off = 'OFF',
  On = 'ON',
  Search = 'SEARCH',
  Use = 'USE'
}

export enum MateCommandFeedbackPacketType {
  MateCommandSuccess = 'MATE_COMMAND_SUCCESS'
}

export enum MiscMode {
  AuxOutputOn = 'AUX_OUTPUT_ON',
  Fx_230VUnit = 'FX_230V_UNIT'
}

export enum Month {
  April = 'APRIL',
  August = 'AUGUST',
  December = 'DECEMBER',
  February = 'FEBRUARY',
  January = 'JANUARY',
  July = 'JULY',
  June = 'JUNE',
  March = 'MARCH',
  May = 'MAY',
  November = 'NOVEMBER',
  October = 'OCTOBER',
  September = 'SEPTEMBER'
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
export type MutationRemoveAuthorizedSenderArgs = {
  authorization: DatabaseAuthorizationInput;
  sender: Scalars['String'];
};

export type OperatingSettingBundle = {
  __typename?: 'OperatingSettingBundle';
  durationHours: Scalars['Int'];
  operatingPowerPercentage: Scalars['Int'];
};

export enum OperationalMode {
  AgsError = 'AGS_ERROR',
  Charge = 'CHARGE',
  ChargerOff = 'CHARGER_OFF',
  ComError = 'COM_ERROR',
  Eq = 'EQ',
  Float = 'FLOAT',
  FxError = 'FX_ERROR',
  InvOff = 'INV_OFF',
  InvOn = 'INV_ON',
  PassThru = 'PASS_THRU',
  Search = 'SEARCH',
  SellEnabled = 'SELL_ENABLED',
  Silent = 'SILENT',
  Support = 'SUPPORT'
}

export type OutbackIdentifier = {
  __typename?: 'OutbackIdentifier';
  /** The address of the port that this device is plugged in to. If 0, this is plugged in to the mate */
  address: Scalars['Int'];
  /** A string representation of this identifier */
  representation: Scalars['String'];
};

export type PvCurrentAndVoltage = {
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

export type PacketNode_DailyFxPacket = {
  __typename?: 'PacketNode_DailyFXPacket';
  dateMillis: Scalars['Long'];
  fragmentDeviceInfo?: Maybe<DeviceInfoPacket>;
  /** The fragment the packet was from */
  fragmentId: Scalars['Int'];
  fragmentIdString: Scalars['String'];
  packet: DailyFxPacket;
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

export type PacketNode_FxacModeChangePacket = {
  __typename?: 'PacketNode_FXACModeChangePacket';
  dateMillis: Scalars['Long'];
  fragmentDeviceInfo?: Maybe<DeviceInfoPacket>;
  /** The fragment the packet was from */
  fragmentId: Scalars['Int'];
  fragmentIdString: Scalars['String'];
  packet: FxacModeChangePacket;
  /** The Source ID the packet was from */
  sourceId: Scalars['String'];
};

export type PacketNode_FxAuxStateChangePacket = {
  __typename?: 'PacketNode_FXAuxStateChangePacket';
  dateMillis: Scalars['Long'];
  fragmentDeviceInfo?: Maybe<DeviceInfoPacket>;
  /** The fragment the packet was from */
  fragmentId: Scalars['Int'];
  fragmentIdString: Scalars['String'];
  packet: FxAuxStateChangePacket;
  /** The Source ID the packet was from */
  sourceId: Scalars['String'];
};

export type PacketNode_FxOperationalModeChangePacket = {
  __typename?: 'PacketNode_FXOperationalModeChangePacket';
  dateMillis: Scalars['Long'];
  fragmentDeviceInfo?: Maybe<DeviceInfoPacket>;
  /** The fragment the packet was from */
  fragmentId: Scalars['Int'];
  fragmentIdString: Scalars['String'];
  packet: FxOperationalModeChangePacket;
  /** The Source ID the packet was from */
  sourceId: Scalars['String'];
};

export type PacketNode_FxStatusPacket = {
  __typename?: 'PacketNode_FXStatusPacket';
  dateMillis: Scalars['Long'];
  fragmentDeviceInfo?: Maybe<DeviceInfoPacket>;
  /** The fragment the packet was from */
  fragmentId: Scalars['Int'];
  fragmentIdString: Scalars['String'];
  packet: FxStatusPacket;
  /** The Source ID the packet was from */
  sourceId: Scalars['String'];
};

export type PacketNode_MxAuxModeChangePacket = {
  __typename?: 'PacketNode_MXAuxModeChangePacket';
  dateMillis: Scalars['Long'];
  fragmentDeviceInfo?: Maybe<DeviceInfoPacket>;
  /** The fragment the packet was from */
  fragmentId: Scalars['Int'];
  fragmentIdString: Scalars['String'];
  packet: MxAuxModeChangePacket;
  /** The Source ID the packet was from */
  sourceId: Scalars['String'];
};

export type PacketNode_MxChargerModeChangePacket = {
  __typename?: 'PacketNode_MXChargerModeChangePacket';
  dateMillis: Scalars['Long'];
  fragmentDeviceInfo?: Maybe<DeviceInfoPacket>;
  /** The fragment the packet was from */
  fragmentId: Scalars['Int'];
  fragmentIdString: Scalars['String'];
  packet: MxChargerModeChangePacket;
  /** The Source ID the packet was from */
  sourceId: Scalars['String'];
};

export type PacketNode_MxRawDayEndPacket = {
  __typename?: 'PacketNode_MXRawDayEndPacket';
  dateMillis: Scalars['Long'];
  fragmentDeviceInfo?: Maybe<DeviceInfoPacket>;
  /** The fragment the packet was from */
  fragmentId: Scalars['Int'];
  fragmentIdString: Scalars['String'];
  packet: MxRawDayEndPacket;
  /** The Source ID the packet was from */
  sourceId: Scalars['String'];
};

export type PacketNode_MxStatusPacket = {
  __typename?: 'PacketNode_MXStatusPacket';
  dateMillis: Scalars['Long'];
  fragmentDeviceInfo?: Maybe<DeviceInfoPacket>;
  /** The fragment the packet was from */
  fragmentId: Scalars['Int'];
  fragmentIdString: Scalars['String'];
  packet: MxStatusPacket;
  /** The Source ID the packet was from */
  sourceId: Scalars['String'];
};

export type PacketNode_PvCurrentAndVoltage = {
  __typename?: 'PacketNode_PVCurrentAndVoltage';
  dateMillis: Scalars['Long'];
  fragmentDeviceInfo?: Maybe<DeviceInfoPacket>;
  /** The fragment the packet was from */
  fragmentId: Scalars['Int'];
  fragmentIdString: Scalars['String'];
  packet: PvCurrentAndVoltage;
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
  queryFXCharging?: Maybe<Array<Maybe<DataNode_FxChargingPacket>>>;
  queryFullDay?: Maybe<SolarThingFullDayStatusQuery>;
  queryLongTermMillis?: Maybe<SolarThingLongTermQuery>;
  queryLongTermMonth?: Maybe<SolarThingLongTermQuery>;
  querySolcast?: Maybe<SolarThingSolcastQuery>;
  querySolcastDay?: Maybe<SolarThingSolcastDayQuery>;
  /** Query status packets in the specified time range. */
  queryStatus: SolarThingStatusQuery;
  /** Query the latest collection of status packets on or before the 'to' timestamp. */
  queryStatusLast: SolarThingStatusQuery;
};


/** Query root */
export type QueryDatabaseAuthorizeArgs = {
  password: Scalars['String'];
  username: Scalars['String'];
};


/** Query root */
export type QueryQueryAlterArgs = {
  sourceId: Scalars['String'];
};


/** Query root */
export type QueryQueryBatteryEstimateArgs = {
  duration?: InputMaybe<Scalars['Duration']>;
  sourceId?: InputMaybe<Scalars['String']>;
  to: Scalars['Long'];
};


/** Query root */
export type QueryQueryBatteryRecordArgs = {
  from: Scalars['Long'];
  sourceId?: InputMaybe<Scalars['String']>;
  to: Scalars['Long'];
};


/** Query root */
export type QueryQueryEventArgs = {
  from: Scalars['Long'];
  includeUnknownChangePackets?: InputMaybe<Scalars['Boolean']>;
  sourceId?: InputMaybe<Scalars['String']>;
  to: Scalars['Long'];
};


/** Query root */
export type QueryQueryEventFragmentArgs = {
  fragmentId: Scalars['Int'];
  from: Scalars['Long'];
  includeUnknownChangePackets?: InputMaybe<Scalars['Boolean']>;
  to: Scalars['Long'];
};


/** Query root */
export type QueryQueryEventIdentifierArgs = {
  acceptSupplementary?: InputMaybe<Scalars['Boolean']>;
  fragmentId: Scalars['Int'];
  from: Scalars['Long'];
  identifier: Scalars['String'];
  includeUnknownChangePackets?: InputMaybe<Scalars['Boolean']>;
  to: Scalars['Long'];
};


/** Query root */
export type QueryQueryFxChargingArgs = {
  fragmentId: Scalars['Int'];
  from: Scalars['Long'];
  to: Scalars['Long'];
};


/** Query root */
export type QueryQueryFullDayArgs = {
  from: Scalars['Long'];
  sourceId?: InputMaybe<Scalars['String']>;
  to: Scalars['Long'];
  useCache?: InputMaybe<Scalars['Boolean']>;
};


/** Query root */
export type QueryQueryLongTermMillisArgs = {
  from: Scalars['Long'];
  sourceId?: InputMaybe<Scalars['String']>;
  to: Scalars['Long'];
};


/** Query root */
export type QueryQueryLongTermMonthArgs = {
  month?: InputMaybe<Month>;
  sourceId?: InputMaybe<Scalars['String']>;
  year: Scalars['Int'];
};


/** Query root */
export type QueryQuerySolcastArgs = {
  from: Scalars['Long'];
  sourceId: Scalars['String'];
  to: Scalars['Long'];
};


/** Query root */
export type QueryQuerySolcastDayArgs = {
  sourceId: Scalars['String'];
  to: Scalars['Long'];
};


/** Query root */
export type QueryQueryStatusArgs = {
  from: Scalars['Long'];
  sourceId?: InputMaybe<Scalars['String']>;
  to: Scalars['Long'];
};


/** Query root */
export type QueryQueryStatusLastArgs = {
  reversed?: InputMaybe<Scalars['Boolean']>;
  sourceId?: InputMaybe<Scalars['String']>;
  to: Scalars['Long'];
};

export enum RoverBatteryType {
  Gel = 'GEL',
  Lithium = 'LITHIUM',
  Lithium_48V = 'LITHIUM_48V',
  Open = 'OPEN',
  Sealed = 'SEALED',
  UserLocked = 'USER_LOCKED',
  UserUnlocked = 'USER_UNLOCKED'
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
  AmbientTempHigh = 'AMBIENT_TEMP_HIGH',
  AntiReverseShort = 'ANTI_REVERSE_SHORT',
  BatteryOverDischarge = 'BATTERY_OVER_DISCHARGE',
  BatteryOverVoltage = 'BATTERY_OVER_VOLTAGE',
  BatteryUnderVoltage = 'BATTERY_UNDER_VOLTAGE',
  ChargeShortCircuit = 'CHARGE_SHORT_CIRCUIT',
  ControllerTempHigh = 'CONTROLLER_TEMP_HIGH',
  LoadOver = 'LOAD_OVER',
  LoadShortCircuit = 'LOAD_SHORT_CIRCUIT',
  PvInputOverpower = 'PV_INPUT_OVERPOWER',
  PvInputSideOverVoltage = 'PV_INPUT_SIDE_OVER_VOLTAGE',
  PvInputSideShortCircuit = 'PV_INPUT_SIDE_SHORT_CIRCUIT',
  SolarPanelCounterCurrent = 'SOLAR_PANEL_COUNTER_CURRENT',
  SolarPanelReverselyConnected = 'SOLAR_PANEL_REVERSELY_CONNECTED',
  SolarPanelWorkingPointOverVoltage = 'SOLAR_PANEL_WORKING_POINT_OVER_VOLTAGE'
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
  FxAcModeChange = 'FX_AC_MODE_CHANGE',
  FxAuxStateChange = 'FX_AUX_STATE_CHANGE',
  /** @deprecated Field no longer supported */
  FxDailyDayEnd = 'FX_DAILY_DAY_END',
  FxErrorModeChange = 'FX_ERROR_MODE_CHANGE',
  FxOperationalModeChange = 'FX_OPERATIONAL_MODE_CHANGE',
  FxWarningModeChange = 'FX_WARNING_MODE_CHANGE',
  MxfmAuxModeChange = 'MXFM_AUX_MODE_CHANGE',
  MxfmChargerModeChange = 'MXFM_CHARGER_MODE_CHANGE',
  /** @deprecated Field no longer supported */
  MxfmDailyDayEnd = 'MXFM_DAILY_DAY_END',
  MxfmErrorModeChange = 'MXFM_ERROR_MODE_CHANGE',
  MxfmRawDayEnd = 'MXFM_RAW_DAY_END',
  RoverChargingStateChange = 'ROVER_CHARGING_STATE_CHANGE',
  RoverErrorModeChange = 'ROVER_ERROR_MODE_CHANGE',
  TracerChargingEquipmentStatusChange = 'TRACER_CHARGING_EQUIPMENT_STATUS_CHANGE'
}

export enum SolarExtraPacketType {
  /** @deprecated Field no longer supported */
  FxCharging = 'FX_CHARGING',
  FxDaily = 'FX_DAILY',
  /** @deprecated Field no longer supported */
  MxfmDaily = 'MXFM_DAILY'
}

export type SolarMode = {
  __typename?: 'SolarMode';
  modeName: Scalars['String'];
  solarModeType: SolarModeType;
};

export enum SolarModeType {
  Absorb = 'ABSORB',
  Bulk = 'BULK',
  BulkAbsorb = 'BULK_ABSORB',
  BulkEqualize = 'BULK_EQUALIZE',
  BulkFloat = 'BULK_FLOAT',
  ChargeControllerOff = 'CHARGE_CONTROLLER_OFF',
  CurrentLimiting = 'CURRENT_LIMITING',
  DirectCharge = 'DIRECT_CHARGE',
  Equalize = 'EQUALIZE',
  Float = 'FLOAT',
  InverterChargerOff = 'INVERTER_CHARGER_OFF',
  InverterOff = 'INVERTER_OFF',
  InverterOn = 'INVERTER_ON',
  InverterPassThru = 'INVERTER_PASS_THRU',
  InverterSearch = 'INVERTER_SEARCH',
  InverterSell = 'INVERTER_SELL',
  InverterSilent = 'INVERTER_SILENT',
  InverterSupport = 'INVERTER_SUPPORT',
  InverterUnknown = 'INVERTER_UNKNOWN'
}

export enum SolarStatusPacketType {
  BatteryVoltageOnly = 'BATTERY_VOLTAGE_ONLY',
  FlexnetDcStatus = 'FLEXNET_DC_STATUS',
  FxStatus = 'FX_STATUS',
  MxfmStatus = 'MXFM_STATUS',
  PzemShunt = 'PZEM_SHUNT',
  RenogyRoverStatus = 'RENOGY_ROVER_STATUS',
  TracerStatus = 'TRACER_STATUS'
}

export type SolarThingAlterQuery = {
  __typename?: 'SolarThingAlterQuery';
  activeFlagStrings: Array<Scalars['String']>;
  activeFlags: Array<FlagPacket>;
  flags: Array<FlagPacket>;
  scheduledCommands: Array<ScheduledCommandPacket>;
};


export type SolarThingAlterQueryActiveFlagsArgs = {
  dateMillis: Scalars['Long'];
};


export type SolarThingAlterQueryFlagsArgs = {
  mustBeActive?: InputMaybe<Scalars['Boolean']>;
};

export type SolarThingBatteryEstimate = {
  __typename?: 'SolarThingBatteryEstimate';
  queryEstimate: Array<DataNode_Double>;
};


export type SolarThingBatteryEstimateQueryEstimateArgs = {
  ratio: Scalars['Float'];
};

export type SolarThingBatteryRecordQuery = {
  __typename?: 'SolarThingBatteryRecordQuery';
  averageBatteryVoltage: Array<DataNode_Double>;
};

export type SolarThingEventQuery = {
  __typename?: 'SolarThingEventQuery';
  fxACModeChange: Array<PacketNode_FxacModeChangePacket>;
  fxAuxStateChange: Array<PacketNode_FxAuxStateChangePacket>;
  fxOperationalModeChange: Array<PacketNode_FxOperationalModeChangePacket>;
  mateCommand: Array<PacketNode_SuccessMateCommandPacket>;
  mxAuxModeChange: Array<PacketNode_MxAuxModeChangePacket>;
  mxChargerModeChange: Array<PacketNode_MxChargerModeChangePacket>;
  mxRawDayEnd: Array<PacketNode_MxRawDayEndPacket>;
  roverChargingStateChange: Array<PacketNode_RoverChargingStateChangePacket>;
};


export type SolarThingEventQueryFxOperationalModeChangeArgs = {
  exclude?: InputMaybe<Array<OperationalMode>>;
  include?: InputMaybe<Array<OperationalMode>>;
};


export type SolarThingEventQueryMateCommandArgs = {
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


export type SolarThingSolcastDayQueryQueryEnergyEstimateArgs = {
  offset?: InputMaybe<Scalars['Int']>;
};

export type SolarThingSolcastQuery = {
  __typename?: 'SolarThingSolcastQuery';
  queryDailyEnergyEstimates: Array<DailyEnergy>;
  queryEstimateActuals: Array<SimpleEstimatedActual>;
  queryForecasts: Array<Forecast>;
};


export type SolarThingSolcastQueryQueryForecastsArgs = {
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
  fxDaily: Array<PacketNode_DailyFxPacket>;
  fxStatus: Array<PacketNode_FxStatusPacket>;
  mxStatus: Array<PacketNode_MxStatusPacket>;
  pzemShuntStatus: Array<PacketNode_PzemShuntStatusPacket>;
  roverStatus: Array<PacketNode_RoverStatusPacket>;
  solar: Array<PacketNode_PvCurrentAndVoltage>;
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
  FullySupported = 'FULLY_SUPPORTED',
  NotSupported = 'NOT_SUPPORTED',
  Unknown = 'UNKNOWN'
}

export enum TargetedMetaPacketType {
  DataInfo = 'DATA_INFO',
  DeviceInfo = 'DEVICE_INFO',
  FxChargingSettings = 'FX_CHARGING_SETTINGS',
  FxChargingTemperatureAdjust = 'FX_CHARGING_TEMPERATURE_ADJUST'
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
  LowTemperature = 'LOW_TEMPERATURE',
  Normal = 'NORMAL',
  OverTemperature = 'OVER_TEMPERATURE'
}

export enum TracerBatteryType {
  Flooded = 'FLOODED',
  Gel = 'GEL',
  Sealed = 'SEALED',
  User = 'USER'
}

export enum TracerBatteryVoltageStatus {
  Fault = 'FAULT',
  LowVoltageDisconnect = 'LOW_VOLTAGE_DISCONNECT',
  Normal = 'NORMAL',
  OverVoltage = 'OVER_VOLTAGE',
  UnderVoltage = 'UNDER_VOLTAGE'
}

export enum TracerChargingType {
  Mppt = 'MPPT',
  Pwm = 'PWM'
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
  AcInputFreqHigh = 'AC_INPUT_FREQ_HIGH',
  AcInputFreqLow = 'AC_INPUT_FREQ_LOW',
  BuyAmpsGtInputSize = 'BUY_AMPS_GT_INPUT_SIZE',
  CommError = 'COMM_ERROR',
  FanFailure = 'FAN_FAILURE',
  InputVacHigh = 'INPUT_VAC_HIGH',
  InputVacLow = 'INPUT_VAC_LOW',
  TempSensorFailed = 'TEMP_SENSOR_FAILED'
}

export enum WeatherPacketType {
  Temperature = 'TEMPERATURE'
}

export type HomeQueryVariables = Exact<{
  sourceId: Scalars['String'];
  currentTimeMillis: Scalars['Long'];
}>;


export type HomeQuery = { __typename?: 'Query', queryStatusLast: { __typename?: 'SolarThingStatusQuery', batteryVoltageAverage: Array<{ __typename?: 'SimpleNode_Float', data: number, dateMillis: any }> } };

export type LoginQueryVariables = Exact<{
  username: Scalars['String'];
  password: Scalars['String'];
}>;


export type LoginQuery = { __typename?: 'Query', databaseAuthorize: { __typename?: 'DatabaseAuthorization', cookie: string, url: string } };


export const HomeDocument = `
    query home($sourceId: String!, $currentTimeMillis: Long!) {
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
      ['home', variables],
      fetcher<HomeQuery, HomeQueryVariables>(client, HomeDocument, variables, headers),
      options
    );
export const LoginDocument = `
    query login($username: String!, $password: String!) {
  databaseAuthorize(username: $username, password: $password) {
    cookie
    url
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
      ['login', variables],
      fetcher<LoginQuery, LoginQueryVariables>(client, LoginDocument, variables, headers),
      options
    );