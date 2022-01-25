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
		assertFalse(didInterrupt.get());
		int numberCancelled = executorService.shutdownNow().size();
		assertEquals(0, numberCancelled); // since it got interrupted, it actually ended so no services were cancelled
		assertTrue(didInterrupt.get());
	}
}
