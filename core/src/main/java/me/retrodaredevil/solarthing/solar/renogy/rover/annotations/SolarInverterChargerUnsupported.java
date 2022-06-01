package me.retrodaredevil.solarthing.solar.renogy.rover.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Documents that a particular field is unsupported on Renogy Solar Inverter Chargers
 * <p>
 * Solar Inverter Chargers include, but are not limited to:
 * <ul>
 *     <li>RIV4835CSH1S-US (48v-3500w-solar-inverter-charger)</li>
 * </ul>
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.SOURCE)
public @interface SolarInverterChargerUnsupported {
}
