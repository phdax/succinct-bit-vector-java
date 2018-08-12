package jp.phdax.bitvector.succinct;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import jp.phdax.bitvector.succinct.SuccinctBitVector;

public class SuccinctBitVectorTest {

	private static final SuccinctBitVector b(int size, long... bits) {
		return new SuccinctBitVector(size, bits);
	}
	
	@Test
	public void posTest() {
		SuccinctBitVector bv8 = b(8, 0b01000101);
		assertThat(bv8.pos(0), is(true));
		assertThat(bv8.pos(1), is(false));
		assertThat(bv8.pos(2), is(true));
		assertThat(bv8.pos(3), is(false));
		assertThat(bv8.pos(4), is(false));
		assertThat(bv8.pos(5), is(false));
		assertThat(bv8.pos(6), is(true));
		assertThat(bv8.pos(7), is(false));
		assertThat(bv8.pos(63), is(false));
		
		SuccinctBitVector bv72 = b(72, 0b01000101, 0b10111010);
		assertThat(bv72.pos(64+0), is(false));
		assertThat(bv72.pos(64+1), is(true));
		assertThat(bv72.pos(64+2), is(false));
		assertThat(bv72.pos(64+3), is(true));
		assertThat(bv72.pos(64+4), is(true));
		assertThat(bv72.pos(64+5), is(true));
		assertThat(bv72.pos(64+6), is(false));
		assertThat(bv72.pos(64+7), is(true));
		assertThat(bv72.pos(64+63), is(false));
		assertThat(bv72.pos(64+9999), is(false));
	}
	
	@Test
	public void rank1Test() {
		SuccinctBitVector bv8 = b(8, 0b01000101);
		assertThat(bv8.rank1(-1), is(-1));
		assertThat(bv8.rank1(0), is(0));
		assertThat(bv8.rank1(1), is(1));
		assertThat(bv8.rank1(2), is(1));
		assertThat(bv8.rank1(3), is(2));
		assertThat(bv8.rank1(4), is(2));
		assertThat(bv8.rank1(5), is(2));
		assertThat(bv8.rank1(6), is(2));
		assertThat(bv8.rank1(7), is(3));
		assertThat(bv8.rank1(8), is(3));
		assertThat(bv8.rank1(9999), is(3));
		
		SuccinctBitVector bv64 = b(64, 0b01000101);
		assertThat(bv64.rank1(-1), is(-1));
		assertThat(bv64.rank1(0), is(0));
		assertThat(bv64.rank1(1), is(1));
		assertThat(bv64.rank1(2), is(1));
		assertThat(bv64.rank1(3), is(2));
		assertThat(bv64.rank1(4), is(2));
		assertThat(bv64.rank1(5), is(2));
		assertThat(bv64.rank1(6), is(2));
		assertThat(bv64.rank1(7), is(3));
		assertThat(bv64.rank1(64), is(3));
		assertThat(bv64.rank1(9999), is(3));
		
		SuccinctBitVector bv72 = b(72, 0b01000101, 0b10111010);
		assertThat(bv72.rank1(64+0), is(3));
		assertThat(bv72.rank1(64+1), is(3));
		assertThat(bv72.rank1(64+2), is(4));
		assertThat(bv72.rank1(64+3), is(4));
		assertThat(bv72.rank1(64+4), is(5));
		assertThat(bv72.rank1(64+5), is(6));
		assertThat(bv72.rank1(64+6), is(7));
		assertThat(bv72.rank1(64+7), is(7));
		assertThat(bv72.rank1(64+8), is(8));
		assertThat(bv72.rank1(64+9999), is(8));

		SuccinctBitVector bv128 = b(128, 0b01000101, 0b10111010);
		assertThat(bv128.rank1(64+0), is(3));
		assertThat(bv128.rank1(64+1), is(3));
		assertThat(bv128.rank1(64+2), is(4));
		assertThat(bv128.rank1(64+3), is(4));
		assertThat(bv128.rank1(64+4), is(5));
		assertThat(bv128.rank1(64+5), is(6));
		assertThat(bv128.rank1(64+6), is(7));
		assertThat(bv128.rank1(64+7), is(7));
		assertThat(bv128.rank1(64+64), is(8));
		assertThat(bv128.rank1(64+9999), is(8));
	}
	
	@Test
	public void rank0Test() {
		SuccinctBitVector bv8 = b(8, 0b01000101);
		assertThat(bv8.rank0(-1), is(-1));
		assertThat(bv8.rank0(0), is(0));
		assertThat(bv8.rank0(1), is(0));
		assertThat(bv8.rank0(2), is(1));
		assertThat(bv8.rank0(3), is(1));
		assertThat(bv8.rank0(4), is(2));
		assertThat(bv8.rank0(5), is(3));
		assertThat(bv8.rank0(6), is(4));
		assertThat(bv8.rank0(7), is(4));
		assertThat(bv8.rank0(8), is(5));
		assertThat(bv8.rank0(9999), is(5));
		
		SuccinctBitVector bv64 = b(64, 0b01000101);
		assertThat(bv64.rank0(-1), is(-1));
		assertThat(bv64.rank0(0), is(0));
		assertThat(bv64.rank0(1), is(0));
		assertThat(bv64.rank0(2), is(1));
		assertThat(bv64.rank0(3), is(1));
		assertThat(bv64.rank0(4), is(2));
		assertThat(bv64.rank0(5), is(3));
		assertThat(bv64.rank0(6), is(4));
		assertThat(bv64.rank0(7), is(4));
		assertThat(bv64.rank0(64), is(61));
		assertThat(bv64.rank0(9999), is(61));

		SuccinctBitVector bv72 = b(72, 0b01000101, 0b10111010);
		assertThat(bv72.rank0(64+0), is(61));
		assertThat(bv72.rank0(64+1), is(62));
		assertThat(bv72.rank0(64+2), is(62));
		assertThat(bv72.rank0(64+3), is(63));
		assertThat(bv72.rank0(64+4), is(63));
		assertThat(bv72.rank0(64+5), is(63));
		assertThat(bv72.rank0(64+6), is(63));
		assertThat(bv72.rank0(64+7), is(64));
		assertThat(bv72.rank0(64+8), is(64));
		assertThat(bv72.rank0(64+9999), is(64));
		
		SuccinctBitVector bv128 = b(128, 0b01000101, 0b10111010);
		assertThat(bv128.rank0(64+0), is(61));
		assertThat(bv128.rank0(64+1), is(62));
		assertThat(bv128.rank0(64+2), is(62));
		assertThat(bv128.rank0(64+3), is(63));
		assertThat(bv128.rank0(64+4), is(63));
		assertThat(bv128.rank0(64+5), is(63));
		assertThat(bv128.rank0(64+6), is(63));
		assertThat(bv128.rank0(64+7), is(64));
		assertThat(bv128.rank0(64+8), is(64));
		assertThat(bv128.rank0(64+9999), is(120));
	}
	
	@Test
	public void select1Test() {
		SuccinctBitVector bv8 = b(8, 0b01000101);
		assertThat(bv8.select1(0), is(0));
		assertThat(bv8.select1(1), is(1));
		assertThat(bv8.select1(2), is(3));
		assertThat(bv8.select1(3), is(7));
		assertThat(bv8.select1(4), is(-1));
		
		SuccinctBitVector bv64 = b(64, 0b01000101);
		assertThat(bv64.select1(0), is(0));
		assertThat(bv64.select1(1), is(1));
		assertThat(bv64.select1(2), is(3));
		assertThat(bv64.select1(3), is(7));
		assertThat(bv64.select1(4), is(-1));
		
		SuccinctBitVector bv72 = b(128, 0b01000101, 0b10111010);
		assertThat(bv72.select1(0), is(0));
		assertThat(bv72.select1(1), is(1));
		assertThat(bv72.select1(2), is(3));
		assertThat(bv72.select1(3), is(7));
		assertThat(bv72.select1(4), is(64+2));
		assertThat(bv72.select1(5), is(64+4));
		assertThat(bv72.select1(6), is(64+5));
		assertThat(bv72.select1(7), is(64+6));
		assertThat(bv72.select1(8), is(64+8));
		assertThat(bv72.select1(9), is(-1));

		SuccinctBitVector bv128 = b(128, 0b01000101, 0b10111010);
		assertThat(bv128.select1(0), is(0));
		assertThat(bv128.select1(1), is(1));
		assertThat(bv128.select1(2), is(3));
		assertThat(bv128.select1(3), is(7));
		assertThat(bv128.select1(4), is(64+2));
		assertThat(bv128.select1(5), is(64+4));
		assertThat(bv128.select1(6), is(64+5));
		assertThat(bv128.select1(7), is(64+6));
		assertThat(bv128.select1(8), is(64+8));
		assertThat(bv128.select1(9), is(-1));
	}
	
	@Test
	public void select0Test() {
		SuccinctBitVector bv8 = b(8, 0b01000101);
		assertThat(bv8.select0(0), is(0));
		assertThat(bv8.select0(1), is(2));
		assertThat(bv8.select0(2), is(4));
		assertThat(bv8.select0(3), is(5));
		assertThat(bv8.select0(4), is(6));
		assertThat(bv8.select0(5), is(8));
		assertThat(bv8.select0(6), is(-1));
		
		SuccinctBitVector bv64 = b(64, 0b01000101);
		assertThat(bv64.select0(0), is(0));
		assertThat(bv64.select0(1), is(2));
		assertThat(bv64.select0(2), is(4));
		assertThat(bv64.select0(3), is(5));
		assertThat(bv64.select0(4), is(6));
		assertThat(bv64.select0(5), is(8));
		assertThat(bv64.select0(61), is(64));
		assertThat(bv64.select0(62), is(-1));
		
		SuccinctBitVector bv72 = b(72, 0b01000101, 0b10111010);
		assertThat(bv72.select0(62), is(64+1));
		assertThat(bv72.select0(63), is(64+3));
		assertThat(bv72.select0(64), is(64+7));
		assertThat(bv72.select0(65), is(-1));

		SuccinctBitVector bv128 = b(128, 0b01000101, 0b10111010);
		assertThat(bv128.select0(62), is(64+1));
		assertThat(bv128.select0(63), is(64+3));
		assertThat(bv128.select0(64), is(64+7));
		assertThat(bv128.select0(65), is(64+9));
		assertThat(bv128.select0(119), is(127));
		assertThat(bv128.select0(120), is(128));
		assertThat(bv128.select0(121), is(-1));
	}
}
