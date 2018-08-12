package jp.phdax.trie;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

/**
 * パトリシアトライを利用したSet構造です。
 * @author phdax
 */
public class PatriciaSet {

	private int maxLen = 0;
	private final RootNode root = new RootNode();
	
	public PatriciaSet() {
	}
	
	public boolean add(String str) {
		char[] chars = str.toCharArray();
		maxLen = Math.max(maxLen, chars.length);
		Node node = root.add(chars, chars.length);
		return node != null;
	}
	
	public boolean addAll(Collection<? extends String> c) {
		boolean added = false;
		for(String s : c) {
			if(add(s)) added = true;
		}
		return added;
	}

	public boolean contains(Object o) {
		if(!(o instanceof String)) return false;
		String str = (String)o; 		 
		char[] chars = str.toCharArray();
		return root.contains(chars, 0);
	}
	
	public boolean containsAll(Collection<?> c) {
		for(Object o : c) {
			if(!contains(o)) {
				return false;
			}
		}
		return true;
	}
	
	public int size() {
		return root.trieSize();
	}
	
	public boolean isEmpty() {
		return root.trieSize() == 0;
	}
	
	public int dictSize() {
		return root.dictSize();
	}
		
	public int maxLen() {
		return maxLen;
	}
	
	public IBreadthFirstIterator<char[]> nodesIterator() {
		return new PatriciaBreadthFirstIterator(root);
	}
	
	private static final int matchLen(char[] obj1, char[] obj2) {
		int min = Math.min(obj1.length, obj2.length);
		for(int i=0; i<min; i++) {
			if(obj1[i] != obj2[i]) {
				return i;
			}
		}	
		return min;
	}
	
	private static final int matchLen(char[] obj1, char[] obj2, int start) {
		int min = Math.min(obj1.length, obj2.length - start);
		for(int i=0; i<min; i++) {
			if(obj1[i] != obj2[start+i]) {
				return i;
			}
		}	
		return min;
	}
	
	private class RootNode extends Node {
		
		private int dictSize = 0;
		private int trieSize = 0;
		
		public RootNode() {
			super(new char[0], false);
		}
		public int dictSize() {
			return dictSize;
		}
		public int trieSize() {
			return trieSize;
		}
		public Node add(char[] chars, int size) {
			for(Node child : children) {
				int childMatchLen = matchLen(chars, child.val);
				if(childMatchLen > 0) {
					Node put = child.add(chars, size);
					if(put != null) {
						dictSize += put.val.length;
						trieSize++;
					}
					return put;
				}
			}
			Node newChild = new Node(chars, true);
			children.add(newChild);
			dictSize += chars.length;
			trieSize++;
			return newChild;
		}
		public boolean contains(char[] chars, int cursor) {
			for(Node child : children) {
				int childMatchLen = matchLen(child.val, chars, cursor);
				if(childMatchLen > 0) {
					return child.contains(chars, cursor);
				}
			}
			return false;
		}
		
		@Override
		public String toString() {
			return "#ROOT#";
		}
	}
	
	private class Node {
		
		protected boolean isLeaf;
		protected char[] val;
		protected List<Node> children;
		
		public Node(char[] val, boolean isLeaf) {
			this.isLeaf = isLeaf;
			this.val = val;
			this.children = new LinkedList<>();
		}
		
		public Node add(char[] chars, int size) {
			int matchLen = matchLen(val, chars);
			// [1] val:"abc" <-> chars:"abcXXXXXX"
			// ノードの分割が不要で、子孫の追加のみで済ませられるケース
			if(matchLen == val.length) {
				// [1-a] val:"abc" <-> chars:"abc"
				if(matchLen == chars.length) {
					return null;
				}
				// [1-b] val:"abc" <-> chars:"abcdef"
				else {
					char[] diff = Arrays.copyOfRange(chars, matchLen, chars.length);
					for(Node child : children) {
						int childMatchLen = matchLen(diff, child.val);
						if(childMatchLen > 0) {
							return child.add(diff, size);
						}
					}
					Node newChild = new Node(diff, true);
					children.add(newChild);
					return newChild;
				}
			}
			// [2] val:"abcXXXXXX" <-> chars:"abcYYYYYY"
			// ノードの分割が必要になるケース
			else {
				Node valTail = new Node(Arrays.copyOfRange(val, matchLen, val.length), this.isLeaf);
				valTail.children = this.children;
				this.isLeaf = false;
				this.val = Arrays.copyOf(val, matchLen);
				this.children = new LinkedList<>();
				children.add(valTail);
				// [2-a] val:"abcdef" <-> chars:"abc"
				if(matchLen == chars.length) {
					return this;
				}
				// [2-b] val:"abcdef" <-> chars:"abcxyz"
				else {
					Node newChild = new Node(Arrays.copyOfRange(chars, matchLen, chars.length), true);
					children.add(newChild);
					return newChild;
				}
			}
		}
		
		public boolean contains(char[] chars, int cursor) {
			int matchLen = matchLen(val, chars, cursor);
			if(matchLen == val.length && matchLen + cursor == chars.length) {
				return isLeaf;
			} else {
				cursor += matchLen;
				for(Node child : children) {
					int childMatchLen = matchLen(child.val, chars, cursor);
					if(childMatchLen > 0) {
						return child.contains(chars, cursor);
					}
				}
				return false;
			}
		}
		
		@Override
		public String toString() {
			return Arrays.toString(val) + ':' + (isLeaf ? "LEAF" : "NODE");
		}
	}
	
	private class PatriciaBreadthFirstIterator implements IBreadthFirstIterator<char[]> {
		
		private final Deque<Node> deq = new ArrayDeque<>();
		private boolean isLeaf = false;
		private boolean isDelim = false;
		private final Node dummy = new DummyNode();
		
		public PatriciaBreadthFirstIterator(Node node) {
			deq.offer(node);
			deq.offer(dummy);
		}
		
		@Override
		public boolean hasNext() {
			return !deq.isEmpty();
		}
		 
		@Override
		public char[] next() {
			Node node = deq.poll();
			if(node instanceof DummyNode) {
				isDelim = true;
				isLeaf = false;
			} else {
				for(Node child : node.children) {
					deq.offer(child);
				}
				deq.offer(dummy);
				isDelim = false;
				isLeaf = node.isLeaf;
			}
			return node.val;
		}
		
		@Override
		public boolean isDelim() {
			return isDelim;
		}
		
		@Override
		public boolean isLeaf() {
			return isLeaf;
		}
		
		private class DummyNode extends Node {
			public DummyNode() {
				super(new char[0], false);
			}
		}
	}
}
