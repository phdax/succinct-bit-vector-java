package jp.phdax.bitvector;

import java.util.LinkedList;
import java.util.List;

public class DenseSelectBlock implements ISelectBlock {
	
	private final int size;
	private final int sum;
	private final int blockSize;
	private final int[][] trees;
	private final int[] roughPositions;
	private final int branch;
	private final boolean reverse;
	private final int BLOCK_SIZE_MIN = 64; // == word size
	
	public DenseSelectBlock(long[] data, int sum, int size, boolean reverse) {
		
		this.size = size;
		this.sum = sum;
		this.reverse = reverse;
		this.blockSize = Math.max(square(log2(size)), BLOCK_SIZE_MIN);
		this.branch = Math.max(2, sqrt(log2(size))); //sqrt(log2(size))分木
		
		// if(0 <= sum <= blockSize) then blockLen = 1
		// if(blockSize+1 <= sum <= 2*blockSize) then blockLen = 2 
		// if(2*blockSize+1 <= sum <= 3*blockSize) then blockLen = 3
		// ...
		int blockLen = (sum-1) / blockSize + 1;
		this.trees = new int[blockLen][];	
		this.roughPositions = new int[blockLen];

		int cnt = 0;
		int blockIdx = 0;
		int prevPos = 0;
		List<Integer> smallBlocks = new LinkedList<>();
		for(int i=0; i<data.length; i++) {
			long bits = data[i];
			if(reverse) bits = reverse(bits, i, size);
			int smallBlock = Long.bitCount(bits);
			smallBlocks.add(smallBlock);
			cnt += smallBlock;
			if(cnt > blockSize || i == data.length-1) {
				roughPositions[blockIdx] = prevPos * Long.SIZE; // small block start
				trees[blockIdx] = accumrateAndStack(smallBlocks, branch);
				cnt = smallBlock - blockSize;
				prevPos = i;
				blockIdx++;
			}
		}
	}
	
	private final long reverse(long bits, int idx, int sum) {
		long rtn = ~bits;
		int nextRange = Math.min(sum - idx * 64, 64);
		if(nextRange < 64) rtn &= (-1 >>> (64 - nextRange));
		return rtn;
	}
	
	/**
	 * 指定した範囲（{@code start <= idx < end}）の配列値を{@code branch}ごとに合計し、<br>
	 * その値を末尾へセット...を繰り返します。<br>
	 * <pre>
	 * (例)<br>
	 * input:<br>
	 * smallBlocks=[0,1,2,3,0,1,0,2](list), start=0, end=8, branch=3<br>
	 * process:<br>
	 * [0,1,2,3,0,1,0,2] -> [0,1,2,3,0,1,0,2,0] (fill zero)
	 * [0,1,2,3,0,1,0,2,0] -> [0,1,2,3,0,1,0,2,0,3,4,2] (accumrateAndStackImpl)
	 * [0,1,2,3,0,1,0,2,0,3,4,2] -> [0,1,2,3,0,1,0,2,0,3,4,2,9] (accumrateAndStackImpl)<br>
	 * output:<br>
	 * [0,1,2,3,0,1,0,2,0,3,4,2,9](array)<br>
	 * </pre>
	 */
	public static final int[] accumrateAndStack(List<Integer> smallBlocks, int branch) {
		//キリよくn-way treeにするため、指定範囲をbranchのべき数にceilして増えた分はゼロ詰
		int ceiled = ceilToPowerOfN(Math.max(branch, smallBlocks.size()), branch);
		int len = geometricSeries(ceiled, branch);
		int[] rtn = new int[len];
		int i = 0;
		for(Integer smallBlock : smallBlocks) {
			rtn[i++] = smallBlock;
		}
		return accumrateAndStackImpl(rtn, 0, ceiled, branch);
	}
	public static final int[] accumrateAndStackImpl(int[] smallBlocks, int start, int end, int branch) {
		int cursor = end;
		if(end - start == 1) {
			return smallBlocks;
		}
		for(int i=start; i<end; i+=branch, cursor++) {
			for(int j=0; j<branch; j++) {					
				smallBlocks[cursor] += smallBlocks[i+j];
			}
		}
		return accumrateAndStackImpl(smallBlocks, end, cursor, branch);
	}
	
	@Override
	public int select(long[] data, int rank) {			
		if(rank < 0 || (reverse ? size-sum : sum) < rank) return -1;
		if(rank == 0) return 0;

		int blockIdx = (rank-1) / blockSize;
		int overrun = rank % blockSize;
		
		int pos = roughPositions[blockIdx];
		if(overrun == 0) return pos;
		pos += searchRemains(data, pos, branch, trees[blockIdx], overrun);
		return pos;
	}
	
	public static int searchRemains(long[] data, int roughPosition, int branch, int[] tree, int remains) {
		long result = searchTree(tree, branch, remains);
		int cursor = (int)(result & 0xFFFFFFFFL);
		remains = (int)(result >>> 32);
		long beInHere = data[roughPosition / Long.SIZE + cursor];
		int miniPos = miniSelect(beInHere, remains);
		return cursor * Long.SIZE + miniPos;
	}
	
	public static long searchTree(int[] tree, int branch, int remains) {
		int depth = expOfGeometricSeries(tree.length, branch);
		int cursor = tree.length-1;
		int buf = 0;
		for(int i=0; i<depth; i++) {
			for(int j=0; j<branch; j++) {
				if(remains <= buf + tree[cursor+j]) {
					if(i == depth-1) {
						cursor = cursor + j;
						break;
					}
					cursor = cursor - (int)Math.pow(branch, i+1) 
										+ j*(int)Math.pow(branch, i);
					break;
				}
				buf += tree[cursor+j];
			}
			//到達し得ない
		}
		remains -= buf;
		long ret = cursor;
		ret |= (long)remains << 32;
		return ret;
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
	
	public static final int ceilToPowerOfN(int obj, int n) {
		double exponent = Math.ceil(Math.log(obj) / Math.log(n));
		return (int)Math.pow(n, exponent);
	}
	
	/**
	 * 初期値{@code a}、公比{@code 1/x}の公比級数を求める
	 */
	public static final int geometricSeries(int a, int x) {
		int exp = (int)(Math.log(a) / Math.log(x)); //log_x(a)
		return ((int)Math.pow(x, exp+1)-1) / (x-1);
	}

	/**
	 * 初項1, 公比{@code base}の公比級数{@code gs}のべき数を計算して返す。<br>
	 * <br>
	 * (1-base^x)/(1-base) = gs<br>
	 * であるから、<br>
	 * x = log_base(gs*(base-1)+1)
	 *   = log(gs*(base-1)+1) / log(base)
	 */
	public static final int expOfGeometricSeries(int gs, int base) {
		return (int)(Math.log(gs*(base-1)+1) / Math.log(base));
	}

	public static final int miniSelect(long bits, int rank) {
		if(rank < 0 || Long.bitCount(bits) < rank) return -1;
		if(rank == 0) return 0; 
		for(; rank>1; bits&=(bits-1), rank--);
		return Long.bitCount(bits ^ (bits-1));
	}
}
