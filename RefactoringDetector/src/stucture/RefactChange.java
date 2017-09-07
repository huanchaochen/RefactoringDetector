package stucture;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import cn.edu.sysu.syntaxsimilar.Token;

public class RefactChange {

	private RefactorType type;
	private int newStartLine;
	private int oldStartLine;
	private int newEndLine;
	private int oldEndLine;
	private Integer methodCalledLine;// 抽取函数的调用行号

	public RefactorType getType() {
		return type;
	}

	public void setType(RefactorType type) {
		this.type = type;
	}

	public int getNewStartLine() {
		return newStartLine;
	}

	public void setNewStartLine(int newStartLine) {
		this.newStartLine = newStartLine;
	}

	public int getOldStartLine() {
		return oldStartLine;
	}

	public void setOldStartLine(int oldStartLine) {
		this.oldStartLine = oldStartLine;
	}

	public int getNewEndLine() {
		return newEndLine;
	}

	public void setNewEndLine(int newEndLine) {
		this.newEndLine = newEndLine;
	}

	public int getOldEndLine() {
		return oldEndLine;
	}

	public void setOldEndLine(int oldEndLine) {
		this.oldEndLine = oldEndLine;
	}

	public Integer getMethodCalledLine() {
		return methodCalledLine;
	}

	public void setMethodCalledLine(Integer collection) {
		this.methodCalledLine = collection;
	}

	public static List<RefactChange> getRefactoringChange(Set<List<Token>> list, Method method, int line, 
			RefactorType refactoringType) {
		List<RefactChange> refactorList = new ArrayList<RefactChange>();
		for (List<Token> s : list) {
			int oldStartLine = 0, oldEndLine = 0, newStartLine = 0, newEndLine = 0;
			if (refactoringType.equals(RefactorType.EXTRACTMETHOD)) {
				oldStartLine = getStartLine(s);
				oldEndLine = getEndLine(s);
				newStartLine = method.getStartLine();
				newEndLine = method.getEndLine();
			}
			if (refactoringType.equals(RefactorType.INLINEMETHOD)) {
				oldStartLine = method.getStartLine();
				oldEndLine = method.getEndLine();
				newStartLine = getStartLine(s);
				newEndLine = getEndLine(s);
			}else {
				oldStartLine = getStartLine(s);
				oldEndLine = getEndLine(s);
				newStartLine = method.getStartLine();
				newEndLine = method.getEndLine();
			}
			RefactChange r = new RefactChange();
			r.setType(refactoringType);
			r.setOldStartLine(oldStartLine);
			r.setOldEndLine(oldEndLine);
			r.setNewStartLine(newStartLine);
			r.setNewEndLine(newEndLine);
			r.setMethodCalledLine(line);

			refactorList.add(r);

//			for (Token t : s) {
//				System.out.println(t.getStartLine() + ":" + t.getTokenName() + ":" + t.getKeyword());
//
//			}
		}
		return refactorList;
	}

	public static int getStartLine(List<Token> l) {
		int startLine = 999999;
		for (Token t : l) {
			if (t.getStartLine() < startLine) {
				startLine = t.getStartLine();
			}
		}
		return startLine;
	}

	public static int getEndLine(List<Token> l) {
		int endLine = 0;
		for (Token t : l) {
			if (t.getEndLine() > endLine) {
				endLine = t.getEndLine();
			}
		}
		return endLine;
	}

}
