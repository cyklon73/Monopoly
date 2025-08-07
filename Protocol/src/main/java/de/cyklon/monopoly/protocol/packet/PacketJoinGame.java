package de.cyklon.monopoly.protocol.packet;

import de.cyklon.monopoly.protocol.Packet;
import de.cyklon.monopoly.protocol.io.BitBuffer;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
public class PacketJoinGame extends Packet {

	private final UUID gameId;
	private final String playerName;

	public PacketJoinGame(byte[] data) {
		super(data);
		BitBuffer buffer = BitBuffer.wrap(data);
		this.gameId = buffer.getUUID();
		this.playerName = buffer.getString();
	}

	@Override
	protected byte[] data() {
		byte[] name = playerName.getBytes();

		return new byte[0];
	}
}
