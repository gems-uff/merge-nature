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
public class ContextFinder {

    private static List<Integer> getBegin(List<String> pattern, List<String> text) {
        List<Integer> result = new ArrayList<>();
        List<String> p = pattern.subList(0, pattern.size());

        while (result.isEmpty() && p.size() > 0) {
            for (int i = 0; i < text.size(); i++) {
                if (text.get(i).equals(p.get(0))) {
                    int upper = i + p.size();

                    if (upper >= text.size()) {
                        upper = text.size();
                    }

                    List<String> subList = text.subList(i, upper);

                    if (p.equals(subList)) {
                        result.add(i);
                    }
                }
            }

            p = p.subList(1, p.size());
        }

        return result;
    }

    private static List<Integer> getEnd(List<String> pattern, List<String> text) {
        List<Integer> result = new ArrayList<>();
        List<String> p = pattern.subList(0, pattern.size());

        while (result.isEmpty() && p.size() > 0) {
            for (int i = 0; i < text.size(); i++) {
                if (text.get(i).equals(p.get(0))) {

                    int upper = i + p.size();

                    if (upper > text.size()) {
                        upper = text.size();
                    }

                    List<String> subList = text.subList(i, upper);

                    if (p.equals(subList)) {
                        result.add(i + p.size());
                    }
                }
            }

            p = p.subList(1, p.size());
        }

        return result;
    }

    public static List<String> getSolution(List<String> patternBegin, List<String> patternEnd, List<String> file) {

        List<Integer> begin = getBegin(patternBegin, file);

        List<Integer> end;

        if (patternEnd != null) {
            end = getEnd(patternEnd, file);
        } else {
            end = new ArrayList<>();
            end.add(file.size());
        }

        int lower = 0, upper = 0;
        if (begin.size() == 1) {
            lower = begin.get(0);
        } else {
            if (end.size() == 1) {
                int difference = Integer.MAX_VALUE;
                for (Integer b : begin) {
                    if (end.get(0) - b < difference && end.get(0) - b > 0) {
                        lower = b;
                        difference = end.get(0) - b;
                    }

                }
            } else {
                System.out.println("Not implemented");
            }
        }

        if (end.size() == 0) {
            upper = file.size();
        } else if (end.size() == 1) {
            upper = end.get(0);
        } else {
            if (begin.size() == 1) {
                int difference = Integer.MAX_VALUE;
                for (Integer e : end) {
                    if (e - begin.get(0) < difference && e - begin.get(0) > 0) {
                        upper = e;
                        difference = e - begin.get(0);
                    }

                }
            } else {
                System.out.println("Not implemented");
            }
        }

        return file.subList(lower, upper);
    }

}
