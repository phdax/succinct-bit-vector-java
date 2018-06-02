package jp.phdax.bitvector;

import org.junit.Test;
import static org.junit.Assert.assertThat;
import static org.hamcrest.core.Is.is;

public class BitVectorBuilderTest {
	
	private static long[] l(long... bits) {
		return bits;
	}
	
	@Test
	public void testCreate() {
		BitVectorBuilder b1 = BitVectorBuilder.create(8, l(0b01000101));
		BitVectorBuilder b2 = BitVectorBuilder.create(8).set(0).set(2).set(6);
		
		BitVectorBuilder b3 = BitVectorBuilder.create(64, l(0b01000101));
		BitVectorBuilder b4 = BitVectorBuilder.create(l(0b01000101));
		
		assertThat(b1, is(b2));
		assertThat(b3, is(b4));
	}
}
