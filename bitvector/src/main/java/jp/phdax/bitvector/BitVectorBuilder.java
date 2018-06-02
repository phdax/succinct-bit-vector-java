package jp.phdax.bitvector;

import java.util.Arrays;

public class BitVectorBuilder {
	
	private final int size;
	private final long[] bits;
	private Type type;
		
	private BitVectorBuilder(int size, long[] bits) {
		this.bits = bits;
		this.type = Type.DEFAULT;
		this.size = size;
	}
	
	public static BitVectorBuilder create(int size) {
		int mod = size & 0b111111;
		int len = size >>> 6;
		if(mod > 0) len++;
		long[] bits = new long[len];	
		
		BitVectorBuilder b = new BitVectorBuilder(size, bits);
		return b;
	}
	
	public static BitVectorBuilder create(int size, long[] bits) {
		BitVectorBuilder b = new BitVectorBuilder(size, bits);
		return b;
	}
	
	public static BitVectorBuilder create(long[] bits) {
		BitVectorBuilder b = new BitVectorBuilder(bits.length << 6, bits);
		return b;
	}
	
	public BitVectorBuilder type(Type type) {
		this.type = type;
		return this;
	}
	
	public BitVectorBuilder set(int idx) {
		if(idx >= size) {
			throw new IllegalArgumentException("Exceeded index range.");
		}
		bits[idx >>> 6] |= 1L << (idx & 0b111111);
		return this;
	}
	
	public BitVector build() {
		if(type == Type.DEFAULT) {
			return new BitVector(size, bits);
		}
		return null;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(bits);
		result = prime * result + size;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BitVectorBuilder other = (BitVectorBuilder) obj;
		if (!Arrays.equals(bits, other.bits))
			return false;
		if (size != other.size)
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	public enum Type {
		DEFAULT;
	}
}
