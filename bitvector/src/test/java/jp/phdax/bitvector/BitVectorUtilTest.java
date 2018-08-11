package jp.phdax.bitvector;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class BitVectorUtilTest {
	
	private static final BitVector2 b(int size, long... bits) {
		return new BitVector2(size, bits);
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
		BitVector2 bv4 = b(4, 0b00000101L);
		assertThat(BitVectorUtil.toString(bv4), is("[1010]"));
		BitVector2 bv8 = b(8, 0b01000101L);
		assertThat(BitVectorUtil.toString(bv8), is("[10100010]"));
		BitVector2 bv12 = b(12, 0b101101000101L);
		assertThat(BitVectorUtil.toString(bv12), is("[10100010,1101]"));
		BitVector2 bv16 = b(16, 0b1110110000L);
		assertThat(BitVectorUtil.toString(bv16), is("[00001101,11000000]"));	
	}
}
