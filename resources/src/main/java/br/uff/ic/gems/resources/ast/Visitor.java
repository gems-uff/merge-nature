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
import org.eclipse.jdt.core.dom.AnnotationTypeMemberDeclaration;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.ArrayCreation;
import org.eclipse.jdt.core.dom.ArrayInitializer;
import org.eclipse.jdt.core.dom.AssertStatement;
import org.eclipse.jdt.core.dom.BlockComment;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.BreakStatement;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.Comment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.ContinueStatement;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.Expression;
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
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.QualifiedType;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.SynchronizedStatement;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclarationStatement;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;

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

    public int begin(BodyDeclaration node) {
        int begin = cu.getLineNumber(node.getStartPosition());

        Javadoc javadoc = node.getJavadoc();
        int javadocBegin = -1;

        if (javadoc != null) {
            javadocBegin = cu.getLineNumber(javadoc.getStartPosition());
        }

        if (begin == javadocBegin) {
            begin = cu.getLineNumber(javadoc.getStartPosition() + javadoc.getLength() + 1);
        }

        return begin;
    }

    public int begin(PackageDeclaration node) {
        int begin = cu.getLineNumber(node.getStartPosition());

        Javadoc javadoc = node.getJavadoc();
        int javadocBegin = -1;

        if (javadoc != null) {
            javadocBegin = cu.getLineNumber(javadoc.getStartPosition());
        }

        if (begin == javadocBegin) {
            begin = cu.getLineNumber(javadoc.getStartPosition() + javadoc.getLength() + 1);
        }

        return begin;
    }

    public String getTypeByIdentifier(String identifier) {
        for (LanguageConstruct languageConstruct : languageConstructs) {
            if (languageConstruct.getIdentifier() != null) {
                if (languageConstruct.getIdentifier().equals(identifier)) {
                    return languageConstruct.getName();
                }
            }

        }
        return null;
    }
    /*=========================================================================
     ***************************************************************************
     |                       Begin visitors selected                           |
     ***************************************************************************
     =========================================================================*/

    @Override
    public boolean visit(AnnotationTypeMemberDeclaration node) {
        int begin = begin(node);
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        languageConstructs.add(new LanguageConstruct(ASTTypes.ANNOTATION_TYPE_MEMBER_DECLARATION, begin, end));

        return true;
    }

    @Override
    public boolean visit(AnonymousClassDeclaration node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());

        languageConstructs.add(new LanguageConstruct(ASTTypes.CLASS_DECLARATION, begin, end));

        return true;
    }

    @Override
    public boolean visit(ArrayAccess node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());

        languageConstructs.add(new LanguageConstruct(ASTTypes.ARRAY_ACCESS, begin, end));

        return true;
    }

    @Override
    public boolean visit(ArrayCreation node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());

        languageConstructs.add(new LanguageConstruct(ASTTypes.METHOD_INVOCATION, begin, end));

        return false;
    }

    @Override
    public boolean visit(ArrayInitializer node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());

        languageConstructs.add(new LanguageConstruct(ASTTypes.ARRAY_INITIALIZER, begin, end));

        return true;
    }

    @Override
    public boolean visit(AssertStatement node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());

        languageConstructs.add(new LanguageConstruct(ASTTypes.ASSERT_STATEMENT, begin, end));

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
    public boolean visit(BreakStatement node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        languageConstructs.add(new LanguageConstruct(ASTTypes.BREAK_STATEMENT, begin, end));

        return true;
    }

    @Override
    public boolean visit(CastExpression node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());

        languageConstructs.add(new LanguageConstruct(ASTTypes.CAST_EXPRESSION, begin, end));

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

        return false;
    }

    public boolean visit(Comment node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        languageConstructs.add(new LanguageConstruct(ASTTypes.COMMENT, begin, end));

        return true;
    }

    @Override
    public boolean visit(ConstructorInvocation node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());

        languageConstructs.add(new LanguageConstruct(ASTTypes.METHOD_INVOCATION, begin, end));

        return false;
    }

    @Override
    public boolean visit(ContinueStatement node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        languageConstructs.add(new LanguageConstruct(ASTTypes.CONTINUE_STATEMENT, begin, end));

        return true;
    }

    @Override
    public boolean visit(DoStatement node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        languageConstructs.add(new LanguageConstruct(ASTTypes.DO_STATEMENT, begin, end));

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
    public boolean visit(EnumConstantDeclaration node) {
        int begin = begin(node);
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());

        Javadoc javadoc = node.getJavadoc();

        languageConstructs.add(new LanguageConstruct(ASTTypes.ENUM_VALUE, begin, end));

        return true;
    }

    @Override
    public boolean visit(EnumDeclaration node) {
        int begin = begin(node);
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        languageConstructs.add(new LanguageConstruct(ASTTypes.ENUM_DECLARATION, begin, end));

        return true;
    }

    @Override
    public boolean visit(FieldDeclaration node) {

        int begin = begin(node);
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());

        List<VariableDeclarationFragment> fragments = node.fragments();

        for (VariableDeclarationFragment fragment : fragments) {
            begin = cu.getLineNumber(fragment.getStartPosition());
            end = cu.getLineNumber(fragment.getStartPosition() + fragment.getLength());
            languageConstructs.add(new LanguageConstruct(ASTTypes.ATTRIBUTE, begin, end, fragment.getName().getIdentifier()));

        }

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
        int begin = begin(node);
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
        int begin = begin(node);
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());

        languageConstructs.add(new LanguageConstruct(ASTTypes.METHOD_DECLARATION, begin, end));

        return true;
    }

    @Override
    public boolean visit(MethodInvocation node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        languageConstructs.add(new LanguageConstruct(ASTTypes.METHOD_INVOCATION, begin, end));

        return false;
    }

    @Override
    public boolean visit(NormalAnnotation node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());

        languageConstructs.add(new LanguageConstruct(ASTTypes.ANNOTATION, begin, end));

        return true;
    }

    @Override
    public boolean visit(PackageDeclaration node) {
        int begin = begin(node);
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
        languageConstructs.add(new LanguageConstruct(ASTTypes.VARIABLE, begin, end, node.getName().getIdentifier()));

        return true;
    }

    @Override
    public boolean visit(SuperConstructorInvocation node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        languageConstructs.add(new LanguageConstruct(ASTTypes.METHOD_INVOCATION, begin, end));

        return false;
    }

    @Override
    public boolean visit(SuperMethodInvocation node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
        languageConstructs.add(new LanguageConstruct(ASTTypes.METHOD_INVOCATION, begin, end));

        return false;
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
    public boolean visit(SynchronizedStatement node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());

        languageConstructs.add(new LanguageConstruct(ASTTypes.SYNCHRONIZED_STATEMENT, begin, end));

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
        int begin = begin(node);
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

        List<VariableDeclarationFragment> fragments = node.fragments();
        for (VariableDeclarationFragment fragment : fragments) {
            int begin = cu.getLineNumber(fragment.getStartPosition());
            int end = cu.getLineNumber(fragment.getStartPosition() + fragment.getLength());
            languageConstructs.add(new LanguageConstruct(ASTTypes.VARIABLE, begin, end, fragment.getName().getIdentifier()));
        }

        return true;
    }

    @Override
    public boolean visit(VariableDeclarationStatement node) {
        int begin, end;

        List<VariableDeclarationFragment> fragments = node.fragments();

        for (VariableDeclarationFragment fragment : fragments) {
            String identifier = fragment.getName().getIdentifier();
            begin = cu.getLineNumber(fragment.getStartPosition());
            end = cu.getLineNumber(fragment.getStartPosition() + fragment.getLength());
            languageConstructs.add(new LanguageConstruct(ASTTypes.VARIABLE, begin, end, identifier));

        }

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
     |                                  Unexecuted                                |
     ***************************************************************************
     =========================================================================*/
    //Annotation
    public boolean visit(Annotation node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());

        languageConstructs.add(new LanguageConstruct("AnnotationDifferent", begin, end));

        return true;
    }

    public boolean visit(Expression node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());

        languageConstructs.add(new LanguageConstruct("Expression", begin, end));

        return true;
    }

    public boolean visit(Name node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());

        languageConstructs.add(new LanguageConstruct("Name", begin, end));

        return true;
    }

    @Override
    public boolean visit(QualifiedType node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());

        languageConstructs.add(new LanguageConstruct("QualifiedType", begin, end));

        return true;
    }

    @Override
    public boolean visit(SimpleName node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());

        String typeByIdentifier = getTypeByIdentifier(node.getIdentifier());

        if (typeByIdentifier != null) {
            languageConstructs.add(new LanguageConstruct(typeByIdentifier, begin, end, node.getIdentifier()));
        }

        return true;
    }

    public boolean visit(Statement node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());

        languageConstructs.add(new LanguageConstruct("Statement", begin, end));

        return true;
    }

    public boolean visit(Type node) {
        int begin = cu.getLineNumber(node.getStartPosition());
        int end = cu.getLineNumber(node.getStartPosition() + node.getLength());

        languageConstructs.add(new LanguageConstruct("Type", begin, end));

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
    /*=========================================================================
     ***************************************************************************
     |                                  Untested                                |
     ***************************************************************************
     =========================================================================*/

}
