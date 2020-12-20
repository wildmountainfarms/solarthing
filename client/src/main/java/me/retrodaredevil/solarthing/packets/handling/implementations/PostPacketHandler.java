package me.retrodaredevil.solarthing.packets.handling.implementations;

import me.retrodaredevil.solarthing.InstantType;
import me.retrodaredevil.solarthing.packets.collection.PacketCollection;
import me.retrodaredevil.solarthing.packets.handling.PacketHandleException;
import me.retrodaredevil.solarthing.packets.handling.PacketHandler;
import okhttp3.*;

import java.io.IOException;

public class PostPacketHandler implements PacketHandler {
	private final String url;
	private final StringPacketHandler stringPacketHandler;
	private final MediaType mediaType;
	private final OkHttpClient client;

	public PostPacketHandler(String url, StringPacketHandler stringPacketHandler, MediaType mediaType) {
		this.url = url;
		this.stringPacketHandler = stringPacketHandler;
		this.mediaType = mediaType;

		client = new OkHttpClient.Builder()
				.build();
	}

	@Override
	public void handle(PacketCollection packetCollection, InstantType instantType) throws PacketHandleException {
		String string = stringPacketHandler.getString(packetCollection);
		Call call = client.newCall(
				new Request.Builder()
						.url(url)
						.post(RequestBody.create(string, mediaType))
						.build()
		);
		try {
			Response response = call.execute();
			if (!response.isSuccessful()) {
				throw new PacketHandleException("Connected with unsuccessful response! code: " + response.code() + " message: " + response.message());
			}
		} catch (IOException e) {
			throw new PacketHandleException("Exception while posting!", e);
		}
	}
}
