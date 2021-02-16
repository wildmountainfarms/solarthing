package me.retrodaredevil.solarthing.misc.error;

import me.retrodaredevil.solarthing.packets.identification.IdentityInfo;

public class ExceptionErrorIdentityInfo implements IdentityInfo {
	private final ExceptionErrorIdentifier exceptionErrorIdentifier;

	public ExceptionErrorIdentityInfo(ExceptionErrorIdentifier exceptionErrorIdentifier) {
		this.exceptionErrorIdentifier = exceptionErrorIdentifier;
	}

	@Override
	public String getName() {
		return exceptionErrorIdentifier.getExceptionCatchLocationIdentifier();
	}

	@Override
	public String getSuffix() {
		return exceptionErrorIdentifier.getExceptionInstanceIdentifier();
	}

	@Override
	public String getShortName() {
		return "EX";
	}
}
