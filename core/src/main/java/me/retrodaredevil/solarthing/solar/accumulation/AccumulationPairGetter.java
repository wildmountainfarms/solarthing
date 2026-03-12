package me.retrodaredevil.solarthing.solar.accumulation;

import me.retrodaredevil.solarthing.packets.identification.IdentifierFragment;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;

@NullMarked
public interface AccumulationPairGetter {
	Map<IdentifierFragment, List<AccumulationPair<?>>> queryDaily(long startDateMillis, @Nullable Long endDateMillis);
}
