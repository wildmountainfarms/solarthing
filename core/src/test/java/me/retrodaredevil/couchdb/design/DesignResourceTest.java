package me.retrodaredevil.couchdb.design;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DesignResourceTest {

	@Test
	void test() {
		for (DesignResource resource : DesignResource.values()) {
			// Confirm that the file exists and can be loaded
			resource.load();
		}
	}
}
