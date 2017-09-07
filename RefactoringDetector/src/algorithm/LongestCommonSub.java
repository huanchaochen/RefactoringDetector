package algorithm;

import java.util.List;

import cn.edu.sysu.syntaxsimilar.Token;

public class LongestCommonSub {
	public static void main(String[] args) {
//        // TODO Auto-generated method stub
//        String str1 = "fjssharpsword";
//        String str2 = "helloworld";
//        //����lcs�ݹ����
//        int[][] re = longestCommonSubsequence(str1, str2);
//        //��ӡ����
//        for (int i = 0; i <= str1.length(); i++) {
//                for (int j = 0; j <= str2.length(); j++) {
//                        System.out.print(re[i][j] + "   ");
//                }
//                System.out.println();
//        }
//
//        System.out.println();
//        System.out.println(re[str1.length()][str2.length()]);
//        //���LCS
//        print(re, str1, str2, str1.length(), str2.length());
	}

	// ���緵�������ַ���������������еĳ���
	public static int[][] longestCommonSubsequence(List<Token> OldTokens, List<Token> MethodTokens) {
        int[][] matrix = new int[OldTokens.size() + 1][MethodTokens.size() + 1];//������ά����
        // ��ʼ���߽�����
        for (int i = 0; i <= OldTokens.size(); i++) {
                matrix[i][0] = 0;//ÿ�е�һ������
        }
        for (int j = 0; j <= MethodTokens.size(); j++) {
                matrix[0][j] = 0;//ÿ�е�һ������
        }
        // ������
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
	//���ݾ������LCS
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
				else {  // ��ȵ����
					print(opt,X, Y, i-1, j);
					print(opt,X,Y,i, j-1);
					return;
				}
			}
		}
		//set.add(reverse(lcs_str));
	}
}
