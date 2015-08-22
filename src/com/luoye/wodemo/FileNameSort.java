package com.luoye.wodemo;
import java.util.*;
import java.io.*;

public class FileNameSort implements Comparator<File>
{
	/**
	 *比较用来排序的两个参数。随第一个参数小于、等于或大于第二个参数而分别返回负整数、零或正整数。
	 *实现程序必须确保对于所有的 x 和 y 而言，都存在 sgn(compare(x, y)) == -sgn(compare(y, x))。（这意味着当且仅当 compare(y, x) 抛出异常时 compare(x, y) 才必须抛出异常。）
	 *实现程序还必须确保关系是可传递的：((compare(x, y)>0) && (compare(y, z)>0)) 意味着 compare(x, z)>0。
	 *最后，实现程序必须确保 compare(x, y)==0 意味着对于所有的 z 而言，都存在 sgn(compare(x, z))==sgn(compare(y, z))。
	 *虽然这种情况很普遍，但并不 严格要求 (compare(x, y)==0) == (x.equals(y))。一般说来，任何违背了这一点的 comparator 都应该清楚地指出这一事实。推荐的语言是“注意：此 comparator 强行进行与等号一致的排序。”
	 */
	@Override
	public int compare(File p1, File p2)
	{
		// TODO: Implement this method
		return p1.getName().compareToIgnoreCase(p2.getName());
	}
}
