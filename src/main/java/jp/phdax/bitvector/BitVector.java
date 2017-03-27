package jp.phdax.bitvector;

public class BitVector {
	
	private final int size;
	private final long[] bits;
	private long[] rankBuffer;
	private long[] selectBuffer;
	private int wholeRank1;
	
	BitVector(int size, long[] bits) {
		this.size = size;
		this.bits = bits;
	}
	
	public boolean pos(int idx) {
		return false;
	}
	
	public int rank1(int idx) {
		return 0;
	}
	
	public int rank0(int idx) {
		return 0;
	}

	public int select1(int rank) {
		return 0;
	}
	
	public int select0(int rank) {
		return 0;
	}
	
	private static final int bitPos(int num) {
		return 0;
	}
}
