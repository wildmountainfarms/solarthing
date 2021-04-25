package me.retrodaredevil.couchdbjava;

import javax.annotation.Nullable;
import java.util.List;

public interface JsonData {
	String toJson(@Nullable String id, @Nullable String rev, List<String> disallowedProperties);
}
