package me.retrodaredevil.solarthing.solar.renogy;

public final class ProductModelUtil {
	private ProductModelUtil(){ throw new UnsupportedOperationException(); }

	public static boolean isRover(String productModel){
		return productModel.contains("RVR");
	}
	public static boolean isPositiveGround(String productModel){
		return productModel.contains("PG");
	}
	public static boolean isWanderer(String productModel){
		return productModel.contains("WND");
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
