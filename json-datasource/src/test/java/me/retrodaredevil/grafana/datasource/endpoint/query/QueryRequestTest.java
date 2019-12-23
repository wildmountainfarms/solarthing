package me.retrodaredevil.grafana.datasource.endpoint.query;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class QueryRequestTest {
	@Test
	void test() throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		String a = "{\n" +
				"      \"requestId\": \"Q266\",\n" +
				"      \"timezone\": \"\",\n" +
				"      \"panelId\": 2,\n" +
				"      \"dashboardId\": 1,\n" +
				"      \"range\": {\n" +
				"        \"from\": \"2019-12-22T16:26:46.656Z\",\n" +
				"        \"to\": \"2019-12-22T22:26:46.657Z\",\n" +
				"        \"raw\": {\n" +
				"          \"from\": \"now-6h\",\n" +
				"          \"to\": \"now\"\n" +
				"        }\n" +
				"      },\n" +
				"      \"interval\": \"20s\",\n" +
				"      \"intervalMs\": 20000,\n" +
				"      \"targets\": [\n" +
				"        {\n" +
				"          \"data\": null,\n" +
				"          \"target\": \"FX_STATUS\",\n" +
				"          \"refId\": \"A\",\n" +
				"          \"hide\": false,\n" +
				"          \"type\": \"timeseries\"\n" +
				"        }\n" +
				"      ],\n" +
				"      \"maxDataPoints\": 932,\n" +
				"      \"scopedVars\": {\n" +
				"        \"__from\": {\n" +
				"          \"text\": \"1577032006654\",\n" +
				"          \"value\": \"1577032006654\"\n" +
				"        },\n" +
				"        \"__to\": {\n" +
				"          \"text\": \"1577053606654\",\n" +
				"          \"value\": \"1577053606654\"\n" +
				"        },\n" +
				"        \"__interval\": {\n" +
				"          \"text\": \"20s\",\n" +
				"          \"value\": \"20s\"\n" +
				"        },\n" +
				"        \"__interval_ms\": {\n" +
				"          \"text\": \"20000\",\n" +
				"          \"value\": 20000\n" +
				"        }\n" +
				"      },\n" +
				"      \"startTime\": 1577053606664,\n" +
				"      \"rangeRaw\": {\n" +
				"        \"from\": \"now-6h\",\n" +
				"        \"to\": \"now\"\n" +
				"      },\n" +
				"      \"adhocFilters\": []\n" +
				"    }";
		QueryRequest object = mapper.readValue(a, QueryRequest.class);
		System.out.println(object);
	}
}
