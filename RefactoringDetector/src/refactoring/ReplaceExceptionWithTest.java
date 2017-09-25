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

public class ReplaceExceptionWithTest {
	List<Token> tokenListOld;
	List<Token> tokenListNew;
	List<DiffType> diffList;
	List<Method> methods;
	List<RefactChange> refactList;
	List<Method> oldMethods;
	List<Method> newMethods;

	public ReplaceExceptionWithTest(List<Token> tokenListOld, List<Token> tokenListNew, List<DiffType> diffList) {
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

	public boolean deleteTryCatch() {
		for (DiffType d : diffList) {
			if (d.getType().equals(DiffTypeName.STATEMENT_DELETE.toString())) {
				boolean hasTryStatement = false;
				boolean hasCatchClause = false;
				for (Token t : d.getOldTokenList()) {
					if (t.getTokenName().equals(TokenType.TryStatement.toString())) {
						hasTryStatement = true;
					}
					if (t.getTokenName().equals(TokenType.CatchClause.toString())) {
						hasCatchClause = true;
					}
				}

				if (hasTryStatement && hasCatchClause) {

					for (Method oldMethod : oldMethods) {
						if (oldMethod.getStartLine() <= d.getOldStartLine()
								&& oldMethod.getEndLine() >= d.getOldEndLine()) {
							Method m = new Method();
							m.setMethodName(oldMethod.getMethodName());
							m.setStartLine(d.getOldStartLine());
							m.setEndLine(d.getOldEndLine());// 起始行结束行设为trycatch对应的行号
							methods.add(m);// 记录有trycatch的旧函数
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

	public boolean addIfStatement() {

		Iterator<Method> iterator = methods.iterator();
		while (iterator.hasNext()) {
			boolean hasIf = false;
			Method tryCatchMethod = iterator.next();
			for (Method newMethod : newMethods) {
				if (tryCatchMethod.getMethodName().equals(newMethod.getMethodName())) {
					for (DiffType d : diffList) {
						if (d.getNewStartLine() >= newMethod.getStartLine()
								&& d.getNewEndLine() <= newMethod.getEndLine()) {
							if (d.getType().equals(DiffTypeName.STATEMENT_INSERT.toString())) {
								for (Token t : d.getNewTokenList()) {
									if (t.getTokenName().equals(TokenType.IfStatement.toString())) {
										hasIf = true;
										if (checkStatementParentScope(tryCatchMethod, newMethod, d)) {
											RefactChange r = new RefactChange();
											r.setType(RefactorType.REPLACEEXCEPTIONWITHTEST);
											r.setOldStartLine(tryCatchMethod.getStartLine());
											r.setOldEndLine(tryCatchMethod.getEndLine());
											r.setNewStartLine(d.getNewStartLine());
											r.setNewEndLine(d.getNewEndLine());
											refactList.add(r);
										}
										break;
									}
								}
							}
						}
					}
				}
			}
			if (!hasIf) {
				iterator.remove();
			}

		}

		if (methods.size() != 0) {
			return true;
		}
		return false;
	}

	// 检查语句是否在ifelse里面，且ifelse里面的语句的父语句是否在代码删除的范围内,
	public boolean checkStatementParentScope(Method m, Method newMethod, DiffType insertIf) {

		for (DiffType d : diffList) {
			if (d.getNewStartLine() >= newMethod.getStartLine() && d.getNewEndLine() <= newMethod.getEndLine()) {
				if (d.getType().equals(DiffTypeName.STATEMENT_PARENT_CHANGE.toString())) {
					if (d.getNewStartLine() >= insertIf.getNewStartLine()
							&& d.getNewEndLine() <= insertIf.getNewEndLine()) {
						if (d.getOldStartLine() >= m.getStartLine() && d.getOldEndLine() <= m.getEndLine()) {

							return true;
						}
					}
				}
			}
		}
		return false;
	}

	public boolean isReplaceExceptionWithTest() {

		if (deleteTryCatch() && addIfStatement()) {
			if (refactList != null) {
				return true;
			}
		}

		return false;
	}

	public List<RefactChange> refactor() {
		if (isReplaceExceptionWithTest()) {
			return refactList;
		}
		return null;
	}
}
