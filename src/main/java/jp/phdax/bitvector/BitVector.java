package jp.phdax.bitvector;

public class BitVector {
	
	private final int size;
	private final long[] bits;
	private int[] rankIdx;
	private int[] selectIdx;
	private int wholeRank1;
	private static final long MOD64MASK = 0b111111;
	
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
			if(decimal64 * 64 < sum) {
				selectIdx[decimal64] = decimal64*64 + bitPos(bits[i], sum%64);
				decimal64++;
			}
		}
	}
	
	public boolean pos(int idx) {
		return ((bits[idx >>> 6] >>> (idx & MOD64MASK)) & 1) != 0;
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
	
	private static final int bitPos(long bits, int num) {
		return 0;
	}
}
