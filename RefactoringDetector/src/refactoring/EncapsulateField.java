package refactoring;

import java.util.ArrayList;
import java.util.List;

import cn.edu.sysu.diffextraction.DiffType;
import cn.edu.sysu.syntaxsimilar.Token;
import cn.edu.sysu.syntaxsimilar.TokenType;
import stucture.DiffTypeName;
import stucture.Method;
import stucture.RefactChange;
import stucture.RefactorType;

public class EncapsulateField {
	List<Token> tokenListOld;
	List<Token> tokenListNew;
	List<DiffType> diffList;
	List<Method> methods;
	List<RefactChange> refactList;
	
	public EncapsulateField(List<Token> tokenListOld, List<Token> tokenListNew, List<DiffType> diffList) {
		super();
		this.tokenListOld = tokenListOld;
		this.tokenListNew = tokenListNew;
		this.diffList = diffList;
		this.methods = new ArrayList<Method>();
		this.refactList = new ArrayList<RefactChange>();
	}
	
	public boolean isEncapsulateField() {
		for (DiffType d : diffList) {
			if (d.getType().equals(DiffTypeName.DECREASING_ACCESSIBILITY_CHANGE.toString())) {
				for(Token t: tokenListNew) {
					if(t.getTokenName().equals(TokenType.FieldDeclaration.toString())) {
						if(t.getStartLine() == d.getNewStartLine()) {
							RefactChange r = new RefactChange();
							r.setType(RefactorType.ENCAPSLUATEFIELD);
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
		
		if(refactList != null) {
			return true;
		}
		return false;
	}
	
	public List<RefactChange> refactor() {
		if (isEncapsulateField()) {
			return refactList;
		}
		return null;
	}
}
