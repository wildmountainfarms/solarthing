package me.retrodaredevil.solarthing.program.check;

import me.retrodaredevil.io.modbus.handling.ErrorCodeException;
import org.jspecify.annotations.NullMarked;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.BeanDescription;
import tools.jackson.databind.JacksonModule;
import tools.jackson.databind.SerializationConfig;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.ser.BeanPropertyWriter;
import tools.jackson.databind.ser.ValueSerializerModifier;

import java.util.List;
import java.util.stream.Collectors;

@NullMarked
public class ModbusSerializationCatcher extends BeanPropertyWriter {


	public ModbusSerializationCatcher(BeanPropertyWriter base) {
		super(base);
	}

	@Override
	public void serializeAsProperty(Object bean, JsonGenerator gen, SerializationContext prov) throws Exception {
		try {
			System.out.println("Serializing: " + _name);
			super.serializeAsProperty(bean, gen, prov);
		} catch (Exception ex) { // likely an InvocationTargetException, but catch exception in case it is anything else
			final ErrorCodeException actual;
			if (ex instanceof ErrorCodeException exception) {
				actual = exception;
			} else {
				if (ex.getCause() instanceof ErrorCodeException) {
					actual = (ErrorCodeException) ex.getCause();
				} else {
					throw ex;
				}
			}
			gen.writeName(_name);
			gen.writeString("Modbus Exception: " + actual.getExceptionCode());
		}
	}

	public static JacksonModule createModule(Class<?> desiredClass) {
		SimpleModule module = new SimpleModule();
		module.setSerializerModifier(new ValueSerializerModifier() {
			@Override
			public List<BeanPropertyWriter> changeProperties(SerializationConfig config, BeanDescription.Supplier beanDesc, List<BeanPropertyWriter> beanProperties) {
				if (beanDesc.get().getBeanClass() == desiredClass) {
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
