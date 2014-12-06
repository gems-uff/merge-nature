/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.github.mergeviewer.ast;

import br.uff.ic.github.mergeviewer.ast.data.SourceCodeFile;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.AnnotationTypeMemberDeclaration;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.BlockComment;
import org.eclipse.jdt.core.dom.BreakStatement;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.ContinueStatement;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EmptyStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IExtendedModifier;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.Initializer;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.LineComment;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.SuperFieldAccess;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.ThisExpression;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclarationStatement;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;
import org.eclipse.jdt.core.dom.WildcardType;
import org.eclipse.jdt.internal.compiler.ast.ForeachStatement;

/**
 *
 * @author gleiph
 */
public class Visitor extends ASTVisitor {

    private SourceCodeFile sourceCode = new SourceCodeFile();
    private final CompilationUnit cu;
    private boolean isInterface;

    public Visitor(CompilationUnit cuArg) {
        this.cu = cuArg;

    }

    private static void print(IExtendedModifier node) {
    }

    /*=========================================================================
     ***************************************************************************
     |                                  Tested                                |
     ***************************************************************************
     =========================================================================*/
    /*>>>>>>>>>>>>>>>>>                   Annotation                          */
    //AnnotationTypeDeclaration
    @Override
    public boolean visit(AnnotationTypeDeclaration node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        sourceCode.add("AnnotationTypeDeclaration", begin, end);

        return true;
    }

    //AnnotationTypeMemberDeclaration
    @Override
    public boolean visit(AnnotationTypeMemberDeclaration node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        sourceCode.add("AnnotationElement", begin, end);

        return true;
    }

    //MarkerAnnotation (Annotation)
    @Override
    public boolean visit(MarkerAnnotation node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        sourceCode.add("Annotation", begin, end);

        return true;
    }

    /*>>>>>>>>>>>>>>>>>>>>>>>>>>>           Comment                          */
    //Block comment
    @Override
    public boolean visit(BlockComment node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        sourceCode.add("BlockComment", begin, end);

//        System.out.println("BlockComment (" + begin + ", " + end + ")");
//        System.out.println(node.toString());
        return true;
    }

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>           BreakStatement
    @Override
    public boolean visit(BreakStatement node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        sourceCode.add("BreakStatement", begin, end);

        return true;
    }

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>           CatchClause
    @Override
    public boolean visit(CatchClause node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        sourceCode.add("CatchClause", begin, end);

//        System.out.println("CatchClause (" + begin + ", " + end + ")");
//        System.out.println(node.toString());
        return true;
    }

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>           LineComment 
    @Override
    public boolean visit(LineComment node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        sourceCode.add("Comment", begin, end);

        return true;
    }

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>           ContinueStatement
    @Override
    public boolean visit(ContinueStatement node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        sourceCode.add("ContinueStatement", begin, end);

//        System.out.println("ContinueStatement (" + begin + ", " + end + ")");
//        System.out.println(node.toString());
        return true;
    }

    ////>>>>>>>>>>>>>>>>>>>>>>>>>>>           DoStatement
    @Override
    public boolean visit(DoStatement node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        sourceCode.add("DoStatement", begin, end);

        return true;
    }

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>           Enum
    @Override
    public boolean visit(EnumConstantDeclaration node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());

        Javadoc javadoc = node.getJavadoc();

        int beginJavadoc = cu.getLineNumber(javadoc.getStartPosition());
        int endJavadoc = cu.getLineNumber(javadoc.getStartPosition() + javadoc.getLength());

        sourceCode.add("EnumConstantDeclaration", endJavadoc + 1, end);

//        System.out.println("EnumConstantDeclaration (" + begin + ", " + end + ")");
//        System.out.println(node.toString());
        return true;
    }

    @Override
    public boolean visit(EnumDeclaration node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        sourceCode.add("EnumConstantDeclaration", begin, end);

//        System.out.println("EnumConstantDeclaration (" + begin + ", " + end + ")");
//        System.out.println(node.toString());
        return true;
    }

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>           Field - Attribute
    @Override
    public boolean visit(FieldDeclaration node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        sourceCode.add("Attribute", begin, end);

        return true;
    }

    /*
     FieldAccess
     this.name
     */
    @Override
    public boolean visit(FieldAccess node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        sourceCode.add("FieldAccess", begin, end);

//        System.out.println("FieldAccess");
//        System.out.println(node.toString());
        return true;
    }

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>           ForStatement
    @Override
    public boolean visit(ForStatement node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        sourceCode.add("ForStatement", begin, end);

//        System.out.println("ForStatement(" + begin + ", " + end + ")");
//        System.out.println(node.toString());
        return true;
    }

    @Override
    public boolean visit(EnhancedForStatement node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        sourceCode.add("ForStatement", begin, end);

//        System.out.println("EnhancedForStatement(" + begin + ", " + end + ")");
//        System.out.println(node.toString());
        return true;
    }

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>           If statement
    @Override
    public boolean visit(IfStatement node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        sourceCode.add("IfStatement", begin, end);

//        System.out.println("IfStatement(" + begin + ", " + end + ")");
//        System.out.println(node.toString());
        return true;
    }

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>           Import
    @Override
    public boolean visit(ImportDeclaration node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        sourceCode.add("ImportDeclaration", begin, end);

//        System.out.println("ImportDeclaration(" + begin + ", " + end + ")");
//        System.out.println(node.toString());
        return true;
    }

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>           Instance initializer
    @Override
    public boolean visit(Initializer node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        sourceCode.add("Initializer", begin, end);

//        System.out.println("Initializer(" + begin + ", " + end + ")");
//        System.out.println(node.toString());
        //Put here the diference between the diferent kinds of initialization 
        return true;
    }

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>           Instanciation
    @Override
    public boolean visit(ClassInstanceCreation node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        sourceCode.add("ClassInstanceCreation", begin, end);

//        System.out.println("ClassInstanceCreation (" + begin + ", " + end + ")");
//        System.out.println(node.toString());
        return true;
    }

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>           Javadoc
    @Override
    public boolean visit(Javadoc node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        sourceCode.add("Javadoc", begin, end);

//        System.out.println("Javadoc (" + begin + ", " + end + ")");
//        System.out.println(node.toString());
        return true;
    }

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>           Method declaration
    @Override
    public boolean visit(MethodDeclaration node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());

        sourceCode.add("MethodDeclaration", begin, end);

        return true;
    }

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>           Method invocation
    @Override
    public boolean visit(MethodInvocation node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        sourceCode.add("MethodInvocation", begin, end);

        return true;
    }

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>           Package 
    @Override
    public boolean visit(PackageDeclaration node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        sourceCode.add("PackageDeclaration", begin, end);

//        System.out.println("PackageDeclaration(" + begin + ", " + end + ")");
//        System.out.println(node.toString());
        return true;
    }

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>           Return statement
    @Override
    public boolean visit(ReturnStatement node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        sourceCode.add("ReturnStatement", begin, end);

        return true;
    }

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>           SuperConstructorInvocation 
    @Override
    public boolean visit(SuperConstructorInvocation node) {

        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        sourceCode.add("SuperConstructorInvocation", begin, end);

//        System.out.println("SuperConstructorInvocation (" + begin + ", " + end + ")");
//        System.out.println(node.toString());
        return true;
    }

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>           SuperFieldAccess 
    @Override
    public boolean visit(SuperFieldAccess node) {

        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        sourceCode.add("SuperFieldAccess", begin, end);

//        System.out.println("SuperFieldAccess (" + begin + ", " + end + ")");
//        System.out.println(node.toString());
        return true;
    }

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>           SuperMethodInvocation 
    @Override
    public boolean visit(SuperMethodInvocation node) {

        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        sourceCode.add("SuperMethodInvocation", begin, end);

//        System.out.println("SuperMethodInvocation (" + begin + ", " + end + ")");
//        System.out.println(node.toString());
        return true;
    }

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>           Switch case statement
    @Override
    public boolean visit(SwitchCase node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        sourceCode.add("CaseStatement", begin, end);

        return true;
    }

    @Override
    public boolean visit(SwitchStatement node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        sourceCode.add("SwitchStatement", begin, end);

        return true;
    }

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>           Throw statement
    @Override
    public boolean visit(ThrowStatement node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        sourceCode.add("ThrowStatement", begin, end);

        return true;
    }

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>           ThisExpression
    @Override
    public boolean visit(ThisExpression node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        sourceCode.add("ThisExpression", begin, end);

//        System.out.println("ThisExpression (" + begin + ", " + end + ")");
//        System.out.println(node.toString());
        return true;
    }

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>           TypeDeclaration (Closest from class)
    @Override
    public boolean visit(TypeDeclaration node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());

        isInterface = node.isInterface();

        if (isInterface) {
            sourceCode.add("Interface declaraton", begin, end);
        } else {
            sourceCode.add("Class declaration", begin, end);
        }

        return true;
    }

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>           Try statement
    @Override
    public boolean visit(TryStatement node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        sourceCode.add("TryStatement", begin, end);

        return true;
    }

    /*
     >>>>>>>>>>>>>>>>>>>>>>>>>>>           Variable
     */
    //VariableDeclarationExpression
    //int i=0 (inside the for)
    @Override
    public boolean visit(VariableDeclarationExpression node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        sourceCode.add("Variable", begin, end);

        return true;
    }

    //VariableDeclarationStatement
    //int j;
    //int k=0;
    //int v=returnStatement();
    @Override
    public boolean visit(VariableDeclarationStatement node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        sourceCode.add("Variable", begin, end);

        return true;
    }

    //SingleVariableDeclaration 
    //String name
    @Override
    public boolean visit(SingleVariableDeclaration node) {

        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        sourceCode.add("Variable", begin, end);

        return true;
    }

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>           While statement
    @Override
    public boolean visit(WhileStatement node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        sourceCode.add("WhileStatement", begin, end);

        return true;
    }

    /*=========================================================================
     ***************************************************************************
     |                                  Untested                                |
     ***************************************************************************
     =========================================================================*/
    //Annotation
    public boolean visit(Annotation node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());

        System.out.println("Annotation(" + begin + ", " + end + ")");
        System.out.println(node.toString());
        return true;
    }

    //
    @Override
    public boolean visit(NormalAnnotation node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        System.out.println("NormalAnnotation");
        System.out.println(node.toString());
        return true;
    }

    //Blank
    //Class declaration
    //Class signature
    //Comment
    @Override
    public boolean visit(ConstructorInvocation node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        System.out.println("ConditionalExpression");
        System.out.println(node.toString());
        return true;
    }

    @Override
    public boolean visit(EmptyStatement node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        System.out.println("EmptyStatement (" + begin + ", " + end + ")");
        System.out.println(node.toString());
        return true;
    }

    @Override
    public boolean visit(SingleMemberAnnotation node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        System.out.println("SingleMemberAnnotation (" + begin + ", " + end + ")");
        System.out.println(node.toString());
        return true;
    }

    public boolean visit(ForeachStatement node) {
        int begin = cu.getLineNumber(node.sourceStart());
        int end = cu.getLineNumber(node.sourceEnd());
        System.out.println("ForeachStatement");
        System.out.println(node.toString());
        return true;
    }

    //QualifiedName 
    @Override
    public boolean visit(QualifiedName node) {

        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
//        System.out.println("QualifiedName (" + begin + ", " + end + ")");
//        System.out.println(node.toString());

        return true;
    }

    //Method interface
    //Method signature
//    public boolean visit(MethodSignature node) {
//        System.out.println("MethodSignature");
//        System.out.println(node.toString());
//        return true;
//    }
    //Static initialization block
    //Try statement
    //>>>>>>>>>>>>>>>>>>>>>>>>>>>           TypeDeclarationStatement
    @Override
    public boolean visit(TypeDeclarationStatement node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());

        System.out.println("TypeDeclarationStatement (" + begin + ", " + end + ")");
        System.out.println(node.toString());
        return true;
    }

    //Variable 
    public boolean visit(VariableDeclaration node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());

        System.out.println("VariableDeclaration");
        System.out.println(node.toString());
        return true;
    }

    //VariableDeclarationExpression (tested)
    //No assiging value
    //Subset of VariableDeclarationExpression
//    @Override
//    public boolean visit(VariableDeclarationFragment node) {
//        int begin = cu.getLineNumber(node.getStartPosition());
//        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
//        System.out.println("VariableDeclarationFragment");
//        System.out.println(node.toString());
//        return true;
//    }
    /**
     * @return the sourceCode
     */
    public SourceCodeFile getSourceCode() {
        return sourceCode;
    }

    /**
     * @param sourceCode the sourceCode to set
     */
    public void setSourceCode(SourceCodeFile sourceCode) {
        this.sourceCode = sourceCode;
    }

    /*=========================================================================
     ***************************************************************************
     |                                  Unused                                |
     ***************************************************************************
     =========================================================================*/

    //>>>>>>>>>>>>>>>>                     Assignment
    @Override
    public boolean visit(Assignment node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
//        sourceCode.add("Assignment", begin, end);

//        System.out.println("Assignment(" + begin + ", " + end + ")");
//        System.out.println(node.toString());
        return true;
    }

        //>>>>>>>>>>>>>>>>>>>>>>>>>>>           Modifier 
    @Override
    public boolean visit(Modifier node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
//        sourceCode.add("Modifier", begin, end);

//        System.out.println("Modifier(" + begin + ", " + end + ")");
//        System.out.println(node.toString());
        //Put here the diference between the diferent kinds of initialization 
        return true;
    }
    
    //WildcardType  
    @Override
    public boolean visit(WildcardType node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());

//        System.out.println("WildcardType (" + begin + ", " + end + ")");
//        System.out.println(node.toString());
        return true;
    }

}
