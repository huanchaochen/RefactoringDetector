package algorithm;

import java.util.List;

import cn.edu.sysu.syntaxsimilar.Token;

public class LongestCommonSub {
	public static void main(String[] args) {
//        // TODO Auto-generated method stub
//        String str1 = "fjssharpsword";
//        String str2 = "helloworld";
//        //计算lcs递归矩阵
//        int[][] re = longestCommonSubsequence(str1, str2);
//        //打印矩阵
//        for (int i = 0; i <= str1.length(); i++) {
//                for (int j = 0; j <= str2.length(); j++) {
//                        System.out.print(re[i][j] + "   ");
//                }
//                System.out.println();
//        }
//
//        System.out.println();
//        System.out.println(re[str1.length()][str2.length()]);
//        //输出LCS
//        print(re, str1, str2, str1.length(), str2.length());
	}

	// 假如返回两个字符串的最长公共子序列的长度
	public static int[][] longestCommonSubsequence(List<Token> OldTokens, List<Token> MethodTokens) {
        int[][] matrix = new int[OldTokens.size() + 1][MethodTokens.size() + 1];//建立二维矩阵
        // 初始化边界条件
        for (int i = 0; i <= OldTokens.size(); i++) {
                matrix[i][0] = 0;//每行第一列置零
        }
        for (int j = 0; j <= MethodTokens.size(); j++) {
                matrix[0][j] = 0;//每列第一行置零
        }
        // 填充矩阵
        for (int i = 1; i <= OldTokens.size(); i++) {
                for (int j = 1; j <= MethodTokens.size(); j++) {
                        if (OldTokens.get(i - 1).getTokenName() == MethodTokens.get(j - 1).getTokenName()) {
                                matrix[i][j] = matrix[i - 1][j - 1] + 1;
                        } else {
                                matrix[i][j] = (matrix[i - 1][j] >= matrix[i][j - 1] ? matrix[i - 1][j]
                                                : matrix[i][j - 1]);
                        }
                }
        }
        return matrix;
	}
	//根据矩阵输出LCS
	public static void print(int[][] opt, List<Token> X, List<Token> Y, int i, int j) {
		while (i>0 && j>0) {
			if (X.get(i-1).getTokenName() == Y.get(j-1).getTokenName()) {
				System.out.println(X.get(i - 1).getStartLine());
				--i;
				--j;
			}
			else {
				if (opt[i-1][j] > opt[i][j-1])
					--i;
				else if (opt[i-1][j] < opt[i][j-1])
					--j;
				else {  // 相等的情况
					print(opt,X, Y, i-1, j);
					print(opt,X,Y,i, j-1);
					return;
				}
			}
		}
		//set.add(reverse(lcs_str));
	}
}
