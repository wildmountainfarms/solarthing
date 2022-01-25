package me.retrodaredevil.solarthing.actions.command;

import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.LinkedAction;
import me.retrodaredevil.action.SimpleAction;
import me.retrodaredevil.solarthing.database.MillisDatabase;
import me.retrodaredevil.solarthing.database.exception.SolarThingDatabaseException;
import me.retrodaredevil.solarthing.database.exception.UpdateConflictSolarThingDatabaseException;
import me.retrodaredevil.solarthing.packets.collection.PacketCollection;
import me.retrodaredevil.solarthing.packets.collection.PacketCollectionCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

/**
 * This action handles the uploading of an {@link PacketCollection} to some {@link MillisDatabase}.
 * While this action is active and not done, it will attempt to asynchronously upload the packet.
 * If the action is ended, uploading is stopped.
 * <p>
 * Note: This is not a "one and done" action. This action will stay active (potentially blocking the sequence of actions in some queue) until
 * the packet is uploaded or this action is ended. This NEVER blocks the thread it is being called on, but {@link #isDone()} will not be true until it is done uploading.
 */
public class SendPacketAction extends SimpleAction implements LinkedAction {
	private static final Logger LOGGER = LoggerFactory.getLogger(SendPacketAction.class);

	private final Supplier<MillisDatabase> millisDatabaseSupplier;
	private final PacketCollectionCreator packetCollectionCreator;
	/** The amount of time in milliseconds to wait between retries. If null, a retry will not happen until the next call to {@link #update()}*/
	private final long retryWaitMillis;
	private final int maxRetries;
	private final Action onSuccessAction;
	private final Action onMaxRetriesAction;

	private volatile Action nextAction = null;

	private final ExecutorService executorService;

	public SendPacketAction(ThreadFactory threadFactory, Supplier<MillisDatabase> millisDatabaseSupplier, PacketCollectionCreator packetCollectionCreator, long retryWaitMillis, int maxRetries, Action onSuccessAction, Action onMaxRetriesAction) {
		super(false);
		executorService = Executors.newSingleThreadExecutor(threadFactory);
		requireNonNull(this.millisDatabaseSupplier = millisDatabaseSupplier);
		requireNonNull(this.packetCollectionCreator = packetCollectionCreator);
		this.retryWaitMillis = retryWaitMillis;
		this.maxRetries = maxRetries;
		requireNonNull(this.onSuccessAction = onSuccessAction);
		requireNonNull(this.onMaxRetriesAction = onMaxRetriesAction);

		if (retryWaitMillis < 0) {
			throw new IllegalArgumentException("retryWaitMillis must be non-negative! retryWaitMillis: " + retryWaitMillis);
		}
		if (maxRetries < 0) {
			throw new IllegalArgumentException("maxRetries must be non-negative! maxRetries: " + maxRetries);
		}
	}

	@Override
	public Action getNextAction() {
		return nextAction;
	}

	@Override
	protected void onStart() {
		super.onStart();
		Instant now = Instant.now();

		MillisDatabase millisDatabase = millisDatabaseSupplier.get();
		PacketCollection packetCollection = packetCollectionCreator.create(now);
		executorService.execute(() -> {
			int retryCounter = 0;
			while (true) {
				boolean success = upload(millisDatabase, packetCollection);
				if (success) {
					nextAction = onSuccessAction;
					break;
				}
				if (retryCounter == maxRetries) {
					nextAction = onMaxRetriesAction;
					LOGGER.info("Reached max retries for packet id: " + packetCollection.getDbId() + " will not try again.");
					break;
				}
				retryCounter++;

				if (retryWaitMillis > 0) {
					try {
						Thread.sleep(retryWaitMillis);
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
						LOGGER.info("A SendPacketAction's sleep was interrupted.");
						// We don't set nextAction to something because we assume someone just called end() on us and won't be using our next action
						break;
					}
				}
			}
			setDone(true);
		});
	}

	@Override
	protected void onEnd(boolean peacefullyEnded) {
		super.onEnd(peacefullyEnded);
		int numberCancelled = executorService.shutdownNow().size();
		if (numberCancelled > 0) {
			LOGGER.info("Ended a SendPacketAction with " + numberCancelled + " tasks not going to be finished.");
		}
	}

	private boolean upload(MillisDatabase millisDatabase, PacketCollection packetCollection) {
		try {
			millisDatabase.uploadPacketCollection(packetCollection, null);
			LOGGER.info("Uploaded packetCollection: " + packetCollection.getDbId());
			return true;
		} catch (UpdateConflictSolarThingDatabaseException e) {
			LOGGER.info("Got update conflict exception. Assuming a previous upload was successful. id: " + packetCollection.getDbId());
			return true;
		} catch (SolarThingDatabaseException e) {
			LOGGER.error("Error while uploading document.", e);
			return false;
		}
	}

}
