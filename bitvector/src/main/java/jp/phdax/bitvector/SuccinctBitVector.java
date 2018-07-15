package jp.phdax.bitvector;

/**
 * 簡潔ビットベクトルの簡易的な実装クラスです。<br>
 * <br>
 * 下記書籍の定義に寄せていますが、<br>
 * 小ブロックのサイズを64に固定する、疎密の判定を大ブロックごとでなくデータ全体で行うなど、<br>
 * 実装の都合上変更している箇所があります。<br>
 * <br>
 * また、可読性を重視するため乗算剰余算はビット演算に置換していません。<br>
 * TODO 速度重視用に、疎密固定・ビット演算化・JNAを通してselectをpdep命令とtzcnt命令で実装したクラスを用意<br>
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
		
		private final int largeBlockBits;
		private final int[] largeBlock2smallBlock; // ビットの位置ではなく、smallBlockの開始インデックスを記録
		private final int[] largeBlock;
		private final int[] smallBlock;
		private final int way;
		private final boolean reverse;
		
		public DenseSelectBlock(long[] data, int sum, boolean reverse) {
			
			this.reverse = reverse;
			
			largeBlockBits = Math.max(square(log2(size)), WORD_SIZE);
			int largeBlockLen = sum / largeBlockBits;
			if(sum % largeBlockBits > 0) largeBlockLen++;
			largeBlock2smallBlock = new int[largeBlockLen];
			largeBlock = new int[largeBlockLen];
						
			//各大ブロックに紐づく木構造のノード分を加味する。
			//リサイズや２度のループが手間なため、最悪ケースで確保（＝大ブロックが偏り、ほぼ全体で大きな１つの木になる場合）
			//それでもサイズはリーフ長の定数倍にとどまる（初期値がリーフ長、公比がsqrt(log2(size))の公比級数に等しい）
			int smallBlockBits = WORD_SIZE;
			int smallBlockLen = size / smallBlockBits;
			if(size % smallBlockBits > 0) smallBlockLen++;
			way = Math.max(2, sqrt(log2(size))); //sqrt(log2(size))分木
			int smallBlockTreeLen = geometricSeries(smallBlockLen, way);
			smallBlock = new int[smallBlockTreeLen];
			
			largeBlock2smallBlock[0] = 0;
			int largeBlockIdx = 1;
			int smallBlockIdx = 0;
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
				smallBlock[smallBlockIdx++] = (byte)popcnt;
				if(rank > largeBlockBits) {
					int overrun = rank - largeBlockBits;
					//木構造の分をsmallBlockに追加していく
					largeBlock[largeBlockIdx] = i;
					largeBlock2smallBlock[largeBlockIdx] = smallBlockIdx;
					smallBlockIdx = accumrateAndStack(smallBlock, largeBlock2smallBlock[largeBlockIdx-1], largeBlock2smallBlock[largeBlockIdx], way);
					largeBlockIdx++;
					rank = overrun;
				}
			}	
		}
		
		/**
		 * 指定した範囲（{@code start <= idx < end}）の配列値を{@code way}で分割し、<br>
		 * それぞれの範囲の合計値を先頭順に配列の末尾へセット...を繰り返します。<br>
		 * 最後にセットした配列のインデックス+1を返します。
		 * <pre>
		 * (例)<br>
		 * input:<br>
		 * smallBlock=[0,1,2,3,0,1,0,2], start=0, end=8, way=3<br>
		 * process:<br>
		 * [0,1,2,3,0,1,0,2] -> [0,1,2,3,0,1,0,2,0] (fill zero)
		 * [0,1,2,3,0,1,0,2,0] -> [0,1,2,3,0,1,0,2,0,3,4,2] (accumrateAndStackImpl)
		 * [0,1,2,3,0,1,0,2,0,3,4,2] -> [0,1,2,3,0,1,0,2,0,3,4,2,9] (accumrateAndStackImpl)<br>
		 * output:<br>
		 * 13 (smallBlock cursor)<br>
		 * </pre>
		 */
		private int accumrateAndStack(int[] smallBlock, int start, int end, int way) {
			//きれいなn-way treeにするため、
			//指定範囲をwayのべき数にceilして増えた分はゼロ詰（インデックスずらすだけ。nop）
			int range = end - start;
			int ceiledRange = ceilToPowerOf(range, way);
			return accumrateAndStackImpl(smallBlock, start, start+ceiledRange, way);
		}
		private int accumrateAndStackImpl(int[] smallBlock, int start, int end, int way) {
			int cursor = end;
			if(end - start == 1) {
				return cursor;
			}
			for(int i=start; i<end; i+=way) {
				smallBlock[cursor++] = smallBlock[i] + smallBlock[i+1] + smallBlock[i+2];
			}
			return accumrateAndStackImpl(smallBlock, end, cursor, way);
		}
		
		@Override
		public int select(int rank) {			
			if(rank < 0 || (reverse ? size-sum : sum) < rank) return -1;
			if(rank == 0) return 0;

			int largeBlockIdx = rank/largeBlockBits;
			int mod = rank%largeBlockBits;
			
			int smallBlockEnd = largeBlock2smallBlock[largeBlockIdx];
			int smallBlockStart = largeBlockIdx > 0 ? largeBlock2smallBlock[largeBlockIdx-1]+1 : 0;
			
			int smallBlockSum = 0;
			int depth = 0;
			int cursor = smallBlockEnd;
			roop:
			while(true) {
				for(int i=0; i<way; i++) {
					int b = smallBlock[cursor+i];
					if(mod <= b + smallBlockSum) {
						depth++;
						int nextCursor = cursor - (int)Math.pow(way, depth) + i*(int)Math.pow(way, depth-1);
						if(nextCursor < smallBlockStart) {
							cursor += i;
							break roop;
						} else {
							cursor = nextCursor;
							continue roop;							
						}
					}
					smallBlockSum += b;
				}
				smallBlockSum = 0;
				break;
			}
			
			int remainRank = mod - smallBlockSum;			
			int dataIdx = largeBlock[largeBlockIdx];
			dataIdx += cursor - smallBlockStart;
			long d = data[dataIdx];
			if(reverse) {
				d = ~d;
			}
			int bitPos = bitPos(d, remainRank);
			return dataIdx * 64 + bitPos;
		}
	}
	
	private static final int log2(int n) {
		return (int)(Math.log(n) / Math.log(2));
	}
	private static final int square(int n) {
		return n*n;
	}
	private static final int sqrt(int n) {
		return (int)Math.sqrt(n);
	}
	public static final int ceilToPowerOf(int n, int base) {
		double exponent = Math.ceil(Math.log(n) / Math.log(base));
		return (int)Math.pow(base, exponent);
	}
	/**
	 * 初期値{@code a}、公比{@code x}の公比級数を求める
	 */
	private static final int geometricSeries(int a, int x) {
		return a * x / (x-1);
	}
	public static final int bitPos(long bits, int n) {
		if(n <= 0 || Long.bitCount(bits) < n) return -1;
		for(; n>1; bits&=(bits-1), n--);
		return Long.bitCount(bits ^ (bits-1));
	}
}
