package jp.phdax.bitvector;

public class BitVectorBuilder {
	
	private int size;
	private long[] bits;
	private Type type;
	
	public BitVectorBuilder(int size) {
		this.bits = new long[size];
		this.type = Type.DEFAULT;
	}
	
	public BitVectorBuilder type(Type type) {
		this.type = type;
		return this;
	}
	
	public BitVectorBuilder set(int idx) {
		return this;
	}
	
	public BitVector build() {
		if(type == Type.DEFAULT) {
			return new BitVector(size, bits);
		}
		return null;
	}
	
	public enum Type {
		DEFAULT;
	}
}
