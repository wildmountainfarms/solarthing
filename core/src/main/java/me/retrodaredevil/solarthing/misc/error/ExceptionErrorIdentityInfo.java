package me.retrodaredevil.solarthing.misc.error;

import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.packets.identification.IdentityInfo;

public class ExceptionErrorIdentityInfo implements IdentityInfo {
	private final ExceptionErrorIdentifier exceptionErrorIdentifier;

	public ExceptionErrorIdentityInfo(ExceptionErrorIdentifier exceptionErrorIdentifier) {
		this.exceptionErrorIdentifier = exceptionErrorIdentifier;
	}

	@Override
	public @NotNull String getName() {
		return exceptionErrorIdentifier.getExceptionCatchLocationIdentifier();
	}

	@Override
	public @NotNull String getSuffix() {
		return exceptionErrorIdentifier.getExceptionInstanceIdentifier();
	}

	@Override
	public @NotNull String getShortName() {
		return "EX";
	}
}
