package jp.phdax.bitvector;

public class BitVector {
	
	private final int size;
	private final long[] bits;
	private final int[] rankBlock;
	private final int[] selectBlock1;
	private final int[] selectBlock0;
	private final int sum1;
	private final int sum0;
	private static final long MOD64MASK = 0b111111;
	private static final int MOD64MASK_I = 0b111111;
	
	BitVector(int size, long[] bits) {
		this.size = size;
		this.bits = bits;
		
		rankBlock = new int[bits.length+1];
		selectBlock1 = new int[bits.length];
		selectBlock0 = new int[bits.length];
		
		int sum1=0;
		int sum0=0;
		int accLimit1 = 64;
		int accLimit0 = 64;
		for(int i=0; i<bits.length; i++) {
			rankBlock[i] = sum1;
			if(accLimit1 < sum1) {
				selectBlock1[accLimit1 >>> 6] = i;
				accLimit1 += 64;
			}
			if(accLimit0 < sum0) {
				selectBlock0[accLimit0 >>> 6] = i;
				accLimit0 += 64;
			}
			long bitCount = Long.bitCount(bits[i]);
			sum1 += bitCount;
			sum0 += 64-bitCount;
		}
		rankBlock[bits.length] = sum1;
		this.sum1 = sum1;
		this.sum0 = sum0;
	}
	
	public final int size() {
		return size;
	}
	
	public final boolean pos(int idx) {
		if(idx < 0) return false;
		if(size <= idx) return false;
		return ((bits[idx >>> 6] >>> (idx & MOD64MASK)) & 1) != 0;
	}
	
	public final int rank1(int idx) {
		if(idx < 0) return 0;
		if(size <= idx) return sum1;
		int mod = idx & MOD64MASK_I;
		int rank = rankBlock[idx >>> 6];
		rank += Long.bitCount(bits[idx >>> 6] & (-1L >>> 64-mod-1));
		return rank;
	}
	
	public final int rank0(int idx) {
		if(idx < 0) return 0;
		if(size <= idx) return size-sum1;
		return idx - rank1(idx) + 1;
	}

	public final int select1(int rank) {
		if(rank <= 0 || sum1 < rank) return -1;
		int lowerLimit = selectBlock1[rank >>> 6];
		int idx = linerSearch1(lowerLimit, rank);
		return (idx << 6) + bitPos(bits[idx], rank-rankBlock[idx]);
	}
		
	public final int select0(int rank) {
		if(rank <= 0 || sum0 < rank) return -1;
		int lowerLimit = selectBlock0[rank >>> 6];
		int idx = linerSearch0(lowerLimit, rank);
		return (idx << 6) + bitPos(~bits[idx], rank-(sum1-rankBlock[idx]));
	}
	
	private final int linerSearch1(int idx, int rank1) {
		for(; rankBlock[idx]<rank1; idx++);
		return idx-1;
	}
	private final int linerSearch0(int idx, int rank0) {
		for(; size-rankBlock[idx]<rank0; idx++);
		return idx-1;
	}
	
	public static final int bitPos(long bits, int n) {
		if(n <= 0 || Long.bitCount(bits) < n) return -1;
		for(; n>1; bits&=(bits-1), n--);
		return Long.bitCount(bits ^ (bits-1))-1;
	}
}
