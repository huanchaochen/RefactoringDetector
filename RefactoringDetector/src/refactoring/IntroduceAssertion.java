package refactoring;

import java.util.ArrayList;
import java.util.List;

import cn.edu.sysu.diffextraction.DiffType;
import cn.edu.sysu.syntaxsimilar.Token;

public class IntroduceAssertion {
	List<Token> tokenListOld;
	List<Token> tokenListNew;
	List<DiffType> diffList;
	List<Method> methods;
	List<RefactChange> refactList;
	List<Method> oldMethods;
	List<Method> newMethods;

	public IntroduceAssertion(List<Token> tokenListOld, List<Token> tokenListNew, List<DiffType> diffList) {
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
				m.methodName = t.getKeyword();
				System.out.println(m.methodName);
				m.startLine = t.getStartLine();
				m.endLine = t.getEndLine();
				oldMethods.add(m);
			}
		}
		for (Token t : tokenListNew) {
			if (t.getTokenName().equals("MethodDeclaration")) {
				Method m = new Method();
				m.methodName = t.getKeyword();
				System.out.println(m.methodName);
				m.startLine = t.getStartLine();
				m.endLine = t.getEndLine();
				newMethods.add(m);
			}
		}
	}

	public boolean isIntroduceAssertion() {
		for (DiffType d : diffList) {
			if (d.getType().equals("STATEMENT_INSERT")) {
				for (Token t : d.getNewTokenList()) {
					if (t.getTokenName().equals("SimpleName")) {
						if (t.getKeyword().equals("Assert")) {
							// System.out.println("-------------------------");
							String newMethodName = null;
							for (Method newMethod : newMethods) {

								if (newMethod.startLine <= t.getStartLine() && newMethod.endLine >= t.getEndLine()) {
									newMethodName = newMethod.methodName;
								}

							}
							for (Method oldMethod : oldMethods) {
								if (oldMethod.methodName.equals(newMethodName)) {

									RefactChange r = new RefactChange();
									r.setType(RefactorType.INTRODUCEASSERTION);
									r.setOldStartLine(oldMethod.startLine);
									r.setOldEndLine(oldMethod.endLine);
									r.setNewStartLine(d.getNewStartLine());
									r.setNewEndLine(d.getNewEndLine());
									refactList.add(r);

								}
							}
							break;
						}
					}
				}
			}
		}

		if (refactList != null) {
			return true;
		}
		return false;
	}

	public List<RefactChange> refactor() {
		if (isIntroduceAssertion()) {
			return refactList;
		}
		return null;
	}

}
