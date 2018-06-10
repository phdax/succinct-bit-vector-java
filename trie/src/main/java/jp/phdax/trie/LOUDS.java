package jp.phdax.trie;

import jp.phdax.bitvector.BitVector;
import jp.phdax.bitvector.BitVectorBuilder;

public class LOUDS {
	
	private final int dictSize;
	private final int trieSize;
	private final int maxValLen;
	private final BitVector tree;
	private final BitVector leaf;
	private final BitVector delim; //select1(N) -> rank0(idx) - rank0(idx-1)
	private final char[] chars;
	
	LOUDS(int dictSize, int trieSize, IBreadthFirstIterator<char[]> itr) {
		
		final BitVectorBuilder treeBuilder = BitVectorBuilder.create();
		final BitVectorBuilder leafBuilder = BitVectorBuilder.create();
		final BitVectorBuilder delimBuilder = BitVectorBuilder.create();
		chars = new char[trieSize];
		
		int treeIdx = 0;
		int leafIdx = 0;
		int charsIdx = 0;
		int maxValLen = 0;
		while(itr.hasNext()) {
			char[] val = itr.next();
			if(!itr.isDelim()) {
				treeBuilder.set(treeIdx);
				delimBuilder.set(charsIdx);
				if(itr.isLeaf()) leafBuilder.set(leafIdx);
				for(int i=0; i<val.length; i++) {
					chars[charsIdx + i] = val[i];
				}
				maxValLen = Math.max(maxValLen, val.length);
				charsIdx += val.length;
			}
			treeIdx++;
			leafIdx++;
		}
		delimBuilder.set(charsIdx);
		
		this.dictSize = dictSize;
		this.trieSize = trieSize;
		this.maxValLen = maxValLen;
		tree = treeBuilder.size(treeIdx).build();
		leaf = leafBuilder.size(leafIdx).build();
		delim = delimBuilder.size(charsIdx).build();
	}

	public int size() {
		return dictSize;
	}
	
	public int trieSize() {
		return trieSize;
	}
	
	public int getId(String str) {
		final char[] target = str.toCharArray();
		int prevNode = 1;
		int node = 1; // node == tree.rank1()
		int cursor = 0;
		cursor:
		while(cursor < target.length) {
			int start = tree.select0(node)+1;
			int end = tree.select0(node+1);
			// current node's children
			children:
			for(int i=start; i<end; i++) {
				int valStart = delim.select1(node);
				int valLen = delim.select1(node+1) - valStart;
				// child's chars
				if(cursor + valLen > target.length) continue children;
				for(int j=0; j<valLen; j++) {
					if(chars[valStart+j] != target[cursor+j]) continue children;
				}
				prevNode = node;
				node = tree.rank1(i); // next node
				cursor += valLen;
				continue cursor;
			}
			return -1;
		}
		// leaf or not
		int idx = tree.select1(prevNode);
		if(leaf.pos(idx)) {
			return node;
		} else {
			return -1;
		}
	}
	
	public String get(int id) {
		char[] chars = new char[maxValLen];
		int cursor = 0;		
		// 
		return String.copyValueOf(chars, 0, cursor);
	}
}
