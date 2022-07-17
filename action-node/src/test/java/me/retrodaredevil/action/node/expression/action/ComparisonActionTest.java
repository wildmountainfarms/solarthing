package me.retrodaredevil.action.node.expression.action;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.action.node.expression.ComparisonExpression;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ComparisonActionTest {

	@Test
	void test() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		assertEquals(ComparisonExpression.Operator.GREATER_THAN_OR_EQUAL, mapper.readValue("\">=\"", ComparisonExpression.Operator.class));
	}
}
