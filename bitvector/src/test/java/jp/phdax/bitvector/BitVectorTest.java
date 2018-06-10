package jp.phdax.bitvector;

import org.junit.Test;
import static org.junit.Assert.assertThat;
import static org.hamcrest.core.Is.is;

public class BitVectorTest {
	
	private static final BitVector b(int size, long... bits) {
		return new BitVector(size, bits);
	}
	
	@Test
	public void sizeTest() {
		BitVector bv8 = b(8, 0b01000101);
		assertThat(bv8.size(), is(8));
		BitVector bv72 = b(72, 0b01000101, 0b10111010);
		assertThat(bv72.size(), is(72));
	}
	
	@Test
	public void bitCountTest() {
		BitVector bv8 = b(8, 0b01000101);
		assertThat(bv8.bitCount(), is(3));
		BitVector bv72 = b(72, 0b01000101, 0b10111010);
		assertThat(bv72.bitCount(), is(8));
	}
	
	@Test
	public void posTest() {
		BitVector bv8 = b(8, 0b01000101);
		assertThat(bv8.pos(0), is(true));
		assertThat(bv8.pos(1), is(false));
		assertThat(bv8.pos(2), is(true));
		assertThat(bv8.pos(3), is(false));
		assertThat(bv8.pos(4), is(false));
		assertThat(bv8.pos(5), is(false));
		assertThat(bv8.pos(6), is(true));
		assertThat(bv8.pos(7), is(false));
		assertThat(bv8.pos(63), is(false));
		
		BitVector bv72 = b(72, 0b01000101, 0b10111010);
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
		BitVector bv8 = b(8, 0b01000101);
		assertThat(bv8.rank1(-1), is(0));
		assertThat(bv8.rank1(0), is(1));
		assertThat(bv8.rank1(1), is(1));
		assertThat(bv8.rank1(2), is(2));
		assertThat(bv8.rank1(3), is(2));
		assertThat(bv8.rank1(4), is(2));
		assertThat(bv8.rank1(5), is(2));
		assertThat(bv8.rank1(6), is(3));
		assertThat(bv8.rank1(7), is(3));
		assertThat(bv8.rank1(8), is(3));
		assertThat(bv8.rank1(9999), is(3));
		
		BitVector bv64 = b(64, 0b01000101);
		assertThat(bv64.rank1(-1), is(0));
		assertThat(bv64.rank1(0), is(1));
		assertThat(bv64.rank1(1), is(1));
		assertThat(bv64.rank1(2), is(2));
		assertThat(bv64.rank1(3), is(2));
		assertThat(bv64.rank1(4), is(2));
		assertThat(bv64.rank1(5), is(2));
		assertThat(bv64.rank1(6), is(3));
		assertThat(bv64.rank1(7), is(3));
		assertThat(bv64.rank1(64), is(3));
		assertThat(bv64.rank1(9999), is(3));
		
		BitVector bv72 = b(72, 0b01000101, 0b10111010);
		assertThat(bv72.rank1(64+0), is(3));
		assertThat(bv72.rank1(64+1), is(4));
		assertThat(bv72.rank1(64+2), is(4));
		assertThat(bv72.rank1(64+3), is(5));
		assertThat(bv72.rank1(64+4), is(6));
		assertThat(bv72.rank1(64+5), is(7));
		assertThat(bv72.rank1(64+6), is(7));
		assertThat(bv72.rank1(64+7), is(8));
		assertThat(bv72.rank1(64+8), is(8));
		assertThat(bv72.rank1(64+9999), is(8));
		
		BitVector bv128 = b(128, 0b01000101, 0b10111010);
		assertThat(bv128.rank1(64+0), is(3));
		assertThat(bv128.rank1(64+1), is(4));
		assertThat(bv128.rank1(64+2), is(4));
		assertThat(bv128.rank1(64+3), is(5));
		assertThat(bv128.rank1(64+4), is(6));
		assertThat(bv128.rank1(64+5), is(7));
		assertThat(bv128.rank1(64+6), is(7));
		assertThat(bv128.rank1(64+7), is(8));
		assertThat(bv128.rank1(64+64), is(8));
		assertThat(bv128.rank1(64+9999), is(8));
	}
	
	@Test
	public void rank0Test() {
		BitVector bv8 = b(8, 0b01000101);
		assertThat(bv8.rank0(-1), is(0));
		assertThat(bv8.rank0(0), is(0));
		assertThat(bv8.rank0(1), is(1));
		assertThat(bv8.rank0(2), is(1));
		assertThat(bv8.rank0(3), is(2));
		assertThat(bv8.rank0(4), is(3));
		assertThat(bv8.rank0(5), is(4));
		assertThat(bv8.rank0(6), is(4));
		assertThat(bv8.rank0(7), is(5));
		assertThat(bv8.rank0(8), is(5));
		assertThat(bv8.rank0(9999), is(5));
		
		BitVector bv64 = b(64, 0b01000101);
		assertThat(bv64.rank0(-1), is(0));
		assertThat(bv64.rank0(0), is(0));
		assertThat(bv64.rank0(1), is(1));
		assertThat(bv64.rank0(2), is(1));
		assertThat(bv64.rank0(3), is(2));
		assertThat(bv64.rank0(4), is(3));
		assertThat(bv64.rank0(5), is(4));
		assertThat(bv64.rank0(6), is(4));
		assertThat(bv64.rank0(7), is(5));
		assertThat(bv64.rank0(64), is(61));
		assertThat(bv64.rank0(9999), is(61));
		
		BitVector bv72 = b(72, 0b01000101, 0b10111010);
		assertThat(bv72.rank0(64+0), is(62));
		assertThat(bv72.rank0(64+1), is(62));
		assertThat(bv72.rank0(64+2), is(63));
		assertThat(bv72.rank0(64+3), is(63));
		assertThat(bv72.rank0(64+4), is(63));
		assertThat(bv72.rank0(64+5), is(63));
		assertThat(bv72.rank0(64+6), is(64));
		assertThat(bv72.rank0(64+7), is(64));
		assertThat(bv72.rank0(64+8), is(64));
		assertThat(bv72.rank0(64+9999), is(64));
		
		BitVector bv128 = b(128, 0b01000101, 0b10111010);
		assertThat(bv128.rank0(64+0), is(62));
		assertThat(bv128.rank0(64+1), is(62));
		assertThat(bv128.rank0(64+2), is(63));
		assertThat(bv128.rank0(64+3), is(63));
		assertThat(bv128.rank0(64+4), is(63));
		assertThat(bv128.rank0(64+5), is(63));
		assertThat(bv128.rank0(64+6), is(64));
		assertThat(bv128.rank0(64+7), is(64));
		assertThat(bv128.rank0(64+8), is(65));
		assertThat(bv128.rank0(64+9999), is(120));
	}
	
	@Test
	public void select1Test() {
		BitVector bv8 = b(8, 0b01000101);
		assertThat(bv8.select1(0), is(-1));
		assertThat(bv8.select1(1), is(0));
		assertThat(bv8.select1(2), is(2));
		assertThat(bv8.select1(3), is(6));
		assertThat(bv8.select1(4), is(-1));
		
		BitVector bv64 = b(64, 0b01000101);
		assertThat(bv64.select1(0), is(-1));
		assertThat(bv64.select1(1), is(0));
		assertThat(bv64.select1(2), is(2));
		assertThat(bv64.select1(3), is(6));
		assertThat(bv64.select1(4), is(-1));
		
		BitVector bv72 = b(128, 0b01000101, 0b10111010);
		assertThat(bv72.select1(0), is(-1));
		assertThat(bv72.select1(1), is(0));
		assertThat(bv72.select1(2), is(2));
		assertThat(bv72.select1(3), is(6));
		assertThat(bv72.select1(4), is(64+1));
		assertThat(bv72.select1(5), is(64+3));
		assertThat(bv72.select1(6), is(64+4));
		assertThat(bv72.select1(7), is(64+5));
		assertThat(bv72.select1(8), is(64+7));
		assertThat(bv72.select1(9), is(-1));
		
		BitVector bv128 = b(128, 0b01000101, 0b10111010);
		assertThat(bv128.select1(0), is(-1));
		assertThat(bv128.select1(1), is(0));
		assertThat(bv128.select1(2), is(2));
		assertThat(bv128.select1(3), is(6));
		assertThat(bv128.select1(4), is(64+1));
		assertThat(bv128.select1(5), is(64+3));
		assertThat(bv128.select1(6), is(64+4));
		assertThat(bv128.select1(7), is(64+5));
		assertThat(bv128.select1(8), is(64+7));
		assertThat(bv128.select1(9), is(-1));
	}
	
	@Test
	public void select0Test() {
		BitVector bv8 = b(8, 0b01000101);
		assertThat(bv8.select0(0), is(-1));
		assertThat(bv8.select0(1), is(1));
		assertThat(bv8.select0(2), is(3));
		assertThat(bv8.select0(3), is(4));
		assertThat(bv8.select0(4), is(5));
		assertThat(bv8.select0(5), is(7));
		assertThat(bv8.select0(6), is(-1));
		
		BitVector bv64 = b(64, 0b01000101);
		assertThat(bv64.select0(0), is(-1));
		assertThat(bv64.select0(1), is(1));
		assertThat(bv64.select0(2), is(3));
		assertThat(bv64.select0(3), is(4));
		assertThat(bv64.select0(4), is(5));
		assertThat(bv64.select0(5), is(7));
		assertThat(bv64.select0(61), is(63));
		assertThat(bv64.select0(62), is(-1));

		BitVector bv72 = b(72, 0b01000101, 0b10111010);
		assertThat(bv72.select0(62), is(64+0));
		assertThat(bv72.select0(63), is(64+2));
		assertThat(bv72.select0(64), is(64+6));
		assertThat(bv72.select0(65), is(-1));

		BitVector bv128 = b(128, 0b01000101, 0b10111010);
		assertThat(bv128.select0(62), is(64+0));
		assertThat(bv128.select0(63), is(64+2));
		assertThat(bv128.select0(64), is(64+6));
		assertThat(bv128.select0(120), is(127));
		assertThat(bv128.select0(121), is(-1));
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
	
	@Test
	public void reverseTest() {
		assertThat(BitVector.bitReverse(1L), is(Long.MIN_VALUE));
		assertThat(BitVector.bitReverse(-1L), is(-1L));
		assertThat(BitVector.bitReverse(0xF0F0F0F0F0F0F0F0L), is(0x0F0F0F0F0F0F0F0FL));
		assertThat(BitVector.bitReverse(0x3333333333333333L), is(0xCCCCCCCCCCCCCCCCL));
		assertThat(BitVector.bitReverse(0b1110110000L), is(0b00001101110000000000000000000000000000000000000000000000000000000000L));
	}
	
	@Test
	public void toStringTest() {
		BitVector bv4 = b(4, 0b00000101L);
		assertThat(bv4.toString(), is("[1010]"));
		BitVector bv8 = b(8, 0b01000101L);
		assertThat(bv8.toString(), is("[10100010]"));
		BitVector bv12 = b(12, 0b101101000101L);
		assertThat(bv12.toString(), is("[10100010,1101]"));
		BitVector bv16 = b(16, 0b1110110000L);
		assertThat(bv16.toString(), is("[00001101,11000000]"));	
	}
}
