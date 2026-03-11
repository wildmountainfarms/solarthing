package me.retrodaredevil.solarthing.misc.error;

import me.retrodaredevil.solarthing.packets.identification.IdentityInfo;
import org.jspecify.annotations.NonNull;

public class ExceptionErrorIdentityInfo implements IdentityInfo {
	private final ExceptionErrorIdentifier exceptionErrorIdentifier;

	public ExceptionErrorIdentityInfo(ExceptionErrorIdentifier exceptionErrorIdentifier) {
		this.exceptionErrorIdentifier = exceptionErrorIdentifier;
	}

	@Override
	public @NonNull String getName() {
		return exceptionErrorIdentifier.getExceptionCatchLocationIdentifier();
	}

	@Override
	public @NonNull String getSuffix() {
		return exceptionErrorIdentifier.getExceptionInstanceIdentifier();
	}

	@Override
	public @NonNull String getShortName() {
		return "EX";
	}
}
