/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.github.mergeviewer.util;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gleiph
 */
public class DeveloperDecision {

    public static DeveloperChoice getDeveloperDecision(List<String> conflict, List<String> solution) {

        List<String> context1, context2, version1, version2;
        int begin = 0, end = 0, separator = 0;

        for (int i = 0; i < conflict.size(); i++) {
            String line = conflict.get(i);

            if (line.contains("<<<<<<<")) {
                begin = i;
            } else if (line.contains(">>>>>>")) {
                end = i;
            } else if (line.contains("=======")) {
                separator = i;
            }
        }

        context1 = subList(conflict, 0, begin);
        version1 = subList(conflict, begin + 1, separator);
        version2 = subList(conflict, separator + 1, end);
        context2 = subList(conflict, end + 1, conflict.size());

        int beginSolution = 0, endSolution = 0;
        if (context1 != null) {
            beginSolution = getIndexFromBegin(solution, context1);
        } else {
            beginSolution = 0;
        }

        if (context2 != null) {
            endSolution = getIndexFromEnd(solution, context2.get(0));
        } else {
            endSolution = solution.size();
        }

        List<String> solutionClean = solution.subList(beginSolution + 1, endSolution);

        if (solutionClean.equals(version1)) {
            return DeveloperChoice.VERSION1;
        } else if (solutionClean.equals(version2)) {
            return DeveloperChoice.VERSION2;
        } else if (isConcatenation(version1, version2, solutionClean, context2)) {
            return DeveloperChoice.CONCATENATION;
        } else if (isCombination(version1, version2, solution)) {
            return DeveloperChoice.COMBINATION;
        } else {
            return DeveloperChoice.MANUAL;
        }
    }

    public static DeveloperChoice getDeveloperDecision(ConflictPartsExtractor cpe, List<String> solution) {

        if (solution == null) {
            return DeveloperChoice.MANUAL;
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
        if (beginSolution + 1 <= endSolution) {
            solutionClean = solution.subList(beginSolution + 1, endSolution);
        } else {
            return DeveloperChoice.MANUAL;
        }

        if (comparison(solutionClean, leftConflict)) {
            return DeveloperChoice.VERSION1;
        } else if (comparison(solutionClean, rightConflict)) {
            return DeveloperChoice.VERSION2;
        } else if (isConcatenation(leftConflict, rightConflict, solutionClean, endContext)) {
            return DeveloperChoice.CONCATENATION;
        } else if (isCombination(leftConflict, rightConflict, solutionClean)) {
            return DeveloperChoice.COMBINATION;
        } else {
            return DeveloperChoice.MANUAL;
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

        for (int j = 0; j < context.size() && j < text.size(); j++) {
            for (int k = 0; k < j + 1; k++) {
                if(context.get(j).equals(text.get(k)))
                    result = j;
            }
        }

        if (result >= text.size() || result < 0) {
            return 0;
        } else{
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
