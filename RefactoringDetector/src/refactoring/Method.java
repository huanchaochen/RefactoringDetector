package refactoring;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.edu.sysu.syntaxsimilar.Token;

public class Method {
	String methodName;
	List<Token> codeToken;
	int startLine;
	int endLine;
	HashMap<Integer, String> methodCalledLine;
	public Method() {
		super();
		codeToken = new ArrayList<Token>();
		methodCalledLine = new HashMap<Integer, String>();
	}

	

	
}
