package com.testnow.shareami.utils;

public interface CommandLineParser <T> {
	public T parse(String[] args);
}
