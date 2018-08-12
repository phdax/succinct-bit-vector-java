package jp.phdax.bitvector.simple;

import jp.phdax.bitvector.IBitVector;

/**
 * rank, selectを実装する簡素なデータ構造です。<br>
 * 補助データのオーダーがo(N)ではなくO(N)のため、簡潔データ構造ではありません。<br>
 * @author phdax
 */
public class BitVector implements IBitVector {
	
	private final int size;
	private final int sum;
	private final long[] bits;
	private final int[] rankBlock;
	private final int[] selectBlock1;
	private final int[] selectBlock0;
	
	public BitVector(int size, long[] bits) {
		
		if(size < 1) throw new IllegalArgumentException("Too small size.");
		if(bits == null || bits.length == 0) throw new IllegalArgumentException("No bits.");
		
		this.size = Math.min(size, bits.length << 6);
		this.bits = bits;
		
		this.rankBlock = new int[((this.size-1) >>> 6) + 2];
		int nextRange = Math.min(size, Long.SIZE);
		for(int i=0; nextRange>0; i++) {
			rankBlock[i+1] = rankBlock[i] + Long.bitCount(bits[i] << (Long.SIZE - nextRange));
			nextRange = Math.min(size - (Long.SIZE << i), Long.SIZE);
		}
		this.sum = rankBlock[rankBlock.length-1];
		
		this.selectBlock1 = new int[(this.sum >>> 6) + 1];
		this.selectBlock0 = new int[(size - this.sum >>> 6) + 1];
		int select1Idx = 1;
		int select0Idx = 1;
		for(int i=1; i<rankBlock.length; i++) {
			final int rank1 = rankBlock[i];
			final int rank0 = (i << 6) - rank1;
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
		if(idx >= size) return false;
		return ((bits[idx >>> 6] >>> (idx & 0b111111)) & 1) != 0;
	}
	
	public final int rank1(int pos) {
		if(pos < 0) return -1;
		if(pos == 0) return 0;
		if(pos >= size) return sum;
		final int mod = (pos-1) & 0b111111;
		final int idx = (pos-1) >>> 6;
		return rankBlock[idx] + Long.bitCount(bits[idx] << Long.SIZE-mod-1);
	}
	
	public final int rank0(int pos) {
		if(pos < 0) return -1;
		if(pos == 0) return 0;
		if(pos >= size) return size-sum;
		return pos-rank1(pos);
	}

	public final int select1(int rank) {
		if(rank < 0 || rank > sum) return -1;
		if(rank == 0) return 0;
		int idx = selectBlock1[rank >>> 6];
		for(; rankBlock[idx]<rank; idx++);
		idx--;
		return (idx<<6) + bitPos(bits[idx], rank-rankBlock[idx]) + 1;
	}
		
	public final int select0(int rank) {
		if(rank < 0 || rank > size-sum) return -1;
		if(rank == 0) return 0;
		int idx = selectBlock0[rank >>> 6];
		for(; (idx<<6)-rankBlock[idx]<rank; idx++);
		idx--;
		return (idx<<6) + bitPos(~bits[idx], rank-((idx<<6)-rankBlock[idx])) + 1;
	}
	
	public static final int bitPos(long bits, int n) {
		if(n <= 0 || Long.bitCount(bits) < n) return -1;
		for(; n>1; bits&=(bits-1), n--);
		return Long.bitCount(bits ^ (bits-1))-1;
	}
}
