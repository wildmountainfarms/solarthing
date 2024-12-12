package me.retrodaredevil.solarthing.integration;

import me.retrodaredevil.couchdbjava.CouchDbInstance;
import me.retrodaredevil.couchdbjava.exception.CouchDbException;
import me.retrodaredevil.solarthing.SolarThingDatabaseType;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.annotations.UtilityClass;
import me.retrodaredevil.solarthing.program.subprogram.run.couchdb.CouchDbSetupMain;

import java.io.OutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@UtilityClass
public class IntegrationSetup {
	private IntegrationSetup() { throw new UnsupportedOperationException(); }

	public static void setup(CouchDbInstance instance) throws CouchDbException {
		CouchDbSetupMain couchDbSetupMain = new CouchDbSetupMain(instance, new PrintStream(OutputStream.nullOutputStream()), new IntegrationPrompt());
		int status = couchDbSetupMain.doCouchDbSetupMain();
		assertEquals(0, status);
	}

	private static class IntegrationPrompt implements CouchDbSetupMain.Prompt {

		@Override
		public void promptContinue() {
		}

		@Override
		public @Nullable String promptContinueOrCustomCommand() {
			return null;
		}

		@Override
		public @Nullable String promptUserName(SolarThingDatabaseType.UserType userType) {
			return IntegrationUtil.getAuthFor(userType).getUsername();
		}

		@Override
		public @NotNull String promptUserPassword(SolarThingDatabaseType.UserType userType) {
			return IntegrationUtil.getAuthFor(userType).getPassword();
		}
	}
}
