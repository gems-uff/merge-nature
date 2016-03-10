/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.gems.resources.data;

import br.uff.ic.gems.resources.ast.ASTTranslator;
import br.uff.ic.gems.resources.ast.ASTTypes;
import br.uff.ic.gems.resources.data.dao.sql.Side;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

/**
 *
 * @author gleiph
 */
@Entity
public class KindConflict implements Serializable {

    @Id
    @GeneratedValue
    private Long id;
    private int beginLine;
    private int endLine;
    @Transient
    private Side side;

    @OneToMany(cascade = CascadeType.PERSIST)
    @JoinTable(name = "KindConflict_LanguageConstruct",
            joinColumns = @JoinColumn(name = "KindConflict_ID"),
            inverseJoinColumns = @JoinColumn(name = "LanguageConstruct_ID"))
    private List<LanguageConstruct> languageConstructs;

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
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

    /**
     * @return the beginLine
     */
    public int getBeginLine() {
        return beginLine;
    }

    /**
     * @param beginLine the beginLine to set
     */
    public void setBeginLine(int beginLine) {
        this.beginLine = beginLine;
    }

    /**
     * @return the endLine
     */
    public int getEndLine() {
        return endLine;
    }

    /**
     * @param endLine the endLine to set
     */
    public void setEndLine(int endLine) {
        this.endLine = endLine;
    }

    public List<LanguageConstruct> getFilteredLanguageConstructs() {
        List<LanguageConstruct> result = new ArrayList<>();
        int currentLine = this.beginLine;
        int currentColumn = 0;

        List<String> bodyASTTypes = new ArrayList<>();

        bodyASTTypes.addAll(ASTTranslator.CATCH_CLAUSE);
        bodyASTTypes.addAll(ASTTranslator.CLASS_DECLARATION);
        bodyASTTypes.addAll(ASTTranslator.DO_STATEMENT);
        bodyASTTypes.addAll(ASTTranslator.ENUM_DECLARATION);
        bodyASTTypes.addAll(ASTTranslator.FOR_STATEMENT);
        bodyASTTypes.addAll(ASTTranslator.IF_STATEMENT);
        bodyASTTypes.addAll(ASTTranslator.INTERFACE_DECLARATION);
        bodyASTTypes.addAll(ASTTranslator.METHOD_DECLARATION);
        bodyASTTypes.addAll(ASTTranslator.STATIC_INITIALIZER);
        bodyASTTypes.addAll(ASTTranslator.SWITCH_STATEMENT);
        bodyASTTypes.addAll(ASTTranslator.TRY_STATEMENT);
        bodyASTTypes.addAll(ASTTranslator.WHILE_STATEMENT);
        bodyASTTypes.addAll(ASTTranslator.ARRAY_INITIALIZER);
        bodyASTTypes.addAll(ASTTranslator.RETURN_STATEMENT);
        bodyASTTypes.addAll(ASTTranslator.SYNCHRONIZED_STATEMENT);
        bodyASTTypes.addAll(ASTTranslator.METHOD_INTERFACE);
        bodyASTTypes.addAll(ASTTranslator.METHOD_INVOCATION);

        List<LanguageConstruct> copyLanguageConstructs = new ArrayList<>();

        //Copying language constructs
        for (LanguageConstruct languageConstruct : this.getLanguageConstructs()) {
            copyLanguageConstructs.add(languageConstruct);
        }

        //Selecting language constructs
        while (currentLine <= this.endLine && !copyLanguageConstructs.isEmpty()) {
            int lineSize = 0;
            int columnDistance = Integer.MAX_VALUE;

            LanguageConstruct currentLanguageConstruct = null;

            for (LanguageConstruct languageConstruct : copyLanguageConstructs) {
                if ( //Begin is in the current line
                        languageConstruct.getBeginLine() == currentLine
                        && (//Next element
                        languageConstruct.getBeginColumn() >= currentColumn
                        && columnDistance > languageConstruct.getBeginColumn() - currentColumn)
                        && (//greater number of lines
                        (lineSize < languageConstruct.getEndLine() - languageConstruct.getBeginLine() + 1)
                        || (lineSize == languageConstruct.getEndLine() - languageConstruct.getBeginLine() + 1
                        && languageConstruct.getEndColumn() > currentLanguageConstruct.getEndColumn()))
                        && !result.contains(languageConstruct)) {
                    currentLanguageConstruct = languageConstruct;
                    lineSize = languageConstruct.getEndLine() - languageConstruct.getBeginLine() + 1;
                    columnDistance = languageConstruct.getBeginColumn() - currentColumn;
                }
            }

            if (currentLanguageConstruct == null) {
                for (LanguageConstruct copyLanguageConstruct : copyLanguageConstructs) {
                    if (copyLanguageConstruct.getBeginLine() < currentLine
                            && currentLine < copyLanguageConstruct.getEndLine()
                            && !bodyASTTypes.contains(copyLanguageConstruct.getName())) {
                        currentLanguageConstruct = copyLanguageConstruct;
                        break;
                    }
                }
            }

            if (currentLanguageConstruct == null) {
                currentLine++;
                currentColumn = 0;
            } else if (ASTTranslator.METHOD_DECLARATION.contains(currentLanguageConstruct.getName())
                    && currentLanguageConstruct.getEndLine() > this.endLine
                    && currentLanguageConstruct.getBeginLine() == currentLine) {

                currentLanguageConstruct = getSubLanguageConstruct(currentLanguageConstruct, ASTTypes.METHOD_SIGNATURE);
                result.add(currentLanguageConstruct);
                currentLine = currentLanguageConstruct.getEndLine();
                currentColumn = currentLanguageConstruct.getEndColumn();
            } else if (ASTTranslator.METHOD_INTERFACE.contains(currentLanguageConstruct.getName())
                    && currentLanguageConstruct.getEndLine() > this.endLine
                    && currentLanguageConstruct.getBeginLine() == currentLine) {

                currentLanguageConstruct = getSubLanguageConstruct(currentLanguageConstruct, ASTTypes.METHOD_SIGNATURE);
                result.add(currentLanguageConstruct);
                currentLine = currentLanguageConstruct.getEndLine();
                currentColumn = currentLanguageConstruct.getEndColumn();
            } else if (ASTTranslator.CLASS_DECLARATION.contains(currentLanguageConstruct.getName())
                    && currentLanguageConstruct.getEndLine() > this.endLine
                    && currentLanguageConstruct.getBeginLine() == currentLine) {
                currentLanguageConstruct = getSubLanguageConstruct(currentLanguageConstruct, ASTTypes.CLASS_SIGNATURE);
                result.add(currentLanguageConstruct);
                currentLine = currentLanguageConstruct.getEndLine();
                currentColumn = currentLanguageConstruct.getEndColumn();
            } else if (ASTTranslator.INTERFACE_DECLARATION.contains(currentLanguageConstruct.getName())
                    && currentLanguageConstruct.getEndLine() > this.endLine
                    && currentLanguageConstruct.getBeginLine() == currentLine) {
                currentLanguageConstruct = getSubLanguageConstruct(currentLanguageConstruct, ASTTypes.INTERFACE_SIGNATURE);
                result.add(currentLanguageConstruct);
                currentLine = currentLanguageConstruct.getEndLine();
                currentColumn = currentLanguageConstruct.getEndColumn();
            } else if (ASTTranslator.ENUM_DECLARATION.contains(currentLanguageConstruct.getName())
                    && currentLanguageConstruct.getEndLine() > this.endLine
                    && currentLanguageConstruct.getBeginLine() == currentLine) {
                currentLanguageConstruct = getSubLanguageConstruct(currentLanguageConstruct, ASTTypes.ENUM_SIGNATURE);
                result.add(currentLanguageConstruct);
                currentLine = currentLanguageConstruct.getEndLine();
                currentColumn = currentLanguageConstruct.getEndColumn();
            } else if (ASTTranslator.FOR_STATEMENT.contains(currentLanguageConstruct.getName())
                    && currentLanguageConstruct.getEndLine() > this.endLine
                    && currentLanguageConstruct.getBeginLine() == currentLine) {
                currentLanguageConstruct = getSubLanguageConstruct(currentLanguageConstruct, ASTTypes.FOR_STATEMENT);
                result.add(currentLanguageConstruct);
                currentLine = currentLanguageConstruct.getEndLine();
                currentColumn = currentLanguageConstruct.getEndColumn();
            } else if (ASTTranslator.IF_STATEMENT.contains(currentLanguageConstruct.getName())
                    && currentLanguageConstruct.getEndLine() > this.endLine
                    && currentLanguageConstruct.getBeginLine() == currentLine) {
                currentLanguageConstruct = getSubLanguageConstruct(currentLanguageConstruct, ASTTypes.IF_STATEMENT);
                result.add(currentLanguageConstruct);
                currentLine = currentLanguageConstruct.getEndLine();
                currentColumn = currentLanguageConstruct.getEndColumn();
            } else if (ASTTranslator.SWITCH_STATEMENT.contains(currentLanguageConstruct.getName())
                    && currentLanguageConstruct.getEndLine() > this.endLine
                    && currentLanguageConstruct.getBeginLine() == currentLine) {
                currentLanguageConstruct = getSubLanguageConstruct(currentLanguageConstruct, ASTTypes.SWITCH_STATEMENT);
                result.add(currentLanguageConstruct);
                currentLine = currentLanguageConstruct.getEndLine();
                currentColumn = currentLanguageConstruct.getEndColumn();
            } else if (ASTTranslator.CATCH_CLAUSE.contains(currentLanguageConstruct.getName())
                    && currentLanguageConstruct.getEndLine() > this.endLine
                    && currentLanguageConstruct.getBeginLine() == currentLine) {
                currentLanguageConstruct = getSubLanguageConstruct(currentLanguageConstruct, ASTTypes.CATCH_CLAUSE);
                result.add(currentLanguageConstruct);
                currentLine = currentLanguageConstruct.getEndLine();
                currentColumn = currentLanguageConstruct.getEndColumn();
            } else if (ASTTranslator.COMMENT.contains(currentLanguageConstruct.getName())
                    && currentLanguageConstruct.getEndLine() > this.endLine
                    && currentLanguageConstruct.getBeginLine() == currentLine) {
                currentLanguageConstruct = getSubLanguageConstruct(currentLanguageConstruct, ASTTypes.COMMENT);
                result.add(currentLanguageConstruct);
                currentLine = currentLanguageConstruct.getEndLine();
                currentColumn = currentLanguageConstruct.getEndColumn();
            } else if (ASTTranslator.DO_STATEMENT.contains(currentLanguageConstruct.getName())
                    && currentLanguageConstruct.getEndLine() > this.endLine
                    && currentLanguageConstruct.getBeginLine() == currentLine) {
                currentLanguageConstruct = getSubLanguageConstruct(currentLanguageConstruct, ASTTypes.DO_STATEMENT);
                result.add(currentLanguageConstruct);
                currentLine = currentLanguageConstruct.getEndLine();
                currentColumn = currentLanguageConstruct.getEndColumn();
            } else if (ASTTranslator.STATIC_INITIALIZER.contains(currentLanguageConstruct.getName())
                    && currentLanguageConstruct.getEndLine() > this.endLine
                    && currentLanguageConstruct.getBeginLine() == currentLine) {
                currentLanguageConstruct = getSubLanguageConstruct(currentLanguageConstruct, ASTTypes.STATIC_INITIALIZER);
                result.add(currentLanguageConstruct);
                currentLine = currentLanguageConstruct.getEndLine();
                currentColumn = currentLanguageConstruct.getEndColumn();
            } else if (ASTTranslator.TRY_STATEMENT.contains(currentLanguageConstruct.getName())
                    && currentLanguageConstruct.getEndLine() > this.endLine
                    && currentLanguageConstruct.getBeginLine() == currentLine) {
                currentLanguageConstruct = getSubLanguageConstruct(currentLanguageConstruct, ASTTypes.TRY_STATEMENT);
                result.add(currentLanguageConstruct);
                currentLine = currentLanguageConstruct.getEndLine();
                currentColumn = currentLanguageConstruct.getEndColumn();
            } else if (ASTTranslator.WHILE_STATEMENT.contains(currentLanguageConstruct.getName())
                    && currentLanguageConstruct.getEndLine() > this.endLine
                    && currentLanguageConstruct.getBeginLine() == currentLine) {
                currentLanguageConstruct = getSubLanguageConstruct(currentLanguageConstruct, ASTTypes.WHILE_STATEMENT);
                result.add(currentLanguageConstruct);
                currentLine = currentLanguageConstruct.getEndLine();
                currentColumn = currentLanguageConstruct.getEndColumn();
            } else if (ASTTranslator.ATTRIBUTE.contains(currentLanguageConstruct.getName())) {

                boolean insideComment = false;
                for (LanguageConstruct languageConstruct : this.getLanguageConstructs()) {
                    if (ASTTranslator.COMMENT.contains(languageConstruct.getName())) {
                        if (languageConstruct.getBeginLine() < currentLanguageConstruct.getBeginLine()
                                && currentLanguageConstruct.getEndLine() <= languageConstruct.getEndLine()) {
                            insideComment = true;
                        }
                    }
                }

                if (!insideComment) {
                    result.add(currentLanguageConstruct);
                }
                currentLine = currentLanguageConstruct.getEndLine();
                currentColumn = currentLanguageConstruct.getEndColumn();
            } else if (ASTTranslator.VARIABLE.contains(currentLanguageConstruct.getName())) {

                boolean insideComment = false;
                for (LanguageConstruct languageConstruct : this.getLanguageConstructs()) {
                    if (ASTTranslator.COMMENT.contains(languageConstruct.getName())) {
                        if (languageConstruct.getBeginLine() < currentLanguageConstruct.getBeginLine()
                                && currentLanguageConstruct.getEndLine() <= languageConstruct.getEndLine()) {
                            insideComment = true;
                        }
                    }
                }

                if (!insideComment) {
                    result.add(currentLanguageConstruct);
                }
                currentLine = currentLanguageConstruct.getEndLine();
                currentColumn = currentLanguageConstruct.getEndColumn();
            } else if (ASTTranslator.RETURN_STATEMENT.contains(currentLanguageConstruct.getName())) {
                currentLanguageConstruct.setEndLine(currentLanguageConstruct.getBeginLine());
                currentLanguageConstruct.setEndColumn(currentLanguageConstruct.getBeginColumn() + "return".length() - 1);

                result.add(currentLanguageConstruct);

                currentLine = currentLanguageConstruct.getEndLine();
                currentColumn = currentLanguageConstruct.getEndColumn();
            } else {
                result.add(currentLanguageConstruct);
                currentLine = currentLanguageConstruct.getEndLine();
                currentColumn = currentLanguageConstruct.getEndColumn() + 1;

            }
        }

        if (result.isEmpty()) {

            for (LanguageConstruct languageConstruct : this.getLanguageConstructs()) {
                if (languageConstruct.getName().equals(ASTTypes.BLANK)) {
                    result.add(languageConstruct);
                    return result;
                }
            }

            result.add(new LanguageConstruct(ASTTypes.OTHER, 0, 0, 0, 0));
            return result;
        }

        return result;
    }

    public LanguageConstruct getSubLanguageConstruct(LanguageConstruct currentLanguageConstruct, String name) {
        int bl = 0, el = 0, bc = 0, ec = 0;
        bl = currentLanguageConstruct.getBeginLine();
        bc = currentLanguageConstruct.getBeginColumn();

        if (currentLanguageConstruct.isHasBlock()) {
            el = currentLanguageConstruct.getBeginLineBlock();
            ec = currentLanguageConstruct.getBeginColumnBlock();

            if (bl == el && bc == ec) {
                ec++;
            }

        } else {
            el = bl + 1;
            ec = 0;
        }
        currentLanguageConstruct = new LanguageConstruct(name, bl, el, bc, ec);
        return currentLanguageConstruct;
    }

    @Deprecated
    public List<LanguageConstruct> getFilteredLanguageConstructsBackup() {
        List<LanguageConstruct> result = new ArrayList<>();
        int currentIndex = this.beginLine;
        int beginColumn = 0, endColumn = 0;

        String[] bodyASTTypes = {ASTTypes.CATCH_CLAUSE, ASTTypes.CLASS_DECLARATION, ASTTypes.DO_STATEMENT, ASTTypes.ENUM_DECLARATION,
            ASTTypes.FOR_STATEMENT, ASTTypes.IF_STATEMENT, ASTTypes.INTERFACE_DECLARATION, ASTTypes.METHOD_DECLARATION, ASTTypes.STATIC_INITIALIZER,
            ASTTypes.SWITCH_STATEMENT, ASTTypes.TRY_STATEMENT, ASTTypes.WHILE_STATEMENT};
        List<String> bodyASTTypesList = Arrays.asList(bodyASTTypes);
        List<LanguageConstruct> copyLanguageConstructs = new ArrayList<>();

        //Copying language constructs
        for (LanguageConstruct languageConstruct : this.getLanguageConstructs()) {
            copyLanguageConstructs.add(languageConstruct);
        }

        //Selecting language constructs
        while (currentIndex <= this.endLine) {
            int size = 0;

            LanguageConstruct currentLanguageConstruct = new LanguageConstruct();

            for (LanguageConstruct languageConstruct : copyLanguageConstructs) {
                if (languageConstruct.getBeginLine() == currentIndex
                        && size < languageConstruct.getEndLine() - languageConstruct.getBeginLine() + 1) {
                    currentLanguageConstruct = languageConstruct;
                    size = languageConstruct.getEndLine() - languageConstruct.getBeginLine() + 1;
                }
            }

            if (currentLanguageConstruct.getId() == null) {
                for (LanguageConstruct copyLanguageConstruct : copyLanguageConstructs) {
                    if (copyLanguageConstruct.getBeginLine() <= currentIndex
                            && currentIndex <= copyLanguageConstruct.getEndLine()
                            && !bodyASTTypesList.contains(copyLanguageConstruct.getName())) {
                        currentLanguageConstruct = copyLanguageConstruct;
                        break;
                    }
                }
            }

            if (currentLanguageConstruct.getId() == null) {
                currentIndex++;
            } else if (currentLanguageConstruct.getName().equals(ASTTypes.METHOD_DECLARATION)
                    && currentLanguageConstruct.getEndLine() > this.endLine
                    && currentLanguageConstruct.getBeginLine() == currentIndex) {
                currentLanguageConstruct = new LanguageConstruct(ASTTypes.METHOD_SIGNATURE, currentIndex, currentIndex, beginColumn, endColumn);
                result.add(currentLanguageConstruct);
                currentIndex++;
            } else if (currentLanguageConstruct.getName().equals(ASTTypes.CLASS_DECLARATION)
                    && currentLanguageConstruct.getEndLine() > this.endLine
                    && currentLanguageConstruct.getBeginLine() == currentIndex) {
                currentLanguageConstruct = new LanguageConstruct(ASTTypes.CLASS_SIGNATURE, currentIndex, currentIndex, beginColumn, endColumn);
                result.add(currentLanguageConstruct);
                currentIndex++;
            } else if (currentLanguageConstruct.getName().equals(ASTTypes.INTERFACE_DECLARATION)
                    && currentLanguageConstruct.getEndLine() > this.endLine
                    && currentLanguageConstruct.getBeginLine() == currentIndex) {
                currentLanguageConstruct = new LanguageConstruct(ASTTypes.INTERFACE_SIGNATURE, currentIndex, currentIndex, beginColumn, endColumn);
                result.add(currentLanguageConstruct);
                currentIndex++;
            } else if (currentLanguageConstruct.getName().equals(ASTTypes.ENUM_DECLARATION)
                    && currentLanguageConstruct.getEndLine() > this.endLine
                    && currentLanguageConstruct.getBeginLine() == currentIndex) {
                currentLanguageConstruct = new LanguageConstruct(ASTTypes.ENUM_SIGNATURE, currentIndex, currentIndex, beginColumn, endColumn);
                result.add(currentLanguageConstruct);
                currentIndex++;
            } else if (currentLanguageConstruct.getName().equals(ASTTypes.FOR_STATEMENT)
                    && currentLanguageConstruct.getEndLine() > this.endLine
                    && currentLanguageConstruct.getBeginLine() == currentIndex) {
                currentLanguageConstruct = new LanguageConstruct(ASTTypes.FOR_STATEMENT, currentIndex, currentIndex, beginColumn, endColumn);
                result.add(currentLanguageConstruct);
                currentIndex++;
            } else if (currentLanguageConstruct.getName().equals(ASTTypes.IF_STATEMENT)
                    && currentLanguageConstruct.getEndLine() > this.endLine
                    && currentLanguageConstruct.getBeginLine() == currentIndex) {
                currentLanguageConstruct = new LanguageConstruct(ASTTypes.IF_STATEMENT, currentIndex, currentIndex, beginColumn, endColumn);
                result.add(currentLanguageConstruct);
                currentIndex++;
            } else if (currentLanguageConstruct.getName().equals(ASTTypes.SWITCH_STATEMENT)
                    && currentLanguageConstruct.getEndLine() > this.endLine
                    && currentLanguageConstruct.getBeginLine() == currentIndex) {
                currentLanguageConstruct = new LanguageConstruct(ASTTypes.SWITCH_STATEMENT, currentIndex, currentIndex, beginColumn, endColumn);
                result.add(currentLanguageConstruct);
                currentIndex++;
            } else if (currentLanguageConstruct.getName().equals(ASTTypes.CATCH_CLAUSE)
                    && currentLanguageConstruct.getEndLine() > this.endLine
                    && currentLanguageConstruct.getBeginLine() == currentIndex) {
                currentLanguageConstruct = new LanguageConstruct(ASTTypes.CATCH_CLAUSE, currentIndex, currentIndex, beginColumn, endColumn);
                result.add(currentLanguageConstruct);
                currentIndex++;
            } else if (currentLanguageConstruct.getName().equals(ASTTypes.COMMENT)
                    && currentLanguageConstruct.getEndLine() > this.endLine
                    && currentLanguageConstruct.getBeginLine() == currentIndex) {
                currentLanguageConstruct = new LanguageConstruct(ASTTypes.COMMENT, currentIndex, currentIndex, beginColumn, endColumn);
                result.add(currentLanguageConstruct);
                currentIndex++;
            } else if (currentLanguageConstruct.getName().equals(ASTTypes.DO_STATEMENT)
                    && currentLanguageConstruct.getEndLine() > this.endLine
                    && currentLanguageConstruct.getBeginLine() == currentIndex) {
                currentLanguageConstruct = new LanguageConstruct(ASTTypes.DO_STATEMENT, currentIndex, currentIndex, beginColumn, endColumn);
                result.add(currentLanguageConstruct);
                currentIndex++;
            } else if (currentLanguageConstruct.getName().equals(ASTTypes.STATIC_INITIALIZER)
                    && currentLanguageConstruct.getEndLine() > this.endLine
                    && currentLanguageConstruct.getBeginLine() == currentIndex) {
                currentLanguageConstruct = new LanguageConstruct(ASTTypes.STATIC_INITIALIZER, currentIndex, currentIndex, beginColumn, endColumn);
                result.add(currentLanguageConstruct);
                currentIndex++;
            } else if (currentLanguageConstruct.getName().equals(ASTTypes.TRY_STATEMENT)
                    && currentLanguageConstruct.getEndLine() > this.endLine
                    && currentLanguageConstruct.getBeginLine() == currentIndex) {
                currentLanguageConstruct = new LanguageConstruct(ASTTypes.TRY_STATEMENT, currentIndex, currentIndex, beginColumn, endColumn);
                result.add(currentLanguageConstruct);
                currentIndex++;
            } else if (currentLanguageConstruct.getName().equals(ASTTypes.WHILE_STATEMENT)
                    && currentLanguageConstruct.getEndLine() > this.endLine
                    && currentLanguageConstruct.getBeginLine() == currentIndex) {
                currentLanguageConstruct = new LanguageConstruct(ASTTypes.WHILE_STATEMENT, currentIndex, currentIndex, beginColumn, endColumn);
                result.add(currentLanguageConstruct);
                currentIndex++;
            } else if (currentLanguageConstruct.getName().equals(ASTTypes.RETURN_STATEMENT)) {
                result.add(currentLanguageConstruct);
                copyLanguageConstructs.remove(currentLanguageConstruct);
            } else if (currentLanguageConstruct.getEndLine() - currentLanguageConstruct.getBeginLine() == 0) {
                result.add(currentLanguageConstruct);
                copyLanguageConstructs.remove(currentLanguageConstruct);
            } else {
                result.add(currentLanguageConstruct);
                currentIndex += currentLanguageConstruct.getEndLine() - currentLanguageConstruct.getBeginLine();
            }
        }

        return result;
    }

    /**
     * @return the side
     */
    public Side getSide() {
        return side;
    }

    /**
     * @param side the side to set
     */
    public void setSide(Side side) {
        this.side = side;
    }
}
