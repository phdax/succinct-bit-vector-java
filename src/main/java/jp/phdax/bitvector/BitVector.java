package jp.phdax.bitvector;

public class BitVector {
	
	private final int size;
	private final long[] bits;
	private final int[] rankBlock;
	private final int[] selectBlock1;
	private final int[] selectBlock0;
	private final int wholeRank1;
	private static final long MOD64MASK = 0b111111;
	private static final int MOD64MASK_I = 0b111111;
	
	BitVector(int size, long[] bits) {
		this.size = size;
		this.bits = bits;
		
		rankBlock = new int[bits.length >>> 6];
		selectBlock1 = new int[bits.length >>> 6];
		selectBlock0 = new int[bits.length >>> 6];
		
		int sumT=0;
		int sumF=0;
		int decimal64T = 0;
		int decimal64F = 0;
		for(int i=0; i<bits.length; i++) {
			//rank用ブロック：各longのtrueビットの累積値を記録する
			rankBlock[i] = sumT;
			long bitCount = Long.bitCount(bits[i]);
			sumT += bitCount;
			sumF += 64-bitCount;
			//select用ブロック：true/falseビットが累積で64の倍数を超えた位置を記録していく
			if(decimal64T<<6 < sumT) {
				selectBlock1[decimal64T] = (decimal64T<<6) + bitPos(bits[i], sumT&MOD64MASK_I);
				decimal64T++;
			}
			if(decimal64F<<6 < sumF) {
				selectBlock0[decimal64F] = (decimal64F<<6) + bitPos(~bits[i], sumF&MOD64MASK_I);
				decimal64F++;
			}
		}
		wholeRank1 = sumT;
	}
	
	public final int size() {
		return size;
	}
	
	public final boolean pos(int idx) {
		return ((bits[idx >>> 6] >>> (idx & MOD64MASK)) & 1) != 0;
	}
	
	public final int rank1(int idx) {
		if(idx < 0) return 0;
		if(size <= idx) return wholeRank1;
		int mod = idx & MOD64MASK_I;
		int rank = rankBlock[idx >>> 6];
		rank += Long.bitCount(bits[idx] & (-1 >>> 64-mod));
		return rank;
	}
	
	public final int rank0(int idx) {
		if(idx < 0) return 0;
		if(size <= idx) return size-wholeRank1;
		return idx - rank1(idx) + 1;
	}

	public final int select1(int rank) {
		if(rank < 0 || size <= rank) return -1;
		int idx64 = selectBlock1[rank >>> 6];
		int idx = lineSearch1(idx64 >>> 6, rank);
		return bitPos(bits[idx], rank-rankBlock[idx >>> 6]);
	}
		
	public final int select0(int rank) {
		if(rank < 0 || size <= rank) return -1;
		int idx64 = selectBlock0[rank >>> 6];
		int idx = lineSearch0(idx64 >>> 6, rank);
		return bitPos(~bits[idx], rank-(size-rankBlock[idx >>> 6]));
	}
	
	/**
	 * <code>rankBlock</code>を<code>idx</code>から線形探索して、
	 * <code>rank</code>以下となる最大のランク値のインデックスを返します。
	 */
	private final int lineSearch1(int idx, int rank1) {
		for(; rankBlock[idx]<rank1; idx++);
		return idx;
	}
	private final int lineSearch0(int idx, int rank0) {
		for(; size-rankBlock[idx]<rank0; idx++);
		return idx;
	}
	
	/**
	 * ビット列<code>bits</code>の<code>n</code>番目にあるtrueビットの位置を返します。<br>
	 * <code>n</code>が必ず<code>Long.bitCount(bits)</code>以下である必要があります。
	 */
	private static final int bitPos(long bits, int n) {
		for(bits&=bits-1; n>1; n--);
		return (int)(bits ^ (bits-1))-1;
	}
}
