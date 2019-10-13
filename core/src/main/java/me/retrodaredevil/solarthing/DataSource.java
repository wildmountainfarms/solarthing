package me.retrodaredevil.solarthing;


/**
 * A helper class to create and parse the source string
 */
public class DataSource {
	private final String sender;
	private final long dateMillis;
	private final String data;
	
	public DataSource(String sender, long dateMillis, String data) {
		this.sender = sender;
		this.dateMillis = dateMillis;
		this.data = data;
	}
	
	/**
	 * NOTE: dateMillis has a radix of 16 while in string form
	 * @param string The string representing the sender, dateMillis, and data
	 * @return The {@link DataSource} represented by {@code string} or null
	 */
	public static DataSource createFromStringOrNull(String string){
		String[] split = string.split("\\|", 3);
		if(split.length != 3){
			return null;
		}
		String sender = split[0];
		String millisString = split[1];
		String data = split[2];
		final long dateMillis;
		try {
			dateMillis = Long.parseLong(millisString, 16);
		} catch(NumberFormatException e){
			return null;
		}
		return new DataSource(sender, dateMillis, data);
	}
	
	@Override
	public String toString() {
		return sender + "|" + Long.toHexString(dateMillis) + "|" + data;
	}
	
	public String getSender() {
		return sender;
	}
	public long getDateMillis() {
		return dateMillis;
	}
	public String getData() {
		return data;
	}
}
