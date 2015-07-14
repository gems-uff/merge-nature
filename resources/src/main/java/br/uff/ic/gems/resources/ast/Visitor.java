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
import org.eclipse.jdt.core.dom.Block;
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
import org.eclipse.jdt.core.dom.Modifier;
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

    private final int INVALID_LINE = -1;
    private final int INVALID_COLUMN = -2;
    private final int INVALID_JAVADOC = -3;

    private final String CLASS = "Class";
    private final String INTERFACE = "Interface";

    public Visitor(CompilationUnit cuArg) {
        languageConstructs = new ArrayList<>();
        this.cu = cuArg;
    }

    /**
     * @return the languageConstructs
     */
    public List<LanguageConstruct> getLanguageConstructs() {
        return languageConstructs;
    }

    /**
     * @param languageConstructs the languageConstructs to set
     */
    public void setLanguageConstructs(List<LanguageConstruct> languageConstructs) {
        this.languageConstructs = languageConstructs;
    }

    public int beginLine(BodyDeclaration node) {
        int beginLine = INVALID_LINE;

        List modifiers = node.modifiers();
        for (Object modifier : modifiers) {
            if (modifier instanceof Modifier) {
                Modifier m = (Modifier) modifier;
                beginLine = cu.getLineNumber(m.getStartPosition());
            }
        }

        if (beginLine == INVALID_LINE) {

            beginLine = cu.getLineNumber(node.getStartPosition());

            Javadoc javadoc = node.getJavadoc();
            int javadocBegin = INVALID_JAVADOC;

            if (javadoc != null) {
                javadocBegin = cu.getLineNumber(javadoc.getStartPosition());
            }

            if (beginLine == javadocBegin) {
                beginLine = cu.getLineNumber(javadoc.getStartPosition() + javadoc.getLength() + 1);
            }
        }
        return beginLine;
    }

    public int beginLine(PackageDeclaration node) {
        int beginLine = cu.getLineNumber(node.getStartPosition());

        Javadoc javadoc = node.getJavadoc();
        int javadocBegin = INVALID_LINE;

        if (javadoc != null) {
            javadocBegin = cu.getLineNumber(javadoc.getStartPosition());
        }

        if (beginLine == javadocBegin) {
            beginLine = cu.getLineNumber(javadoc.getStartPosition() + javadoc.getLength() + 1);
        }

        return beginLine;
    }

    public int beginColunm(BodyDeclaration node) {

        int begincolumn = INVALID_COLUMN;

        List modifiers = node.modifiers();
        for (Object modifier : modifiers) {
            if (modifier instanceof Modifier) {
                Modifier m = (Modifier) modifier;
                begincolumn = cu.getColumnNumber(m.getStartPosition());
            }
        }

        if (begincolumn == INVALID_COLUMN) {

            begincolumn = cu.getColumnNumber(node.getStartPosition());

            Javadoc javadoc = node.getJavadoc();
            int javadocBegin = INVALID_JAVADOC;

            if (javadoc != null) {
                javadocBegin = cu.getColumnNumber(javadoc.getStartPosition());
            }

            if (begincolumn == javadocBegin) {
                begincolumn = cu.getColumnNumber(javadoc.getStartPosition() + javadoc.getLength() + 1);
            }
        }

        return begincolumn;
    }

    public int beginColunm(PackageDeclaration node) {
        int beginColumn = cu.getColumnNumber(node.getStartPosition());

        Javadoc javadoc = node.getJavadoc();
        int javadocBegin = INVALID_JAVADOC;

        if (javadoc != null) {
            javadocBegin = cu.getColumnNumber(javadoc.getStartPosition());
        }

        if (beginColumn == javadocBegin) {
            beginColumn = cu.getColumnNumber(javadoc.getStartPosition() + javadoc.getLength() + 1);
        }

        return beginColumn;
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
        int beginLine = beginLine(node);
        int endLine = cu.getLineNumber(node.getStartPosition() + node.getLength());
        int beginColumn = beginColunm(node);
        int endColumn = cu.getColumnNumber(node.getStartPosition() + node.getLength());

        languageConstructs.add(new LanguageConstruct(node.getClass().getSimpleName(), beginLine, endLine, beginColumn, endColumn));

        return true;
    }

    @Override
    public boolean visit(AnonymousClassDeclaration node) {
        int beginLine = cu.getLineNumber(node.getStartPosition());
        int endLine = cu.getLineNumber(node.getStartPosition() + node.getLength());
        int beginColumn = cu.getColumnNumber(node.getStartPosition());
        int endColumn = cu.getColumnNumber(node.getStartPosition() + node.getLength());

        languageConstructs.add(new LanguageConstruct(node.getClass().getSimpleName(), beginLine, endLine, beginColumn, endColumn));

        return true;
    }

    @Override
    public boolean visit(ArrayAccess node) {
        int beginLine = cu.getLineNumber(node.getStartPosition());
        int endLine = cu.getLineNumber(node.getStartPosition() + node.getLength());
        int beginColumn = cu.getColumnNumber(node.getStartPosition());
        int endColumn = cu.getColumnNumber(node.getStartPosition() + node.getLength());

        languageConstructs.add(new LanguageConstruct(node.getClass().getSimpleName(), beginLine, endLine, beginColumn, endColumn));

        return true;
    }

    @Override
    public boolean visit(ArrayCreation node) {
        int beginLine = cu.getLineNumber(node.getStartPosition());
        int endLine = cu.getLineNumber(node.getStartPosition() + node.getLength());
        int beginColumn = cu.getColumnNumber(node.getStartPosition());
        int endColumn = cu.getColumnNumber(node.getStartPosition() + node.getLength());

        languageConstructs.add(new LanguageConstruct(node.getClass().getSimpleName(), beginLine, endLine, beginColumn, endColumn));

        return false;
    }

    @Override
    public boolean visit(ArrayInitializer node) {
        int beginLine = cu.getLineNumber(node.getStartPosition());
        int endLine = cu.getLineNumber(node.getStartPosition() + node.getLength());
        int beginColumn = cu.getColumnNumber(node.getStartPosition());
        int endColumn = cu.getColumnNumber(node.getStartPosition() + node.getLength());

        languageConstructs.add(new LanguageConstruct(node.getClass().getSimpleName(), beginLine, endLine, beginColumn, endColumn));

        return true;
    }

    @Override
    public boolean visit(AssertStatement node) {
        int beginLine = cu.getLineNumber(node.getStartPosition());
        int endLine = cu.getLineNumber(node.getStartPosition() + node.getLength());
        int beginColumn = cu.getColumnNumber(node.getStartPosition());
        int endColumn = cu.getColumnNumber(node.getStartPosition() + node.getLength());

        languageConstructs.add(new LanguageConstruct(node.getClass().getSimpleName(), beginLine, endLine, beginColumn, endColumn));

        return true;
    }

    @Override
    public boolean visit(BlockComment node) {
        int beginLine = cu.getLineNumber(node.getStartPosition());
        int endLine = cu.getLineNumber(node.getStartPosition() + node.getLength());
        int beginColumn = cu.getColumnNumber(node.getStartPosition());
        int endColumn = cu.getColumnNumber(node.getStartPosition() + node.getLength());

        languageConstructs.add(new LanguageConstruct(node.getClass().getSimpleName(), beginLine, endLine, beginColumn, endColumn));

        return true;
    }

    @Override
    public boolean visit(BreakStatement node) {
        int beginLine = cu.getLineNumber(node.getStartPosition());
        int endLine = cu.getLineNumber(node.getStartPosition() + node.getLength());
        int beginColumn = cu.getColumnNumber(node.getStartPosition());
        int endColumn = cu.getColumnNumber(node.getStartPosition() + node.getLength());

        languageConstructs.add(new LanguageConstruct(node.getClass().getSimpleName(), beginLine, endLine, beginColumn, endColumn));

        return true;
    }

    @Override
    public boolean visit(CastExpression node) {
        int beginLine = cu.getLineNumber(node.getStartPosition());
        int endLine = cu.getLineNumber(node.getStartPosition() + node.getLength());
        int beginColumn = cu.getColumnNumber(node.getStartPosition());
        int endColumn = cu.getColumnNumber(node.getStartPosition() + node.getLength());

        languageConstructs.add(new LanguageConstruct(node.getClass().getSimpleName(), beginLine, endLine, beginColumn, endColumn));

        return true;
    }

    @Override
    public boolean visit(CatchClause node) {
        int beginLine = cu.getLineNumber(node.getStartPosition());
        int endLine = cu.getLineNumber(node.getStartPosition() + node.getLength());
        int beginColumn = cu.getColumnNumber(node.getStartPosition());
        int endColumn = cu.getColumnNumber(node.getStartPosition() + node.getLength());

        Block body = node.getBody();

        if (body != null) {

            int beginLineBody = cu.getLineNumber(body.getStartPosition());
            int endLineBody = cu.getLineNumber(body.getStartPosition() + body.getLength());
            int beginColumnBody = cu.getColumnNumber(body.getStartPosition());
            int endColumnBody = cu.getColumnNumber(body.getStartPosition() + body.getLength());

            languageConstructs.add(new LanguageConstruct(node.getClass().getSimpleName(), beginLine, endLine, beginColumn, endColumn,
                    beginLineBody, endLineBody, beginColumnBody, endColumnBody, null));
        } else {
            languageConstructs.add(new LanguageConstruct(node.getClass().getSimpleName(), beginLine, endLine, beginColumn, endColumn));
        }
        return true;
    }

    @Override
    public boolean visit(ClassInstanceCreation node) {
        int beginLine = cu.getLineNumber(node.getStartPosition());
        int endLine = cu.getLineNumber(node.getStartPosition() + node.getLength());
        int beginColumn = cu.getColumnNumber(node.getStartPosition());
        int endColumn = cu.getColumnNumber(node.getStartPosition() + node.getLength());

        languageConstructs.add(new LanguageConstruct(node.getClass().getSimpleName(), beginLine, endLine, beginColumn, endColumn));

        return false;
    }

    public boolean visit(Comment node) {
        int beginLine = cu.getLineNumber(node.getStartPosition());
        int endLine = cu.getLineNumber(node.getStartPosition() + node.getLength());
        int beginColumn = cu.getColumnNumber(node.getStartPosition());
        int endColumn = cu.getColumnNumber(node.getStartPosition() + node.getLength());

        languageConstructs.add(new LanguageConstruct(node.getClass().getSimpleName(), beginLine, endLine, beginColumn, endColumn));

        return true;
    }

    @Override
    public boolean visit(ConstructorInvocation node) {
        int beginLine = cu.getLineNumber(node.getStartPosition());
        int endLine = cu.getLineNumber(node.getStartPosition() + node.getLength());
        int beginColumn = cu.getColumnNumber(node.getStartPosition());
        int endColumn = cu.getColumnNumber(node.getStartPosition() + node.getLength());

        languageConstructs.add(new LanguageConstruct(node.getClass().getSimpleName(), beginLine, endLine, beginColumn, endColumn));

        return false;
    }

    @Override
    public boolean visit(ContinueStatement node) {
        int beginLine = cu.getLineNumber(node.getStartPosition());
        int endLine = cu.getLineNumber(node.getStartPosition() + node.getLength());
        int beginColumn = cu.getColumnNumber(node.getStartPosition());
        int endColumn = cu.getColumnNumber(node.getStartPosition() + node.getLength());

        languageConstructs.add(new LanguageConstruct(node.getClass().getSimpleName(), beginLine, endLine, beginColumn, endColumn));

        return true;
    }

    @Override
    public boolean visit(DoStatement node) {
        int beginLine = cu.getLineNumber(node.getStartPosition());
        int endLine = cu.getLineNumber(node.getStartPosition() + node.getLength());
        int beginColumn = cu.getColumnNumber(node.getStartPosition());
        int endColumn = cu.getColumnNumber(node.getStartPosition() + node.getLength());

        Statement body = node.getBody();

        if (body != null) {

            int beginLineBody = cu.getLineNumber(body.getStartPosition());
            int endLineBody = cu.getLineNumber(body.getStartPosition() + body.getLength());
            int beginColumnBody = cu.getColumnNumber(body.getStartPosition());
            int endColumnBody = cu.getColumnNumber(body.getStartPosition() + body.getLength());

            languageConstructs.add(new LanguageConstruct(node.getClass().getSimpleName(), beginLine, endLine, beginColumn, endColumn,
                    beginLineBody, endLineBody, beginColumnBody, endColumnBody, null));
        } else {
            languageConstructs.add(new LanguageConstruct(node.getClass().getSimpleName(), beginLine, endLine, beginColumn, endColumn));
        }
        return true;
    }

    @Override
    public boolean visit(EnhancedForStatement node) {
        int beginLine = cu.getLineNumber(node.getStartPosition());
        int endLine = cu.getLineNumber(node.getStartPosition() + node.getLength());
        int beginColumn = cu.getColumnNumber(node.getStartPosition());
        int endColumn = cu.getColumnNumber(node.getStartPosition() + node.getLength());

        Statement body = node.getBody();
        if (body != null) {

            int beginLineBody = cu.getLineNumber(body.getStartPosition());
            int endLineBody = cu.getLineNumber(body.getStartPosition() + body.getLength());
            int beginColumnBody = cu.getColumnNumber(body.getStartPosition());
            int endColumnBody = cu.getColumnNumber(body.getStartPosition() + body.getLength());

            languageConstructs.add(new LanguageConstruct(node.getClass().getSimpleName(), beginLine, endLine, beginColumn, endColumn,
                    beginLineBody, endLineBody, beginColumnBody, endColumnBody, null));
        } else {
            languageConstructs.add(new LanguageConstruct(node.getClass().getSimpleName(), beginLine, endLine, beginColumn, endColumn));
        }

        return true;
    }

    @Override
    public boolean visit(EnumConstantDeclaration node) {
        int beginLine = beginLine(node);
        int endLine = cu.getLineNumber(node.getStartPosition() + node.getLength());
        int beginColumn = beginColunm(node);
        int endColumn = cu.getColumnNumber(node.getStartPosition() + node.getLength());

        languageConstructs.add(new LanguageConstruct(node.getClass().getSimpleName(), beginLine, endLine, beginColumn, endColumn));

        return true;
    }

    @Override
    public boolean visit(EnumDeclaration node) {
        int beginLine = beginLine(node);
        int endLine = cu.getLineNumber(node.getStartPosition() + node.getLength());
        int beginColumn = beginColunm(node);
        int endColumn = cu.getColumnNumber(node.getStartPosition() + node.getLength());

        languageConstructs.add(new LanguageConstruct(node.getClass().getSimpleName(), beginLine, endLine, beginColumn, endColumn));

        return true;
    }

    @Override
    public boolean visit(FieldDeclaration node) {

        List<VariableDeclarationFragment> fragments = node.fragments();

        for (VariableDeclarationFragment fragment : fragments) {
            int beginLine = cu.getLineNumber(fragment.getStartPosition());
            int endLine = cu.getLineNumber(fragment.getStartPosition() + fragment.getLength());
            int beginColumn = beginColunm(node);
            int endColumn = cu.getColumnNumber(node.getStartPosition() + node.getLength());

            SimpleName name = fragment.getName();

            if (name != null) {
                endLine = cu.getLineNumber(name.getStartPosition() + name.getLength());
                endColumn = cu.getColumnNumber(name.getStartPosition() + name.getLength());
            }

            languageConstructs.add(new LanguageConstruct(node.getClass().getSimpleName(), beginLine, endLine, beginColumn, endColumn, fragment.getName().getIdentifier()));

        }

        return true;
    }

    @Override
    public boolean visit(ForStatement node) {
        int beginLine = cu.getLineNumber(node.getStartPosition());
        int endLine = cu.getLineNumber(node.getStartPosition() + node.getLength());
        int beginColumn = cu.getColumnNumber(node.getStartPosition());
        int endColumn = cu.getColumnNumber(node.getStartPosition() + node.getLength());

        Statement body = node.getBody();
        if (body != null) {

            int beginLineBody = cu.getLineNumber(body.getStartPosition());
            int endLineBody = cu.getLineNumber(body.getStartPosition() + body.getLength());
            int beginColumnBody = cu.getColumnNumber(body.getStartPosition());
            int endColumnBody = cu.getColumnNumber(body.getStartPosition() + body.getLength());

            languageConstructs.add(new LanguageConstruct(node.getClass().getSimpleName(), beginLine, endLine, beginColumn, endColumn,
                    beginLineBody, endLineBody, beginColumnBody, endColumnBody, null));
        } else {
            languageConstructs.add(new LanguageConstruct(node.getClass().getSimpleName(), beginLine, endLine, beginColumn, endColumn));
        }
        return true;
    }

    @Override
    public boolean visit(IfStatement node) {
        int beginLine = cu.getLineNumber(node.getStartPosition());
        int endLine = cu.getLineNumber(node.getStartPosition() + node.getLength());
        int beginColumn = cu.getColumnNumber(node.getStartPosition());
        int endColumn = cu.getColumnNumber(node.getStartPosition() + node.getLength());

        int beginLineBlock = 0;
        int beginColumnBlock = 0;

        Expression expression = node.getExpression();

        if (expression != null) {
            beginLineBlock = cu.getLineNumber(expression.getStartPosition() + expression.getLength());
            beginColumnBlock = cu.getColumnNumber(expression.getStartPosition() + expression.getLength());
            languageConstructs.add(new LanguageConstruct(node.getClass().getSimpleName(), beginLine, endLine, beginColumn, endColumn,
                    beginLineBlock, endLine, beginColumnBlock, endColumn, null));
        } else {
            languageConstructs.add(new LanguageConstruct(node.getClass().getSimpleName(), beginLine, endLine, beginColumn, endColumn));
        }

        return true;
    }

    @Override
    public boolean visit(ImportDeclaration node) {
        int beginLine = cu.getLineNumber(node.getStartPosition());
        int endLine = cu.getLineNumber(node.getStartPosition() + node.getLength());
        int beginColumn = cu.getColumnNumber(node.getStartPosition());
        int endColumn = cu.getColumnNumber(node.getStartPosition() + node.getLength());

        languageConstructs.add(new LanguageConstruct(node.getClass().getSimpleName(), beginLine, endLine, beginColumn, endColumn));

        return true;
    }

    @Override
    public boolean visit(Initializer node) {

        int beginLine = beginLine(node);
        int endLine = cu.getLineNumber(node.getStartPosition() + node.getLength());
        int beginColumn = beginColunm(node);
        int endColumn = cu.getColumnNumber(node.getStartPosition() + node.getLength());

        Block body = node.getBody();

        if (body != null) {

            int beginLineBody = cu.getLineNumber(body.getStartPosition());
            int endLineBody = cu.getLineNumber(body.getStartPosition() + body.getLength());
            int beginColumnBody = cu.getColumnNumber(body.getStartPosition());
            int endColumnBody = cu.getColumnNumber(body.getStartPosition() + body.getLength());

            languageConstructs.add(new LanguageConstruct(node.getClass().getSimpleName(), beginLine, endLine, beginColumn, endColumn,
                    beginLineBody, endLineBody, beginColumnBody, endColumnBody, null));
        } else {
            languageConstructs.add(new LanguageConstruct(node.getClass().getSimpleName(), beginLine, endLine, beginColumn, endColumn));
        }

        return true;
    }

    @Override
    public boolean visit(Javadoc node) {
        int beginLine = cu.getLineNumber(node.getStartPosition());
        int endLine = cu.getLineNumber(node.getStartPosition() + node.getLength());
        int beginColumn = cu.getColumnNumber(node.getStartPosition());
        int endColumn = cu.getColumnNumber(node.getStartPosition() + node.getLength());

        languageConstructs.add(new LanguageConstruct(node.getClass().getSimpleName(), beginLine, endLine, beginColumn, endColumn));

        return true;
    }

    @Override
    public boolean visit(LineComment node) {
        int beginLine = cu.getLineNumber(node.getStartPosition());
        int endLine = cu.getLineNumber(node.getStartPosition() + node.getLength());
        int beginColumn = cu.getColumnNumber(node.getStartPosition());
        int endColumn = cu.getColumnNumber(node.getStartPosition() + node.getLength());

        languageConstructs.add(new LanguageConstruct(node.getClass().getSimpleName(), beginLine, endLine, beginColumn, endColumn));

        return true;
    }

    @Override
    public boolean visit(MarkerAnnotation node) {
        int beginLine = cu.getLineNumber(node.getStartPosition());
        int endLine = cu.getLineNumber(node.getStartPosition() + node.getLength());
        int beginColumn = cu.getColumnNumber(node.getStartPosition());
        int endColumn = cu.getColumnNumber(node.getStartPosition() + node.getLength());

        languageConstructs.add(new LanguageConstruct(node.getClass().getSimpleName(), beginLine, endLine, beginColumn, endColumn));

        return true;
    }

    @Override
    public boolean visit(MethodDeclaration node) {

        int beginLine = beginLine(node);
        int endLine = cu.getLineNumber(node.getStartPosition() + node.getLength());
        int beginColumn = beginColunm(node);
        int endColumn = cu.getColumnNumber(node.getStartPosition() + node.getLength());

        Block body = node.getBody();

        if (body != null) {

            int beginLineBody = cu.getLineNumber(body.getStartPosition());
            int endLineBody = cu.getLineNumber(body.getStartPosition() + body.getLength());
            int beginColumnBody = cu.getColumnNumber(body.getStartPosition());
            int endColumnBody = cu.getColumnNumber(body.getStartPosition() + body.getLength());

            languageConstructs.add(new LanguageConstruct(node.getClass().getSimpleName(), beginLine, endLine, beginColumn, endColumn,
                    beginLineBody, endLineBody, beginColumnBody, endColumnBody, null));
        } else {
            languageConstructs.add(new LanguageConstruct(node.getClass().getSimpleName(), beginLine, endLine, beginColumn, endColumn));
        }

        return true;
    }

    @Override
    public boolean visit(MethodInvocation node) {
        int beginLine = cu.getLineNumber(node.getStartPosition());
        int endLine = cu.getLineNumber(node.getStartPosition() + node.getLength());
        int beginColumn = cu.getColumnNumber(node.getStartPosition());
        int endColumn = cu.getColumnNumber(node.getStartPosition() + node.getLength());

        languageConstructs.add(new LanguageConstruct(node.getClass().getSimpleName(), beginLine, endLine, beginColumn, endColumn));

        return false;
    }

    @Override
    public boolean visit(NormalAnnotation node) {
        int beginLine = cu.getLineNumber(node.getStartPosition());
        int endLine = cu.getLineNumber(node.getStartPosition() + node.getLength());
        int beginColumn = cu.getColumnNumber(node.getStartPosition());
        int endColumn = cu.getColumnNumber(node.getStartPosition() + node.getLength());

        languageConstructs.add(new LanguageConstruct(node.getClass().getSimpleName(), beginLine, endLine, beginColumn, endColumn));

        return true;
    }

    @Override
    public boolean visit(PackageDeclaration node) {
        int beginLine = beginLine(node);
        int endLine = cu.getLineNumber(node.getStartPosition() + node.getLength());
        int beginColumn = beginColunm(node);
        int endColumn = cu.getColumnNumber(node.getStartPosition() + node.getLength());

        languageConstructs.add(new LanguageConstruct(node.getClass().getSimpleName(), beginLine, endLine, beginColumn, endColumn));

        return true;
    }

    @Override
    public boolean visit(ReturnStatement node) {
        int beginLine = cu.getLineNumber(node.getStartPosition());
        int endLine = cu.getLineNumber(node.getStartPosition() + node.getLength());
        int beginColumn = cu.getColumnNumber(node.getStartPosition());
        int endColumn = cu.getColumnNumber(node.getStartPosition() + node.getLength());

        languageConstructs.add(new LanguageConstruct(node.getClass().getSimpleName(), beginLine, endLine, beginColumn, beginColumn + "return".length() - 1));

        return true;
    }

    @Override
    public boolean visit(SingleMemberAnnotation node) {
        int beginLine = cu.getLineNumber(node.getStartPosition());
        int endLine = cu.getLineNumber(node.getStartPosition() + node.getLength());
        int beginColumn = cu.getColumnNumber(node.getStartPosition());
        int endColumn = cu.getColumnNumber(node.getStartPosition() + node.getLength());

        languageConstructs.add(new LanguageConstruct(node.getClass().getSimpleName(), beginLine, endLine, beginColumn, endColumn));

        return true;
    }

    @Override
    public boolean visit(SingleVariableDeclaration node) {
        int beginLine = cu.getLineNumber(node.getStartPosition());
        int endLine = cu.getLineNumber(node.getStartPosition() + node.getLength());
        int beginColumn = cu.getColumnNumber(node.getStartPosition());
        int endColumn = cu.getColumnNumber(node.getStartPosition() + node.getLength());

        SimpleName name = node.getName();

        if (name != null) {
            endLine = cu.getLineNumber(name.getStartPosition() + name.getLength());
            endColumn = cu.getColumnNumber(name.getStartPosition() + name.getLength());
        }

        languageConstructs.add(new LanguageConstruct(node.getClass().getSimpleName(), beginLine, endLine, beginColumn, endColumn, node.getName().getIdentifier()));

        return true;
    }

    @Override
    public boolean visit(SuperConstructorInvocation node) {
        int beginLine = cu.getLineNumber(node.getStartPosition());
        int endLine = cu.getLineNumber(node.getStartPosition() + node.getLength());
        int beginColumn = cu.getColumnNumber(node.getStartPosition());
        int endColumn = cu.getColumnNumber(node.getStartPosition() + node.getLength());

        languageConstructs.add(new LanguageConstruct(node.getClass().getSimpleName(), beginLine, endLine, beginColumn, endColumn));

        return false;
    }

    @Override
    public boolean visit(SuperMethodInvocation node) {
        int beginLine = cu.getLineNumber(node.getStartPosition());
        int endLine = cu.getLineNumber(node.getStartPosition() + node.getLength());
        int beginColumn = cu.getColumnNumber(node.getStartPosition());
        int endColumn = cu.getColumnNumber(node.getStartPosition() + node.getLength());

        languageConstructs.add(new LanguageConstruct(node.getClass().getSimpleName(), beginLine, endLine, beginColumn, endColumn));

        return false;
    }

    @Override
    public boolean visit(SwitchCase node) {
        int beginLine = cu.getLineNumber(node.getStartPosition());
        int endLine = cu.getLineNumber(node.getStartPosition() + node.getLength());
        int beginColumn = cu.getColumnNumber(node.getStartPosition());
        int endColumn = cu.getColumnNumber(node.getStartPosition() + node.getLength());

        languageConstructs.add(new LanguageConstruct(node.getClass().getSimpleName(), beginLine, endLine, beginColumn, endColumn));

        return true;
    }

    @Override
    public boolean visit(SwitchStatement node) {
        int beginLine = cu.getLineNumber(node.getStartPosition());
        int endLine = cu.getLineNumber(node.getStartPosition() + node.getLength());
        int beginColumn = cu.getColumnNumber(node.getStartPosition());
        int endColumn = cu.getColumnNumber(node.getStartPosition() + node.getLength());

        languageConstructs.add(new LanguageConstruct(node.getClass().getSimpleName(), beginLine, endLine, beginColumn, endColumn));

        return true;
    }

    @Override
    public boolean visit(SynchronizedStatement node) {
        int beginLine = cu.getLineNumber(node.getStartPosition());
        int endLine = cu.getLineNumber(node.getStartPosition() + node.getLength());
        int beginColumn = cu.getColumnNumber(node.getStartPosition());
        int endColumn = cu.getColumnNumber(node.getStartPosition() + node.getLength());

        Block body = node.getBody();

        if (body != null) {

            int beginLineBody = cu.getLineNumber(body.getStartPosition());
            int endLineBody = cu.getLineNumber(body.getStartPosition() + body.getLength());
            int beginColumnBody = cu.getColumnNumber(body.getStartPosition());
            int endColumnBody = cu.getColumnNumber(body.getStartPosition() + body.getLength());

            languageConstructs.add(new LanguageConstruct(node.getClass().getSimpleName(), beginLine, endLine, beginColumn, endColumn,
                    beginLineBody, endLineBody, beginColumnBody, endColumnBody, null));
        } else {
            languageConstructs.add(new LanguageConstruct(node.getClass().getSimpleName(), beginLine, endLine, beginColumn, endColumn));
        }
        return true;
    }

    @Override
    public boolean visit(ThrowStatement node) {
        int beginLine = cu.getLineNumber(node.getStartPosition());
        int endLine = cu.getLineNumber(node.getStartPosition() + node.getLength());
        int beginColumn = cu.getColumnNumber(node.getStartPosition());
        int endColumn = cu.getColumnNumber(node.getStartPosition() + node.getLength());

        languageConstructs.add(new LanguageConstruct(node.getClass().getSimpleName(), beginLine, endLine, beginColumn, endColumn));

        return true;
    }

    @Override
    public boolean visit(TryStatement node) {
        int beginLine = cu.getLineNumber(node.getStartPosition());
        int endLine = cu.getLineNumber(node.getStartPosition() + node.getLength());
        int beginColumn = cu.getColumnNumber(node.getStartPosition());
        int endColumn = cu.getColumnNumber(node.getStartPosition() + node.getLength());

        Block body = node.getBody();

        if (body != null) {

            int beginLineBody = cu.getLineNumber(body.getStartPosition());
            int endLineBody = cu.getLineNumber(body.getStartPosition() + body.getLength());
            int beginColumnBody = cu.getColumnNumber(body.getStartPosition());
            int endColumnBody = cu.getColumnNumber(body.getStartPosition() + body.getLength());

            languageConstructs.add(new LanguageConstruct(node.getClass().getSimpleName(), beginLine, endLine, beginColumn, endColumn,
                    beginLineBody, endLineBody, beginColumnBody, endColumnBody, null));
        } else {
            languageConstructs.add(new LanguageConstruct(node.getClass().getSimpleName(), beginLine, endLine, beginColumn, endColumn));
        }

        return true;
    }

    @Override
    public boolean visit(TypeDeclaration node) {
        int beginLine = beginLine(node);
        int endLine = cu.getLineNumber(node.getStartPosition() + node.getLength());
        int beginColumn = beginColunm(node);
        int endColumn = cu.getColumnNumber(node.getStartPosition() + node.getLength());

        isInterface = node.isInterface();

        if (isInterface) {
            languageConstructs.add(new LanguageConstruct(node.getClass().getSimpleName() + INTERFACE, beginLine, endLine, beginColumn, endColumn));
        } else {
            languageConstructs.add(new LanguageConstruct(node.getClass().getSimpleName() + CLASS, beginLine, endLine, beginColumn, endColumn));
        }

        return true;
    }

    @Override
    public boolean visit(VariableDeclarationExpression node) {

        List<VariableDeclarationFragment> fragments = node.fragments();
        for (VariableDeclarationFragment fragment : fragments) {
            int beginLine = cu.getLineNumber(fragment.getStartPosition());
            int endLine = cu.getLineNumber(fragment.getStartPosition() + fragment.getLength());
            int beginColumn = cu.getColumnNumber(node.getStartPosition());
            int endColumn = cu.getColumnNumber(node.getStartPosition() + node.getLength());

            SimpleName name = fragment.getName();

            if (name != null) {
                endLine = cu.getLineNumber(name.getStartPosition() + name.getLength());
                endColumn = cu.getColumnNumber(name.getStartPosition() + name.getLength());
            }

            languageConstructs.add(new LanguageConstruct(node.getClass().getSimpleName(), beginLine, endLine, beginColumn, endColumn, fragment.getName().getIdentifier()));
        }

        return true;
    }

    @Override
    public boolean visit(VariableDeclarationStatement node) {

        List<VariableDeclarationFragment> fragments = node.fragments();

        for (VariableDeclarationFragment fragment : fragments) {
            String identifier = fragment.getName().getIdentifier();
            int beginLine = cu.getLineNumber(fragment.getStartPosition());
            int endLine = cu.getLineNumber(fragment.getStartPosition() + fragment.getLength());
            int beginColumn = cu.getColumnNumber(node.getStartPosition());
            int endColumn = cu.getColumnNumber(node.getStartPosition() + node.getLength());

            SimpleName name = fragment.getName();

            if (name != null) {
                endLine = cu.getLineNumber(name.getStartPosition() + name.getLength());
                endColumn = cu.getColumnNumber(name.getStartPosition() + name.getLength());
            }

            languageConstructs.add(new LanguageConstruct(node.getClass().getSimpleName(), beginLine, endLine, beginColumn, endColumn, identifier));

        }

        return true;
    }

    @Override
    public boolean visit(WhileStatement node) {
        int beginLine = cu.getLineNumber(node.getStartPosition());
        int endLine = cu.getLineNumber(node.getStartPosition() + node.getLength());
        int beginColumn = cu.getColumnNumber(node.getStartPosition());
        int endColumn = cu.getColumnNumber(node.getStartPosition() + node.getLength());

        Statement body = node.getBody();
        if (body != null) {

            int beginLineBody = cu.getLineNumber(body.getStartPosition());
            int endLineBody = cu.getLineNumber(body.getStartPosition() + body.getLength());
            int beginColumnBody = cu.getColumnNumber(body.getStartPosition());
            int endColumnBody = cu.getColumnNumber(body.getStartPosition() + body.getLength());

            languageConstructs.add(new LanguageConstruct(node.getClass().getSimpleName(), beginLine, endLine, beginColumn, endColumn,
                    beginLineBody, endLineBody, beginColumnBody, endColumnBody, null));
        } else {
            languageConstructs.add(new LanguageConstruct(node.getClass().getSimpleName(), beginLine, endLine, beginColumn, endColumn));
        }

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
        int beginLine = cu.getLineNumber(node.getStartPosition());
        int endLine = cu.getLineNumber(node.getStartPosition() + node.getLength());
        int beginColumn = cu.getColumnNumber(node.getStartPosition());
        int endColumn = cu.getColumnNumber(node.getStartPosition() + node.getLength());

        languageConstructs.add(new LanguageConstruct(node.getClass().getSimpleName(), beginLine, endLine, beginColumn, endColumn));

        return true;
    }

    public boolean visit(Expression node) {
        int beginLine = cu.getLineNumber(node.getStartPosition());
        int endLine = cu.getLineNumber(node.getStartPosition() + node.getLength());
        int beginColumn = cu.getColumnNumber(node.getStartPosition());
        int endColumn = cu.getColumnNumber(node.getStartPosition() + node.getLength());

        languageConstructs.add(new LanguageConstruct(node.getClass().getSimpleName(), beginLine, endLine, beginColumn, endColumn));

        return true;
    }

    public boolean visit(Name node) {
        int beginLine = cu.getLineNumber(node.getStartPosition());
        int endLine = cu.getLineNumber(node.getStartPosition() + node.getLength());
        int beginColumn = cu.getColumnNumber(node.getStartPosition());
        int endColumn = cu.getColumnNumber(node.getStartPosition() + node.getLength());

        languageConstructs.add(new LanguageConstruct(node.getClass().getSimpleName(), beginLine, endLine, beginColumn, endColumn));

        return true;
    }

    @Override
    public boolean visit(QualifiedType node) {
        int beginLine = cu.getLineNumber(node.getStartPosition());
        int endLine = cu.getLineNumber(node.getStartPosition() + node.getLength());
        int beginColumn = cu.getColumnNumber(node.getStartPosition());
        int endColumn = cu.getColumnNumber(node.getStartPosition() + node.getLength());

        languageConstructs.add(new LanguageConstruct(node.getClass().getSimpleName(), beginLine, endLine, beginColumn, endColumn));

        return true;
    }

    @Override
    public boolean visit(SimpleName node) {
        int beginLine = cu.getLineNumber(node.getStartPosition());
        int endLine = cu.getLineNumber(node.getStartPosition() + node.getLength());
        int beginColumn = cu.getColumnNumber(node.getStartPosition());
        int endColumn = cu.getColumnNumber(node.getStartPosition() + node.getLength());

        String typeByIdentifier = getTypeByIdentifier(node.getIdentifier());

        if (typeByIdentifier != null) {
            languageConstructs.add(new LanguageConstruct(typeByIdentifier, beginLine, endLine, beginColumn, endColumn, node.getIdentifier()));
        }

        return true;
    }

    public boolean visit(Statement node) {
        int beginLine = cu.getLineNumber(node.getStartPosition());
        int endLine = cu.getLineNumber(node.getStartPosition() + node.getLength());
        int beginColumn = cu.getColumnNumber(node.getStartPosition());
        int endColumn = cu.getColumnNumber(node.getStartPosition() + node.getLength());

        languageConstructs.add(new LanguageConstruct(node.getClass().getSimpleName(), beginLine, endLine, beginColumn, endColumn));

        return true;
    }

    public boolean visit(Type node) {
        int beginLine = cu.getLineNumber(node.getStartPosition());
        int endLine = cu.getLineNumber(node.getStartPosition() + node.getLength());
        int beginColumn = cu.getColumnNumber(node.getStartPosition());
        int endColumn = cu.getColumnNumber(node.getStartPosition() + node.getLength());

        languageConstructs.add(new LanguageConstruct(node.getClass().getSimpleName(), beginLine, endLine, beginColumn, endColumn));

        return true;
    }

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>           TypeDeclarationStatement
    @Override
    public boolean visit(TypeDeclarationStatement node) {
        int beginLine = cu.getLineNumber(node.getStartPosition());
        int endLine = cu.getLineNumber(node.getStartPosition() + node.getLength());
        int beginColumn = cu.getColumnNumber(node.getStartPosition());
        int endColumn = cu.getColumnNumber(node.getStartPosition() + node.getLength());

        languageConstructs.add(new LanguageConstruct(node.getClass().getSimpleName(), beginLine, endLine, beginColumn, endColumn));

        return true;
    }

    //Variable 
    public boolean visit(VariableDeclaration node) {
        int beginLine = cu.getLineNumber(node.getStartPosition());
        int endLine = cu.getLineNumber(node.getStartPosition() + node.getLength());
        int beginColumn = cu.getColumnNumber(node.getStartPosition());
        int endColumn = cu.getColumnNumber(node.getStartPosition() + node.getLength());

        languageConstructs.add(new LanguageConstruct(node.getClass().getSimpleName(), beginLine, endLine, beginColumn, endColumn));

        return true;
    }

}
