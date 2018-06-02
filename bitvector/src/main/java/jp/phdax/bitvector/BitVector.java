package jp.phdax.bitvector;

public class BitVector {
	
	private final int size;
	private final long[] bits;
	private final int[] rankBlock;
	private final int[] selectBlock1;
	private final int[] selectBlock0;
	private final int sum;
	
	BitVector(int size, long[] bits) {
		this.size = Math.min(size, bits.length << 6);
		this.bits = bits;
		
		int rankBlockSize = (size >>> 6) + 1;
		if((size & 0b111111) > 0) rankBlockSize++;
		rankBlock = new int[rankBlockSize];
		int nextRange = Math.min(size, 64);
		for(int i=0; nextRange>0 && i<rankBlockSize-1; i++) {
			rankBlock[i+1] = rankBlock[i] + Long.bitCount(bits[i] << (64 - nextRange));
			nextRange = Math.min(size - (64 << i), 64);
		}
		this.sum = rankBlock[rankBlockSize-1];
		
		selectBlock1 = new int[(this.sum >>> 6) + 1];
		selectBlock0 = new int[(size - this.sum >>> 6) + 1];
		int select1Idx = 1;
		int select0Idx = 1;
		for(int i=1; i<rankBlock.length; i++) {
			int rank1 = rankBlock[i];
			int rank0 = (i << 6) - rank1;
			if(select1Idx << 6 < rank1) {
				selectBlock1[select1Idx] = i;
				select1Idx++;
			}
			if(select0Idx << 6 < rank0) {
				selectBlock0[select0Idx] = i;
				select0Idx++;				
			}
		}
	}
	
	public final int size() {
		return size;
	}
	
	public final int bitCount() {
		return sum;
	}
	
	public final boolean pos(int idx) {
		if(idx < 0) return false;
		if(size <= idx) return false;
		return ((bits[idx >>> 6] >>> (idx & 0b111111)) & 1) != 0;
	}
	
	public final int rank1(int idx) {
		if(idx < 0) return 0;
		if(size <= idx) return sum;
		int mod = idx & 0b111111;
		int rank = rankBlock[idx >>> 6];
		rank += Long.bitCount(bits[idx >>> 6] & (-1L >>> 64-mod-1));
		return rank;
	}
	
	public final int rank0(int idx) {
		if(idx < 0) return 0;
		if(size <= idx) return size-sum;
		int mod = idx & 0b111111;
		int rank = rankBlock[idx >>> 6];
		rank += Long.bitCount(bits[idx >>> 6] & (-1L >>> 64-mod-1));
		return idx - rank + 1;
	}

	public final int select1(int rank) {
		if(rank <= 0 || sum < rank) return -1;
		int lowerLimit = selectBlock1[rank >>> 6];
		int idx = linerSearch1(lowerLimit, rank);
		return (idx << 6) + bitPos(bits[idx], rank-rankBlock[idx]);
	}
		
	public final int select0(int rank) {
		if(rank <= 0 || size - sum < rank) return -1;
		int lowerLimit = selectBlock0[rank >>> 6];
		int idx = linerSearch0(lowerLimit, rank);
		return (idx << 6) + bitPos(~bits[idx], rank-((idx << 6) - rankBlock[idx]));
	}
	
	private final int linerSearch1(int idx, int rank1) {
		for(; rankBlock[idx]<rank1; idx++);
		return idx-1;
	}
	private final int linerSearch0(int idx, int rank0) {
		for(; (idx<<6)-rankBlock[idx]<rank0; idx++);
		return idx-1;
	}
	
	public static final int bitPos(long bits, int n) {
		if(n <= 0 || Long.bitCount(bits) < n) return -1;
		for(; n>1; bits&=(bits-1), n--);
		return Long.bitCount(bits ^ (bits-1))-1;
	}
}
