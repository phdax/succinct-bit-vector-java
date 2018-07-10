package jp.phdax.bitvector;

/**
 * 簡潔ビットベクトルの簡易的な実装クラスです。<br>
 * <br>
 * 下記書籍の定義に寄せていますが、<br>
 * 小ブロックのサイズを64に固定する、疎密の判定を大ブロックごとでなくデータ全体で行うなど、<br>
 * 実装の都合上変更している箇所があります。<br>
 * <br>
 * また、可読性を重視するため乗算剰余算はビット演算に置換していません。<br>
 * TODO 速度重視用に、疎密固定・ビット演算徹底・JNAを通してselectをpdep命令とtzcnt命令で実装したクラスを用意<br>
 * @see 定兼 邦彦 (2018), "簡潔データ構造", 共立出版, https://www.amazon.co.jp/dp/4320121740
 * @author phdax
 */
public class SuccinctBitVector {
	
	private final int size;
	private final long[] data;
	
	private final int largeBlockBits; // (log(n))^2
	private final int smallBlockBits; // log(n)/2
	private final int[] largeBlock; 
	private final byte[] smallBlock;
	
	private final ISelectBlock selectBlock1;
	private final ISelectBlock selectBlock0;
	private int sum = 0;
	private static final int WORD_SIZE = 64; // Long.bitCount()=popcnt命令を考慮
	
	public SuccinctBitVector(int size, long[] data) {
		this(size, data, square(log2(size)));
	}

	public SuccinctBitVector(int size, long[] data, int sparsenessRatio) {

		if(size < 0) throw new IllegalArgumentException("too small size : " + size);
		
		this.data = data;
		this.size = Math.min(size, data.length * Long.SIZE);
		
		largeBlockBits = Math.max(square(log2(size)), WORD_SIZE);
		int largeBlockLen = size / largeBlockBits;
		if(size % largeBlockBits > 0) largeBlockLen++;
		largeBlock = new int[largeBlockLen];

		smallBlockBits = WORD_SIZE;
		int smallBlockLen = size / smallBlockBits;
		if(size % smallBlockBits > 0) smallBlockLen++;
		smallBlock = new byte[smallBlockLen];
		
		buildRankBlock();
		
		//本来は大ブロックごとに疎密を定義するところ、簡素化のためデータ全体に対して適用している。
		//（大ブロック内の1ビット数が1/square(log2(size))を下回ることが疎の条件だが、これをデータ全体に読み替え）
		if(sum * sparsenessRatio < size) {
			selectBlock1 = new SparseSelectBlock(data, sum, false);
		} else {
			selectBlock1 = new DenseSelectBlock(data, sum, false);
		}
		if((size-sum) * sparsenessRatio < size) {
			selectBlock0 = new SparseSelectBlock(data, size-sum, true);
		} else {
			selectBlock0 = new DenseSelectBlock(data, size-sum, true);
		}
	}
	
	private void buildRankBlock() {
		int largeBlockIdx = 0;
		int smallBlockIdx = 0;
		byte smallSum = 0;
		for(int i=0; i<data.length; i++) {
			if(i * Long.SIZE % smallBlockBits == 0) {
				smallBlock[smallBlockIdx++] = smallSum;
			}
			if(i * Long.SIZE % largeBlockBits == 0) {
				largeBlock[largeBlockIdx++] = sum;
				smallSum = 0;
			}
			int popcnt = Long.bitCount(data[i]);
			sum += popcnt;
			smallSum += popcnt;
		}
	}

	public final boolean pos(int idx) {
		if(idx < 0) return false;
		if(size <= idx) return false;
		return ((data[idx/64] >>> (idx % 64)) & 1) != 0;
	}
	
	public int rank1(int pos) {
		if(pos < 0) return -1;
		if(size <= pos) return sum;
		int rank = largeBlock[pos/largeBlockBits];
		if(pos % largeBlockBits > 0) {
			rank += smallBlock[pos/smallBlockBits];
			if(pos % smallBlockBits > 0) {
				long d = data[pos/64];
				rank += Long.bitCount(d << (64 - (pos % 64)));
			}			
		} else {
			return rank;
		}
		return rank;
	}
	
	public int rank0(int pos) {
		if(pos < 0) return -1;
		if(size <= pos) return size - sum;
		return pos - rank1(pos);
	}
	
	public int select1(int rank) {
		return selectBlock1.select(rank);
	}
	
	public int select0(int rank) {
		return selectBlock0.select(rank);
	}
	
	private interface ISelectBlock {
		public int select(int rank);
	}
	
	private class SparseSelectBlock implements ISelectBlock {
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
		public int select(int rank) {
			//rankの値域が0以上のため、逆関数であるselectは引数0を正常系として扱うべき
			if(rank < 0 || pos.length < rank) return -1;
			if(rank == 0) return 0;
			return pos[rank-1]; 
		}
	}
	
	private class DenseSelectBlock implements ISelectBlock {
		private final int[] largeBlock; // ビットの位置ではなく、smallBlockの開始インデックスを記録
		private final byte[] smallBlock;
		public DenseSelectBlock(long[] data, int sum, boolean reverse) {		
			int largeBlockBits = Math.max(square(log2(size)), WORD_SIZE);
			int largeBlockLen = sum / largeBlockBits;
			if(sum % largeBlockBits > 0) largeBlockLen++;
			largeBlock = new int[largeBlockLen];
			
			int smallBlockBits = WORD_SIZE;
			int smallBlockLen = size / smallBlockBits;
			if(size % smallBlockBits > 0) smallBlockLen++;
			smallBlock = new byte[smallBlockLen];
			
			largeBlock[0] = 0;
			int largeBlockIdx = 1;
			int rank = 0;
			for(int i=0; i<data.length; i++) {
				long d = data[i];
				if(reverse) {
					d = ~d;
					int nextRange = Math.min(size-i*64, 64);
					if(nextRange < 64) d = d << 64-nextRange;					
				}
				int popcnt = Long.bitCount(d);
				rank += popcnt;
				smallBlock[i] = (byte)popcnt;
				if(rank > largeBlockBits) {
					int diff = rank - largeBlockBits;
					largeBlock[largeBlockIdx++] = i;
					rank = diff;
				}
			}	
		}
		@Override
		public int select(int rank) {
			return 0;
		}
	}
	
	private static final int log2(int n) {
		return (int)(Math.log(n) / Math.log(2));
	}
	private static final int square(int n) {
		return n*n;
	}
}
