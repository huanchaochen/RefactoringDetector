package refactoring;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import algorithm.LongestCommonSubsequence;
import cn.edu.sysu.diffextraction.DiffType;
import cn.edu.sysu.syntaxsimilar.Token;
import stucture.Method;
import stucture.RefactChange;
import stucture.RefactorType;

public class ExtractMethod {
	List<Token> tokenListOld;
	List<Token> tokenListNew;
	List<DiffType> diffList;
	List<Method> methods;
	List<RefactChange> refactList;
	List<Method> oldMethods;
	List<Method> newMethods;

	public ExtractMethod(List<Token> tokenListOld, List<Token> tokenListNew, List<DiffType> diffList) {
		super();
		this.tokenListOld = tokenListOld;
		this.tokenListNew = tokenListNew;
		this.diffList = diffList;
		this.methods = new ArrayList<Method>();
		this.refactList = new ArrayList<RefactChange>();
		this.oldMethods = new ArrayList<Method>();
		this.newMethods = new ArrayList<Method>();
		for (Token t : tokenListOld) {
			if (t.getTokenName().equals("MethodDeclaration")) {
				Method m = new Method();
				m.setMethodName(t.getKeyword());
				//System.out.println(m.methodName);
				m.setStartLine(t.getStartLine());
				m.setEndLine(t.getEndLine());
				oldMethods.add(m);
			}
		}
		for (Token t : tokenListNew) {
			if (t.getTokenName().equals("MethodDeclaration")) {
				Method m = new Method();
				m.setMethodName(t.getKeyword());
				//System.out.println(m.methodName);
				m.setStartLine(t.getStartLine());
				m.setEndLine(t.getEndLine());
				newMethods.add(m);
			}
		}
	}

	// 提取增加的函数
	public void addedMethod() {
		for (DiffType d : diffList) {
			if (d.getType().equals("ADDITIONAL_FUNCTIONALITY")) {
				Method m = new Method();

				//m.startLine = d.getNewStartLine();
				m.setStartLine(d.getNewStartLine());
				//m.endLine = d.getNewEndLine();
				m.setEndLine(d.getNewEndLine());
				List<Token> l = d.getNewTokenList();
				for (int i = 0; i < l.size(); i++) {
					// System.out.println(l.get(i).getTokenName() + ":" + l.get(i).getKeyword());
					if (l.get(i).getTokenName().equals("MethodDeclaration")) {
						String s = l.get(i).getKeyword();
						m.setMethodName(s.substring(0, s.indexOf("(")));
					}
					if (l.get(i).getStartLine() >= m.getStartLine() && l.get(i).getEndLine() <= m.getEndLine()) {
						//m.codeToken.add(l.get(i));
						m.setCodeToken(l.get(i));

					}
				}
				methods.add(m);

			}
		}
	}

	public boolean similarBody(Method m) {
		// String tokenNameOld = "";
		// for (Token t : tokenListOld) {
		// tokenNameOld += t.getTokenName();
		// }

		// int[][] re = LongestCommonSub.longestCommonSubsequence(tokenListOld,
		// m.codeToken);
		// System.out.println(m.codeTokenName.length());
		// System.out.println(re[tokenNameOld.length()][m.codeTokenName.length()]);
		// if (re[tokenNameOld.length()][m.codeTokenName.length()] >=
		// m.codeTokenName.length() * 0.8) {
		// return true;
		// }
		// 过滤旧版本的代码
		
		// for (Token t : tokenListOld) {
		// if (t.getKeyword() != null && ! t.getKeyword().equals("")) {
		// simpleCode.add(t);
		// }
		// }
		// for (DiffType d : diffList) {
		// if (d.getType().equals("STATEMENT_DELETE")) {
		// for (Token t : d.getOldTokenList()) {
		// if (t.getKeyword() != null && !t.getKeyword().equals("")) {// 去除关键字为空的token
		//
		// simpleCode.add(t);
		// // System.out.println(t.getKeyword());
		// }
		// }
		// }
		// }

		List<Token> temp = new ArrayList<Token>();
		for (int i = 0; i < m.getCodeToken().size(); i++) {
			// System.out.println(m.codeToken.get(i).getTokenName() + ":" +
			// m.codeToken.get(i).getKeyword());
			if (m.getCodeToken().get(i).getTokenName().equals("Block")) {
				for (int j = i + 1; j < m.getCodeToken().size(); j++) {
					temp.add(m.getCodeToken().get(j));// 得到函数体
				}
				break;
			}
		}

		if (temp.size() != 0) {

			List<Token> methodCode = new ArrayList<Token>();
			for (int i = 0; i < temp.size(); i++) {
				//String s = temp.get(i).getKeyword();
				//if (s != null && !s.equals("")) {// 去除关键字为空的token
					// System.out.println(s);
					methodCode.add(temp.get(i));
				//}
			}
			if (methodCode.size() != 0) {
//				 for (int i = 0; i < methodCode.size(); i++) {
//					 System.out.println(methodCode.get(i).getKeyword());
//				 }
				
				boolean flag = false;
				//System.out.println(m.methodCalledLine.size());
				Iterator iter = m.getMethodCalledLine().entrySet().iterator();
				while (iter.hasNext()) {
					//System.out.println("in");
					List<Token> simpleCode = new ArrayList<Token>();
					Map.Entry entry = (Map.Entry) iter.next();
					int line = (int) entry.getKey();
					String name = (String) entry.getValue();
					for (Method oldMethod : oldMethods) {
						if (name.equals(oldMethod.getMethodName())) {
							for (DiffType d : diffList) {
								if (d.getType().equals("STATEMENT_DELETE")) {
									if (d.getOldStartLine() >= oldMethod.getStartLine()
											&& d.getOldEndLine() <= oldMethod.getEndLine()) {
										for (Token t : d.getOldTokenList()) {
											//if (t.getKeyword() != null && !t.getKeyword().equals("")) {// 去除关键字为空的token

												simpleCode.add(t);
												// System.out.println(t.getKeyword());
											//}
										}
									}
								}
							}
						}
					}
					if (simpleCode.size() != 0) {
						
						LongestCommonSubsequence lcs = new LongestCommonSubsequence(simpleCode, methodCode);
						int length = lcs.getCommonLength();
						if (length >= methodCode.size() * 0.65) {// TODO 相似度阈值
							flag = true;
							System.out.println(methodCode.size());
							System.out.println("getSimilarity");
							Set<List<Token>> list = lcs.getCommonTokenListSet();
							List<RefactChange> tempList1 = RefactChange.getRefactoringChange(list, m, line,
									RefactorType.EXTRACTMETHOD);
							// List<RefactChange> tempList2 = tempList1;
							Iterator<RefactChange> iterator1 = tempList1.iterator();
//							// Iterator<RefactChange> iterator2 = tempList2.iterator();
							int lastStartLine = 0, lastEndLine = 99999;
							while (iterator1.hasNext()) {
								RefactChange r1 = iterator1.next();
								
								if (lastStartLine >= r1.getOldStartLine() && lastEndLine <= r1.getOldEndLine()) {
									iterator1.remove();
								} else {
									lastStartLine = r1.getOldStartLine();
									lastEndLine = r1.getOldEndLine();
								}			
								
							}
							iterator1 = tempList1.iterator();
							while (iterator1.hasNext()) {
								RefactChange r1 = iterator1.next();			
								if(r1.getOldEndLine() - r1.getOldStartLine() > methodCode.size()) {
									iterator1.remove();
								}
							}

							refactList.addAll(tempList1);
							
							
						}
					}
				}
				return flag;
			}
		}

		return false;
	}

	public boolean methodCall(Method m) {

		for (DiffType d : diffList) {
			if (d.getType().equals("STATEMENT_INSERT") | d.getType().equals("STATEMENT_UPDATE")) {
				List<Token> tokenList = d.getNewTokenList();
				for (int i = 0; i < tokenList.size(); i++) {
					if (tokenList.get(i).getTokenName().equals("MethodInvocation")) {
						//for (int j = 1; j <= tokenList.size() - i - 1; j++) {
							//String s = tokenList.get(i + j).getKeyword();
						String s = tokenList.get(i).getKeyword();
							if (s != null) {
								if (s.equals(m.getMethodName())) {
									// System.out.println(tokenList.get(i + j).getStartLine());
//									 System.out.println(m.methodName);
//									 System.out.println(tokenList.get(i + j).getStartLine());
									int callLine = tokenList.get(i).getStartLine();
									for (Method newMethod : newMethods) {
										if (newMethod.getStartLine() <= callLine && newMethod.getEndLine() >= callLine) {
											m.setMethodCalledLine(
													tokenList.get(i).getStartLine(), newMethod.getMethodName());
											//System.out.println(newMethod.methodName);
											//System.out.println(tokenList.get(i + j).getStartLine());
										}
									}

								}
							//}
						}
					}
				}
			}
		}
		if (m.getMethodCalledLine().size() != 0) {
			return true;
		}
		return false;
	}

	public boolean isExtractMethod() {
		addedMethod();
		// for (Method m : methods) {
		// if (!(similarBody(m) && methodCall(m))) {
		// //System.out.println("ExtractedMethodFound!");
		//
		// }
		// }
		// System.out.println(methods.size());
		Iterator<Method> iterator = methods.iterator();
		while (iterator.hasNext()) {
			Method m = iterator.next();
			//System.out.println(m.methodName + ":");
			if (m.getCodeToken().size() == 0) {
				System.out.println("ERROR!");
				continue;
			}
			if (!(methodCall(m) && similarBody(m)))
				iterator.remove();
		}
		for (Method m : methods) {
			System.out.println(m.getMethodName());
		}
		if (methods.size() != 0) {
			return true;
		}
		return false;
	}

	public List<RefactChange> refactor() {
		if (isExtractMethod()) {
			
			return refactList;
		}
		return null;
	}

	public void testMethodCalled() {
		addedMethod();
		Iterator<Method> iterator = methods.iterator();
		while (iterator.hasNext()) {
			Method m = iterator.next();

			if (m.getCodeToken().size() == 0) {
				System.out.println("ERROR!");
				continue;
			}
			methodCall(m);
				
		}
	}
	
	
}
