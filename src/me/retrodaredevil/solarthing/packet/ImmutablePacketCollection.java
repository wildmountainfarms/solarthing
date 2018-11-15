package me.retrodaredevil.solarthing.packet;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

class ImmutablePacketCollection implements PacketCollection {
	private final List<Packet> packets;
	/** An array that represents the date [year, day, month, hour, minute, second, millisecond] (local time)*/
	private final int[] dateArray;
	/** The UTC date represented in milliseconds */
	private final long dateMillis;
	/** A special field that's used when serializing an object into a couchdb database */
	private final String _id;

	/**
	 * Creates a new PacketCollection
	 * <p>
	 * If packets is mutated after this constructor is called, it will have no effect; it is tolerated
	 * @param packets The packets collection
	 */
	ImmutablePacketCollection(Collection<Packet> packets){
		this.packets = Collections.unmodifiableList(new ArrayList<>(packets));
		final Calendar cal = new GregorianCalendar();
		final int year = cal.get(Calendar.YEAR);
		final int month = cal.get(Calendar.MONTH) + 1; // [1..12]
		final int day = cal.get(Calendar.DAY_OF_MONTH);
		final int hour = cal.get(Calendar.HOUR_OF_DAY);
		final int minute = cal.get(Calendar.MINUTE);
		final int second = cal.get(Calendar.SECOND);
		final int millisecond = cal.get(Calendar.MILLISECOND);
		this.dateArray = new int[] {
				year, month, day,
				hour, minute, second, millisecond
		};
		dateMillis = cal.getTimeInMillis(); // in UTC
		this._id = "" + year + "," + month + "," + day + "," +
				hour + "," + minute + "," + second + "," + Math.random(); // avoid collisions
	}
	ImmutablePacketCollection(JsonObject object){
		final List<Packet> packets = new ArrayList<>();
		for(JsonElement elementPacket : object.getAsJsonArray("packets")){
            packets.add(StatusPackets.createFromJson(elementPacket.getAsJsonObject()));
		}
		this.packets = Collections.unmodifiableList(packets);
		this.dateArray = new int[7];
		JsonArray jsonDateArray = object.getAsJsonArray("dateArray");
		for(int i = 0; i < dateArray.length; i++){
			dateArray[i] = jsonDateArray.get(i).getAsInt();
		}
		this.dateMillis = object.get("dateMillis").getAsLong();
		this._id = object.get("_id").getAsString();
	}

	@Override
	public List<Packet> getPackets() {
		return packets;
	}

	@Override
	public int[] getDateArray() {
		return dateArray;
	}

	@Override
	public long getDateMillis() {
		return dateMillis;
	}

	@Override
	public String getDbId() {
        return _id;
	}
}
