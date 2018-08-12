package jp.phdax.bitvector.succinct;

public class SparseSelectBlock implements ISelectBlock {
	private final int[] pos;
	
	public SparseSelectBlock(long[] data, int sum, boolean reverse) {
		this.pos = new int[sum];
		int idx = 0;
		for(int i=0; i<data.length; i++) {
			long d = data[i];
			if(reverse) d = ~d;
			for(; d>0; d&=d-1) {
				pos[idx++] = Long.bitCount(d ^ d-1)-1;
			}
		}
	}
	
	@Override
	public int select(long[] data, int rank) {
		//rankの値域が0以上のため、逆関数であるselectは引数0を正常系として扱うべき
		if(rank < 0 || pos.length < rank) return -1;
		if(rank == 0) return 0;
		return pos[rank-1]; 
	}
}
