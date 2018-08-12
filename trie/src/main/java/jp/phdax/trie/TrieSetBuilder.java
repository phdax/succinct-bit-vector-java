package jp.phdax.trie;

public class TrieSetBuilder {
	
	private final PatriciaSetWithID pat;
	private Type type = Type.DEFAULT;
	
	public TrieSetBuilder() {
		this.pat = new PatriciaSetWithID();
	}
	
	public TrieSetBuilder(int size) {
		this.pat = new PatriciaSetWithID(size);
	}
	
	public TrieSetBuilder put(String str) {
		pat.put(str);
		return this;
	}
	
	public TrieSetBuilder type(Type type) {
		this.type = type;
		return this;
	}
	
	public LOUDSSet build() {
		return type.create(pat.size(), pat.trieSize(), pat.maxLen(), pat.bfIterator());
	}
	
	public enum Type {
		DEFAULT {
			@Override
			public LOUDSSet create(int dictSize, int trieSize, int maxLen, IBreadthFirstIterator<char[]> itr) {
				return new LOUDSSet(dictSize, trieSize, maxLen, itr);
			}
		};
		abstract public LOUDSSet create(int dictSize, int trieSize, int maxLen, IBreadthFirstIterator<char[]> itr);
	}
}
