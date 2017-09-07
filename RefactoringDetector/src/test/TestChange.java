package test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jface.text.Assert;

import ch.uzh.ifi.seal.changedistiller.ChangeDistiller;
import ch.uzh.ifi.seal.changedistiller.ChangeDistiller.Language;
import ch.uzh.ifi.seal.changedistiller.distilling.FileDistiller;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeChange;
import cn.edu.sysu.diffextraction.ChangeAnalysis;
import cn.edu.sysu.diffextraction.DiffType;
import cn.edu.sysu.syntaxsimilar.Parser2;
import cn.edu.sysu.syntaxsimilar.Token;
import cn.edu.sysu.syntaxsimilar.Tokenizer2;
import cn.edu.sysu.test.CodeSpliterTool;
import refactoring.ExtractMethod;
import refactoring.IntroduceAssertion;
import refactoring.ReplaceExceptionWithTest;
import stucture.RefactChange;

public class TestChange {

	static List<Token> parse2TokenList(String path) throws IOException {
		ASTParser parser = ASTParser.newParser(AST.JLS4);
		String source = CodeSpliterTool.fileToString(path);
		char[] codeCharArrays = source.toCharArray();
		parser.setSource(codeCharArrays);
		CompilationUnit unit = (CompilationUnit) parser.createAST(null);
		Tokenizer2 tokenizer = Parser2.parseAST2Tokens(unit);
		return tokenizer.getTokenList();
	}

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String oldPath = "file\\ReplaceExceptionWithTest\\v1\\REWT.java";
		String newPath = "file\\ReplaceExceptionWithTest\\v2\\REWT.java";

		// FileDistiller distiller =
		// ChangeDistiller.createFileDistiller(Language.JAVA);
		List<DiffType> diffList = null;

		List<Token> tokenListOld = new ArrayList<Token>();
		tokenListOld = parse2TokenList(oldPath);

		List<Token> tokenListNew = new ArrayList<Token>();
		tokenListNew = parse2TokenList(newPath);
		System.out.println("old:");
		for (Token t : tokenListOld) {
			System.out.println(t.getTokenName());
			System.out.println(t.getKeyword());
		}
		System.out.println("new:");
		for (Token t : tokenListNew) {
			System.out.println(t.getTokenName());
			System.out.println(t.getKeyword());
		}

		try {
			diffList = ChangeAnalysis.changeDistill(oldPath, newPath, tokenListNew, tokenListOld);

		} catch (Exception e) {
			/*
			 * An exception most likely indicates a bug in ChangeDistiller. Please file a
			 * bug report at https://bitbucket.org/sealuzh/tools-changedistiller/issues and
			 * attach the full stack trace along with the two files that you tried to
			 * distill.
			 */
			System.err.println("Warning: error while change distilling. " + e.getMessage());
		}
		for (DiffType d : diffList) {
			System.out.println(d.getType());
			System.out.println(d.getOldStartLine());
			System.out.println(d.getOldEndLine());
			System.out.println(d.getNewStartLine());
			System.out.println(d.getNewEndLine());
		}
		ReplaceExceptionWithTest e = new ReplaceExceptionWithTest(tokenListOld, tokenListNew, diffList);
		// e.testMethodCalled();
		//e.isReplaceExceptionWithTest();
		List<RefactChange> l = new ArrayList<RefactChange>();
		l = e.refactor();

		if (l != null) {
			for (RefactChange r : l) {
				System.out.println(r.getType());
				System.out.println(r.getOldStartLine());
				System.out.println(r.getOldEndLine());
				System.out.println(r.getNewStartLine());
				System.out.println(r.getNewEndLine());
				if (r.getMethodCalledLine() != null)
					System.out.println(r.getMethodCalledLine());

			}
		}

		// AddParameter a = new AddParameter(tokenListOld, tokenListNew, diffList);
		// a.getAddParameterMethod();
		// ExtractMethod e = new ExtractMethod(tokenListOld, tokenListNew, diffList);
		// e.getExtractMethod();
		// for(Token t : tokenListOld) {
		// System.out.println(t.getTokenName()+" : "+t.getKeyword());
		// }
		// for (DiffType d : diffList) {
		// System.out.println(d.getType());
		////// if (d.getType().equals("STATEMENT_INSERT")) {
		////// List<String> keyWordOld = d.getOldKeywordList();
		////// List<String> keyWordNew = d.getNewKeywordList();
		// List<Token> tokenNew = d.getNewTokenList();
		////// if (keyWordOld != null) {
		////// for (String s : keyWordOld) {
		////// System.out.println(s);
		////// }
		////// }
		////// if (keyWordNew != null) {
		////// for (String s : keyWordNew) {
		////// System.out.println(s);
		////// }
		////// }
		////// System.out.println(d.getNewStartLine());
		////// System.out.println(d.getOldStartLine());
		// if(tokenNew != null){
		// for(Token t: tokenNew){
		// System.out.println(t.getTokenName()+" : "+t.getKeyword());
		// }
		// }
		////// }
		// }

	}

}
