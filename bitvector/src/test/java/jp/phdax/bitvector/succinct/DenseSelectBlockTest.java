package jp.phdax.bitvector.succinct;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;

import org.junit.Test;

import jp.phdax.bitvector.succinct.DenseSelectBlock;

public class DenseSelectBlockTest {

	private static final DenseSelectBlock d(int sum, int size, boolean reverse, long... data) {
		return new DenseSelectBlock(data, sum, size, reverse);
	}
	
	@Test
	public void getTreeTest() {
		assertThat(d(3, 8, false, 0b01000101).getTrees(), is(ll(l(3,0,3))));
		assertThat(d(5, 8, true, 0b01000101).getTrees(), is(ll(l(5,0,5))));
		
		assertThat(d(8, 72, false, 0b01000101, 0b10111010).getTrees(), is(ll(l(3,5,8))));
		assertThat(d(64, 72, true, 0b01000101, 0b10111010).getTrees(), is(ll(l(61,3,64))));
		
		assertThat(d(8, 128, false, 0b01000101, 0b10111010).getTrees(), is(ll(l(3,5,8))));
		assertThat(d(120, 128, true, 0b01000101, 0b10111010).getTrees(), is(ll(l(61,3,64),l(56,0,56))));
	}
	
	@Test
	public void getRoughPositionsTest() {
		assertThat(d(3, 8, false, 0b01000101).getRoughPositions(), is(l(0)));
		assertThat(d(5, 8, true, 0b01000101).getRoughPositions(), is(l(0)));
		
		assertThat(d(8, 72, false, 0b01000101, 0b10111010).getRoughPositions(), is(l(0)));
		assertThat(d(64, 72, true, 0b01000101, 0b10111010).getRoughPositions(), is(l(0)));
		
		assertThat(d(8, 128, false, 0b01000101, 0b10111010).getRoughPositions(), is(l(0)));
		assertThat(d(120, 128, true, 0b01000101, 0b10111010).getRoughPositions(), is(l(0,72)));
	}
	
	private static final int[] l(int... v) {
		return v;
	}
	private static final int[][] ll(int[]... v) {
		return v;
	}
	
	@Test
	public void geometricSeriesTest() {
		assertThat(DenseSelectBlock.geometricSeries(2, 2), is(3));
		assertThat(DenseSelectBlock.geometricSeries(4, 2), is(7));
		assertThat(DenseSelectBlock.geometricSeries(8, 2), is(15));
		assertThat(DenseSelectBlock.geometricSeries(3, 3), is(4));
		assertThat(DenseSelectBlock.geometricSeries(9, 3), is(13));
		assertThat(DenseSelectBlock.geometricSeries(27, 3), is(40));
	}
	
	@Test
	public void ceilToPowerOfNTest() {
		assertThat(DenseSelectBlock.ceilToPowerOfN(2, 3), is(3));
		assertThat(DenseSelectBlock.ceilToPowerOfN(3, 3), is(3));
		assertThat(DenseSelectBlock.ceilToPowerOfN(4, 3), is(9));
		assertThat(DenseSelectBlock.ceilToPowerOfN(8, 3), is(9));
		assertThat(DenseSelectBlock.ceilToPowerOfN(9, 3), is(9));
		assertThat(DenseSelectBlock.ceilToPowerOfN(10, 3), is(27));
		assertThat(DenseSelectBlock.ceilToPowerOfN(26, 3), is(27));
		assertThat(DenseSelectBlock.ceilToPowerOfN(27, 3), is(27));
		assertThat(DenseSelectBlock.ceilToPowerOfN(28, 3), is(81));
	}
	
	@Test
	public void accumerateAndStackTest() {
		assertThat(DenseSelectBlock.accumrateAndStack(Arrays.asList(0,1,2,3,0,1,0,2,0), 3), 
				   is(new int[] {0,1,2,3,0,1,0,2,0,  3,4,2,  9}));
		assertThat(DenseSelectBlock.accumrateAndStack(Arrays.asList(0,1,2), 3), 
				   is(new int[] {0,1,2,  3}));
		assertThat(DenseSelectBlock.accumrateAndStack(Arrays.asList(0,1), 3), 
				   is(new int[] {0,1,0,  1}));
		assertThat(DenseSelectBlock.accumrateAndStack(Arrays.asList(0), 3), 
				   is(new int[] {0,0,0,  0}));
	}
	
	@Test
	public void searchTreeTest() {
		int[] blocks = new int[] {0,1,2,3,0,1,0,2,0,  3,4,2,  9};
		int branch = 3;
		assertThat(DenseSelectBlock.searchTree(blocks, branch, 0), is(v(0, 0)));
		assertThat(DenseSelectBlock.searchTree(blocks, branch, 1), is(v(1, 1)));
		assertThat(DenseSelectBlock.searchTree(blocks, branch, 2), is(v(2, 1)));
		assertThat(DenseSelectBlock.searchTree(blocks, branch, 3), is(v(2, 2)));
		assertThat(DenseSelectBlock.searchTree(blocks, branch, 4), is(v(3, 1)));
		assertThat(DenseSelectBlock.searchTree(blocks, branch, 5), is(v(3, 2)));
		assertThat(DenseSelectBlock.searchTree(blocks, branch, 6), is(v(3, 3)));
		assertThat(DenseSelectBlock.searchTree(blocks, branch, 7), is(v(5, 1)));
		assertThat(DenseSelectBlock.searchTree(blocks, branch, 8), is(v(7, 1)));
		assertThat(DenseSelectBlock.searchTree(blocks, branch, 9), is(v(7, 2)));
	}
	
	private static final long v(int cursor, int remain) {
		long rtn = cursor;
		rtn |= (long)remain << 32;
		return rtn;
	}
}
