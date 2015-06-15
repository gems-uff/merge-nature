/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.gems.resources.ast;

import br.uff.ic.gems.resources.data.LanguageConstruct;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.AnnotationTypeMemberDeclaration;
import org.eclipse.jdt.core.dom.BlockComment;
import org.eclipse.jdt.core.dom.BreakStatement;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.Comment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.ContinueStatement;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EmptyStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.Initializer;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.LineComment;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclarationStatement;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;
import org.eclipse.jdt.internal.compiler.ast.ForeachStatement;

/**
 *
 * @author gleiph
 */
public class Visitor extends ASTVisitor {

    List<LanguageConstruct> languageConstructs;
    private final CompilationUnit cu;
    private boolean isInterface;
    private boolean processed = false;

    public Visitor(CompilationUnit cuArg) {
        languageConstructs = new ArrayList<>();
        this.cu = cuArg;
        processed = false;
    }

    /**
     * @return the languageConstructs
     */
    public List<LanguageConstruct> getLanguageConstructs() {

        if (!processed) {

            for (LanguageConstruct languageConstruct : languageConstructs) {
                if (languageConstruct.getName().equals(ASTTypes.METHOD_DECLARATION)) {
                    for (LanguageConstruct languageConstruct1 : languageConstructs) {
                        if (languageConstruct1.getName().equals(ASTTypes.ANNOTATION)
                                && languageConstruct.getBeginLine() == languageConstruct1.getBeginLine()) {
                            languageConstruct.setBeginLine(languageConstruct.getBeginLine() + 1);
                        }
                    }
                }
            }

        }

        return languageConstructs;
    }

    /**
     * @param languageConstructs the languageConstructs to set
     */
    public void setLanguageConstructs(List<LanguageConstruct> languageConstructs) {
        this.languageConstructs = languageConstructs;
    }

    /*=========================================================================
     ***************************************************************************
     |                       Begin visitors selected                           |
     ***************************************************************************
     =========================================================================*/
    @Override
    public boolean visit(AnnotationTypeMemberDeclaration node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        languageConstructs.add(new LanguageConstruct(ASTTypes.ANNOTATION_TYPE_MEMBER_DECLARATION, begin, end));

        return true;
    }

    @Override
    public boolean visit(BlockComment node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        languageConstructs.add(new LanguageConstruct(ASTTypes.COMMENT, begin, end));

        return true;
    }

    @Override
    public boolean visit(CatchClause node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        languageConstructs.add(new LanguageConstruct(ASTTypes.CATCH_CLAUSE, begin, end));

        return true;
    }

    @Override
    public boolean visit(ClassInstanceCreation node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        languageConstructs.add(new LanguageConstruct(ASTTypes.METHOD_INVOCATION, begin, end));

        return true;
    }

    public boolean visit(Comment node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        languageConstructs.add(new LanguageConstruct(ASTTypes.COMMENT, begin, end));

        return true;
    }

    @Override
    public boolean visit(EnhancedForStatement node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        languageConstructs.add(new LanguageConstruct(ASTTypes.FOR_STATEMENT, begin, end));

        return true;
    }

    @Override
    public boolean visit(FieldDeclaration node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        languageConstructs.add(new LanguageConstruct(ASTTypes.ATTRIBUTE, begin, end));

        return true;
    }

    @Override
    public boolean visit(ForStatement node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        languageConstructs.add(new LanguageConstruct(ASTTypes.FOR_STATEMENT, begin, end));

        return true;
    }

    @Override
    public boolean visit(IfStatement node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        languageConstructs.add(new LanguageConstruct(ASTTypes.IF_STATEMENT, begin, end));

        return true;
    }

    @Override
    public boolean visit(ImportDeclaration node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        languageConstructs.add(new LanguageConstruct(ASTTypes.IMPORT_DECLARATION, begin, end));

        return true;
    }

    @Override
    public boolean visit(Initializer node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        languageConstructs.add(new LanguageConstruct(ASTTypes.INITIALIZER, begin, end));

        return true;
    }

    @Override
    public boolean visit(Javadoc node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        languageConstructs.add(new LanguageConstruct(ASTTypes.COMMENT, begin, end));

        return true;
    }

    @Override
    public boolean visit(LineComment node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        languageConstructs.add(new LanguageConstruct(ASTTypes.COMMENT, begin, end));

        return true;
    }

    @Override
    public boolean visit(MarkerAnnotation node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        languageConstructs.add(new LanguageConstruct(ASTTypes.ANNOTATION, begin, end));

        return true;
    }

    @Override
    public boolean visit(MethodDeclaration node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());

        languageConstructs.add(new LanguageConstruct(ASTTypes.METHOD_DECLARATION, begin, end));

        return true;
    }

    @Override
    public boolean visit(MethodInvocation node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        languageConstructs.add(new LanguageConstruct(ASTTypes.METHOD_INVOCATION, begin, end));

        return true;
    }

    @Override
    public boolean visit(PackageDeclaration node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        languageConstructs.add(new LanguageConstruct(ASTTypes.PACKAGE_DECLARATION, begin, end));

        return true;
    }

    @Override
    public boolean visit(ReturnStatement node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        languageConstructs.add(new LanguageConstruct(ASTTypes.RETURN_STATEMENT, begin, end));

        return true;
    }

    @Override
    public boolean visit(SingleMemberAnnotation node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());

        languageConstructs.add(new LanguageConstruct(ASTTypes.ANNOTATION, begin, end));

        return true;
    }

    @Override
    public boolean visit(SingleVariableDeclaration node) {

        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        languageConstructs.add(new LanguageConstruct(ASTTypes.VARIABLE, begin, end));

        return true;
    }

    @Override
    public boolean visit(SuperMethodInvocation node) {

        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        languageConstructs.add(new LanguageConstruct(ASTTypes.METHOD_INVOCATION, begin, end));

        return true;
    }

    @Override
    public boolean visit(SwitchCase node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        languageConstructs.add(new LanguageConstruct(ASTTypes.CASE_STATEMENT, begin, end));

        return true;
    }

    @Override
    public boolean visit(SwitchStatement node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        languageConstructs.add(new LanguageConstruct(ASTTypes.SWITCH_STATEMENT, begin, end));

        return true;
    }

    @Override
    public boolean visit(ThrowStatement node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        languageConstructs.add(new LanguageConstruct(ASTTypes.THROW_STATEMENT, begin, end));

        return true;
    }

    @Override
    public boolean visit(TryStatement node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        languageConstructs.add(new LanguageConstruct(ASTTypes.TRY_STATEMENT, begin, end));

        return true;
    }

    @Override
    public boolean visit(TypeDeclaration node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());

        isInterface = node.isInterface();

        if (isInterface) {
            languageConstructs.add(new LanguageConstruct(ASTTypes.INTERFACE_DECLARATION, begin, end));
        } else {
            languageConstructs.add(new LanguageConstruct(ASTTypes.CLASS_DECLARATION, begin, end));
        }

        return true;
    }

    @Override
    public boolean visit(VariableDeclarationExpression node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        languageConstructs.add(new LanguageConstruct(ASTTypes.VARIABLE, begin, end));

        return true;
    }

    @Override
    public boolean visit(VariableDeclarationStatement node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        languageConstructs.add(new LanguageConstruct(ASTTypes.VARIABLE, begin, end));

        return true;
    }

    @Override
    public boolean visit(WhileStatement node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        languageConstructs.add(new LanguageConstruct(ASTTypes.WHILE_STATEMENT, begin, end));

        return true;
    }

    /*=========================================================================
     ***************************************************************************
     |                         End visitors selected                           |
     ***************************************************************************
     =========================================================================*/
    /*=========================================================================
     ***************************************************************************
     |                                  Tested                                |
     ***************************************************************************
     =========================================================================*/
    /*>>>>>>>>>>>>>>>>>                   Annotation                          */
    //>>>>>>>>>>>>>>>>>>>>>>>>>>>           BreakStatement
    @Override
    public boolean visit(BreakStatement node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        languageConstructs.add(new LanguageConstruct("BreakStatement", begin, end));

        return true;
    }

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>           ContinueStatement
    @Override
    public boolean visit(ContinueStatement node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        languageConstructs.add(new LanguageConstruct("ContinueStatement", begin, end));

//        System.out.println("ContinueStatement (" + begin + ", " + end + ")");
//        System.out.println(node.toString());
        return true;
    }

    ////>>>>>>>>>>>>>>>>>>>>>>>>>>>           DoStatement
    @Override
    public boolean visit(DoStatement node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        languageConstructs.add(new LanguageConstruct("DoStatement", begin, end));

        return true;
    }

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>           Enum
    @Override
    public boolean visit(EnumConstantDeclaration node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());

        Javadoc javadoc = node.getJavadoc();

        if (javadoc != null) {

            int beginJavadoc = cu.getLineNumber(javadoc.getStartPosition());
            int endJavadoc = cu.getLineNumber(javadoc.getStartPosition() + javadoc.getLength());

            languageConstructs.add(new LanguageConstruct("EnumConstantDeclaration", endJavadoc + 1, end));
        } else {
            languageConstructs.add(new LanguageConstruct("EnumConstantDeclaration", begin, end));
        }

//        System.out.println("EnumConstantDeclaration (" + begin + ", " + end + ")");
//        System.out.println(node.toString());
        return true;
    }

    @Override
    public boolean visit(EnumDeclaration node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        languageConstructs.add(new LanguageConstruct("EnumConstantDeclaration", begin, end));

//        System.out.println("EnumConstantDeclaration (" + begin + ", " + end + ")");
//        System.out.println(node.toString());
        return true;
    }

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>           SuperConstructorInvocation 
    @Override
    public boolean visit(SuperConstructorInvocation node) {

        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        languageConstructs.add(new LanguageConstruct("SuperConstructorInvocation", begin, end));

//        System.out.println("SuperConstructorInvocation (" + begin + ", " + end + ")");
//        System.out.println(node.toString());
        return true;
    }

    /*
     */
    /*=========================================================================
     ***************************************************************************
     |                                  Untested                                |
     ***************************************************************************
     =========================================================================*/
    //Annotation
    public boolean visit(Annotation node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());

        languageConstructs.add(new LanguageConstruct("AnnotationDifferent", begin, end));

        return true;
    }

    //
    @Override
    public boolean visit(NormalAnnotation node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());

        languageConstructs.add(new LanguageConstruct("NormalAnnotation", begin, end));

        return true;
    }

    @Override
    public boolean visit(ConstructorInvocation node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());

        languageConstructs.add(new LanguageConstruct("ConstructorInvocation", begin, end));

        return true;
    }

    @Override
    public boolean visit(EmptyStatement node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());

        languageConstructs.add(new LanguageConstruct("EmptyStatement", begin, end));

        return true;
    }

    public boolean visit(ForeachStatement node) {
        int begin = cu.getLineNumber(node.sourceStart());
        int end = cu.getLineNumber(node.sourceEnd());

        languageConstructs.add(new LanguageConstruct("ForeachStatement", begin, end));

        return true;
    }

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>           TypeDeclarationStatement
    @Override
    public boolean visit(TypeDeclarationStatement node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());

        languageConstructs.add(new LanguageConstruct("TypeDeclarationStatement", begin, end));

        return true;
    }

    //Variable 
    public boolean visit(VariableDeclaration node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());

        languageConstructs.add(new LanguageConstruct("VariableDeclaration", begin, end));

        return true;
    }
}
