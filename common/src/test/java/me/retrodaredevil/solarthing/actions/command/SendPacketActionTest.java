package me.retrodaredevil.solarthing.actions.command;

import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class SendPacketActionTest {

	@Test
	void testShutdownCausesInterruptException() {
		AtomicBoolean didInterrupt = new AtomicBoolean(false);

		ExecutorService executorService = Executors.newSingleThreadExecutor();
		executorService.execute(() -> {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				didInterrupt.set(true);
			}
		});
		try {
			// This line doesn't seem to be necessary when running locally, but when running on GitHub actions,
			//   we want to make sure that the action actually began executing.
			Thread.sleep(100);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		assertFalse(didInterrupt.get());
		int numberCancelled = executorService.shutdownNow().size();
		assertEquals(0, numberCancelled); // since it got interrupted, it actually ended so no services were cancelled
		assertTrue(didInterrupt.get());
	}
}
