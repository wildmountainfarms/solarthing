package me.retrodaredevil.solarthing.solar.daily;

import me.retrodaredevil.solarthing.packets.identification.IdentifierFragment;
import me.retrodaredevil.solarthing.annotations.Nullable;

import java.util.List;
import java.util.Map;

public interface DailyPairGetter {
	Map<IdentifierFragment, List<DailyPair<?>>> queryDaily(long startDateMillis, @Nullable Long endDateMillis);
}
