package me.retrodaredevil.solarthing.datasource.endpoint;

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
			String from = node.get("from").asText().replace('T', ' ').replace("Z", "");
			String to = node.get("to").asText().replace('T', ' ').replace("Z", "");
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
			final Date fromDate;
			final Date toDate;
			try {
				fromDate = format.parse(from);
				toDate = format.parse(to);
			} catch (ParseException e) {
				throw new IOException(e);
			}
			return new Range(fromDate, toDate);
		}
	}
}
