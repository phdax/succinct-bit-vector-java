package jp.phdax.trie;

import org.junit.Test;
import static org.junit.Assert.assertThat;
import static org.hamcrest.core.Is.is;

public class PatriciaTest {
	
	@Test
	public void putAndSizeTest() {
		Patricia pat = new Patricia();
		pat.put("aaa");
		pat.put("aaa1");
		pat.put("aaa2");
		pat.put("aaa3");
		pat.put("aab");
		pat.put("aab");
		assertThat(pat.size(), is(5));
	}
	
	@Test
	public void getIdTest() {
		Patricia pat = new Patricia();
		pat.put("aaa");
		pat.put("aaa1");
		pat.put("aaa2");
		pat.put("aaa3");
		pat.put("aab");
		pat.put("aab");
		assertThat(pat.getId("aaa"), is(0));
		assertThat(pat.getId("aaa1"), is(1));
		assertThat(pat.getId("aaa2"), is(2));
		assertThat(pat.getId("aaa3"), is(3));
		assertThat(pat.getId("aab"), is(4));
	}
	
	@Test
	public void getTest() {
		Patricia pat = new Patricia();
		pat.put("aaa");
		pat.put("aaa1");
		pat.put("aaa2");
		pat.put("aaa3");
		pat.put("aab");
		pat.put("aab");
		assertThat(pat.get(0), is("aaa"));
		assertThat(pat.get(1), is("aaa1"));
		assertThat(pat.get(2), is("aaa2"));
		assertThat(pat.get(3), is("aaa3"));
		assertThat(pat.get(4), is("aab"));
	}
	
	@Test
	public void iteratorTest() {
		Patricia pat = new Patricia();
		pat.put("aaa");
		pat.put("aaa1");
		pat.put("aaa2");
		pat.put("aaa3");
		pat.put("aab");
		pat.put("aab");
		IBreadthFirstIterator<char[]> itr = pat.bfIterator();
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
	
	private static final char[] c(String str) {
		return str.toCharArray();
	}
}
