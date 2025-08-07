package de.cyklon.monopoly.protocol.io;

import lombok.experimental.UtilityClass;

@UtilityClass
class Serializer {

	public static void main(String[] args) {
		calculateRange(-40, 10);
	}

	public boolean[] serializeByteArray(byte[] bytes) {
		boolean[] bits = new boolean[bytes.length * 8];
		for (int i = 0; i < bytes.length; i++) {
			for (int bit = 0; bit < 8; bit++) {
				bits[i * 8 + bit] = ((bytes[i] >> (7 - bit)) & 1) == 1;
			}
		}
		return bits;
	}

	public byte[] deserializeByteArray(boolean[] bits) {
		int byteLength = (bits.length + 7) / 8;
		byte[] bytes = new byte[byteLength];
		for (int i = 0; i < bits.length; i++) {
			if (bits[i]) {
				bytes[i / 8] |= (byte) (1 << (7 - (i % 8)));
			}
		}
		return bytes;
	}


	public static boolean[] serializeInt(int value) {
		boolean[] bits = new boolean[32];
		for (int i = 0; i < 32; i++) {
			bits[i] = ((value >>> (31 - i)) & 1) == 1;
		}
		return bits;
	}

	public static int deserializeInt(boolean[] bits) {
		if (bits.length < 32) throw new IllegalArgumentException("bits array too short");
		int value = 0;
		for (int i = 0; i < 32; i++) {
			if (bits[i]) value |= 1 << (31 - i);
		}
		return value;
	}

	public static boolean[] serializeVarNumber(long value, int bitCount, boolean signed) {
		if (bitCount < 1 || bitCount > 64)
			throw new IllegalArgumentException("bitCount must be between 1 and 64");

		if (!signed && value < 0) throw new IllegalArgumentException("negative numbers arent allowed on unsigned numbers");

		long minValue = signed ? -(1L << (bitCount - 1)) : 0;
		long maxValue = signed ? ((1L << (bitCount - 1)) - 1) : ((1L << bitCount) - 1);

		if (value < minValue || value > maxValue)
			throw new IllegalArgumentException("value " + value + " doesn't fit in " + bitCount + " bits (signed=" + signed + ") allowed range: " + minValue + "-" + maxValue);

		if (signed && value < 0) {
			value += 1L << bitCount;
		}

		boolean[] bits = new boolean[bitCount];
		for (int i = 0; i < bitCount; i++) {
			bits[i] = ((value >>> ((bitCount - 1) - i)) & 1) == 1;
		}
		return bits;
	}

	public static long deserializeVarNumber(boolean[] bits, boolean signed) {
		if (bits.length < 1 || bits.length > 64)
			throw new IllegalArgumentException("bit length must be between 1 and 64");

		int bitCount = bits.length;
		long value = 0;

		for (int i = 0; i < bitCount; i++) {
			if (bits[i]) {
				value |= 1L << (bitCount - 1 - i);
			}
		}

		if (signed) {
			// höchstes Bit = Vorzeichenbit (MSB)
			long signBit = 1L << (bitCount - 1);
			if ((value & signBit) != 0) {
				value -= 1L << bitCount;
			}
		}
		return value;
	}

	public void calculateRange(int bits, boolean signed) {
		long min, max;

		if (signed) {
			min = -(1L << (bits - 1));
			max = (1L << (bits -1)) -1;
		} else {
			min = 0;
			max = (1L << bits) -1;
		}

		System.out.println("Signed: " + signed);
		System.out.println("Bits: " + bits);
		System.out.println("Range: " + min + "-" + max);
	}

	public void calculateRange(long minValue, long maxValue) {
		if (minValue > maxValue) throw new IllegalArgumentException("minValue must be lower than maxValue");

		boolean signed = minValue < 0;

		long min, max;

		int bits;
		if (signed) {
			long absMax = Math.max(Math.abs(minValue), Math.abs(maxValue));
			bits = 65 - Long.numberOfLeadingZeros(absMax);
			min = -(1L << (bits - 1));
			max = (1L << (bits -1)) -1;
		} else {
			bits = 64 - Long.numberOfLeadingZeros(maxValue);
			min = 0;
			max = (1L << bits) -1;
		}

		System.out.println("Signed: " + signed);
		System.out.println("Required Bits: " + bits);
		System.out.println("Range: " + min + "-" + max);
	}

	public static boolean[] serializeByte(byte value) {
		boolean[] bits = new boolean[8];
		for (int i = 0; i < 8; i++) {
			bits[i] = ((value >>> (7 - i)) & 1) == 1;
		}
		return bits;
	}

	public static byte deserializeByte(boolean[] bits) {
		if (bits.length < 8) throw new IllegalArgumentException("bits array too short");
		byte value = 0;
		for (int i = 0; i < 8; i++) {
			if (bits[i]) value |= (byte) (1 << (7 - i));
		}
		return value;
	}


	public static boolean[] serializeShort(short value) {
		boolean[] bits = new boolean[16];
		for (int i = 0; i < 16; i++) {
			bits[i] = ((value >>> (15 - i)) & 1) == 1;
		}
		return bits;
	}

	public static short deserializeShort(boolean[] bits) {
		if (bits.length < 16) throw new IllegalArgumentException("bits array too short");
		short value = 0;
		for (int i = 0; i < 16; i++) {
			if (bits[i]) value |= 1 << (15 - i);
		}
		return value;
	}


	public static boolean[] serializeLong(long value) {
		boolean[] bits = new boolean[64];
		for (int i = 0; i < 64; i++) {
			bits[i] = ((value >>> (63 - i)) & 1L) == 1L;
		}
		return bits;
	}

	public static long deserializeLong(boolean[] bits) {
		if (bits.length < 64) throw new IllegalArgumentException("bits array too short");
		long value = 0L;
		for (int i = 0; i < 64; i++) {
			if (bits[i]) value |= 1L << (63 - i);
		}
		return value;
	}


	public static boolean[] serializeFloat(float value) {
		int intBits = Float.floatToIntBits(value);
		return serializeInt(intBits);
	}

	public static float deserializeFloat(boolean[] bits) {
		int intBits = deserializeInt(bits);
		return Float.intBitsToFloat(intBits);
	}


	public static boolean[] serializeDouble(double value) {
		long longBits = Double.doubleToLongBits(value);
		return serializeLong(longBits);
	}

	public static double deserializeDouble(boolean[] bits) {
		long longBits = deserializeLong(bits);
		return Double.longBitsToDouble(longBits);
	}


	public static boolean[] serializeChar(char value) {
		boolean[] bits = new boolean[16];
		for (int i = 0; i < 16; i++) {
			bits[i] = ((value >>> (15 - i)) & 1) == 1;
		}
		return bits;
	}

	public static char deserializeChar(boolean[] bits) {
		if (bits.length < 16) throw new IllegalArgumentException("bits array too short");
		char value = 0;
		for (int i = 0; i < 16; i++) {
			if (bits[i]) value |= (char) (1 << (15 - i));
		}
		return value;
	}

}
