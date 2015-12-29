package com.luoye.wodemo;

public class JniBridge
{
	public static native String getPostUrl();
	public static native String getTail();
	public static native String getBmobKey();
	public static native String getAuthor();
	public static native String getWodemo();
	public static native String getHelp();
	static {
		System.loadLibrary("data");
	}
}
