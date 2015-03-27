/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.gems.merge.diff.translator;

import br.uff.ic.gems.merge.operation.Add;
import br.uff.ic.gems.merge.operation.Operation;
import br.uff.ic.gems.merge.operation.OperationType;
import br.uff.ic.gems.merge.operation.Remove;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Gleiph
 * @since Apr 17, 2013
 *
 */
public class GitTranslator {

    private static String DELTABEGIN = "diff --git a/";
    private static String LINEINTERVAL = "@@";
    private static String DELETEDFILE = "deleted file mode";
    private static String NEWFILE = "new file mode";
    private static String REMOVEDLINE = "-";
    private static String ADDEDLINE = "+";
    private static String FROMFILE = "---";
    private static String TOFILE = "+++";

    /**
     * Transforms a Git textual delta into an raw OO structure.
     *
     * @param delta
     * @return
     */
    public List<Operation> translateDelta(String delta) {
        //-----------------------------------------------
        //Variables used in the method
        //-----------------------------------------------
        String filename = "";
        List<Operation> result = new ArrayList<>();
        int initialline, iteratorAdd = 0, iteratorRemove = 0;

        //-----------------------------------------------
        //Breaking the input string (delta) into lines
        //-----------------------------------------------
        String[] deltaLines = delta.split("\n");

        //--------------------------------------------------------
        //Reading the delta content to transform into OO structure
        //--------------------------------------------------------
        for (String line : deltaLines) {

            //--------------------------------------------------------
            //Removing whitespace in the begin of the line
            //--------------------------------------------------------
            line = removingWhitespace(line);//work it better

            if (line.startsWith(LINEINTERVAL)) {
                //read the interval line
                initialline = initialLine(line);
                if (initialline == 0) {//it is a added file
                    initialline = 1;
                }
                iteratorAdd = initialline;
                iteratorRemove = initialline;

            } else if (isAddedLine(line)) {
                Operation operation = getOperation(line, OperationType.ADD, iteratorAdd);
                result.add(operation);
                iteratorAdd++;

            } else if (isRemovedLine(line)) {
                Operation operation = getOperation(line, OperationType.REMOVE, iteratorRemove);
                result.add(operation);
                iteratorRemove++;

            } else {

                iteratorAdd++;
                iteratorRemove++;

            }
        }

        return result;
    }

    private String getFilename(String line) {
        line = line.replaceFirst(DELTABEGIN, "");
        String[] words = line.split(" ");
        return words[0];
    }

    private String removingWhitespace(String line) {
        while (line.startsWith(" ")) {
            line = line.replaceFirst(" ", "");
        }

        return line;
    }

    private boolean isAddedLine(String line) {
        return (line.startsWith(ADDEDLINE) && (!line.startsWith(TOFILE)));
    }

    private boolean isRemovedLine(String line) {
        return (line.startsWith(REMOVEDLINE) && (!line.startsWith(FROMFILE)));
    }

    private int initialLine(String line) {

        line = line.replaceFirst("@@ -", "");
        String[] intervals = line.split("\\+");
        String[] limits = intervals[0].split(",");

        return Integer.parseInt(limits[0].replace(" ", ""));
    }

    private Operation getOperation(String line, OperationType kind, int iterator) {

        Operation result = null;
        String content = null;

        if (kind == OperationType.ADD) {
            result = new Add();
            result.setLine(iterator);

        } else if (kind == OperationType.REMOVE) {
            result = new Remove();
            result.setLine(iterator);
        }

        return result;
    }

}
