package jp.phdax.trie;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Patricia {
	
	private int size;
	private List<Node> nodes;
	private final Node root;
	private static final int NONE_ID = -1;
	private static final int ROOT_ID = -2;
	
	public Patricia() {
		this.size = 0;
		this.nodes = new ArrayList<>();
		this.root = new RootNode();
	}
	
	public Patricia(int initSize) {
		this.nodes = new ArrayList<>(initSize);
		this.root = new RootNode();
	}
	
	public void put(String str) {
		char[] chars = str.toCharArray();
		Node node = root.put(size, chars, chars.length);
		if(node != null) {
			nodes.add(node);
			size++;
		}
	}
	
	public int size() {
		return size;
	}
	
	public int getId(String str) {
		return root.getId(str.toCharArray(), 0);
	}
	
	public String get(int id) {
		Node node = nodes.get(id);
		char[] chars = node.get();
		return String.valueOf(chars);
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
		public RootNode() {
			super(null, ROOT_ID, new char[0], 0);
		}
		@Override
		public Node put(int newId, char[] chars, int size) {
			for(Node child : children) {
				int childMatchLen = matchLen(chars, child.val);
				if(childMatchLen > 0) {
					return child.put(newId, chars, size);
				}
			}
			Node newChild = new Node(this, newId, chars, size);
			children.add(newChild);
			return newChild;
		}
		@Override
		public int getId(char[] chars, int cursor) {
			for(Node child : children) {
				int childMatchLen = matchLen(child.val, chars, cursor);
				if(childMatchLen > 0) {
					return child.getId(chars, cursor);
				}
			}
			return NONE_ID;
		}
		@Override
		protected char[] getImpl(char[] buf, int cursor) {
			return buf;
		}
	}
	
	private class Node {
		
		protected int id;
		protected int size;
		protected char[] val;
		protected Node parent;
		protected List<Node> children;
		
		public Node(Node parent, int id, char[] val, int size) {
			this.id = id;
			this.val = val;
			this.size = size;
			this.parent = parent;
			this.children = new LinkedList<>();
		}
		
		public Node put(int newId, char[] chars, int size) {
			int matchLen = matchLen(val, chars);
			// [1] val:"abc" <-> chars:"abcXXXXXX"
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
							return child.put(newId, diff, size);
						}
					}
					Node newChild = new Node(this, newId, diff, size);
					children.add(newChild);
					return newChild;
				}
			}
			// [2] val:"abcXXXXXX" <-> chars:"abcYYYYYY"
			else {
				//split and inherit
				char[] diffVal = Arrays.copyOfRange(val, matchLen, val.length);
				Node inherit = new Node(this, id, diffVal, size);
				inherit.parent = this;
				inherit.children = this.children;
				for(Node child : inherit.children) {
					child.parent = inherit;
				}
				nodes.set(id, inherit);
				// [2-a] val:"abcdef" <-> chars:"abc"
				if(matchLen == chars.length) {
					// change to leaf node
					this.val = Arrays.copyOf(val, matchLen);
					this.id = newId;
					this.children = new LinkedList<>();
					children.add(inherit);
					return this;
				}
				// [2-a] val:"abcdef" <-> chars:"abcDEF"
				else {
					// change to inner node
					this.val = Arrays.copyOf(val, matchLen);
					this.id = NONE_ID;
					this.children = new LinkedList<>();
					children.add(inherit);
					// create new child
					char[] diffChars = Arrays.copyOfRange(chars, matchLen, chars.length);
					Node newChild = new Node(this, newId, diffChars, size);
					children.add(newChild);
					return newChild;
				}
			}
		}
		
		public int getId(char[] chars, int cursor) {
			int matchLen = matchLen(val, chars, cursor);
			if(matchLen == val.length && matchLen + cursor == chars.length) {
				return id;
			} else {
				cursor += matchLen;
				for(Node child : children) {
					int childMatchLen = matchLen(child.val, chars, cursor);
					if(childMatchLen > 0) {
						return child.getId(chars, cursor);
					}
				}
				return NONE_ID;
			}
		}
		
		public char[] get() {
			char[] buf = new char[size];
			return getImpl(buf, buf.length);
		}
		protected char[] getImpl(char[] buf, int cursor) {
			cursor -= val.length;
			for(int i=0; i<val.length; i++) {
				buf[i+cursor] = val[i];
			}
			if(parent == null) {
				return buf;
			}
			return parent.getImpl(buf, cursor);
		}
	}
}
