package jp.phdax.trie;

public class LOUDSBuilder {
	
	private final Patricia pat;
	private Type type = Type.DEFAULT;
	
	public LOUDSBuilder() {
		this.pat = new Patricia();
	}
	
	public LOUDSBuilder(int size) {
		this.pat = new Patricia(size);
	}
	
	public LOUDSBuilder put(String str) {
		pat.put(str);
		return this;
	}
	
	public LOUDSBuilder type(Type type) {
		this.type = type;
		return this;
	}
	
	public LOUDS build() {
		return type.create(pat.size(), pat.trieSize(), pat.bfIterator());
	}
	
	public enum Type {
		DEFAULT {
			@Override
			public LOUDS create(int dictSize, int trieSize, IBreadthFirstIterator<char[]> itr) {
				return new LOUDS(dictSize, trieSize, itr);
			}
		};
		abstract public LOUDS create(int dictSize, int trieSize, IBreadthFirstIterator<char[]> itr);
	}
}
