package jp.phdax.trie;

import java.util.Iterator;

public interface IBreadthFirstIterator<T> extends Iterator<T> {

	public boolean hasNext();
	public T next();
	public boolean isLeaf();
	public boolean isDelim();
}
