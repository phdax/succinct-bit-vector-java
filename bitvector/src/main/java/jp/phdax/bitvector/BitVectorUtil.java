package jp.phdax.bitvector;

import java.lang.reflect.Field;

public enum BitVectorUtil {
	
	;
	
	public static final long bitReverse(long bits) {
	    bits = (bits & 0x00000000FFFFFFFFL) << 32 | (bits & 0xFFFFFFFF00000000L) >>> 32;
	    bits = (bits & 0x0000FFFF0000FFFFL) << 16 | (bits & 0xFFFF0000FFFF0000L) >>> 16;
	    bits = (bits & 0x00FF00FF00FF00FFL) << 8 | (bits & 0xFF00FF00FF00FF00L) >>> 8;
	    bits = (bits & 0x0F0F0F0F0F0F0F0FL) << 4 | (bits & 0xF0F0F0F0F0F0F0F0L) >>> 4;
	    bits = (bits & 0x3333333333333333L) << 2 | (bits & 0xCCCCCCCCCCCCCCCCL) >>> 2;
	    bits = (bits & 0x5555555555555555L) << 1 | (bits & 0xAAAAAAAAAAAAAAAAL) >>> 1;
	    return bits;
	}
	
	public static final String toString(BitVector2 bv) throws NoSuchFieldException, IllegalAccessException {
		Class<?> clazz = bv.getClass();
		Field fSize = clazz.getDeclaredField("size");
		fSize.setAccessible(true);
		int size = fSize.getInt(bv);
		Field fBits = clazz.getDeclaredField("bits");
		fBits.setAccessible(true);
		long[] bits = (long[])fBits.get(bv);
		
		if(size == 0) return "[]";
		StringBuilder b = new StringBuilder();
		for(int i=0; i<bits.length; i++) {
			long v = bits[i];
			b.append('[');
			for(int j=0; j<64; j++) {
				if((i<<6)+j >= size) {
					b.append(']');
					return b.toString();
				}
				if(j%8==0 && j>0) b.append(',');
				if((v >>> j & 1) == 1) {
					b.append('1');
				} else {
					b.append('0');					
				}
			}
			b.append(']');
			b.append(System.lineSeparator());
		}
		return b.toString();
	}
}
