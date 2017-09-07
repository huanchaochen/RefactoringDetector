package test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;

import cn.edu.sysu.diffextraction.ChangeAnalysis;
import cn.edu.sysu.diffextraction.DiffType;
import cn.edu.sysu.syntaxsimilar.Parser2;
import cn.edu.sysu.syntaxsimilar.Token;
import cn.edu.sysu.syntaxsimilar.Tokenizer2;
import cn.edu.sysu.test.CodeSpliterTool;
import refactoring.InlineMethod;
import stucture.RefactChange;

public class TestAll {

	public static String rootPathOld;
	public static String rootPathNew;
	

	public static void main(String[] args) throws IOException {
		int count = 0;
		String workSpace = "D:\\log";// TODO workspace name change
		String outputFileName = "D:\\InlineMethodResult_65.txt";
		File file = new File(workSpace);
		File[] fs = file.listFiles();
		System.out.println(fs.length);
		int i = 0;
		while (i < fs.length) {

			// System.out.println(fs[i].getPath());
			File file2 = new File(fs[i].getPath());
			File[] fs2 = file2.listFiles();
			System.out.println(fs2.length);
			for (int j = 0; j < fs2.length; j++) {
				List<String> listNew = new ArrayList<>();
				List<String> listOld = new ArrayList<>();
				rootPathOld = "";
				rootPathNew = "";
				getRootPath(fs2[j].getPath());
				if (rootPathNew.equals("") | rootPathOld.equals("")) {
					break;
				}

				getData(rootPathOld, listOld);
				getData(rootPathNew, listNew);
				for (String s : listOld) {
					if (listNew.contains(s)) {
						SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
						System.out.println(df.format(new Date()));// new Date()为获取当前系统时间

						String oldPath = rootPathOld + "\\src\\" + s;
						String newPath = rootPathNew + "\\src\\" + s;

						System.out.println(oldPath);

						// System.out.println(oldPath);
						// System.out.println(newPath);
						List<DiffType> diffList = null;

						List<Token> tokenListOld = new ArrayList<Token>();
						tokenListOld = parse2TokenList(oldPath);
						List<Token> tokenListNew = new ArrayList<Token>();
						tokenListNew = parse2TokenList(newPath);

						// for(Token t: tokenListOld){
						// System.out.println(t.getTokenName());
						// System.out.println(t.getKeyword());
						// }
						//

						try {
							diffList = ChangeAnalysis.changeDistill(oldPath, newPath, tokenListNew, tokenListOld);

						} catch (Exception e) {
							/*
							 * An exception most likely indicates a bug in ChangeDistiller. Please file a
							 * bug report at https://bitbucket.org/sealuzh/tools-changedistiller/issues and
							 * attach the full stack trace along with the two files that you tried to
							 * distill.
							 */
							System.err.println("Warning: error while change distilling. " + e.getMessage());
						}

						InlineMethod e = new InlineMethod(tokenListOld, tokenListNew, diffList); // TODO class change
						List<RefactChange> l = new ArrayList<RefactChange>();
						l = e.refactor();

						if (l != null) {
							for (RefactChange r : l) {
								count++;
								System.out.println(r.getType());
								System.out.println(r.getOldStartLine());
								System.out.println(r.getOldEndLine());
								System.out.println(r.getNewStartLine());
								System.out.println(r.getNewEndLine());
								if (r.getMethodCalledLine() != null) {
									//for (Integer inter : r.getMethodCalledLine()) {
										//System.out.println(inter);
									//}
								}
								// 写入文件
								File log = new File(outputFileName);
								try {
									FileWriter fw = new FileWriter(log, true);
									BufferedWriter bw = new BufferedWriter(fw);
									bw.write(rootPathOld.split("\\\\")[2]);
									bw.write("\n");
									bw.write(String.valueOf(rootPathOld.split("\\\\")[3]));
									bw.write("\n");
									bw.write(s.substring(0, s.lastIndexOf(".")));
									bw.write("\n");
									bw.write(String.valueOf(r.getOldStartLine()));
									bw.write("\n");
									bw.write(String.valueOf(r.getOldEndLine()));
									bw.write("\n");
									bw.write(String.valueOf(r.getNewStartLine()));
									bw.write("\n");
									bw.write(String.valueOf(r.getNewEndLine()));
									bw.write("\n");
									if (r.getMethodCalledLine() != null) {
										//for (Integer inter : r.getMethodCalledLine()) {
											//bw.write(String.valueOf(inter));
											bw.write("\n");
										//}
									}
									bw.write("\n");
									bw.flush();
									bw.close();
									fw.close();
								} catch (IOException e1) {

									e1.printStackTrace();
								}

							}
						}

					}

				}
			}
			i++;
		}
		// 写入文件
		File log = new File(outputFileName);
		try {
			FileWriter fw = new FileWriter(log, true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write("\n");
			bw.write(String.valueOf(count));
			bw.write("\n");
			bw.flush();
			bw.close();
			fw.close();
		} catch (IOException e1) {

			e1.printStackTrace();
		}
	}

	private static void getRootPath(String path) {

		try {
			File f = new File(path);
			File[] fs = f.listFiles();
			int i = 0;
			while (i < fs.length) {
				if (fs[i].isDirectory()) {
					if (fs[i].getName().equals("new")) {
						rootPathNew = fs[i].getPath();

					}
					if (fs[i].getName().equals("old")) {
						rootPathOld = fs[i].getPath();

					}

					getRootPath(fs[i].getPath());// 递归查询目录下的文件夹

				}
				i++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static void getData(String path, List<String> list) {

		try {
			File f = new File(path);
			File[] fs = f.listFiles();
			int i = 0;
			while (i < fs.length) {
				if (fs[i].isDirectory()) {
					if (fs[i].getName().equals("new")) {
						rootPathNew = fs[i].getPath();
					}
					if (fs[i].getName().equals("old")) {
						rootPathOld = fs[i].getPath();
					}

					getData(fs[i].getPath(), list);// 递归查询目录下的文件夹

				}
				if (fs[i].getName().endsWith(".java"))// 查找java后缀的文件
					list.add(fs[i].getName());
					//list.add(fs[i].getPath());
				i++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	static List<Token> parse2TokenList(String path) throws IOException {
		ASTParser parser = ASTParser.newParser(AST.JLS4);
		String source = CodeSpliterTool.fileToString(path);
		char[] codeCharArrays = source.toCharArray();
		parser.setSource(codeCharArrays);
		CompilationUnit unit = (CompilationUnit) parser.createAST(null);
		Tokenizer2 tokenizer = Parser2.parseAST2Tokens(unit);
		return tokenizer.getTokenList();
	}

}
