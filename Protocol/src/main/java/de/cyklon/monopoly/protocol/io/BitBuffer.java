package de.cyklon.monopoly.protocol.io;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.util.UUID;

import static de.cyklon.monopoly.protocol.io.Serializer.*;

public class BitBuffer {

	private final boolean[] bits;

	private int pos = 0;


	private BitBuffer(int capacity) {
		this.bits = new boolean[capacity];
	}

	/**
	 * Allocates a new bit buffer.
	 *
	 * <p> The new buffer's position will be zero
	 *
	 * It will have a {@link #array backing array}
	 *
	 * @param  capacity
	 *         The new buffer's capacity, in bits
	 *
	 * @return  The new bit buffer
	 *
	 * @throws  IllegalArgumentException
	 *          If the {@code capacity} is a negative integer
	 */
	public static BitBuffer allocate(int capacity) {
		if (capacity < 0) throw new IllegalArgumentException("capacity < 0: (" + capacity + " < 0)");
		return new BitBuffer(capacity);
	}

	/**
	 * Wraps a boolean array (bit array) into a buffer.
	 *
	 * <p> The new buffer will copy the given boolean array;
	 * that is, modifications to the buffer will not affect the array
	 * The new buffer's capacity will be
	 * {@code array.length}, its position will be zero.


	 * Its {@link #array backing array} will be a copy of the given array</p>
	 *
	 * @param  array
	 *         The array that will back this buffer
	 *
	 * @return  The new bit buffer
	 */
	public static BitBuffer wrap(boolean[] array) {
		BitBuffer buffer = allocate(array.length);
		System.arraycopy(array, 0, buffer.bits, 0, array.length);
		return buffer;
	}

	/**
	 * Wraps a byte array into a buffer.
	 *
	 * <p> The new buffer will convert the byte array in bits and wrap the bit array;
	 * that is, modifications to the buffer will not affect the array
	 * The new buffer's capacity will be
	 * {@code array.length}, its position will be zero.


	 * Its {@link #array backing array} will be the converted bits from the byte array</p>
	 *
	 * @param  array
	 *         The array that will be converted to back this buffer
	 *
	 * @return  The new bit buffer
	 */
	public static BitBuffer wrap(byte[] array) {
		return wrap(serializeByteArray(array));
	}

	/**
	 * Returns the bit array (boolean array) that backs this
	 * buffer
	 *
	 * <p> Modifications to this buffer's content will cause the returned
	 * array's content to be modified, and vice versa.
	 *
	 * @return  The array that backs this buffer
	 */
	public boolean[] array() {
		return bits;
	}

	/**
	 * Returns the bit array that backs this
	 * buffer represented as byte array
	 *
	 * <p> Modifications to this buffer's content will not affect the returned
	 * array's content
	 *
	 * @return  The array that backs this buffer represented as byte array
	 */
	public byte[] asByteArray() {
		return deserializeByteArray(bits);
	}

	public void rewind() {
		pos = 0;
	}

	public int position() {
		return pos;
	}

	public void position(int pos) {
		this.pos = pos;
	}

	public int capacity() {
		return bits.length;
	}

	//Primitives

	/**
	 * <p> Writes the given boolean (bit) into this buffer at the current
	 * position, and then increments the position. </p>
	 *
	 * @param  value
	 *         The boolean to be written
	 *
	 * @return  This buffer for chained calls
	 *
	 * @throws  BufferOverflowException
	 *          If this buffer's current position is not smaller than its limit
	 */
	public BitBuffer put(boolean value) {
		if (pos==bits.length) throw new BufferOverflowException();
		bits[pos++] = value;
		return this;
	}

	/**
	 * <p> This method transfers the entire content of the given source
	 * boolean array into this buffer.
	 *
	 * @param   src
	 *          The source array
	 *
	 * @return  This buffer for chained calls
	 *
	 * @throws  BufferOverflowException
	 *          If there is insufficient space in this buffer
	 */
	public BitBuffer put(boolean[] src) {
		for (boolean b1 : src) put(b1);
		return this;
	}

	/**
	 * <p> Writes eight bits containing the given byte value,
	 * into this buffer at the current position, and then
	 * increments the position by eight.  </p>
	 *
	 * @param  value
	 *         The byte value to be written
	 *
	 * @return  This buffer for chained calls
	 *
	 * @throws  BufferOverflowException
	 *          If there are fewer than eight bits
	 *          remaining in this buffer
	 */
	public BitBuffer putByte(byte value) {
		put(serializeByte(value));
		return this;
	}

	/**
	 * <p> This method transfers the entire content of the given source
	 * byte array into this buffer after converting to bits.
	 *
	 * @param   src
	 *          The source array
	 *
	 * @return  This buffer for chained calls
	 *
	 * @throws  BufferOverflowException
	 *          If there is insufficient space in this buffer
	 */
	public BitBuffer putByteArray(byte[] src) {
		put(serializeByteArray(src));
		return this;
	}

	/**
	 * <p> Writes sixteen bits containing the given char value,
	 * into this buffer at the current position, and then
	 * increments the position by sixteen.  </p>
	 *
	 * @param  value
	 *         The char value to be written
	 *
	 * @return  This buffer for chained calls
	 *
	 * @throws  BufferOverflowException
	 *          If there are fewer than sixteen bits
	 *          remaining in this buffer
	 */
	public BitBuffer putChar(char value) {
		put(serializeChar(value));
		return this;
	}

	/**
	 * <p> Writes sixteen bits containing the given short value,
	 * into this buffer at the current position, and then
	 * increments the position by sixteen.  </p>
	 *
	 * @param  value
	 *         The short value to be written
	 *
	 * @return  This buffer for chained calls
	 *
	 * @throws  BufferOverflowException
	 *          If there are fewer than sixteen bits
	 *          remaining in this buffer
	 */
	public BitBuffer putShort(short value) {
		put(serializeShort(value));
		return this;
	}

	/**
	 * <p> Writes thirty-two bits containing the given int value,
	 * into this buffer at the current position, and then
	 * increments the position by thirty-two.  </p>
	 *
	 * @param  value
	 *         The int value to be written
	 *
	 * @return  This buffer for chained calls
	 *
	 * @throws  BufferOverflowException
	 *          If there are fewer than thirty-two bits
	 *          remaining in this buffer
	 */
	public BitBuffer putInt(int value) {
		put(serializeInt(value));
		return this;
	}

	/**
	 * <p> Writes sixty-four bits containing the given long value,
	 * into this buffer at the current position, and then
	 * increments the position by sixty-four.  </p>
	 *
	 * @param  value
	 *         The long value to be written
	 *
	 * @return  This buffer for chained calls
	 *
	 * @throws  BufferOverflowException
	 *          If there are fewer than sixty-four bits
	 *          remaining in this buffer
	 */
	public BitBuffer putLong(long value) {
		put(serializeLong(value));
		return this;
	}

	/**
	 * <p> Writes thirty-two bits containing the given float value,
	 * into this buffer at the current position, and then
	 * increments the position by thirty-two.  </p>
	 *
	 * @param  value
	 *         The float value to be written
	 *
	 * @return  This buffer for chained calls
	 *
	 * @throws  BufferOverflowException
	 *          If there are fewer than thirty-two bits
	 *          remaining in this buffer
	 */
	public BitBuffer putFloat(float value) {
		put(serializeFloat(value));
		return this;
	}

	/**
	 * <p> Writes sixty-four bits containing the given double value,
	 * into this buffer at the current position, and then
	 * increments the position by sixty-four.  </p>
	 *
	 * @param  value
	 *         The double value to be written
	 *
	 * @return  This buffer for chained calls
	 *
	 * @throws  BufferOverflowException
	 *          If there are fewer than sixty-four bits
	 *          remaining in this buffer
	 */
	public BitBuffer putDouble(double value) {
		put(serializeDouble(value));
		return this;
	}

	/**
	 * <p> Writes the given amount of bits containing the given numeric value,
	 * into this buffer at the current position, and then
	 * increments the position by the set number of bits.  </p>
	 *
	 * @param  value
	 *         The numeric value to be written
	 *
	 * @param bits
	 *         The amount of bits to be used to store the value. allowed range: 1-64 bits
	 *
	 * @param signed
	 *         Marks the value as signed/unsigned. Signed values can be negative and positive. Unsigned values can only be positive
	 *
	 * @return  This buffer for chained calls
	 *
	 * @throws  BufferOverflowException
	 *          If there are fewer than sixty-four bits
	 *          remaining in this buffer
	 *
	 * @throws IllegalArgumentException
	 *          If the given value doesn't fit in the given amount of bits<br>
	 *          - If the given value is negative but marked as unsigned<br>
	 *          - If the given bit count is outside the allowed range 1-64
	 */
	public BitBuffer putVarNumber(long value, int bits, boolean signed) {
		put(serializeVarNumber(value, bits, signed));
		return this;
	}

	/**
	 * Reads the bit at this buffer's
	 * current position, and then increments the position.
	 *
	 * @return  The bit at the buffer's current position
	 *
	 * @throws  BufferUnderflowException
	 *          If the buffer's current position is not smaller than its capacity
	 */
	public boolean get() {
		if (pos==bits.length) throw new BufferUnderflowException();
		return bits[pos++];
	}

	/**
	 * <p> This method transfers bits from this buffer into the given
	 * destination array.
	 *
	 * @param   dst
	 *          The destination array
	 *
	 * @throws  BufferUnderflowException
	 *          If there are fewer than {@code length} bits
	 *          remaining in this buffer
	 */
	public synchronized void get(boolean[] dst) {
		if (pos + dst.length > bits.length) throw new BufferUnderflowException();
		System.arraycopy(bits, pos, dst, 0, dst.length);
		pos += dst.length;
	}

	/**
	 * <p> Reads the next eight bits at this buffer's current position,
	 * composing them into a byte value,
	 * and then increments the position by eight.  </p>
	 *
	 * @return  The byte value at the buffer's current position
	 *
	 * @throws  BufferUnderflowException
	 *          If there are fewer than eight bits
	 *          remaining in this buffer
	 */
	public byte getByte() {
		boolean[] src = new boolean[Byte.SIZE];
		get(src);
		return deserializeByte(src);
	}

	/**
	 * <p> Reads the given length*8 bits at this buffer's current position,
	 * composing them into a byte array with the given length,
	 * and then increments the position by the length*8.  </p>
	 *
	 * @return  The byte array at the buffer's current position with the given length
	 *
	 * @throws  BufferUnderflowException
	 *          If there are fewer than the given length*8 bits
	 *          remaining in this buffer
	 */
	public byte[] getByteArray(int length) {
		if (pos + length*Byte.SIZE >= bits.length) throw new BufferOverflowException();
		boolean[] src = new boolean[length*Byte.SIZE];
		get(src);
		return deserializeByteArray(src);
	}

	/**
	 * <p> Reads the next sixteen bits at this buffer's current position,
	 * composing them into a char value,
	 * and then increments the position by sixteen.  </p>
	 *
	 * @return  The char value at the buffer's current position
	 *
	 * @throws  BufferUnderflowException
	 *          If there are fewer than sixteen bits
	 *          remaining in this buffer
	 */
	public char getChar() {
		boolean[] src = new boolean[Character.SIZE];
		get(src);
		return deserializeChar(src);
	}

	/**
	 * <p> Reads the next sixteen bits at this buffer's current position,
	 * composing them into a byte value,
	 * and then increments the position by sixteen.  </p>
	 *
	 * @return  The short value at the buffer's current position
	 *
	 * @throws  BufferUnderflowException
	 *          If there are fewer than sixteen bits
	 *          remaining in this buffer
	 */
	public short getShort() {
		boolean[] src = new boolean[Short.SIZE];
		get(src);
		return deserializeShort(src);
	}

	/**
	 * <p> Reads the next thirty-two bits at this buffer's current position,
	 * composing them into a int value,
	 * and then increments the position by thirty-two.  </p>
	 *
	 * @return  The int value at the buffer's current position
	 *
	 * @throws  BufferUnderflowException
	 *          If there are fewer than thirty-two bits
	 *          remaining in this buffer
	 */
	public int getInt() {
		boolean[] src = new boolean[Integer.SIZE];
		get(src);
		return deserializeInt(src);
	}

	/**
	 * <p> Reads the next sixty-four bits at this buffer's current position,
	 * composing them into a long value,
	 * and then increments the position by sixty-four.  </p>
	 *
	 * @return  The byte value at the buffer's current position
	 *
	 * @throws  BufferUnderflowException
	 *          If there are fewer than eight bits
	 *          remaining in this buffer
	 */
	public long getLong() {
		boolean[] src = new boolean[Long.SIZE];
		get(src);
		return deserializeLong(src);
	}

	/**
	 * <p> Reads the next thirty-two bits at this buffer's current position,
	 * composing them into a float value,
	 * and then increments the position by thirty-two.  </p>
	 *
	 * @return  The float value at the buffer's current position
	 *
	 * @throws  BufferUnderflowException
	 *          If there are fewer than thirty-two bits
	 *          remaining in this buffer
	 */
	public float getFloat() {
		boolean[] src = new boolean[Float.SIZE];
		get(src);
		return deserializeFloat(src);
	}

	/**
	 * <p> Reads the sixty-four eight bits at this buffer's current position,
	 * composing them into a double value,
	 * and then increments the position by sixty-four.  </p>
	 *
	 * @return  The double value at the buffer's current position
	 *
	 * @throws  BufferUnderflowException
	 *          If there are fewer than sixty-four bits
	 *          remaining in this buffer
	 */
	public double getDouble() {
		boolean[] src = new boolean[Double.SIZE];
		get(src);
		return deserializeDouble(src);
	}

	/**
	 * <p> Reads the given amount of bits at this buffer's current position,
	 * composing them into a numeric value,
	 * and then increments the position by the given bit count.  </p>
	 *
	 * @param bits
	 *         The amount of bits to be used to read the value. allowed range: 1-64 bits
	 *
	 * @param signed
	 *         Marks the value as signed/unsigned. Signed values can be negative and positive. Unsigned values can only be positive
	 *
	 * @return  The numeric value at the buffer's current position
	 *
	 * @throws  BufferUnderflowException
	 *          If there are fewer than the given bit count
	 *          remaining in this buffer
	 *
	 * @throws IllegalArgumentException
	 *          If the given bit count is outside the allowed range 1-64
	 */
	public long getVarNumber(int bits, boolean signed) {
		boolean[] src = new boolean[bits];
		get(src);
		return deserializeVarNumber(src, signed);
	}



	//Assigned

	public void putAssigned(boolean[] value) {
		putInt(value.length);
		put(value);
	}

	public void putAssigned(byte[] value) {
		putInt(value.length);
		putByteArray(value);
	}

	public void putAssigned(char[] value) {
		putInt(value.length);
		for (char c : value) putChar(c);
	}

	public void putAssigned(short[] value) {
		putInt(value.length);
		for (short i : value) putShort(i);
	}

	public void putAssigned(int[] value) {
		putInt(value.length);
		for (int i : value) putInt(i);
	}

	public void putAssigned(long[] value) {
		putInt(value.length);
		for (long l : value) putLong(l);
	}

	public void putAssigned(float[] value) {
		putInt(value.length);
		for (float v : value) putFloat(v);
	}

	public void putAssigned(double[] value) {
		putInt(value.length);
		for (double v : value) putDouble(v);
	}


	public boolean[] getAssigned() {
		boolean[] dest = new boolean[getInt()];
		get(dest);
		return dest;
	}

	public byte[] getAssignedBytes() {
		boolean[] bits = new boolean[getInt() * Byte.SIZE];
		get(bits);
		return deserializeByteArray(bits);
	}

	public char[] getAssignedChars() {
		char[] result = new char[getInt()];
		for (int i = 0; i < result.length; i++) {
			result[i] = getChar();
		}
		return result;
	}

	public short[] getAssignedShorts() {
		short[] result = new short[getInt()];
		for (int i = 0; i < result.length; i++) {
			result[i] = getShort();
		}
		return result;
	}

	public int[] getAssignedInts() {
		int[] result = new int[getInt()];
		for (int i = 0; i < result.length; i++) {
			result[i] = getInt();
		}
		return result;
	}

	public long[] getAssignedLongs() {
		long[] result = new long[getInt()];
		for (int i = 0; i < result.length; i++) {
			result[i] = getLong();
		}
		return result;
	}

	public float[] getAssignedFloats() {
		float[] result = new float[getInt()];
		for (int i = 0; i < result.length; i++) {
			result[i] = getFloat();
		}
		return result;
	}

	public double[] getAssignedDoubles() {
		double[] result = new double[getInt()];
		for (int i = 0; i < result.length; i++) {
			result[i] = getDouble();
		}
		return result;
	}


	//Types

	public void putUUID(UUID id) {
		putLong(id.getMostSignificantBits());
		putLong(id.getLeastSignificantBits());
	}

	public UUID getUUID() {
		return new UUID(getLong(), getLong());
	}

	public String getString() {
		return new String(getByteArray(getInt()));
	}




	@Override
	public String toString() {
		return getClass().getName() +
				"[pos=" +
				pos +
				" cap=" +
				bits.length +
				"]";
	}

	public String toBitString() {
		StringBuilder sb = new StringBuilder();
		for (boolean b : array()) sb.append(b ? '1' : '0');
		return sb.toString();
	}
}
