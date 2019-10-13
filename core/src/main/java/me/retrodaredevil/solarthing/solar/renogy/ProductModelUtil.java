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
}
