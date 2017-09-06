package refactoring;

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

import cn.edu.sysu.diffextraction.ChangeAnalysis;
import cn.edu.sysu.diffextraction.DiffType;
import cn.edu.sysu.syntaxsimilar.Parser2;
import cn.edu.sysu.syntaxsimilar.Token;
import cn.edu.sysu.syntaxsimilar.Tokenizer2;
import cn.edu.sysu.test.CodeSpliterTool;

public class ReadHibernate {

	public static String rootPathOld;
	public static String rootPathNew;
	public static List<String> listNew;
	public static List<String> listOld;
	

	public static void main(String[] args) throws IOException {
		int count = 0;
		String workSpace = "D:\\hibernate";// TODO workspace name change
		String outputFileName = "D:\\ExtractMethodResult_hibernate_65.txt";
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
				rootPathOld = "";
				rootPathNew = ""; 
				listNew = new ArrayList<String>();
				listOld = new ArrayList<String>();
				getRootPath(fs2[j].getPath());
				//System.out.println(rootPathNew);
				getPath(rootPathNew, "new");
				getPath(rootPathOld, "old");
				for(String n : listNew) {
					for(String o : listOld) {
						String n1 = n.substring(rootPathNew.length());
						
						String o1 = o.substring(rootPathOld.length());
						if(n1.equals(o1)){	
							SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
							System.out.println(df.format(new Date()));// new Date()为获取当前系统时间

							String oldPath = o;
							String newPath = n;

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

							ExtractMethod e = new ExtractMethod(tokenListOld, tokenListNew, diffList); // TODO class change
							List<RefactChange> l = new ArrayList<RefactChange>();
							l = e.refactor();

							if (l != null && l.size() != 0) {
								File log = new File(outputFileName);
								FileWriter fw = new FileWriter(log, true);
								BufferedWriter bw = new BufferedWriter(fw);
								bw.write(String.valueOf(l.size()));
								bw.write("\n");
								bw.write(oldPath);
								bw.write("\n");
								for (RefactChange r : l) {
									count++;
									System.out.println(r.getType());
									System.out.println(r.getOldStartLine());
									System.out.println(r.getOldEndLine());
									System.out.println(r.getNewStartLine());
									System.out.println(r.getNewEndLine());
									System.out.println(r.getMethodCalledLine());
									// 写入文件
									
									try {
										
										
										bw.write(String.valueOf(r.getOldStartLine()));
										bw.write("\n");
										bw.write(String.valueOf(r.getOldEndLine()));
										bw.write("\n");
										bw.write(String.valueOf(r.getNewStartLine()));
										bw.write("\n");
										bw.write(String.valueOf(r.getNewEndLine()));
										bw.write("\n");
										bw.write(String.valueOf(r.getMethodCalledLine()));
										bw.write("\n");
										
										bw.write("\n");
										
									} catch (IOException e1) {

										e1.printStackTrace();
									}
									

								}
								bw.flush();
								bw.close();
								fw.close();
							}
						}
					}
				}
				
			}
			i++;
		}
	}

	public static void getRootPath(String path) {

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

	
	
	public static void getPath(String path, String type) {

		try {
			File f = new File(path);
			File[] fs = f.listFiles();
			int i = 0;
			while (i < fs.length) {
				if (fs[i].isDirectory()) {
					getPath(fs[i].getPath(), type);// 递归查询目录下的文件夹
				}
				if (fs[i].getName().endsWith(".java")) {// 查找java后缀的文件
					if(type.equals("new")) {
						listNew.add(fs[i].getPath());
					}
					if(type.equals("old")) {
						listOld.add(fs[i].getPath());
					}
				}
				
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
