package jp.phdax.bitvector;

import org.junit.Test;
import static org.junit.Assert.assertThat;
import static org.hamcrest.core.Is.is;

public class BitVectorTest {
	
	@Test
	public void posTest() {
		posTestImpl(l(0b01000101), 0, "is", true);
		posTestImpl(l(0b01000101), 1, "is", false);
		posTestImpl(l(0b01000101), 2, "is", true);
		posTestImpl(l(0b01000101), 3, "is", false);
		posTestImpl(l(0b01000101), 4, "is", false);
		posTestImpl(l(0b01000101), 5, "is", false);
		posTestImpl(l(0b01000101), 6, "is", true);
		posTestImpl(l(0b01000101), 7, "is", false);
		posTestImpl(l(0b01000101), 63, "is", false);
		
		posTestImpl(l(0b01000101, 0b10111010), 64+0, "is", false);
		posTestImpl(l(0b01000101, 0b10111010), 64+1, "is", true);
		posTestImpl(l(0b01000101, 0b10111010), 64+2, "is", false);
		posTestImpl(l(0b01000101, 0b10111010), 64+3, "is", true);
		posTestImpl(l(0b01000101, 0b10111010), 64+4, "is", true);
		posTestImpl(l(0b01000101, 0b10111010), 64+5, "is", true);
		posTestImpl(l(0b01000101, 0b10111010), 64+6, "is", false);
		posTestImpl(l(0b01000101, 0b10111010), 64+7, "is", true);
		posTestImpl(l(0b01000101, 0b10111010), 64+63, "is", false);
		posTestImpl(l(0b01000101, 0b10111010), 64+9999, "is", false);
	}
	private void posTestImpl(long[] bits, int pos, String is, boolean exp) {
		BitVector bv = new BitVector(bits.length*Long.SIZE, bits);
		boolean act = bv.pos(pos);
		assertThat(act, is(exp));
	}
		
	@Test
	public void rank1Test() {
		rank1TestImpl(l(0b01000101), -1, "is", 0);
		rank1TestImpl(l(0b01000101), 0, "is", 1);
		rank1TestImpl(l(0b01000101), 1, "is", 1);
		rank1TestImpl(l(0b01000101), 2, "is", 2);
		rank1TestImpl(l(0b01000101), 3, "is", 2);
		rank1TestImpl(l(0b01000101), 4, "is", 2);
		rank1TestImpl(l(0b01000101), 5, "is", 2);
		rank1TestImpl(l(0b01000101), 6, "is", 3);
		rank1TestImpl(l(0b01000101), 7, "is", 3);
		rank1TestImpl(l(0b01000101), 64, "is", 3);
		rank1TestImpl(l(0b01000101), 9999, "is", 3);
		
		rank1TestImpl(l(0b01000101, 0b10111010), 64+0, "is", 3);
		rank1TestImpl(l(0b01000101, 0b10111010), 64+1, "is", 4);
		rank1TestImpl(l(0b01000101, 0b10111010), 64+2, "is", 4);
		rank1TestImpl(l(0b01000101, 0b10111010), 64+3, "is", 5);
		rank1TestImpl(l(0b01000101, 0b10111010), 64+4, "is", 6);
		rank1TestImpl(l(0b01000101, 0b10111010), 64+5, "is", 7);
		rank1TestImpl(l(0b01000101, 0b10111010), 64+6, "is", 7);
		rank1TestImpl(l(0b01000101, 0b10111010), 64+7, "is", 8);
		rank1TestImpl(l(0b01000101, 0b10111010), 64+64, "is", 8);
		rank1TestImpl(l(0b01000101, 0b10111010), 64+9999, "is", 8);
	}
	private void rank1TestImpl(long[] bits, int pos, String is, int exp) {
		BitVector bv = new BitVector(bits.length*Long.SIZE, bits);
		int act = bv.rank1(pos);
		assertThat(act, is(exp));
	}
	
	@Test
	public void rank0Test() {
		rank0TestImpl(l(0b01000101), -1, "is", 0);
		rank0TestImpl(l(0b01000101), 0, "is", 0);
		rank0TestImpl(l(0b01000101), 1, "is", 1);
		rank0TestImpl(l(0b01000101), 2, "is", 1);
		rank0TestImpl(l(0b01000101), 3, "is", 2);
		rank0TestImpl(l(0b01000101), 4, "is", 3);
		rank0TestImpl(l(0b01000101), 5, "is", 4);
		rank0TestImpl(l(0b01000101), 6, "is", 4);
		rank0TestImpl(l(0b01000101), 7, "is", 5);
		rank0TestImpl(l(0b01000101), 64, "is", 61);
		rank0TestImpl(l(0b01000101), 9999, "is", 61);
		
		rank0TestImpl(l(0b01000101, 0b10111010), 64+0, "is", 62);
		rank0TestImpl(l(0b01000101, 0b10111010), 64+1, "is", 62);
		rank0TestImpl(l(0b01000101, 0b10111010), 64+2, "is", 63);
		rank0TestImpl(l(0b01000101, 0b10111010), 64+3, "is", 63);
		rank0TestImpl(l(0b01000101, 0b10111010), 64+4, "is", 63);
		rank0TestImpl(l(0b01000101, 0b10111010), 64+5, "is", 63);
		rank0TestImpl(l(0b01000101, 0b10111010), 64+6, "is", 64);
		rank0TestImpl(l(0b01000101, 0b10111010), 64+7, "is", 64);
		rank0TestImpl(l(0b01000101, 0b10111010), 64+64, "is", 120);
		rank0TestImpl(l(0b01000101, 0b10111010), 64+9999, "is", 120);
	}
	private void rank0TestImpl(long[] bits, int pos, String is, int exp) {
		BitVector bv = new BitVector(bits.length*Long.SIZE, bits);
		int act = bv.rank0(pos);
		assertThat(act, is(exp));
	}
	
	@Test
	public void select1Test() {
		select1TestImpl(l(0b01000101), 0, "is", -1);
		select1TestImpl(l(0b01000101), 1, "is", 0);
		select1TestImpl(l(0b01000101), 2, "is", 2);
		select1TestImpl(l(0b01000101), 3, "is", 6);
		select1TestImpl(l(0b01000101), 4, "is", -1);
		
		select1TestImpl(l(0b01000101, 0b10111010), 0, "is", -1);
		select1TestImpl(l(0b01000101, 0b10111010), 1, "is", 0);
		select1TestImpl(l(0b01000101, 0b10111010), 2, "is", 2);
		select1TestImpl(l(0b01000101, 0b10111010), 3, "is", 6);
		select1TestImpl(l(0b01000101, 0b10111010), 4, "is", 64+1);
		select1TestImpl(l(0b01000101, 0b10111010), 5, "is", 64+3);
		select1TestImpl(l(0b01000101, 0b10111010), 6, "is", 64+4);
		select1TestImpl(l(0b01000101, 0b10111010), 7, "is", 64+5);
		select1TestImpl(l(0b01000101, 0b10111010), 8, "is", 64+7);
		select1TestImpl(l(0b01000101, 0b10111010), 9, "is", -1);
	}
	private void select1TestImpl(long[] bits, int rank, String is, int exp) {
		BitVector bv = new BitVector(bits.length*Long.SIZE, bits);
		int act = bv.select1(rank);
		assertThat(act, is(exp));	
	}
	
	@Test
	public void bitPosTest() {
		assertThat(BitVector.bitPos(0b0, 0), is(-1));
		assertThat(BitVector.bitPos(0b0, 1), is(-1));
		assertThat(BitVector.bitPos(0b00010000, 1), is(4));
		assertThat(BitVector.bitPos(0b00010010, 2), is(4));
		assertThat(BitVector.bitPos(0b00010010, 3), is(-1));
		assertThat(BitVector.bitPos(0b11111111, 8), is(7));
		assertThat(BitVector.bitPos(0b11111111, 9), is(-1));
		assertThat(BitVector.bitPos(0b1111111111111111111111111111111111111111111111111111111111111111L, 64), is(63));
		assertThat(BitVector.bitPos(0b1111111111111111111111111111111111111111111111111111111111111111L, 65), is(-1));	
	}
	
	private static long[] l(long... longs) {
		return longs;
	}
}
