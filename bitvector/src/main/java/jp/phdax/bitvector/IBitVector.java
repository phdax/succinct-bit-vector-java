package jp.phdax.bitvector;

public interface IBitVector {
	public boolean pos(int idx);
	public int rank1(int pos);
	public int rank0(int pos);
	public int select1(int rank);
	public int select0(int rank);
}
