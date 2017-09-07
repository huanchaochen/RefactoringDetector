package refactoring;

import java.util.ArrayList;
import java.util.List;

import cn.edu.sysu.diffextraction.DiffType;
import cn.edu.sysu.syntaxsimilar.Token;
import stucture.DiffTypeName;
import stucture.Method;
import stucture.RefactChange;
import stucture.RefactorType;

public class RenameMethod {
	List<Token> tokenListOld;
	List<Token> tokenListNew;
	List<DiffType> diffList;
	List<Method> methods;
	List<RefactChange> refactList;

	public RenameMethod(List<Token> tokenListOld, List<Token> tokenListNew, List<DiffType> diffList) {
		super();
		this.tokenListOld = tokenListOld;
		this.tokenListNew = tokenListNew;
		this.diffList = diffList;
		this.methods = new ArrayList<Method>();
		this.refactList = new ArrayList<RefactChange>();
	}
	
	public boolean isRenameMethod() {
		for (DiffType d : diffList) {
			if (d.getType().equals(DiffTypeName.METHOD_RENAMING.toString())) {
				RefactChange r = new RefactChange();
				r.setType(RefactorType.RENAMEMETHOD);
				r.setOldStartLine(d.getOldStartLine());
				r.setOldEndLine(d.getOldEndLine());
				r.setNewStartLine(d.getNewStartLine());
				r.setNewEndLine(d.getNewEndLine());
				refactList.add(r);
			}
		}
		
		if(refactList != null) {
			return true;
		}
		return false;
	}	

	public List<RefactChange> refactor() {
		if (isRenameMethod()) {
			return refactList;
		}
		return null;
	}
	
}
