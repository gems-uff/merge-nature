/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.github.mergeviewer.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author gleiph
 */
public class ContextFinder {

    public static List<Context> getBegin(List<String> pattern, List<String> text) {
        List<Context> result = new ArrayList<>();
        List<String> p = pattern.subList(0, pattern.size());
        Context c;

        while (result.isEmpty() && p.size() > 0) {
            for (int i = 0; i < text.size(); i++) {
                if (text.get(i).equals(p.get(0))) {
                    int upper = i + p.size();

                    if (upper >= text.size()) {
                        upper = text.size();
                    }

                    List<String> subList = text.subList(i, upper);

                    if (p.equals(subList)) {
                        c = new Context(i, p.size());
                        result.add(c);
                    }
                }
            }

            p = p.subList(1, p.size());
        }

        return result;
    }

    private static List<Context> getEnd(List<String> pattern, List<String> text) {
        List<Context> result = new ArrayList<>();
        List<String> p = pattern.subList(0, pattern.size());
        Context c;

        while (result.isEmpty() && p.size() > 0) {
            for (int i = 0; i < text.size(); i++) {
                if (text.get(i).equals(p.get(0))) {

                    int upper = i + p.size();

                    if (upper > text.size()) {
                        upper = text.size();
                    }

                    List<String> subList = text.subList(i, upper);

                    if (p.equals(subList)) {
                        c = new Context(i + p.size(), p.size());
                        result.add(c);
                    }
                }
            }

            p = p.subList(0, p.size() - 1);
        }

        return result;
    }

    public static List<String> getSolution(List<String> patternBegin, List<String> patternEnd, List<String> file, int conflictCkunkBegin, int conflictCkunkEnd) {

        List<Context> begin = getBegin(patternBegin, file);

        List<Context> end;

        if (patternEnd != null) {
            end = getEnd(patternEnd, file);
        } else {
            end = new ArrayList<>();

            end.add(new Context(file.size(), 0));
        }

        int lower = 0, upper = 0;
        if (begin.size() == 1) {
            lower = begin.get(0).getLineNumber();
        } else {
            if (end.size() == 1) {
                int difference = Integer.MAX_VALUE;
                for (Context b : begin) {
                    if (end.get(0).getLineNumber() - b.getLineNumber()
                            < difference && end.get(0).getLineNumber() - b.getLineNumber() > 0) {
                        lower = b.getLineNumber();
                        difference = end.get(0).getLineNumber() - b.getLineNumber();
                    }

                }
            } else {
                int lowerSize = 0;
                int difference = Integer.MAX_VALUE;
                for (Context b : begin) {
                    int d = Math.abs(conflictCkunkBegin - b.getLineNumber());
                    if (d < difference) {
                        difference = d;
                        lower = b.getLineNumber();
                        lowerSize = b.getSize();
                    }
                }

                difference = Integer.MAX_VALUE;

                for (Context e : end) {
                    int d = Math.abs(conflictCkunkEnd - e.getLineNumber());
                    if (d < difference && e.getLineNumber() > lower && d >= e.getSize() + lowerSize) {
                        difference = d;
                        upper = e.getLineNumber();
                    }
                }

//                System.out.println("Not implemented");
            }
        }

        if (end.size() == 0) {
            upper = file.size();
        } else if (end.size() == 1) {
            upper = end.get(0).getLineNumber();
        } else {
            if (begin.size() == 1) {
                int difference = Integer.MAX_VALUE;
                for (Context e : end) {
                    if (e.getLineNumber() - begin.get(0).getLineNumber()
                            < difference && e.getLineNumber() - begin.get(0).getLineNumber() > 0) {
                        upper = e.getLineNumber();
                        difference = e.getLineNumber() - begin.get(0).getLineNumber();
                    }

                }
            } else {

                int lowerSize = 0;
                int difference = Integer.MAX_VALUE;
                for (Context b : begin) {
                    int d = Math.abs(conflictCkunkBegin - b.getLineNumber());
                    if (d < difference) {
                        difference = d;
                        lower = b.getLineNumber();
                        lowerSize = b.getSize();
                    }
                }

                difference = Integer.MAX_VALUE;

                for (Context e : end) {
                    int d = Math.abs(conflictCkunkEnd - e.getLineNumber());
                    if (d < difference && e.getLineNumber() > lower && d >= e.getSize() + lowerSize) {
                        difference = d;
                        upper = e.getLineNumber();
                    }
                }
//                System.out.println("Not implemented");
            }
        }

        if (begin.size() > 1 && end.size() > 1) {
            int lowerBegin = begin.get(0).getLineNumber();
            int upperEnd = end.get(end.size() - 1).getLineNumber();

            List<Context> remove = new ArrayList<>();

            for (Context b : begin) {
                if (b.getLineNumber() > upperEnd) {
                    remove.add(b);
                }
            }

            begin.removeAll(remove);

            remove.clear();

            for (Context e : end) {
                if (e.getLineNumber() < lowerBegin) {
                    remove.add(e);
                }
            }

            end.removeAll(remove);

            int lowerSize = 0;
            int difference = Integer.MAX_VALUE;
            for (Context b : begin) {
                int d = Math.abs(conflictCkunkBegin - b.getLineNumber());
                if (d < difference) {
                    difference = d;
                    lower = b.getLineNumber();
                    lowerSize = b.getSize();
                }
            }

            difference = Integer.MAX_VALUE;

            for (Context e : end) {
                int d = Math.abs(conflictCkunkEnd - e.getLineNumber());
                if (d < difference && e.getLineNumber() > lower /*&& d >= e.getSize() + lowerSize*/) {
                    difference = d;
                    upper = e.getLineNumber();
                }
            }

        }

        if (lower <= upper) {
            return file.subList(lower, upper);
        } else {
            return null;
        }
    }

}
