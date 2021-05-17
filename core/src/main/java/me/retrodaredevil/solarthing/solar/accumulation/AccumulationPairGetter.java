package me.retrodaredevil.solarthing.solar.accumulation;

import me.retrodaredevil.solarthing.packets.identification.IdentifierFragment;
import me.retrodaredevil.solarthing.annotations.Nullable;

import java.util.List;
import java.util.Map;

public interface AccumulationPairGetter {
	Map<IdentifierFragment, List<AccumulationPair<?>>> queryDaily(long startDateMillis, @Nullable Long endDateMillis);
}
