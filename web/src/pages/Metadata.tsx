import styles from './SolarThing.module.css';
import Layout from "../layout/Layout";
import React from "react";
import {
  createArrayNodeStructure,
  createNumberNodeStructure,
  createObjectNodeStructure,
  createObjectUnionNodeStructure
} from "../StructureEdit";

const TIME_NODE_STRUCTURE = createNumberNodeStructure({
  defaultValue: null,
  nullable: true,
  forceDefaultValue: false,
});
const BASIC_META_PACKET_STRUCTURE = createObjectUnionNodeStructure({ // BasicMetaPacket
  key: "packetType",
  objects: {

  }
});
const METADATA_STRUCTURE = createObjectNodeStructure({ // RootMetaPacket
  displayName: "Metadata",
  fields: [
    {
      displayName: "Meta",
      key: "meta", // RootMetaPacket#meta
      optional: false,
      valueStructure: createArrayNodeStructure({
        elementStructure: createObjectNodeStructure({ // TimedMetaCollection
          displayName: "Timed Entry",
          fields: [
            {
              displayName: "Start Time",
              key: "start",
              optional: false,
              valueStructure: TIME_NODE_STRUCTURE,
            },
            {
              displayName: "End Time",
              key: "start",
              optional: false,
              valueStructure: TIME_NODE_STRUCTURE,
            },
            {
              displayName: "Packets",
              key: "packets",
              optional: false,
              valueStructure: createArrayNodeStructure({
                minLength: 1,
                elementStructure: BASIC_META_PACKET_STRUCTURE
              })
            },
          ]
        })
      })
    }
  ]
});

export default function Metadata() {
  const rootMeta = {}; // TODO fetch root meta here
  return (
    <Layout>
      <div className={styles.contentDiv}>
      </div>
    </Layout>
  );
}
