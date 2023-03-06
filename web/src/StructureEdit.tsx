import React from "react";

// TODO follow drag and drop tutorial: https://www.freecodecamp.org/news/how-to-add-drag-and-drop-in-react-with-react-beautiful-dnd/


export enum NodeStructureType {
  ARRAY = "ARRAY",
  STRING = "STRING",
  NUMBER = "NUMBER",
  BOOLEAN = "BOOLEAN",
  OBJECT_UNION = "OBJECT_UNION",
  OBJECT = "OBJECT",
}

export interface NodeStructure {
  type: string;

}
export interface ArrayNodeStructure extends NodeStructure {
  elementStructure: NodeStructure;
  minLength?: number;
  maxLength?: number;
}
export interface PrimitiveNodeStructure extends NodeStructure {
  nullable: boolean;
  defaultValue: any;
  /** If true, the property will not be modifiable and will contain whatever {@link defaultValue} has. */
  forceDefaultValue: boolean;
}
export interface StringNodeStructure extends PrimitiveNodeStructure {
  defaultValue: string | null;
}
export interface NumberNodeStructure extends PrimitiveNodeStructure {
  defaultValue: number | null;
}
export interface BooleanNodeStructure extends PrimitiveNodeStructure {
  defaultValue: boolean | null;
}


export interface ObjectUnionNodeStructure extends NodeStructure {
  key: string;
  objects: {[value: string]: ObjectNodeStructure}
}

export interface ObjectNodeStructure extends NodeStructure {
  displayName: string;
  fields: Array<ObjectField>;
}
export interface ObjectField {
  displayName: string;
  key: string;
  valueStructure: NodeStructure;
  optional: boolean;
}

//region NodeStructure creators
type CreateNodeStructureInput<T> = Omit<T, 'type'> & Partial<Pick<T, 'type'>>
export function createArrayNodeStructure(data: CreateNodeStructureInput<ArrayNodeStructure>): ArrayNodeStructure {
  return {...data, "type": NodeStructureType.ARRAY}
}
export function createStringNodeStructure(data: CreateNodeStructureInput<StringNodeStructure>): StringNodeStructure {
  return {...data, "type": NodeStructureType.STRING}
}
export function createNumberNodeStructure(data: CreateNodeStructureInput<NumberNodeStructure>): NumberNodeStructure {
  return {...data, "type": NodeStructureType.NUMBER}
}
export function createBooleanNodeStructure(data: CreateNodeStructureInput<BooleanNodeStructure>): BooleanNodeStructure {
  return {...data, "type": NodeStructureType.BOOLEAN}
}
export function createObjectUnionNodeStructure(data: CreateNodeStructureInput<ObjectUnionNodeStructure>): ObjectUnionNodeStructure {
  return {...data, "type": NodeStructureType.OBJECT_UNION}
}
export function createObjectNodeStructure(data: CreateNodeStructureInput<ObjectNodeStructure>): ObjectNodeStructure {
  return {...data, "type": NodeStructureType.OBJECT}
}
//endregion

function EditNode(jsonData: any, displayName: string) {

}
