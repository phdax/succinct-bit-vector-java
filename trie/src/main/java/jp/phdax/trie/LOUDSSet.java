package jp.phdax.trie;

import jp.phdax.bitvector.BitVectorBuilder;
import jp.phdax.bitvector.IBitVector;

/**
 * LOUDSを利用したSet構造です。
 * @author phdax
 */
public class LOUDSSet implements ITrieSet {
	
	private final int dictSize;
	private final int trieSize;
	private final int maxLen;
	private final IBitVector tree;
	private final IBitVector leaf;
	private final IBitVector delim;
	private final char[] chars;
	
	LOUDSSet(int dictSize, int trieSize, int maxLen, IBreadthFirstIterator<char[]> itr) {
		
		final BitVectorBuilder treeBuilder = BitVectorBuilder.create();
		final BitVectorBuilder leafBuilder = BitVectorBuilder.create();
		final BitVectorBuilder delimBuilder = BitVectorBuilder.create();
		chars = new char[trieSize];
		
		int treeIdx = 0;
		int leafIdx = 0;
		int charsIdx = 0;
		while(itr.hasNext()) {
			char[] val = itr.next();
			if(!itr.isDelim()) {
				treeBuilder.set(treeIdx);
				delimBuilder.set(charsIdx);
				if(itr.isLeaf()) leafBuilder.set(leafIdx);
				for(int i=0; i<val.length; i++) {
					chars[charsIdx + i] = val[i];
				}
				charsIdx += val.length;
			}
			treeIdx++;
			leafIdx++;
		}
		delimBuilder.set(charsIdx++);
		
		this.dictSize = dictSize;
		this.trieSize = trieSize;
		this.maxLen = maxLen;
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
		int hitIdx = 0;
		int node = 1; // node == tree.rank1()
		int cursor = 0;
		cursor:
		while(cursor < target.length) {
			int start = tree.select0(node)+1;
			int end = tree.select0(node+1);
			// current node's children
			children:
			for(int i=start; i<end; i++) {
				node = tree.rank1(i);
				int valStart = delim.select1(node-1);
				int valLen = delim.select1(node) - valStart;
				// child's chars
				if(cursor + valLen > target.length) continue children;
				for(int j=0; j<valLen; j++) {
					if(chars[valStart+j] != target[cursor+j]) continue children;
				}
				hitIdx = i;
				cursor += valLen;
				continue cursor;
			}
			return -1;
		}
		// leaf or not
		if(leaf.pos(hitIdx)) {
			return leaf.rank1(hitIdx)-1;
		} else {
			return -1;
		}
	}
	
	public String get(int id) {
		char[] target = new char[maxLen];
		int cursor = target.length-1;
		int idx = leaf.select1(id+1);
		int node = tree.rank1(idx);
		while(node > 1) {
			int valEnd = delim.select1(node);
			int valLen = valEnd - delim.select1(node-1);
			for(int j=0; j<valLen; j++) {
				target[cursor-j] = chars[valEnd-1-j];
			}
			cursor -= valLen;
			idx = tree.select1(node);
			node = tree.rank0(idx);
		}
		return String.copyValueOf(target, cursor+1, maxLen-cursor-1);
	}
}
