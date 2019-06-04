package me.retrodaredevil.solarthing.util;

public class CheckSumException extends Exception {
	public CheckSumException(int expectedAmount, int calculatedAmount, String chars){
		super("The chksum wasn't correct! Something must have gone wrong. chars: '" + chars + "'" +
				" chksum: " + expectedAmount + " calculated chksum: " + calculatedAmount);
	}
}
