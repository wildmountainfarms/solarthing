package me.retrodaredevil.solarthing.solar.outback;

import me.retrodaredevil.solarthing.packets.BitmaskMode;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.identification.IdentityInfo;
import me.retrodaredevil.solarthing.solar.SolarStatusPacketType;
import org.junit.jupiter.api.Test;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OutbackDuplicatePacketRemoverTest {
	@Test
	void test1(){
		List<Packet> list = toPackets(1, 2, 3, 1, 2, 3, 4, 2, 3);
		OutbackDuplicatePacketRemover.INSTANCE.receive(list, true);
		assertEquals(4, list.size());
	}
	@Test
	void test2(){
		List<Packet> list = toPackets(1, 2, 3);
		OutbackDuplicatePacketRemover.INSTANCE.receive(list, true);
		assertEquals(3, list.size());
	}
	@Test
	void test3(){
		List<Packet> list = toPackets(1, 2, 3, 3);
		OutbackDuplicatePacketRemover.INSTANCE.receive(list, true);
		assertEquals(3, list.size());
	}
	@Test
	void test4(){
		List<Packet> list = toPackets(1, 2, 3, 1, 2);
		OutbackDuplicatePacketRemover.INSTANCE.receive(list, true);
		assertEquals(3, list.size());
	}
	@Test
	void test5(){
		List<Packet> list = toPackets(1, 2, 3, 1, 2, 1, 2, 3, 4);
		OutbackDuplicatePacketRemover.INSTANCE.receive(list, true);
		assertEquals(4, list.size());
	}
	@Test
	void test6(){
		List<Packet> list = toPackets(1, 2, 3, 4, 1, 2, 1, 2, 3);
		OutbackDuplicatePacketRemover.INSTANCE.receive(list, true);
		assertEquals(4, list.size());
	}
	private List<Packet> toPackets(int... addresses){
		List<Packet> r = new ArrayList<>();
		for(int address : addresses){
			r.add(create(address));
		}
		return r;
	}
	private OutbackStatusPacket create(int address){
		return new OutbackStatusPacket() {
			@Override
			public int getAddress() {
				return address;
			}

			@NotNull
            @Override
			public OutbackIdentifier getIdentifier() {
				throw new UnsupportedOperationException();
			}

			@Override
			public @NotNull IdentityInfo getIdentityInfo() {
				throw new UnsupportedOperationException();
			}

			@NotNull
            @Override
			public SolarStatusPacketType getPacketType() {
				throw new UnsupportedOperationException();
			}

			@Override
			public int getErrorModeValue() {
				throw new UnsupportedOperationException();
			}

			@Override
			public Collection<? extends BitmaskMode> getErrorModes() {
				throw new UnsupportedOperationException();
			}
		};
	}
}
