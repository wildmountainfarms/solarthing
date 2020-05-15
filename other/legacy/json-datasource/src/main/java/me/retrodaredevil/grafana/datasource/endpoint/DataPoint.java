package me.retrodaredevil.grafana.datasource.endpoint;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.IOException;

@JsonSerialize(using = DataPoint.DataPointSerializer.class)
public class DataPoint {
	@JsonSerialize
	private final double value;
	@JsonSerialize
	private final long timestamp;

	public DataPoint(double value, long timestamp) {
		this.value = value;
		this.timestamp = timestamp;
	}

	static class DataPointSerializer extends JsonSerializer<DataPoint> {

		@Override
		public void serialize(DataPoint value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
			gen.writeArray(new double[] { value.value, value.timestamp }, 0, 2);
		}
	}
}
