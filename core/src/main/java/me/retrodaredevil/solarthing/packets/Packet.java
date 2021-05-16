package me.retrodaredevil.solarthing.packets;

/**
 * By implementing this, your class should be able to be serialized. At this time, we use Jackson to serialize Packets into JSON, so you should
 * annotate your classes to be serializable by Jackson.
 * <p>
 * It is recommended that you avoid having fields with certain words (below).
 * <p>
 * If you're implementing this class, your subclass should be immutable.
 * @see <a href="https://github.com/influxdata/influxql/blob/master/README.md#keywords">InfluxDB keywords to avoid</a>
 */
public interface Packet {
}
