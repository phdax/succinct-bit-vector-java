package jp.phdax.bitvector;

public class BitVector {
	
	private final int size;
	private final long[] bits;
	private int[] rankIdx;
	private int[] selectIdx;
	private int wholeRank1;
	private static final long MOD64MASK = 0b111111;
	private static final int MOD64MASK_I = 0b111111;
	
	BitVector(int size, long[] bits) {
		this.size = size;
		this.bits = bits;
		
		rankIdx = new int[bits.length];
		selectIdx = new int[bits.length];
		
		int sum=0;
		int decimal64 = 0;
		for(int i=0; i<bits.length; i++) {
			//rank用インデックス：各longのtrueビットの累積値を記録する
			rankIdx[i] = sum;
			sum += Long.bitCount(bits[i]);
			//select用インデックス：trueビットが累積で64の倍数を超えた位置を記録していく
			if(decimal64<<6 < sum) {
				selectIdx[decimal64] = decimal64<<6 + bitPos(bits[i], sum&MOD64MASK_I);
				decimal64++;
			}
		}
		wholeRank1 = sum;
	}
	
	public boolean pos(int idx) {
		return ((bits[idx >>> 6] >>> (idx & MOD64MASK)) & 1) != 0;
	}
	
	public int rank1(int idx) {
		if(idx < 0) return 0;
		if(size <= idx) return wholeRank1;
		int mod = idx & MOD64MASK_I;
		int rank = rankIdx[idx >>> 6];
		rank += Long.bitCount(bits[idx] & (-1 >>> (64-mod)));
		return rank;
	}
	
	public int rank0(int idx) {
		if(idx < 0) return 0;
		if(size <= idx) return size-wholeRank1;
		return idx - rank1(idx) + 1;
	}

	public int select1(int rank) {
		if(rank < 0 || size <= rank) return -1;
		
		return 0;
	}
		
	public int select0(int rank) {
		if(rank < 0 || size <= rank) return -1;
		
		return 0;
	}
	
	private static final int bitPos(long bits, int num) {
		return 0;
	}
}
