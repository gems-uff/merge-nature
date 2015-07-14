/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.gems.resources.data;

import br.uff.ic.gems.resources.ast.ASTTranslator;
import br.uff.ic.gems.resources.repositioning.Repositioning;
import br.uff.ic.gems.resources.states.DeveloperDecision;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.OneToOne;

/**
 *
 * @author gleiph
 */
@Entity
public class ConflictingChunk implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    private int beginLine;
    private int endLine;
    private int separatorLine;
    private String identifier;
    private DeveloperDecision developerDecision;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinTable(name = "ConflictingChunk_LeftKindConflict",
            joinColumns = @JoinColumn(name = "ConflictingChunk_ID"),
            inverseJoinColumns = @JoinColumn(name = "LeftKindConflict_ID"))
    private KindConflict leftKindConflict;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinTable(name = "ConflictingChunk_RightKindConflict",
            joinColumns = @JoinColumn(name = "ConflictingChunk_ID"),
            inverseJoinColumns = @JoinColumn(name = "RightKindConflict_ID"))
    private KindConflict rightKindConflict;

    @ElementCollection
    @Lob
    private List<String> conflictingContent;
    @ElementCollection
    @Lob
    private List<String> solutionContent;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getBeginLine() {
        return beginLine;
    }

    public void setBeginLine(int beginLine) {
        this.beginLine = beginLine;
    }

    public int getEndLine() {
        return endLine;
    }

    public void setEndLine(int endLine) {
        this.endLine = endLine;
    }

    public int getSeparatorLine() {
        return separatorLine;
    }

    public void setSeparatorLine(int separatorLine) {
        this.separatorLine = separatorLine;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public DeveloperDecision getDeveloperDecision() {
        return developerDecision;
    }

    public void setDeveloperDecision(DeveloperDecision developerChoice) {
        this.developerDecision = developerChoice;
    }

    public List<String> getConflictingContent() {
        return conflictingContent;
    }

    public void setConflictingContent(List<String> conflictingContent) {
        this.conflictingContent = conflictingContent;
    }

    public List<String> getSolutionContent() {
        return solutionContent;
    }

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
//        try {
//            List fileConflict = FileUtils.readLines(new File(initialPath));
//            List fileSolution = FileUtils.readLines(new File(finalPath));
//            System.out.println(context2Original + " => " + context2);
//            System.out.println("\t" + fileConflict.get(context2Original - 1));
//            System.out.println("\t" + fileSolution.get(context2 - 1));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        
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

//        try {
//            List fileConflict = FileUtils.readLines(new File(initialPath));
//            List fileSolution = FileUtils.readLines(new File(finalPath));
//            System.out.println(context1Original + " => " + context1);
//            System.out.println("\t" + fileConflict.get(context1Original - 1));
//            System.out.println("\t" + fileSolution.get(context1 - 1));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        return context1;
    }

    public KindConflict getLeftKindConflict() {
        return leftKindConflict;
    }

    public void setLeftKindConflict(KindConflict leftKindConflict) {
        this.leftKindConflict = leftKindConflict;
    }

    public KindConflict getRightKindConflict() {
        return rightKindConflict;
    }

    public void setRightKindConflict(KindConflict rightKindConflict) {
        this.rightKindConflict = rightKindConflict;
    }
    
    public List<String> generalKindConflict(){
        
        List<LanguageConstruct> leftFiltered = this.getLeftKindConflict().getFilteredLanguageConstructs();
        List<LanguageConstruct> rightFiltered = this.getRightKindConflict().getFilteredLanguageConstructs();
        
        if(leftFiltered == null || rightFiltered == null)
            return new ArrayList<>();
        
        List<String> result = new ArrayList<>();
        
        for (LanguageConstruct lf : leftFiltered) {
            String translatedName = ASTTranslator.translate(lf.getName());
            if (!result.contains(translatedName)) {
                result.add(translatedName);
            }
        }

        for (LanguageConstruct rf : rightFiltered) {
            
            String translatedName = ASTTranslator.translate(rf.getName());

            if (!result.contains(translatedName)) {
                result.add(translatedName);
            }
        }

        Collections.sort(result);
        
        return result;
    }
}
