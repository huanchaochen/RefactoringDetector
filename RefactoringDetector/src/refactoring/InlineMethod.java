package refactoring;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import algorithm.LongestCommonSubsequence;
import cn.edu.sysu.diffextraction.DiffType;
import cn.edu.sysu.syntaxsimilar.Token;
import cn.edu.sysu.syntaxsimilar.TokenType;
import stucture.DiffTypeName;
import stucture.Method;
import stucture.RefactChange;
import stucture.RefactorType;

public class InlineMethod {
	List<Token> tokenListOld;
	List<Token> tokenListNew;
	List<DiffType> diffList;
	List<Method> methods;
	List<RefactChange> refactList;
	List<Method> oldMethods;
	List<Method> newMethods;
	
	public InlineMethod(List<Token> tokenListOld, List<Token> tokenListNew, List<DiffType> diffList) {
		super();
		this.tokenListOld = tokenListOld;
		this.tokenListNew = tokenListNew;
		this.diffList = diffList;
		this.methods = new ArrayList<Method>();
		this.refactList = new ArrayList<RefactChange>();
		this.oldMethods = new ArrayList<Method>();
		this.newMethods = new ArrayList<Method>();
		for (Token t : tokenListOld) {
			if (t.getTokenName().equals(TokenType.MethodDeclaration.toString())) {
				Method m = new Method();
				m.setMethodName(t.getKeyword());
				//System.out.println(m.methodName);
				m.setStartLine(t.getStartLine());
				m.setEndLine(t.getEndLine());
				oldMethods.add(m);
			}
		}
		for (Token t : tokenListNew) {
			if (t.getTokenName().equals(TokenType.MethodDeclaration.toString())) {
				Method m = new Method();
				m.setMethodName(t.getKeyword());
				//System.out.println(m.methodName);
				m.setStartLine(t.getStartLine());
				m.setEndLine(t.getEndLine());
				newMethods.add(m);
			}
		}
	}

	// 提取删除的函数
	public void deletedMethod() {
		for (DiffType d : diffList) {
			if (d.getType().equals(DiffTypeName.REMOVED_FUNCTIONALITY.toString())) {
				Method m = new Method();
 
				//m.startLine = d.getOldStartLine();
				m.setStartLine(d.getOldStartLine());
				//m.endLine = d.getOldEndLine();
				m.setEndLine(d.getOldEndLine());
				List<Token> l = d.getOldTokenList();
				for (int i = 0; i < l.size(); i++) {
					//System.out.println(l.get(i).getTokenName() + ":" + l.get(i).getKeyword());
					if (l.get(i).getTokenName().equals(TokenType.MethodDeclaration.toString())) {
						String s = l.get(i).getKeyword();
						//m.methodName = s.substring(0, s.indexOf("("));
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
		
//		for(Method m : methods) {
//			System.out.println(m.methodName);
//			System.out.println(m.startLine);
//			System.out.println(m.endLine);
//		}
	}

	public boolean similarBody(Method m) {
//		List<Token> simpleCode = new ArrayList<Token>();
//		for (DiffType d : diffList) {
//			if (d.getType().equals("STATEMENT_INSERT")) {
//				for (Token t : d.getNewTokenList()) {
//					if (t.getKeyword() != null && !t.getKeyword().equals("")) {//去除关键字为空的token
//						simpleCode.add(t);
//						// System.out.println(t.getKeyword());
//					}
//				}
//			}
//		}

		List<Token> temp = new ArrayList<Token>();
		for (int i = 0; i < m.getCodeToken().size(); i++) {
			//System.out.println(m.codeToken.get(i).getTokenName() + ":" + m.codeToken.get(i).getKeyword());
			if (m.getCodeToken().get(i).getTokenName().equals(TokenType.Block.toString())) {
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
				//if (s != null && !s.equals("")) {//去除关键字为空的token
					// System.out.println(s);
					methodCode.add(temp.get(i));
				//}
			}
			if (methodCode.size() != 0) {
				// for (int i = 0; i < methodCode.size(); i++) {
				// System.out.println(methodCode.get(i).getKeyword());
				// }

				boolean flag = false;
				//System.out.println(m.methodCalledLine.size());
				Iterator iter = m.getMethodCalledLine().entrySet().iterator();
				while (iter.hasNext()) {
					//System.out.println("in");
					List<Token> simpleCode = new ArrayList<Token>();
					Map.Entry entry = (Map.Entry) iter.next();
					int line = (int) entry.getKey();
					String name = (String) entry.getValue();
					//System.out.println(name);
					for (Method newMethod : newMethods) {
						if (name.equals(newMethod.getMethodName())) {
							for (DiffType d : diffList) {
								if (d.getType().equals(DiffTypeName.STATEMENT_INSERT.toString())) {
									if (d.getNewStartLine() >= newMethod.getStartLine()
											&& d.getNewEndLine() <= newMethod.getEndLine()) {
										for (Token t : d.getNewTokenList()) {
											//if (t.getKeyword() != null && !t.getKeyword().equals("")) {// 去除关键字为空的token

												simpleCode.add(t);
												System.out.println(t.getKeyword());
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
									RefactorType.INLINEMETHOD);
							// List<RefactChange> tempList2 = tempList1;
							Iterator<RefactChange> iterator1 = tempList1.iterator();
//							// Iterator<RefactChange> iterator2 = tempList2.iterator();
							int lastStartLine = 0, lastEndLine = 99999;
							while (iterator1.hasNext()) {
								RefactChange r1 = iterator1.next();
								
								if (lastStartLine >= r1.getNewStartLine() && lastEndLine <= r1.getNewEndLine()) {
									iterator1.remove();
								} else {
									lastStartLine = r1.getNewStartLine();
									lastEndLine = r1.getNewEndLine();
								}			
								
							}
							iterator1 = tempList1.iterator();
							while (iterator1.hasNext()) {
								RefactChange r1 = iterator1.next();			
								if(r1.getNewEndLine() - r1.getNewStartLine() > methodCode.size()) {
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
			if (d.getType().equals(DiffTypeName.STATEMENT_DELETE.toString()) | d.getType().equals(DiffTypeName.STATEMENT_UPDATE.toString())) {
				List<Token> tokenList = d.getOldTokenList();
				for (int i = 0; i < tokenList.size(); i++) {
					//System.out.println(tokenList.get(i).getTokenName()+ ":" + tokenList.get(i).getKeyword());
					if (tokenList.get(i).getTokenName().equals(TokenType.MethodInvocation.toString())) {
						//for (int j = 1; j <= tokenList.size() - i - 1; j++) {
							String s = tokenList.get(i).getKeyword();
							//System.out.println(s);
							if (s != null) {
								if (s.equals(m.getMethodName())) {
//									System.out.println(tokenList.get(i + j).getStartLine());
//									System.out.println(m.methodName);
//									System.out.println(tokenList.get(i + j).getStartLine());
									int callLine = tokenList.get(i).getStartLine();
									for (Method oldMethod : oldMethods) {
										if (oldMethod.getStartLine() <= callLine && oldMethod.getEndLine() >= callLine) {
											m.setMethodCalledLine(
													tokenList.get(i).getStartLine(), oldMethod.getMethodName());
											System.out.println(oldMethod.getMethodName());
											System.out.println(tokenList.get(i).getStartLine());
										}
									}
								}
							}
						//}
					}
				}
			}
		}
		if (m.getMethodCalledLine().size() != 0) {
			return true;
		}
		return false;
	}

	public boolean isInlineMethod() {
		deletedMethod();
		// for (Method m : methods) {
		// if (!(similarBody(m) && methodCall(m))) {
		// //System.out.println("ExtractedMethodFound!");
		//
		// }
		// }
		//System.out.println(methods.size());
		Iterator<Method> iterator = methods.iterator();
		while (iterator.hasNext()) {
			Method m = iterator.next();
			
			if (m.getCodeToken().size() == 0) {
				System.out.println("ERROR!");
				continue;
			}
			if (!( methodCall(m) && similarBody(m)))
				iterator.remove();
		}
		for(Method m : methods) {
			System.out.println(m.getMethodName());
		}
		if (methods.size() != 0) {
			return true;
		}
		return false;
	}

	public List<RefactChange> refactor() {
		if (isInlineMethod()) {
			return refactList;
		}
		return null;
	}
	
	public void testMethodCalled() {
		deletedMethod();
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
