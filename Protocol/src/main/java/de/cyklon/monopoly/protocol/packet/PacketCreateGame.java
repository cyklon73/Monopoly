package de.cyklon.monopoly.protocol.packet;

import de.cyklon.monopoly.protocol.Packet;
import de.cyklon.monopoly.protocol.io.BitBuffer;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PacketCreateGame extends Packet {

	private final String playerName;
	private final int maxPlayers;
	private final boolean privateGame;

	public PacketCreateGame(byte[] data) {
		super(data);
		BitBuffer buffer = BitBuffer.wrap(data);
		this.playerName = new String(buffer.getAssignedBytes());
		this.maxPlayers = buffer.getInt();
		this.privateGame = buffer.get();
	}

	@Override
	protected byte[] data() {
		byte[] name = playerName.getBytes();

		BitBuffer buffer = BitBuffer.allocate((4 + name.length + 4 + 1)*8);

		buffer.putAssigned(name);

		buffer.putInt(maxPlayers);

		buffer.put(privateGame);
		return buffer.asByteArray();
	}
}
