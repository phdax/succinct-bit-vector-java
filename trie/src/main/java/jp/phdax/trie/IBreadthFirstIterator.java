package jp.phdax.trie;

public interface IBreadthFirstIterator<T> {

	public boolean hasNext();
	public T next();
	public boolean isLeaf();
	public boolean isDelim();
}
