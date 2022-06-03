package me.retrodaredevil.solarthing.program.check;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import me.retrodaredevil.io.modbus.handling.ErrorCodeException;

import java.util.List;
import java.util.stream.Collectors;

public class ModbusSerializationCatcher extends BeanPropertyWriter {


	public ModbusSerializationCatcher(BeanPropertyWriter base) {
		super(base);
	}

	@Override
	public void serializeAsField(Object bean, JsonGenerator gen, SerializerProvider prov) throws Exception {
		try {
			super.serializeAsField(bean, gen, prov);
		} catch (ErrorCodeException ex) {
			gen.writeFieldName(_name);
			gen.writeString("Modbus Exception: " + ex.getExceptionCode());
		}
	}

	public static Module createModule(Class<?> desiredClass) {
		SimpleModule module = new SimpleModule();
		module.setSerializerModifier(new BeanSerializerModifier() {
			@Override
			public List<BeanPropertyWriter> changeProperties(SerializationConfig config, BeanDescription beanDesc, List<BeanPropertyWriter> beanProperties) {
				if (beanDesc.getBeanClass() == desiredClass) {
					return beanProperties.stream()
							.map(ModbusSerializationCatcher::new)
							.collect(Collectors.toList());

				}

				return super.changeProperties(config, beanDesc, beanProperties);
			}
		});
		return module;
	}
}
