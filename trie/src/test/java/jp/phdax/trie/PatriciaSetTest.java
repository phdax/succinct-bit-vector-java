package jp.phdax.trie;

import org.junit.Test;
import static org.junit.Assert.assertThat;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.core.Is.is;

public class PatriciaSetTest {
	
	@Test
	public void putAndSizeTest() {
		PatriciaSet pat = new PatriciaSet();
		pat.add("aaa");
		pat.add("aaa1");
		pat.add("aaa2");
		pat.add("aaa3");
		pat.add("aab");
		pat.add("aab");
		assertThat(pat.size(), is(5));
	}
	
	@Test
	public void containsTest() {
		PatriciaSet pat = new PatriciaSet();
		pat.add("aaa");
		pat.add("aaa1");
		pat.add("aaa2");
		pat.add("aaa3");
		pat.add("aab");
		pat.add("aab");
		assertThat(pat.contains("aaa"), is(true));
		assertThat(pat.contains("aaa1"), is(true));
		assertThat(pat.contains("aaa2"), is(true));
		assertThat(pat.contains("aaa3"), is(true));
		assertThat(pat.contains("aab"), is(true));
		assertThat(pat.contains(""), is(false));
		assertThat(pat.contains("a"), is(false));
		assertThat(pat.contains("aa"), is(false));
		assertThat(pat.contains("aaa4"), is(false));
		assertThat(pat.contains("aac"), is(false));
	}
	
	@Test
	public void containsAllTest() {
		PatriciaSet pat = new PatriciaSet();
		pat.add("aaa");
		pat.add("aaa1");
		pat.add("aaa2");
		pat.add("aaa3");
		pat.add("aab");
		pat.add("aab");
		assertThat(pat.containsAll(setOf("aaa")), is(true));
		assertThat(pat.containsAll(setOf("aaa","aaa1","aaa2","aaa3","aab")), is(true));
		assertThat(pat.containsAll(setOf("aac")), is(false));
		assertThat(pat.containsAll(setOf("aaa","aaa1","aaa2","aaa3","aac")), is(false));
	}
	
	@Test
	public void iteratorTest() {
		PatriciaSet pat = new PatriciaSet();
		pat.add("aaa");
		pat.add("aaa1");
		pat.add("aaa2");
		pat.add("aaa3");
		pat.add("aab");
		pat.add("aab");
		IBreadthFirstIterator<char[]> itr = pat.nodesIterator();
		assertThat(itr.next(), is(c(""))); // root
		assertThat(itr.next(), is(c("")));
		assertThat(itr.next(), is(c("aa")));
		assertThat(itr.next(), is(c("")));
		assertThat(itr.next(), is(c("a")));
		assertThat(itr.next(), is(c("b")));
		assertThat(itr.next(), is(c("")));
		assertThat(itr.next(), is(c("1")));
		assertThat(itr.next(), is(c("2")));
		assertThat(itr.next(), is(c("3")));
		assertThat(itr.next(), is(c("")));
		assertThat(itr.next(), is(c(""))); // b
		assertThat(itr.next(), is(c(""))); // 1
		assertThat(itr.next(), is(c(""))); // 2
		assertThat(itr.next(), is(c(""))); // 3
		assertThat(itr.hasNext(), is(false));
	}
	
	@SuppressWarnings("unchecked")
	private static final <T> Set<T> setOf(T... args) {
		Set<T> set = new HashSet<>();
		for(T arg : args) {
			set.add(arg);
		}
		return set;
	}
	
	private static final char[] c(String str) {
		return str.toCharArray();
	}
}
