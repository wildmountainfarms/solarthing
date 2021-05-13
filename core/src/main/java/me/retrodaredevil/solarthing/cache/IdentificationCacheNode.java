package me.retrodaredevil.solarthing.cache;

import me.retrodaredevil.solarthing.packets.identification.Identifier;
import me.retrodaredevil.solarthing.packets.identification.IdentifierFragment;

public interface IdentificationCacheNode<T extends IdentificationCacheData> {
	Identifier getIdentifier();
	int getFragment();
	IdentifierFragment getIdentifierFragment();

	T getData();
}
