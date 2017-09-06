package refactoring;

import java.util.ArrayList;
import java.util.List;

import cn.edu.sysu.diffextraction.DiffType;
import cn.edu.sysu.syntaxsimilar.Token;

public class AddParameter {
	List<Token> tokenListOld;
	List<Token> tokenListNew;
	List<DiffType> diffList;
	List<Method> methods;
	List<RefactChange> refactList;
 
	public void getAddParameterMethod() {
		for (DiffType d : diffList) {
			if (d.getType().equals("PARAMETER_INSERT")) {
				Method m = new Method();
				m.startLine = d.getNewStartLine();
				m.endLine = d.getNewEndLine();
				List<Token> tokenNew = d.getNewTokenList();
				for (int i = 1; i < tokenNew.size(); i++) {
					System.out.println(tokenNew.get(i).getTokenName() + ":" + tokenNew.get(i).getKeyword());
					if (tokenNew.get(i).getTokenName().equals("SingleVariableDeclaration")) {
							String s = tokenNew.get(i - 1).getKeyword();
							m.methodName = s;
							break;
						
					}
				}
				methods.add(m);
			}
		}

		for (Method m : methods) {
			System.out.println(m.methodName);
			System.out.println(m.startLine);
			System.out.println(m.endLine);
		}
	}

	public AddParameter(List<Token> tokenListOld, List<Token> tokenListNew, List<DiffType> diffList) {
		super();
		this.tokenListOld = tokenListOld;
		this.tokenListNew = tokenListNew;
		this.diffList = diffList;
		this.methods = new ArrayList<Method>();
		this.refactList = new ArrayList<RefactChange>();
	}

	public boolean isAddParameter() {
		getAddParameterMethod();
		if (methods.size() != 0) {
			// TODO:find old method using method name
			for (Method m : methods) {
				for (DiffType d : diffList) {
					if (d.getType().equals("STATEMENT_UPDATE")) {
						List<Token> tokenNew = d.getNewTokenList();
						for (int i = 0; i <= tokenNew.size() - 1; i++) {
							if (tokenNew.get(i).getTokenName().equals("MethodInvocation")) {
								
								for (int j = 1; j < tokenNew.size() - i - 1; j++) {
									String s = tokenNew.get(i + j).getKeyword();
									if (s != null) {
										if (s.equals(m.methodName)) {
											RefactChange r = new RefactChange();
											r.setType(RefactorType.ADDPARAMETER);
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
		if (isAddParameter()) {
			return refactList;
		}
		return null;
	}
}
