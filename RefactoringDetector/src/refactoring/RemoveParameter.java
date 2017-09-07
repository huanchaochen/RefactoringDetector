package refactoring;

import java.util.ArrayList;
import java.util.List;

import cn.edu.sysu.diffextraction.DiffType;
import cn.edu.sysu.syntaxsimilar.Token;
import stucture.Method;
import stucture.RefactChange;
import stucture.RefactorType;

public class RemoveParameter {
	List<Token> tokenListOld;
	List<Token> tokenListNew;
	List<DiffType> diffList;
	List<Method> methods;
	List<RefactChange> refactList;

	public void getRemoveParameterMethod() {
		for (DiffType d : diffList) {
			if (d.getType().equals("PARAMETER_DELETE")) {
				Method m = new Method();
				//m.startLine = d.getOldStartLine();
				m.setStartLine(d.getOldStartLine());
				//m.endLine = d.getOldEndLine();//²»¿¼ÂÇº¯ÊýÌå
				m.setEndLine(d.getOldEndLine());
				List<Token> tokenOld = d.getOldTokenList();
				for (int i = 1; i < tokenOld.size(); i++) {
					System.out.println(tokenOld.get(i).getTokenName() + ":" + tokenOld.get(i).getKeyword());
					if (tokenOld.get(i).getTokenName().equals("SingleVariableDeclaration")) {
							String s = tokenOld.get(i - 1).getKeyword();
							//m.methodName = s;
							m.setMethodName(s);
							break;
						
					}
				}
				methods.add(m);
			}
		}

//		for (Method m : methods) {
//			System.out.println(m.methodName);
//			System.out.println(m.startLine);
//			System.out.println(m.endLine);
//		}
	}

	public RemoveParameter(List<Token> tokenListOld, List<Token> tokenListNew, List<DiffType> diffList) {
		super();
		this.tokenListOld = tokenListOld;
		this.tokenListNew = tokenListNew;
		this.diffList = diffList;
		this.methods = new ArrayList<Method>();
		this.refactList = new ArrayList<RefactChange>();
	}

	public boolean isRemoveParameter() {
		getRemoveParameterMethod();
		if (methods.size() != 0) {
			// TODO:find old method using method name
			for (Method m : methods) {
				for (DiffType d : diffList) {
					if (d.getType().equals("STATEMENT_UPDATE")) {
						List<Token> tokenNew = d.getNewTokenList();
						for (int i = 0; i < tokenNew.size() - 1; i++) {
							if (tokenNew.get(i).getTokenName().equals("MethodInvocation")) {
								
								for (int j = 1; j <= tokenNew.size() - i - 1; j++) {
									String s = tokenNew.get(i + j).getKeyword();
									if (s != null) {
										if (s.equals(m.getMethodName())) {
											RefactChange r = new RefactChange();
											r.setType(RefactorType.REMOVEPARAMETER);
											r.setOldStartLine(d.getOldStartLine());
											r.setOldEndLine(d.getOldEndLine());
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
			}
			return true;
		}
		return false;
	}

	public List<RefactChange> refactor() {
		if (isRemoveParameter()) {
			return refactList;
		}
		return null;
	}
}
