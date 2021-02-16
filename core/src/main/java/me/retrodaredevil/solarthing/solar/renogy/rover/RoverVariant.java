package me.retrodaredevil.solarthing.solar.renogy.rover;

import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.solar.renogy.ProductModelUtil;

public enum RoverVariant {
	ROVER,
	ROVER_ELITE,
	WANDERER,
	DCDC,
	TRAKMAX,
	ZENITH,
	COMET,
	ROVER_BOOST,
	;
	public static @Nullable RoverVariant getVariant(String productModel) {
		if (ProductModelUtil.isWanderer(productModel)) {
			return WANDERER;
		}
		if (ProductModelUtil.isDcdc(productModel)) {
			return DCDC;
		}
		if (ProductModelUtil.isComet(productModel)) {
			return COMET;
		}
		if (ProductModelUtil.isTrakmax(productModel)) {
			return TRAKMAX;
		}
		if (ProductModelUtil.isZenith(productModel)) {
			return ZENITH;
		}
		if (ProductModelUtil.isRoverElite(productModel)) {
			return ROVER_ELITE;
		}
		if (ProductModelUtil.isRover(productModel)) {
			return ROVER;
		}
		return null;
	}
}
