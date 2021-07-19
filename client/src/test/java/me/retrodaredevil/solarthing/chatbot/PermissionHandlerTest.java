package me.retrodaredevil.solarthing.chatbot;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PermissionHandlerTest {


	@Test
	void test() {
		PermissionHandler permissionHandler = new PermissionHandler();
		assertTrue(permissionHandler.permissionMatches("solarthing.command.*", "solarthing.command.1.GEN OFF"));
		assertTrue(permissionHandler.permissionMatches("solarthing.command.*", "solarthing.command"));
		assertTrue(permissionHandler.permissionMatches("solarthing.command.1.*", "solarthing.command.1.GEN OFF"));
		assertFalse(permissionHandler.permissionMatches("solarthing.command.2.*", "solarthing.command.1.GEN OFF"));

		assertTrue(permissionHandler.permissionMatches("solarthing.command.?.GEN OFF", "solarthing.command.1.GEN OFF"));
		assertTrue(permissionHandler.permissionMatches("solarthing.command.*.GEN OFF", "solarthing.command.1.GEN OFF"));
		assertTrue(permissionHandler.permissionMatches("solarthing.command.*.GEN OFF", "solarthing.command.1.something.GEN OFF"));
		assertFalse(permissionHandler.permissionMatches("solarthing.command.?.GEN OFF", "solarthing.command.1.something.GEN OFF"));

		assertTrue(permissionHandler.permissionMatches("a.?.c", "a.b.c"));
		assertFalse(permissionHandler.permissionMatches("a.?.c", "a.b.b.c"));
		assertTrue(permissionHandler.permissionMatches("a.*.c", "a.b.b.c"));

		assertTrue(permissionHandler.permissionMatches("*.b.c", "a.a.a.b.c"));
		assertTrue(permissionHandler.permissionMatches("?.*.b.c", "a.a.a.b.c"));
		assertTrue(permissionHandler.permissionMatches("?.*.b.c.?", "a.a.a.b.c.d"));
	}
	/*
	Undefined cases: "*.?.c", "*.a.*.c"
	 */

}
