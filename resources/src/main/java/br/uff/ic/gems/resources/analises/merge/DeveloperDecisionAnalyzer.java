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
}
