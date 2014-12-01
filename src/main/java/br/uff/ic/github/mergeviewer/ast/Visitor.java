/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.github.mergeviewer.ast;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.AnnotationTypeMemberDeclaration;
import org.eclipse.jdt.core.dom.BlockComment;
import org.eclipse.jdt.core.dom.Comment;
import org.eclipse.jdt.core.dom.CompilationUnit;
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
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.ThrowStatement;
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

    private final CompilationUnit cu;

    public Visitor(CompilationUnit cuArg) {
        this.cu = cuArg;

    }

    private static void print(IExtendedModifier node) {
    }

    /*-----------------------------------------------------------------------
     |                                  Tested                                |
     -------------------------------------------------------------------------*/
    
    //Block comment
    @Override
    public boolean visit(BlockComment node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());

        System.out.println("BlockComment (" + begin + ", " + end + ")");
        System.out.println(node.toString());
        return true;
    }

    //Field - Attribute
    //String name;
    @Override
    public boolean visit(FieldDeclaration node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        System.out.println("FieldDeclaration (" + begin + ", " + end + ")");
        System.out.println(node.toString());
        return true;
    }
    
    //ForStatement
    @Override
    public boolean visit(ForStatement node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        System.out.println("ForStatement(" + begin + ", " + end + ")");
        System.out.println(node.toString());
        return true;
    }

    @Override
    public boolean visit(EnhancedForStatement node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        System.out.println("EnhancedForStatement(" + begin + ", " + end + ")");
        System.out.println(node.toString());
        return true;
    }
    
    //If statement
    @Override
    public boolean visit(IfStatement node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        System.out.println("IfStatement(" + begin + ", " + end + ")");
        System.out.println(node.toString());
        return true;
    }

    //Import
    @Override
    public boolean visit(ImportDeclaration node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        System.out.println("ImportDeclaration(" + begin + ", " + end + ")");
        System.out.println(node.toString());
        return true;
    }

    //Javadoc
    @Override
    public boolean visit(Javadoc node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());

        System.out.println("Javadoc (" + begin + ", " + end + ")");
        System.out.println(node.toString());
        return true;
    }

    //Method declaration
    @Override
    public boolean visit(MethodDeclaration node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        System.out.println("MethodDeclaration(" + begin + ", " + end + ")");
        System.out.println(node.toString());

        return true;
    }

    //Method invocation
    @Override
    public boolean visit(MethodInvocation node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        System.out.println("MethodInvocation(" + begin + ", " + end + ")");
        System.out.println(node.toString());

        return true;
    }

    //Return statement
    @Override
    public boolean visit(ReturnStatement node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        System.out.println("ReturnStatement(" + begin + ", " + end + ")");
        System.out.println(node.toString());
        return true;
    }

    //Throw statement
    @Override
    public boolean visit(ThrowStatement node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        System.out.println("ThrowStatement(" + begin + ", " + end + ")");
        System.out.println(node.toString());
        return true;
    }
    
    //While statement
    @Override
    public boolean visit(WhileStatement node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        System.out.println("WhileStatement(" + begin + ", " + end + ")");
        System.out.println(node.toString());
        return true;
    }

    //VariableDeclarationExpression
    //int i=0 (inside the for)
    @Override
    public boolean visit(VariableDeclarationExpression node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        System.out.println("VariableDeclarationExpression (" + begin + ", " + end + ")");
        System.out.println(node.toString());
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
        System.out.println("VariableDeclarationStatement (" + begin + ", " + end + ")");
        System.out.println(node.toString());
        return true;
    }

    /*--------------------------------------------------------------------------
     |                                  Untested                                |
     --------------------------------------------------------------------------*/
    //Annotation
    public boolean visit(Annotation node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());

        System.out.println("Annotation(" + begin + ", " + end + ")");
        System.out.println(node.toString());
        return true;
    }

    @Override
    public boolean visit(AnnotationTypeDeclaration node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        System.out.println("AnnotationTypeDeclaration");
        System.out.println(node.toString());
        return true;
    }

    @Override
    public boolean visit(AnnotationTypeMemberDeclaration node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        System.out.println("AnnotationTypeMemberDeclaration");
        System.out.println(node.toString());
        return true;
    }

    //Annotation element
    

    @Override
    public boolean visit(FieldAccess node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        System.out.println("FieldAccess");
        System.out.println(node.toString());
        return true;
    }

    //Blank
    //Class declaration
    //Class signature
    //Comment
    public boolean visit(Comment node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        System.out.println("Comment");
        System.out.println(node.toString());
        return true;
    }

    //Enum
    @Override
    public boolean visit(EnumConstantDeclaration node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        System.out.println("EnumConstantDeclaration");
        System.out.println(node.toString());
        return true;
    }

    @Override
    public boolean visit(EnumDeclaration node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        System.out.println("EnumDeclaration");
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

    

    //Instance initializer
    @Override
    public boolean visit(Initializer node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        System.out.println("Initializer(" + begin + ", " + end + ")");
        System.out.println(node.toString());

        //Put here the diference between the diferent kinds of initialization 
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
    //Switch case statement
    @Override
    public boolean visit(SwitchCase node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        System.out.println("SwitchCase");
        System.out.println(node.toString());
        return true;
    }

    @Override
    public boolean visit(SwitchStatement node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        System.out.println("SwitchStatement");
        System.out.println(node.toString());
        return true;
    }

    //Try statement 
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
}
