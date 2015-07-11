/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.gems.resources.ast;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jdt.core.dom.AnnotationTypeMemberDeclaration;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.ArrayCreation;
import org.eclipse.jdt.core.dom.ArrayInitializer;
import org.eclipse.jdt.core.dom.AssertStatement;
import org.eclipse.jdt.core.dom.BlockComment;
import org.eclipse.jdt.core.dom.BreakStatement;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.Comment;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.ContinueStatement;
import org.eclipse.jdt.core.dom.DoStatement;
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
import org.eclipse.jdt.core.dom.SynchronizedStatement;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;

/**
 *
 * @author gleiph
 */
public class ASTTranslator {

    private static final String CLASS = "Class";
    private static final String INTERFACE = "Interface";

    public static List<String> ANNOTATION_TYPE_MEMBER_DECLARATION = new ArrayList<>();

    static {
        ANNOTATION_TYPE_MEMBER_DECLARATION.add(AnnotationTypeMemberDeclaration.class.getSimpleName());
    }

    public static List<String> CLASS_DECLARATION = new ArrayList<>();

    static {
        CLASS_DECLARATION.add(AnonymousClassDeclaration.class.getSimpleName());
        CLASS_DECLARATION.add(TypeDeclaration.class.getSimpleName() + CLASS);
    }

    public static List<String> ARRAY_ACCESS = new ArrayList<>();

    static {
        ARRAY_ACCESS.add(ArrayAccess.class.getSimpleName());
    }

    public static List<String> METHOD_INVOCATION = new ArrayList<>();

    static {
        METHOD_INVOCATION.add(ArrayCreation.class.getSimpleName());
        METHOD_INVOCATION.add(ClassInstanceCreation.class.getSimpleName());
        METHOD_INVOCATION.add(ConstructorInvocation.class.getSimpleName());
        METHOD_INVOCATION.add(MethodInvocation.class.getSimpleName());
        METHOD_INVOCATION.add(SuperConstructorInvocation.class.getSimpleName());
        METHOD_INVOCATION.add(SuperMethodInvocation.class.getSimpleName());
    }

    public static List<String> ARRAY_INITIALIZER = new ArrayList<>();

    static {
        ARRAY_INITIALIZER.add(ArrayInitializer.class.getSimpleName());
    }

    public static List<String> ASSERT_STATEMENT = new ArrayList<>();

    static {
        ASSERT_STATEMENT.add(AssertStatement.class.getSimpleName());
    }

    public static List<String> COMMENT = new ArrayList<>();

    static {
        COMMENT.add(BlockComment.class.getSimpleName());
        COMMENT.add(Comment.class.getSimpleName());
        COMMENT.add(Javadoc.class.getSimpleName());
        COMMENT.add(LineComment.class.getSimpleName());
    }

    public static List<String> BREAK_STATEMENT = new ArrayList<>();

    static {
        BREAK_STATEMENT.add(BreakStatement.class.getSimpleName());
    }

    public static List<String> CAST_EXPRESSION = new ArrayList<>();

    static {
        CAST_EXPRESSION.add(CastExpression.class.getSimpleName());
    }

    public static List<String> CATCH_CLAUSE = new ArrayList<>();

    static {
        CATCH_CLAUSE.add(CatchClause.class.getSimpleName());
    }

    public static List<String> CONTINUE_STATEMENT = new ArrayList<>();

    static {
        CONTINUE_STATEMENT.add(ContinueStatement.class.getSimpleName());
    }

    public static List<String> DO_STATEMENT = new ArrayList<>();

    static {
        DO_STATEMENT.add(DoStatement.class.getSimpleName());
    }

    public static List<String> FOR_STATEMENT = new ArrayList<>();

    static {
        FOR_STATEMENT.add(EnhancedForStatement.class.getSimpleName());
        FOR_STATEMENT.add(ForStatement.class.getSimpleName());
    }

    public static List<String> ENUM_VALUE = new ArrayList<>();

    static {
        ENUM_VALUE.add(EnumConstantDeclaration.class.getSimpleName());
    }

    public static List<String> ENUM_DECLARATION = new ArrayList<>();

    static {
        ENUM_DECLARATION.add(EnumDeclaration.class.getSimpleName());
    }

    public static List<String> ATTRIBUTE = new ArrayList<>();

    static {
        ATTRIBUTE.add(FieldDeclaration.class.getSimpleName());
    }

    public static List<String> IF_STATEMENT = new ArrayList<>();

    static {
        ATTRIBUTE.add(IfStatement.class.getSimpleName());
    }

    public static List<String> IMPORT_DECLARATION = new ArrayList<>();

    static {
        IMPORT_DECLARATION.add(ImportDeclaration.class.getSimpleName());
    }

    public static List<String> STATIC_INITIALIZER = new ArrayList<>();

    static {
        STATIC_INITIALIZER.add(Initializer.class.getSimpleName());
    }

    public static List<String> ANNOTATION = new ArrayList<>();

    static {
        ANNOTATION.add(MarkerAnnotation.class.getSimpleName());
        ANNOTATION.add(NormalAnnotation.class.getSimpleName());
        ANNOTATION.add(SingleMemberAnnotation.class.getSimpleName());
    }

    public static List<String> METHOD_DECLARATION = new ArrayList<>();

    static {
        METHOD_DECLARATION.add(MethodDeclaration.class.getSimpleName());
    }

    public static List<String> PACKAGE_DECLARATION = new ArrayList<>();

    static {
        PACKAGE_DECLARATION.add(PackageDeclaration.class.getSimpleName());
    }

    public static List<String> RETURN_STATEMENT = new ArrayList<>();

    static {
        RETURN_STATEMENT.add(ReturnStatement.class.getSimpleName());
    }

    public static List<String> VARIABLE = new ArrayList<>();

    static {
        VARIABLE.add(SingleVariableDeclaration.class.getSimpleName());
        VARIABLE.add(VariableDeclarationExpression.class.getSimpleName());
        VARIABLE.add(VariableDeclarationStatement.class.getSimpleName());
    }

    public static List<String> CASE_STATEMENT = new ArrayList<>();

    static {
        CASE_STATEMENT.add(SwitchCase.class.getSimpleName());
    }

    public static List<String> SWITCH_STATEMENT = new ArrayList<>();

    static {
        SWITCH_STATEMENT.add(SwitchStatement.class.getSimpleName());
    }

    public static List<String> SYNCHRONIZED_STATEMENT = new ArrayList<>();

    static {
        SYNCHRONIZED_STATEMENT.add(SynchronizedStatement.class.getSimpleName());
    }

    public static List<String> THROW_STATEMENT = new ArrayList<>();

    static {
        THROW_STATEMENT.add(ThrowStatement.class.getSimpleName());
    }

    public static List<String> TRY_STATEMENT = new ArrayList<>();

    static {
        TRY_STATEMENT.add(TryStatement.class.getSimpleName());
    }

    public static List<String> INTERFACE_DECLARATION = new ArrayList<>();

    static {
        INTERFACE_DECLARATION.add(TypeDeclaration.class.getSimpleName() + INTERFACE);
    }

    public static List<String> WHILE_STATEMENT = new ArrayList<>();

    static {
        WHILE_STATEMENT.add(WhileStatement.class.getSimpleName() + INTERFACE);
    }

    public static String translate(String name) {

        if (ANNOTATION_TYPE_MEMBER_DECLARATION.contains(name)) {
            return ASTTypes.ANNOTATION_TYPE_MEMBER_DECLARATION;
        } else if (CLASS_DECLARATION.contains(name)) {
            return ASTTypes.CLASS_DECLARATION;
        } else if (ARRAY_ACCESS.contains(name)) {
            return ASTTypes.ARRAY_ACCESS;
        } else if (METHOD_INVOCATION.contains(name)) {
            return ASTTypes.METHOD_INVOCATION;
        } else if (ARRAY_INITIALIZER.contains(name)) {
            return ASTTypes.ARRAY_INITIALIZER;
        } else if (ASSERT_STATEMENT.contains(name)) {
            return ASTTypes.ASSERT_STATEMENT;
        } else if (COMMENT.contains(name)) {
            return ASTTypes.COMMENT;
        } else if (BREAK_STATEMENT.contains(name)) {
            return ASTTypes.BREAK_STATEMENT;
        } else if (CAST_EXPRESSION.contains(name)) {
            return ASTTypes.CAST_EXPRESSION;
        } else if (CATCH_CLAUSE.contains(name)) {
            return ASTTypes.CATCH_CLAUSE;
        } else if (CONTINUE_STATEMENT.contains(name)) {
            return ASTTypes.CONTINUE_STATEMENT;
        } else if (DO_STATEMENT.contains(name)) {
            return ASTTypes.DO_STATEMENT;
        } else if (FOR_STATEMENT.contains(name)) {
            return ASTTypes.FOR_STATEMENT;
        } else if (ENUM_VALUE.contains(name)) {
            return ASTTypes.ENUM_VALUE;
        } else if (ENUM_DECLARATION.contains(name)) {
            return ASTTypes.ENUM_DECLARATION;
        } else if (ATTRIBUTE.contains(name)) {
            return ASTTypes.ATTRIBUTE;
        } else if (IF_STATEMENT.contains(name)) {
            return ASTTypes.IF_STATEMENT;
        } else if (IMPORT_DECLARATION.contains(name)) {
            return ASTTypes.IMPORT_DECLARATION;
        } else if (STATIC_INITIALIZER.contains(name)) {
            return ASTTypes.STATIC_INITIALIZER;
        } else if (ANNOTATION.contains(name)) {
            return ASTTypes.ANNOTATION;
        } else if (METHOD_DECLARATION.contains(name)) {
            return ASTTypes.METHOD_DECLARATION;
        } else if (PACKAGE_DECLARATION.contains(name)) {
            return ASTTypes.PACKAGE_DECLARATION;
        } else if (RETURN_STATEMENT.contains(name)) {
            return ASTTypes.RETURN_STATEMENT;
        } else if (VARIABLE.contains(name)) {
            return ASTTypes.VARIABLE;
        } else if (CASE_STATEMENT.contains(name)) {
            return ASTTypes.CASE_STATEMENT;
        } else if (SWITCH_STATEMENT.contains(name)) {
            return ASTTypes.SWITCH_STATEMENT;
        } else if (SYNCHRONIZED_STATEMENT.contains(name)) {
            return ASTTypes.SYNCHRONIZED_STATEMENT;
        } else if (THROW_STATEMENT.contains(name)) {
            return ASTTypes.THROW_STATEMENT;
        } else if (TRY_STATEMENT.contains(name)) {
            return ASTTypes.TRY_STATEMENT;
        } else if (INTERFACE_DECLARATION.contains(name)) {
            return ASTTypes.INTERFACE_DECLARATION;
        } else if (WHILE_STATEMENT.contains(name)) {
            return ASTTypes.WHILE_STATEMENT;
        }

        return name;

    }

}
