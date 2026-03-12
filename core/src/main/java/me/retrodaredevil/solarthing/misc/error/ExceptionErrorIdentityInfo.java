package me.retrodaredevil.solarthing.misc.error;

import me.retrodaredevil.solarthing.packets.identification.IdentityInfo;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class ExceptionErrorIdentityInfo implements IdentityInfo {
	private final ExceptionErrorIdentifier exceptionErrorIdentifier;

	public ExceptionErrorIdentityInfo(ExceptionErrorIdentifier exceptionErrorIdentifier) {
		this.exceptionErrorIdentifier = exceptionErrorIdentifier;
	}

	// TODO remove NonNull
	@Override
	public @NonNull String getName() {
		return exceptionErrorIdentifier.getExceptionCatchLocationIdentifier();
	}

	// TODO remove NonNull
	@Override
	public @NonNull String getSuffix() {
		return exceptionErrorIdentifier.getExceptionInstanceIdentifier();
	}

	// TODO remove NonNull
	@Override
	public @NonNull String getShortName() {
		return "EX";
	}
}
