package me.retrodaredevil.solarthing.solar.renogy;

public final class ProductModelUtil {
	private ProductModelUtil(){ throw new UnsupportedOperationException(); }
	/*
	Renogy models:
	RBC30D1S-G1      - Dual Input DC-DC on board battery charger
	RNG-CTRL-RVRPG40 - Renogy Rover 40A Positive Ground
	RCC20RVRE-G1     - Rover Elite 20A
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

	public static boolean isRenogy(String productModel) {
		return isRover(productModel) || isWanderer(productModel) || productModel.contains("RCC") || productModel.contains("RBC");
	}
	public boolean isDualInput(String productModel) {
		return productModel.contains("RBC");
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
	// CHC-MPPT-40BT
	// CHC-MPPT-40BT-CHC-TMBT-01
}
