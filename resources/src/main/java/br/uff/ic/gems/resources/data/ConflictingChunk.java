/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.gems.resources.data;

import br.uff.ic.gems.resources.repositioning.Repositioning;
import br.uff.ic.gems.resources.states.DeveloperDecision;
import java.io.Serializable;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;

/**
 *
 * @author gleiph
 */
@Entity
@SequenceGenerator(name="CC_SEQ", sequenceName = "CC_SEQ", 
        initialValue = 1, allocationSize = 1)
public class ConflictingChunk implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CC_SEQ")
    private Long id;

    private int beginLine;
    private int endLine;
    private int separatorLine;
    private String identifier;
    private DeveloperDecision developerDecision;

    @OneToOne(cascade = CascadeType.ALL)
    private KindConflict leftKindConflict;
    @OneToOne(cascade = CascadeType.ALL)
    private KindConflict rightKindConflict;

    @ElementCollection
    @Lob
    private List<String> conflictingContent;
    @ElementCollection
    @Lob
    private List<String> solutionContent;

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

    /**
     * @return the separatorLine
     */
    public int getSeparatorLine() {
        return separatorLine;
    }

    /**
     * @param separatorLine the separatorLine to set
     */
    public void setSeparatorLine(int separatorLine) {
        this.separatorLine = separatorLine;
    }

    /**
     * @return the identifier
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * @param identifier the identifier to set
     */
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    /**
     * @return the developerChoice
     */
    public DeveloperDecision getDeveloperDecision() {
        return developerDecision;
    }

    /**
     * @param developerChoice the developerChoice to set
     */
    public void setDeveloperDecision(DeveloperDecision developerChoice) {
        this.developerDecision = developerChoice;
    }

    /**
     * @return the conflictingContent
     */
    public List<String> getConflictingContent() {
        return conflictingContent;
    }

    /**
     * @param conflictingContent the conflictingContent to set
     */
    public void setConflictingContent(List<String> conflictingContent) {
        this.conflictingContent = conflictingContent;
    }

    /**
     * @return the solutionContent
     */
    public List<String> getSolutionContent() {
        return solutionContent;
    }

    /**
     * @param solutionContent the solutionContent to set
     */
    public void setSolutionContent(List<String> solutionContent) {
        this.solutionContent = solutionContent;
    }

    public static int checkContext2(List<String> solutionContent, List<String> conflictingContent, int context2eOriginal, int context2bOriginal, Repositioning repositioning, String initialPath, String finalPath, int separator, int begin, int end) {
        int context2 = solutionContent.size() + 1;
        int context2Original = conflictingContent.size();
        boolean changed = false;
        for (int i = context2eOriginal; i >= context2bOriginal; i--) {
            context2 = repositioning.repositioning(initialPath, finalPath, i);
            if (context2 != -1) {
                context2Original = i;
                changed = true;
                break;
            }
        }
        if (context2 == solutionContent.size() && !changed) {
            int c2a0 = -1;
            int c2b0 = -1;
            int c2a = -1;
            int c2b = -1;
            for (int i = separator - 1; i > begin; i--) {
                c2a = repositioning.repositioning(initialPath, finalPath, i);
                if (c2a != -1) {
                    c2a0 = i;
                    break;
                }
            }
            for (int i = end - 1; i > separator; i--) {
                c2b = repositioning.repositioning(initialPath, finalPath, i);
                if (c2b != -1) {
                    c2b0 = i;
                    break;
                }
            }
            if (c2a != -1 || c2b != -1) {
                if (c2a == -1) {
                    context2 = c2b;
                    context2Original = c2b0;
                } else if (c2b == -1) {
                    context2 = c2a;
                    context2Original = c2a0;
                } else if (c2a < c2b) {
                    context2 = c2b;
                    context2Original = c2b0;
                } else {
                    context2 = c2a;
                    context2Original = c2a0;
                }
            }
        }
        if (context2 > solutionContent.size() || context2 < 1) {
            context2 = solutionContent.size();
        }
        return context2;
    }

    public static int checkContext1(int context1bOriginal, int context1eOriginal, Repositioning repositioning, String initialPath, String finalPath, int begin, int separator, int end) {
        int context1 = -1;
        int context1Original = -1;
        boolean changed = false;
        for (int i = context1bOriginal; i <= context1eOriginal; i++) {
            context1 = repositioning.repositioning(initialPath, finalPath, i);
            if (context1 != -1) {
                context1Original = i;
                changed = true;
                break;
            }
        }
        if (context1 == -1 && !changed) {
            int c1a0 = -1;
            int c1b0 = -1;
            int c1a = -1;
            int c1b = -1;
            for (int i = begin; i < separator; i++) {
                c1a = repositioning.repositioning(initialPath, finalPath, i);
                if (c1a != -1) {
                    c1a0 = i;
                    break;
                }
            }
            for (int i = separator + 1; i < end - 1; i++) {
                c1b = repositioning.repositioning(initialPath, finalPath, i);
                if (c1b != -1) {
                    c1b0 = i;
                    break;
                }
            }
            if (c1a != -1 || c1b != -1) {
                if (c1a == -1) {
                    context1 = c1b;
                    context1Original = c1b0;
                } else if (c1b == -1) {
                    context1 = c1a;
                    context1Original = c1a0;
                } else if (c1a < c1b) {
                    context1 = c1a;
                    context1Original = c1a0;
                } else {
                    context1 = c1b;
                    context1Original = c1b0;
                }
            }
        }
        if (context1 < 1) {
            context1 = 1;
        }
        return context1;
    }

    /**
     * @return the leftKindConflict
     */
    public KindConflict getLeftKindConflict() {
        return leftKindConflict;
    }

    /**
     * @param leftKindConflict the leftKindConflict to set
     */
    public void setLeftKindConflict(KindConflict leftKindConflict) {
        this.leftKindConflict = leftKindConflict;
    }

    /**
     * @return the rightKindConflict
     */
    public KindConflict getRightKindConflict() {
        return rightKindConflict;
    }

    /**
     * @param rightKindConflict the rightKindConflict to set
     */
    public void setRightKindConflict(KindConflict rightKindConflict) {
        this.rightKindConflict = rightKindConflict;
    }
}
