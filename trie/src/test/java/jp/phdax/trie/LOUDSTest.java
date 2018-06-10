package jp.phdax.trie;

import org.junit.Test;
import static org.junit.Assert.assertThat;
import static org.hamcrest.core.Is.is;

public class LOUDSTest {
	
	@Test
	public void putAndSizeTest() {
		LOUDSBuilder builder = new LOUDSBuilder();
		builder.put("aaa");
		builder.put("aaa1");
		builder.put("aaa2");
		builder.put("aaa3");
		builder.put("aab");
		builder.put("aab");
		LOUDS louds = builder.build();
		assertThat(louds.size(), is(5));
	}
	
	@Test
	public void getIdTest() {
		LOUDSBuilder builder = new LOUDSBuilder();
		builder.put("aaa");
		builder.put("aaa1");
		builder.put("aaa2");
		builder.put("aaa3");
		builder.put("aab");
		builder.put("aab");
		LOUDS louds = builder.build();
		assertThat(louds.getId("aaa"), is(0));
		assertThat(louds.getId("aaa1"), is(1));
		assertThat(louds.getId("aaa2"), is(2));
		assertThat(louds.getId("aaa3"), is(3));
		assertThat(louds.getId("aab"), is(4));
	}
	
	@Test
	public void getTest() {
		LOUDSBuilder builder = new LOUDSBuilder();
		builder.put("aaa");
		builder.put("aaa1");
		builder.put("aaa2");
		builder.put("aaa3");
		builder.put("aab");
		builder.put("aab");
		LOUDS louds = builder.build();
		assertThat(louds.get(0), is("aaa"));
		assertThat(louds.get(1), is("aaa1"));
		assertThat(louds.get(2), is("aaa2"));
		assertThat(louds.get(3), is("aaa3"));
		assertThat(louds.get(4), is("aab"));
	}
}
