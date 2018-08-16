package jp.phdax.trie;

import org.junit.Test;
import static org.junit.Assert.assertThat;
import static org.hamcrest.core.Is.is;

public class LOUDSSetTest {
	
	@Test
	public void putAndSizeTest() {
		TrieSetBuilder builder = new TrieSetBuilder();
		builder.put("aaa");
		builder.put("aaa1");
		builder.put("aaa2");
		builder.put("aaa3");
		builder.put("aab");
		builder.put("aab");
		LOUDSSet louds = builder.build();
		assertThat(louds.size(), is(5));
	}
	
	@Test
	public void getIdTest() {
//		TrieSetBuilder builder = new TrieSetBuilder();
//		builder.put("aaa");
//		builder.put("aaa1");
//		builder.put("aaa2");
//		builder.put("aaa3");
//		builder.put("aab");
//		builder.put("aab");
//		LOUDSSet louds = builder.build();
//		assertThat(louds.getId("aaa"), is(0));
//		assertThat(louds.getId("aaa1"), is(2));
//		assertThat(louds.getId("aaa2"), is(3));
//		assertThat(louds.getId("aaa3"), is(4));
//		assertThat(louds.getId("aab"), is(1));
	}
	
	@Test
	public void getTest() {
//		TrieSetBuilder builder = new TrieSetBuilder();
//		builder.put("aaa");
//		builder.put("aaa1");
//		builder.put("aaa2");
//		builder.put("aaa3");
//		builder.put("aab");
//		builder.put("aab");
//		LOUDSSet louds = builder.build();
//		assertThat(louds.get(0), is("aaa"));
//		assertThat(louds.get(2), is("aaa1"));
//		assertThat(louds.get(3), is("aaa2"));
//		assertThat(louds.get(4), is("aaa3"));
//		assertThat(louds.get(1), is("aab"));
	}
}
