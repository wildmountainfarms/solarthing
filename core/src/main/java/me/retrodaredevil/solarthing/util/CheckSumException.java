package me.retrodaredevil.solarthing.util;

import me.retrodaredevil.solarthing.solar.outback.OutbackUtil;

public class CheckSumException extends Exception {
	public CheckSumException(int expectedAmount, int calculatedAmount, String chars){
		super("The chksum wasn't correct! Something must have gone wrong. chars: '" + OutbackUtil.escapeOutputtedMateData(chars) + "'" +
				" chksum: " + expectedAmount + " calculated chksum: " + calculatedAmount);
	}
}
