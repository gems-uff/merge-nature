/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.gems.resources.analises.merge;

import br.uff.ic.gems.resources.states.DeveloperDecision;
import br.uff.ic.gems.resources.utils.ConflictPartsExtractor;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gleiph
 */
public class DeveloperDecisionAnalyzer {

    public static int countBegin;

    /**
     * 
     * @param cpe
     * @param solution
     * @param context
     * @return 
     * @deprecated 
     * use public static DeveloperDecision developerDevision(List<String> conflictingChunkContent, List<String> solutionContent, int beginLine, int separatorLine, int endLine) throws Exception 
     */
    public static DeveloperDecision getDeveloperDecision(ConflictPartsExtractor cpe, List<String> solution, int context) {

        if (solution == null) {
            return DeveloperDecision.MANUAL;
        }

        List<String> beginContext, endContext, leftConflict, rightConflict;

        beginContext = cpe.getBeginContext();
        leftConflict = cpe.getLeftConflict();
        rightConflict = cpe.getRightConflict();
        endContext = cpe.getEndContext();

        int beginSolution = 0, endSolution = 0;
        if (beginContext != null && !beginContext.isEmpty()) {
            beginSolution = getIndexFromBegin(solution, beginContext);
        } else {
            beginSolution = 0;
        }

        if (endContext != null && !endContext.isEmpty()) {
            endSolution = getIndexFromEnd(solution, endContext.get(0));
        } else {
            endSolution = solution.size();
        }

        List<String> solutionClean;
        if (beginSolution + 1 == endSolution) {
            if (countBegin < context) {
                solutionClean = solution.subList(beginSolution, endSolution);
            } else if (cpe.getBeginContext().size() + cpe.getEndContext().size() != solution.size()) {
                solutionClean = solution.subList(beginSolution, endSolution);
            } else {
                solutionClean = solution.subList(beginSolution + 1, endSolution);
            }

        } else if (beginSolution + 1 < endSolution && beginSolution == 0 && cpe.getBeginContext().isEmpty()) {
            solutionClean = solution.subList(beginSolution, endSolution);
        } else if (beginSolution + 1 < endSolution) {
            solutionClean = solution.subList(beginSolution + 1, endSolution);
        } else {
            return DeveloperDecision.MANUAL;
        }

        if (comparison(solutionClean, leftConflict)) {
            return DeveloperDecision.VERSION1;
        } else if (comparison(solutionClean, rightConflict)) {
            return DeveloperDecision.VERSION2;
        } else if (isConcatenation(leftConflict, rightConflict, solutionClean, endContext)) {
            return DeveloperDecision.CONCATENATION;
        } else if (isCombination(leftConflict, rightConflict, solutionClean)) {
            return DeveloperDecision.COMBINATION;
        } else {
            return DeveloperDecision.MANUAL;
        }
    }

    private static boolean comparison(List<String> list1, List<String> list2) {
        if (list1.size() != list2.size()) {
            return false;
        }

        for (int i = 0; i < list1.size(); i++) {
            if (!list1.get(i).equals(list2.get(i))) {
                return false;
            }
        }

        return true;
    }

    private static boolean isCombination(List<String> version1, List<String> version2, List<String> solution) {
        //TODO implement

        for (String line : solution) {

            if (!version1.contains(line) && !version2.contains(line)) {
                return false;
            }

        }

        return true;
    }

    private static boolean isConcatenation(List<String> version1, List<String> version2, List<String> solution, List<String> end) {

        List<String> aux;

        if (solution.equals(concatLists(version1, version2))) {
            return true;
        } else if (solution.equals(concatLists(version2, version1))) {
            return true;
        } else if (end != null && !end.isEmpty()) {
            for (int i = 1; i < end.size(); i++) {
                aux = end.subList(0, i);
                if (solution.equals(concatLists(concatLists(version1, aux), version2))) {
                    return true;
                } else if (solution.equals(concatLists(concatLists(version2, aux), version1))) {
                    return true;
                }
            }
        }

        return false;
    }

    private static List<String> subList(List<String> list, int lower, int upper) {

        if (lower >= upper) {
            return new ArrayList<>();
        } else {
            return list.subList(lower, upper);
        }
    }

    private static void printList(List<String> list) {
        for (String line : list) {
            System.out.println(line);
        }
    }

    private static List<String> concatLists(List<String> list1, List<String> list2) {
        List<String> result = new ArrayList<>();

        result.addAll(list1);
        result.addAll(list2);

        return result;
    }

    private static int getIndexFromBegin(List<String> text, List<String> context) {
        int result = -1;

        countBegin = 0;

        for (int j = 0; j < context.size() && j < text.size(); j++) {
            for (int k = 0; k < j + 1; k++) {
                if (context.get(j).equals(text.get(k))) {
                    result = j;
                    countBegin++;
                }
            }
        }

        if (result >= text.size() || result < 0) {
            return 0;
        } else {
            return result;
        }
    }

    private static int getIndexFromEnd(List<String> text, String line) {
        int i = 0;

        for (int j = text.size() - 1; j >= 0; j--) {
            if (text.get(j).equals(line)) {
                i = j;
                break;
            }

        }

        if (i <= 0) {
            return text.size() - 1;
        } else {
            return i;
        }
    }

    /**
     * Used to analyze the developer decision 
     * @param conflictingChunkContent
     * @param solutionContent
     * @param beginLine
     * @param separatorLine
     * @param endLine
     * @return
     * @throws Exception 
     */
    
    public static DeveloperDecision developerDevision(List<String> conflictingChunkContent, List<String> solutionContent, int beginLine, int separatorLine, int endLine) throws Exception {

        List<String> context1, version1, version2, context2, solutionClean;
        int solutionCleanBegin = 0, solutionCleanEnd = solutionContent.size();

        boolean passBegin = false, passEnd = false;

        if (beginLine > separatorLine || separatorLine > endLine) {
            throw new Exception("Invalid conflicting chunk content!");
        }

        context1 = conflictingChunkContent.subList(0, beginLine);
        version1 = conflictingChunkContent.subList(beginLine + 1, separatorLine);
        version2 = conflictingChunkContent.subList(separatorLine + 1, endLine);
        context2 = conflictingChunkContent.subList(endLine + 1, conflictingChunkContent.size());

        context1 = cleanFormatList(context1);
        version1 = cleanFormatList(version1);
        version2 = cleanFormatList(version2);
        context2 = cleanFormatList(context2);
        solutionContent = cleanFormatList(solutionContent);

        //Finding the range of cleansolution
        for (int i = 0; i < context1.size(); i++) {
            for (int j = 0; j < context1.size(); j++) {
                if (i >= solutionContent.size() - 1 || j >= solutionContent.size() - 1) {
                    break;
                } else if (context1.get(i).equals(solutionContent.get(j))) {
                    if (j >= solutionCleanBegin) {
                        solutionCleanBegin = j;
                        passBegin = true;
                    }
                }
            }
        }

        for (int i = context2.size() - 1; i >= 0; i--) {
            for (int j = solutionContent.size() - 1; j >= solutionContent.size() - context2.size(); j--) {
                if (i < 0 || j < 0) {
                    break;
                } else if (context2.get(i).equals(solutionContent.get(j))) {
                    if (j <= solutionCleanEnd) {
                        solutionCleanEnd = j;
                        passEnd = true;
                    }
                }
            }
        }

        if (solutionCleanBegin > solutionCleanEnd) {
            throw new Exception("Invalid conflicting chunk content!");
        }

        //Cleaning the solution 
        if (solutionCleanBegin == solutionCleanEnd) {
            solutionClean = solutionContent.subList(solutionCleanBegin, solutionCleanEnd);
        } else if (!passBegin && !passEnd) {
            solutionClean = solutionContent;
        } else if (!passBegin) {
            solutionClean = solutionContent.subList(solutionCleanBegin, solutionCleanEnd);
        } else if (!passEnd) {
            solutionClean = solutionContent.subList(solutionCleanBegin + 1, solutionCleanEnd);
        } else if (solutionContent.size() >= solutionCleanEnd + 1) {
            solutionClean = solutionContent.subList(solutionCleanBegin + 1, solutionCleanEnd);
        } else {
            solutionClean = solutionContent.subList(solutionCleanBegin, solutionCleanEnd);
        }

        //Decising developer decision
        if (isEqual(version1, solutionClean)) {
            return DeveloperDecision.VERSION1;
        } else if (isEqual(version2, solutionClean)) {
            return DeveloperDecision.VERSION2;
        } else if (isNone(version1, version2, solutionClean)) {
            return DeveloperDecision.NONE;
        } else if (isCleanConcatenation(version1, version2, solutionClean, context1, context2)) {
            return DeveloperDecision.CONCATENATION;
        } else if (isCleanCombination(version1, version2, solutionClean)) {
            return DeveloperDecision.COMBINATION;
        } else {
            return DeveloperDecision.MANUAL;
        }
    }

    public static boolean isCleanCombination(List<String> content1, List<String> content2, List<String> solution) {
        List<String> union = new ArrayList<>();
        union.addAll(content1);
        union.addAll(content2);

        for (String line : solution) {
            if (line.equals("")) {
            } else if (!union.contains(line)) {
                return false;
            } else {
                union.remove(line);
            }
        }

        return true;
    }

    public static boolean isCleanConcatenation(List<String> content1, List<String> content2, List<String> solution, List<String> context1, List<String> context2) {
        List<String> aux1 = new ArrayList<>();
        List<String> aux2 = new ArrayList<>();
        List<String> aux3 = new ArrayList<>();
        List<String> aux4 = new ArrayList<>();
        List<String> aux5 = new ArrayList<>();
        List<String> aux6 = new ArrayList<>();
        List<String> aux7 = new ArrayList<>();
        List<String> aux8 = new ArrayList<>();

        //Version 1 + Version 2
        aux1.addAll(content1);
        aux1.addAll(content2);

        //Version 2 + Version 1
        aux2.addAll(content2);
        aux2.addAll(content1);

        //Version 1 + end context 2 + end context 1 + version 2
        if (!context1.isEmpty() && !context2.isEmpty()) {
            aux3.addAll(content1);
            aux3.add(context2.get(0));
            aux3.add(context1.get(context1.size() - 1));
            aux3.addAll(content2);
        }
        //Version 1 + end context 1 + version 2
        if (!context1.isEmpty()) {
            aux4.addAll(content1);
            aux4.add(context1.get(context1.size() - 1));
            aux4.addAll(content2);
        }

        //Version 1 + end context 2 + version 2
        if (!context2.isEmpty()) {
            aux5.addAll(content1);
            aux5.add(context2.get(0));
            aux5.addAll(content2);
        }
        
        //Version 2 + end context 2 + end context 1 + version 1
        if (!context1.isEmpty() && !context2.isEmpty()) {
            aux6.addAll(content2);
            aux6.add(context2.get(0));
            aux6.add(context1.get(context1.size() - 1));
            aux6.addAll(content1);
        }
        //Version 2 + end context 1 + version 1
        if (!context1.isEmpty()) {
            aux7.addAll(content2);
            aux7.add(context1.get(context1.size() - 1));
            aux7.addAll(content1);
        }
        //Version 2 + end context 2 + version 1
        if (!context2.isEmpty()) {
            aux8.addAll(content2);
            aux8.add(context2.get(0));
            aux8.addAll(content1);
        }
        if (isEqual(aux1, solution)) {
            return true;
        } else if (isEqual(aux2, solution)) {
            return true;
        } else if (isEqual(aux3, solution)) {
            return true;
        } else if (isEqual(aux4, solution)) {
            return true;
        } else if (isEqual(aux5, solution)) {
            return true;
        } else if (isEqual(aux6, solution)) {
            return true;
        } else if (isEqual(aux7, solution)) {
            return true;
        } else if (isEqual(aux8, solution)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isEqual(List<String> content1, List<String> content2) {
        String stringContent1 = listTostring(content1);
        String stringContent2 = listTostring(content2);

        return stringContent1.equals(stringContent2);

    }

    public static String listTostring(List<String> list) {
        StringBuilder result = new StringBuilder();

        for (String line : list) {
            result.append(line);
        }

        return result.toString().replaceAll("\n", "");
    }

    public static String cleanFormat(String line) {

        String lineChanged = line;

        lineChanged = lineChanged.trim();
        lineChanged = lineChanged.replaceAll(" ", "");
        lineChanged = lineChanged.replaceAll("\t", "");

        return lineChanged;
    }

    public static List<String> cleanFormatList(List<String> list) {
        List<String> clone = new ArrayList<>(list);
        List<String> result = new ArrayList<>();

        for (String line : clone) {
            result.add(cleanFormat(line));
        }

        return result;
    }

    private static boolean isNone(List<String> version1, List<String> version2, List<String> solutionClean) {
        String version1String = listTostring(version1);
        String version2String = listTostring(version2);
        String solutionCleanString = listTostring(solutionClean);

        if (!version1String.equals("") && !version2String.equals("") && solutionCleanString.equals("")) {
            return true;
        } else {
            return false;
        }
    }
}
