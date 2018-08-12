package jp.phdax.bitvector.simple;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import jp.phdax.bitvector.simple.BitVector;
import jp.phdax.bitvector.simple.BitVectorUtil;

public class BitVectorUtilTest {
	
	private static final BitVector b(int size, long... bits) {
		return new BitVector(size, bits);
	}
	
	@Test
	public void reverseTest() {
		assertThat(BitVectorUtil.bitReverse(1L), is(Long.MIN_VALUE));
		assertThat(BitVectorUtil.bitReverse(-1L), is(-1L));
		assertThat(BitVectorUtil.bitReverse(0xF0F0F0F0F0F0F0F0L), is(0x0F0F0F0F0F0F0F0FL));
		assertThat(BitVectorUtil.bitReverse(0x3333333333333333L), is(0xCCCCCCCCCCCCCCCCL));
		assertThat(BitVectorUtil.bitReverse(0b1110110000L), is(0b0000110111000000000000000000000000000000000000000000000000000000L));
	}
	
	@Test
	public void toStringTest() throws Exception {
		BitVector bv4 = b(4, 0b00000101L);
		assertThat(BitVectorUtil.toString(bv4), is("[1010]"));
		BitVector bv8 = b(8, 0b01000101L);
		assertThat(BitVectorUtil.toString(bv8), is("[10100010]"));
		BitVector bv12 = b(12, 0b101101000101L);
		assertThat(BitVectorUtil.toString(bv12), is("[10100010,1101]"));
		BitVector bv16 = b(16, 0b1110110000L);
		assertThat(BitVectorUtil.toString(bv16), is("[00001101,11000000]"));	
	}
}
