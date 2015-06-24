/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.gems.resources.data;

import br.uff.ic.gems.resources.ast.ASTTypes;
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
        int currentIndex = this.beginLine;

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
                currentLanguageConstruct = new LanguageConstruct(ASTTypes.METHOD_SIGNATURE, currentIndex, currentIndex);
                result.add(currentLanguageConstruct);
                currentIndex++;
            } else if (currentLanguageConstruct.getName().equals(ASTTypes.CLASS_DECLARATION)
                    && currentLanguageConstruct.getEndLine() > this.endLine
                    && currentLanguageConstruct.getBeginLine() == currentIndex) {
                currentLanguageConstruct = new LanguageConstruct(ASTTypes.CLASS_SIGNATURE, currentIndex, currentIndex);
                result.add(currentLanguageConstruct);
                currentIndex++;
            } else if (currentLanguageConstruct.getName().equals(ASTTypes.INTERFACE_DECLARATION)
                    && currentLanguageConstruct.getEndLine() > this.endLine
                    && currentLanguageConstruct.getBeginLine() == currentIndex) {
                currentLanguageConstruct = new LanguageConstruct(ASTTypes.INTERFACE_SIGNATURE, currentIndex, currentIndex);
                result.add(currentLanguageConstruct);
                currentIndex++;
            } else if (currentLanguageConstruct.getName().equals(ASTTypes.ENUM_DECLARATION)
                    && currentLanguageConstruct.getEndLine() > this.endLine
                    && currentLanguageConstruct.getBeginLine() == currentIndex) {
                currentLanguageConstruct = new LanguageConstruct(ASTTypes.ENUM_SIGNATURE, currentIndex, currentIndex);
                result.add(currentLanguageConstruct);
                currentIndex++;
            } else if (currentLanguageConstruct.getName().equals(ASTTypes.FOR_STATEMENT)
                    && currentLanguageConstruct.getEndLine() > this.endLine
                    && currentLanguageConstruct.getBeginLine() == currentIndex) {
                currentLanguageConstruct = new LanguageConstruct(ASTTypes.FOR_STATEMENT, currentIndex, currentIndex);
                result.add(currentLanguageConstruct);
                currentIndex++;
            } else if (currentLanguageConstruct.getName().equals(ASTTypes.IF_STATEMENT)
                    && currentLanguageConstruct.getEndLine() > this.endLine
                    && currentLanguageConstruct.getBeginLine() == currentIndex) {
                currentLanguageConstruct = new LanguageConstruct(ASTTypes.IF_STATEMENT, currentIndex, currentIndex);
                result.add(currentLanguageConstruct);
                currentIndex++;
            } else if (currentLanguageConstruct.getName().equals(ASTTypes.SWITCH_STATEMENT)
                    && currentLanguageConstruct.getEndLine() > this.endLine
                    && currentLanguageConstruct.getBeginLine() == currentIndex) {
                currentLanguageConstruct = new LanguageConstruct(ASTTypes.SWITCH_STATEMENT, currentIndex, currentIndex);
                result.add(currentLanguageConstruct);
                currentIndex++;
            } else if (currentLanguageConstruct.getName().equals(ASTTypes.CATCH_CLAUSE)
                    && currentLanguageConstruct.getEndLine() > this.endLine
                    && currentLanguageConstruct.getBeginLine() == currentIndex) {
                currentLanguageConstruct = new LanguageConstruct(ASTTypes.CATCH_CLAUSE, currentIndex, currentIndex);
                result.add(currentLanguageConstruct);
                currentIndex++;
            } else if (currentLanguageConstruct.getName().equals(ASTTypes.COMMENT)
                    && currentLanguageConstruct.getEndLine() > this.endLine
                    && currentLanguageConstruct.getBeginLine() == currentIndex) {
                currentLanguageConstruct = new LanguageConstruct(ASTTypes.COMMENT, currentIndex, currentIndex);
                result.add(currentLanguageConstruct);
                currentIndex++;
            } else if (currentLanguageConstruct.getName().equals(ASTTypes.DO_STATEMENT)
                    && currentLanguageConstruct.getEndLine() > this.endLine
                    && currentLanguageConstruct.getBeginLine() == currentIndex) {
                currentLanguageConstruct = new LanguageConstruct(ASTTypes.DO_STATEMENT, currentIndex, currentIndex);
                result.add(currentLanguageConstruct);
                currentIndex++;
            } else if (currentLanguageConstruct.getName().equals(ASTTypes.STATIC_INITIALIZER)
                    && currentLanguageConstruct.getEndLine() > this.endLine
                    && currentLanguageConstruct.getBeginLine() == currentIndex) {
                currentLanguageConstruct = new LanguageConstruct(ASTTypes.STATIC_INITIALIZER, currentIndex, currentIndex);
                result.add(currentLanguageConstruct);
                currentIndex++;
            } else if (currentLanguageConstruct.getName().equals(ASTTypes.TRY_STATEMENT)
                    && currentLanguageConstruct.getEndLine() > this.endLine
                    && currentLanguageConstruct.getBeginLine() == currentIndex) {
                currentLanguageConstruct = new LanguageConstruct(ASTTypes.TRY_STATEMENT, currentIndex, currentIndex);
                result.add(currentLanguageConstruct);
                currentIndex++;
            } else if (currentLanguageConstruct.getName().equals(ASTTypes.WHILE_STATEMENT)
                    && currentLanguageConstruct.getEndLine() > this.endLine
                    && currentLanguageConstruct.getBeginLine() == currentIndex) {
                currentLanguageConstruct = new LanguageConstruct(ASTTypes.WHILE_STATEMENT, currentIndex, currentIndex);
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
}
