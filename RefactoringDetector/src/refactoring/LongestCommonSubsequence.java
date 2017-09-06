package refactoring;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.edu.sysu.syntaxsimilar.Token;

public class LongestCommonSubsequence {

	private int t;
	private List<Token> X;
	private List<Token> Y;
	private int[][] table; // 动态规划表
	public Set<List<Token>> list = new HashSet<List<Token>>();//公共子token序列

	/**
	 * 功能：带参数的构造器
	 */
	public LongestCommonSubsequence(List<Token> X, List<Token> Y) {
		this.X = X;
		this.Y = Y;
	}

	/**
	 * 功能：求两个数中的较大者
	 */
	private int max(int a, int b) {
		return (a > b) ? a : b;
	}
	
	//TODO: 找出公共子序列的起始行和结束行，对接到refacingchange类成员变量中

	/**
	 * 功能：构造表，并返回X和Y的LCS的长度
	 */
	public int lcs(int m, int n) {
		table = new int[m + 1][n + 1]; // 表的大小为(m+1)*(n+1)
		for (int i = 0; i < m + 1; ++i) {
			for (int j = 0; j < n + 1; ++j) {
				// 第一行和第一列置0
				if (i == 0 || j == 0)
					table[i][j] = 0;
				else if (X.get(i - 1).getTokenName().equals(Y.get(j - 1).getTokenName()))
					table[i][j] = table[i - 1][j - 1] + 1;
				else
					table[i][j] = max(table[i - 1][j], table[i][j - 1]);
			}
		}
		return table[m][n];
	}

	/**
	 * 功能：回溯，求出所有的最长公共子序列，并放入set中
	 */
	private void traceBack(int i, int j, List<Token> lcs_str) {
		while (i > 0 && j > 0) {
			if (X.get(i - 1).getTokenName().equals(Y.get(j - 1).getTokenName())) {
				lcs_str.add(X.get(i - 1));
				--i;
				--j;
			} else {
				if (table[i - 1][j] > table[i][j - 1])
					--i;
				else if (table[i - 1][j] <= table[i][j - 1])
					--j;
				else { // 相等的情况
					traceBack(i - 1, j, lcs_str);
					traceBack(i, j - 1, lcs_str);
					return;
				}
			}
		}
		list.add(reverse(lcs_str));
		t = i;
	}

	/**
	 * 功能：字符串逆序
	 */
	private List<Token> reverse(List<Token> str) {
		Collections.reverse(str);
		return str;
	}
	
	public int getCommonLength() {
		int m = X.size();
		int n = Y.size();
		int length = lcs(m, n);
		return length;
	}
	
	public Set<List<Token>> getCommonTokenListSet(){
		int m = X.size();
		int n = Y.size();
		int length = lcs(m, n);
		
		t = m;
		while (t > 0 && table[t][n] == length) {
			List<Token> str = new ArrayList<Token>();
			traceBack(t, n, str);
		}

		System.out.println("The length of LCS is: " + length);
		return list;
	}

	
	
	public int getEndLine(List<Token> l) {
		int endLine = 0;
		for(Token t : l) {
			if(t.getEndLine() > endLine) {
				endLine = t.getEndLine();
			}
		}
		return endLine;
	}
	
	public int getT() {
		return t;
	}

	public void setT(int t) {
		this.t = t;
	}

	public List<Token> getX() {
		return X;
	}

	public void setX(List<Token> x) {
		X = x;
	}

	public List<Token> getY() {
		return Y;
	}

	public void setY(List<Token> y) {
		Y = y;
	}

	public int[][] getTable() {
		return table;
	}

	public void setTable(int[][] table) {
		this.table = table;
	}

	public Set<List<Token>> getList() {
		return list;
	}

	public void setList(Set<List<Token>> list) {
		this.list = list;
	}


	/**
	 * 功能：main方法 —— 程序的入口
	 */
	public static void main(String[] args) {
		// LongestCommonSubsequence lcs = new
		// LongestCommonSubsequence("ABCBDAB","BDCABA");
		// lcs.printLCS();
	}
}