package me.retrodaredevil.solarthing.rest.integration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest // we use SpringBootTest instead of ContextConfiguration as it configures stuff as similar to the real application
@TestPropertySource("/test.properties")
public class ApplicationTest {
	/*
	This test was created specifically for https://github.com/wildmountainfarms/solarthing/issues/135
	This test is also more general purpose than that. You might think this doesn't do anything, but it does!
	This test is configured to run all the components and create their beans and do all the springy stuff.
	If there's some sort of dependency error, this test will fail.

	In the future if we want to add actual integration tests that actually test stuff, we should likely do that in another file,
	but that's a decision for future me to make
	 */

	@Test
	void test() {
	}
}
