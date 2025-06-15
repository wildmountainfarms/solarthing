package me.retrodaredevil.solarthing.pvoutput.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Map;

public class FormBodyJacksonConverterFactory extends Converter.Factory {
	private final ObjectMapper mapper;

	public FormBodyJacksonConverterFactory(ObjectMapper mapper) {
		this.mapper = mapper;
	}

	@SuppressWarnings("NullableProblems")
	@Override
	public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
		return (Converter<Object, RequestBody>) value -> {
			JsonNode node = mapper.valueToTree(value);
			if(!node.isObject()){
				throw new IOException("node is not an object!");
			}
			FormBody.Builder formBuilder = new FormBody.Builder();
			ObjectNode object = (ObjectNode) node;
			object.propertyStream().forEach(field -> {
				String key = field.getKey();
				JsonNode fieldNode = field.getValue();
				if(!fieldNode.isValueNode()){
					// TODO should this be an exception or at least a log message?
					System.out.println("key=" + key + " fieldNode is not a value node! fieldNode=" + fieldNode);
				} else {
					formBuilder.add(key, fieldNode.asText());
				}
			});
			return formBuilder.build();
		};
	}
}
