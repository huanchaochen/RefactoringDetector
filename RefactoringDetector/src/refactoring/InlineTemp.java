package refactoring;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.edu.sysu.diffextraction.DiffType;
import cn.edu.sysu.syntaxsimilar.Token;
import cn.edu.sysu.syntaxsimilar.TokenType;
import stucture.DiffTypeName;
import stucture.Method;
import stucture.RefactChange;
import stucture.RefactorType;

public class InlineTemp {
	List<Token> tokenListOld;
	List<Token> tokenListNew;
	List<DiffType> diffList;
	List<Method> methods;
	List<RefactChange> refactList;
	List<Method> oldMethods;
	List<Method> newMethods;

	public InlineTemp(List<Token> tokenListOld, List<Token> tokenListNew, List<DiffType> diffList) {
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
				// System.out.println(m.methodName);
				m.setStartLine(t.getStartLine());
				m.setEndLine(t.getEndLine());
				oldMethods.add(m);
			}
		}
		for (Token t : tokenListNew) {
			if (t.getTokenName().equals(TokenType.MethodDeclaration.toString())) {
				Method m = new Method();
				m.setMethodName(t.getKeyword());
				// System.out.println(m.methodName);
				m.setStartLine(t.getStartLine());
				m.setEndLine(t.getEndLine());
				newMethods.add(m);
			}
		}
	}

	public boolean deleteVariable() {
		for (DiffType d : diffList) {
			if (d.getType().equals(DiffTypeName.STATEMENT_DELETE.toString())) {
				for (Token t : d.getOldTokenList()) {
					if (t.getTokenName().equals(TokenType.VariableDeclarationFragment.toString())) {
						System.out.println(t.getKeyword());
						for (Method oldMethod : oldMethods) {
							if (oldMethod.getStartLine() <= d.getOldStartLine()
									&& oldMethod.getEndLine() >= d.getOldEndLine()) {
								Method m = new Method();
								m.setMethodName(oldMethod.getMethodName());
								m.setStartLine(d.getOldStartLine());
								m.setEndLine(d.getOldEndLine());
								m.setCodeTokenList(d.getOldTokenList());
								methods.add(m);
							}
						}
					}

					if (t.getTokenName().equals(TokenType.Assignment.toString())) {
						// System.out.println(t.getKeyword());
						for (Method oldMethod : oldMethods) {
							if (oldMethod.getStartLine() <= d.getOldStartLine()
									&& oldMethod.getEndLine() >= d.getOldEndLine()) {
								Method m = new Method();
								m.setMethodName(oldMethod.getMethodName());
								m.setStartLine(d.getOldStartLine());
								m.setEndLine(d.getOldEndLine());
								m.setCodeTokenList(d.getOldTokenList());
								methods.add(m);
							}
						}
					}

				}
			}

		}
		if (methods.size() != 0) {
			return true;
		}
		return false;
	}

	public boolean deleteAssignment() {
		Iterator<Method> iterator = methods.iterator();
		while (iterator.hasNext()) {
			Method m = iterator.next();
			for (Token t : m.getCodeToken()) {
				if (t.getTokenName().equals(TokenType.MethodInvocation.toString())) {
					m.setMethodCalledLine(1, t.getKeyword());// 记录被删除的函数调用的名字,代码行忽略
				}

			}
			if (m.getMethodCalledLine().size() == 0) {
				iterator.remove();
			}
		}

		if (methods.size() != 0) {
			return true;
		}
		return false;
	}

	public boolean updateStatement() {

		Iterator<Method> iterator = methods.iterator();
		while (iterator.hasNext()) {
			Method m = iterator.next();

			String variableName = "";
			String callMethodName = m.getMethodCalledLine().get(1);
			System.out.println(callMethodName);
			for (int i = 0; i < m.getCodeToken().size(); i++) {
				if (m.getCodeToken().get(i).getTokenName().equals(TokenType.VariableDeclarationFragment.toString())) {
					variableName = m.getCodeToken().get(i).getKeyword(); // 获取被删除的变量名
					// System.out.println(variableName);
				}
				if (m.getCodeToken().get(i).getTokenName().equals(TokenType.Assignment.toString())) {
					if (m.getCodeToken().get(i + 1).getTokenName().equals(TokenType.SimpleName.toString())) {
						variableName = m.getCodeToken().get(i + 1).getKeyword(); // 获取被删除的变量名
						// System.out.println(variableName);
					}
				}
			}

			for (Method newMethod : newMethods) {
				System.out.println(m.getMethodName());
				if (newMethod.getMethodName().equals(m.getMethodName())) {
					System.out.println("equal");
					for (DiffType d : diffList) {
						if (d.getType().equals(DiffTypeName.STATEMENT_UPDATE.toString())) {
							System.out.println(d.getNewStartLine());
							System.out.println(d.getNewEndLine());
							System.out.println(newMethod.getStartLine());
							System.out.println(newMethod.getEndLine());
							if (d.getNewStartLine() >= newMethod.getStartLine()
									&& d.getNewEndLine() <= newMethod.getEndLine()) {
								System.out.println("in");
								boolean deleted = false;
								boolean added = false;
								for (Token t : d.getOldTokenList()) {
									if (t.getTokenName().equals(TokenType.SimpleName.toString())) {
										if (t.getKeyword().equals(variableName)) {
											deleted = true;
										}
									}
								}
								for (Token t : d.getNewTokenList()) {
									if (t.getTokenName().equals(TokenType.MethodInvocation.toString())) {
										if (t.getKeyword().equals(callMethodName)) {
											added = true;
										}
									}
								}

								if (deleted && added) {
									RefactChange r = new RefactChange();
									int oldStart = m.getStartLine();
									int oldEnd = m.getEndLine();
									r.setType(RefactorType.INLINETEMP);
									if (d.getOldStartLine() < m.getStartLine()) {
										oldStart = d.getOldStartLine();
									}
									if (d.getOldEndLine() > m.getEndLine()) {
										oldEnd = d.getOldEndLine();
									}
									r.setOldStartLine(oldStart);
									r.setOldEndLine(oldEnd);
									r.setNewStartLine(d.getNewStartLine());
									r.setNewEndLine(d.getNewEndLine());
									refactList.add(r);
								}
							}
						}
					}

				}
			}
		}
		if (refactList.size() != 0) {
			return true;
		}
		return false;
	}

	public boolean isInlineTemp() {
		if (deleteVariable() && deleteAssignment() && updateStatement()) {
			return true;
		}
		return false;
	}

	public List<RefactChange> refactor() {
		if (isInlineTemp()) {
			return refactList;
		}
		return null;
	}

}
