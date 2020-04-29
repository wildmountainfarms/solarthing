package me.retrodaredevil.solarthing.misc.error;

import me.retrodaredevil.solarthing.packets.identification.IdentityInfo;

public class ExceptionErrorIdentityInfo implements IdentityInfo {
	private final ExceptionErrorIdentifier exceptionErrorIdentifier;

	public ExceptionErrorIdentityInfo(ExceptionErrorIdentifier exceptionErrorIdentifier) {
		this.exceptionErrorIdentifier = exceptionErrorIdentifier;
	}

	@Override
	public String getDisplayName() {
		return exceptionErrorIdentifier.getExceptionCatchLocationIdentifier() + " " + exceptionErrorIdentifier.getExceptionInstanceIdentifier();
	}
}
