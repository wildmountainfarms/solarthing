package me.retrodaredevil.solarthing.solar.renogy;

import me.retrodaredevil.solarthing.annotations.UtilityClass;

@UtilityClass
public final class ProductModelUtil {
	private ProductModelUtil(){ throw new UnsupportedOperationException(); }
	/*
	Renogy models:
	RBC30D1S-G1      - Dual Input DC-DC on board battery charger
	RNG-CTRL-RVRPG40 - Renogy Rover 40A Positive Ground
	RCC20RVRE-G1     - Rover Elite 20A (No MES Load)
	ML2440           - SRNE 40 AMP
	RNG-CTRL-ADV30   - Renogy Adventurer 30A (I believe this is only available in the Lithium model)
	RNG-CTRL-WND10   - Renogy Wanderer 10A (but apparently getRatedChargingCurrentValue() returns 20)
	RNG-CTRL-RVR30   - Renogy Rover 30A
	RNG-CTRL-WND30   - Renogy Wanderer 30A
	RNG-CTRL-RVR40   - Renogy Rover 40A (negative ground)
	RNG-CTRL-ROVER60 - Renogy Rover 60A (negative ground)
	 */

	public static boolean isRoverElite(String productModel){
		return productModel.contains("RVRE");
	}
	public static boolean isRover(String productModel){
		return productModel.contains("RVR");
	}
	public static boolean isPositiveGround(String productModel){
		return productModel.contains("PG");
	}
	public static boolean isWanderer(String productModel){
		return productModel.contains("WND");
	}
	public static boolean isDcdc(String productModel) {
		return productModel.startsWith("RBC");
	}
	public static boolean isRenogy(String productModel) {
		return isRover(productModel) || isWanderer(productModel) || isDcdc(productModel) || isRoverElite(productModel)
				|| productModel.contains("RNG") || productModel.contains("RCC");
	}

	/**
	 * @return true if it's a Grape Zenith Solar Charge Controller
	 */
	public static boolean isZenith(String productModel) {
		/*
		(Not confirmed) Examples: "GSMPPTZENITH60", "GS-MPPT-ZENITH-20", "GS-MPPT-ZENITH-40"
		 */
		return productModel.contains("ZENITH");
	}

	/**
	 * @return true if it's a Grape Solar COMET Charge Controller
	 */
	public static boolean isComet(String productModel) {
		// GS-PWM-COMET-40
		return productModel.contains("COMET");
	}
	public static boolean isTrakmax(String productModel) {
		return productModel.contains("TRAKMAX");
	}

	// CHC-MPPT-40BT
	// CHC-MPPT-40BT-CHC-TMBT-01
}
