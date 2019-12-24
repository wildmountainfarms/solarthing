package me.retrodaredevil.grafana.datasource.endpoint;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@JsonDeserialize(using = Range.Deserializer.class)
public class Range {
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'");
	private final Date from;
	private final Date to;

	public Range(Date from, Date to) {
		this.from = from;
		this.to = to;
	}

	public Date getFrom() {
		return from;
	}

	public Date getTo() {
		return to;
	}

	@Override
	public String toString() {
		return "Range(" +
				"from=" + from +
				", to=" + to +
				')';
	}

	static class Deserializer extends JsonDeserializer<Range> {

		@Override
		public Range deserialize(JsonParser p, DeserializationContext context) throws IOException, JsonProcessingException {
			JsonNode node = p.getCodec().readTree(p);
			String from = node.get("from").asText();
			String to = node.get("to").asText();
			final Date fromDate;
			final Date toDate;
			try {
				fromDate = DATE_FORMAT.parse(from);
				toDate = DATE_FORMAT.parse(to);
			} catch (ParseException e) {
				throw new IOException(e);
			}
			return new Range(fromDate, toDate);
		}
	}
}
