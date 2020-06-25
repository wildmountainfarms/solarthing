package me.retrodaredevil.solarthing.meta;

import java.util.ArrayList;
import java.util.List;

public class DefaultMetaDatabase implements MetaDatabase {
	private final RootMetaPacket rootMetaPacket;

	public DefaultMetaDatabase(RootMetaPacket rootMetaPacket) {
		this.rootMetaPacket = rootMetaPacket;
	}
	@Override
	public List<TargetedMetaPacket> getMeta(long dateMillis, int fragmentId) {
		List<TargetedMetaPacket> r = new ArrayList<>();
		for (TimedMetaCollection timedMetaCollection : rootMetaPacket.getMeta()) {
			if (timedMetaCollection.getTimeRange().contains(dateMillis)) {
				for (BasicMetaPacket basicMetaPacket : timedMetaCollection.getPackets()) {
					if (basicMetaPacket instanceof TargetMetaPacket) {
						TargetMetaPacket targetMetaPacket = (TargetMetaPacket) basicMetaPacket;
						if (targetMetaPacket.getFragmentIds().contains(fragmentId)) {
							r.addAll(targetMetaPacket.getPackets());
						}
					}
				}
			}
		}
		return r;
	}

	@Override
	public List<BasicMetaPacket> getMeta(long dateMillis) {
		List<BasicMetaPacket> r = new ArrayList<>();
		for (TimedMetaCollection timedMetaCollection : rootMetaPacket.getMeta()) {
			if (timedMetaCollection.getTimeRange().contains(dateMillis)) {
				r.addAll(timedMetaCollection.getPackets());
			}
		}
		return r;
	}
}
