package jp.phdax.bitvector.succinct;

public interface ISelectBlock {
	public int select(long[] data, int rank);
}
