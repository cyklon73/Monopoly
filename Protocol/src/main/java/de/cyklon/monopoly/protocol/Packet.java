package de.cyklon.monopoly.protocol;

import de.cyklon.monopoly.protocol.io.BitBuffer;

import java.io.InvalidClassException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public abstract class Packet {

	protected Packet(byte[] data) {}

	protected Packet() {}

	protected abstract byte[] data();

	/**
	 * Serializes the packet in bytes. can be deserialized with {@link Packet#read(byte[])}
	 * @return the serialized data
	 */
	public byte[] getBytes() {
		byte[] type = this.getClass().getName().getBytes();
		byte[] data = data();

		BitBuffer buffer = BitBuffer.allocate((4 + type.length + 4 + data.length)*8);
		buffer.putAssigned(type);
		buffer.putAssigned(data);

		return buffer.asByteArray();
	}

	/**
	 * Parses a packet from byte data
	 * @param data the packet data to parse
	 * @return the parsed packet
	 * @param <T> the packet type
	 * @throws InvalidClassException thrown if the data declares a invalid packet class
	 * @throws InvocationTargetException thrown when invoking packet class fails
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Packet> T read(byte[] data) throws InvalidClassException, InvocationTargetException {
		BitBuffer buffer = BitBuffer.wrap(data);


		String tName = new String(buffer.getAssignedBytes());
		try {
			Class<?> t = Class.forName(tName);
			if (!Packet.class.isAssignableFrom(t)) throw new InvalidClassException(tName, "Class is not a Packet class");
			Class<T> type = (Class<T>) t;

			Constructor<T> constructor = type.getConstructor(byte[].class);

			return constructor.newInstance((Object) buffer.getAssignedBytes());
		} catch (ClassNotFoundException e) {
			throw new InvalidClassException(tName, "Packet class not found");
		} catch (NoSuchMethodException e) {
			throw new InvalidClassException(tName, "Invalid Packet class");
		} catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
			throw new InvocationTargetException(e, "Failed invoke Packet");
		}
	}

}
