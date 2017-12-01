package com.cn.test.utils;

import java.io.UnsupportedEncodingException;

public class SensitivewordFilter {
	public static DFA dfa = new DFA();
	public static void init() {
		try {
			dfa.createKeywordTree(dfa.readSensitiveWordFile());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
