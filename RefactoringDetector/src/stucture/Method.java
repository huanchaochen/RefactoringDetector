package stucture;

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
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public List<Token> getCodeToken() {
		return codeToken;
	}
	public void setCodeToken(Token codeToken) {
		this.codeToken.add(codeToken);
	}
	
	public void setCodeTokenList(List<Token> codeTokenList) {
		this.codeToken = codeTokenList;
	}
	
	public int getStartLine() {
		return startLine;
	}
	public void setStartLine(int startLine) {
		this.startLine = startLine;
	}
	public int getEndLine() {
		return endLine;
	}
	public void setEndLine(int endLine) {
		this.endLine = endLine;
	}
	public HashMap<Integer, String> getMethodCalledLine() {
		return methodCalledLine;
	}
	public void setMethodCalledLine(int line, String methodName) {
		this.methodCalledLine.put(line, methodName);
	}

	

	
}
