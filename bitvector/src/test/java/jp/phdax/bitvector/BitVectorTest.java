package jp.phdax.bitvector;

import org.junit.Test;
import static org.junit.Assert.assertThat;
import static org.hamcrest.core.Is.is;


public class BitVectorTest {

	@Test
	public void rank1Test() {
		
	}
	
	@Test
	public void bitPosTest() {
		assertThat(BitVector.bitPos(0b00100, 1), is(3));
	}
	
}
