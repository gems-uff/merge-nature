/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.coupling;

import br.uff.ic.gems.resources.operation.Add;
import br.uff.ic.gems.resources.operation.Operation;
import br.uff.ic.gems.resources.operation.OperationType;
import br.uff.ic.gems.resources.operation.Remove;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Gleiph, Cristiane
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
        int initialline, iteratorRemove = 0;

        //-----------------------------------------------
        //Breaking the input string (delta) into lines
        //-----------------------------------------------
        String OS = System.getProperty("os.name");

        String[] deltaLines = null;

        if (delta.contains(", ")) {
            deltaLines = delta.split(", ");
        } else if  (delta.contains("\n")) {
            deltaLines = delta.split("\n");
        }

        //--------------------------------------------------------
        //Reading the delta content to transform into OO structure
        //--------------------------------------------------------
        for (String line : deltaLines) {

            //--------------------------------------------------------
            //Removing whitespace in the begin of the line
            //--------------------------------------------------------
//            line = removingWhitespace(line);//work it better
            if (line.startsWith(LINEINTERVAL)) {
                //read the interval line
                initialline = initialLine(line);
                if (initialline == 0) {//it is a added file
                    initialline = 1;
                }
//                iteratorAdd = initialline;
                iteratorRemove = initialline;

            } else if (isAddedLine(line)) {
                Operation operation = getOperation(line, OperationType.ADD, iteratorRemove);
                result.add(operation);
//                iteratorAdd++;
                  iteratorRemove++; 

            } else if (isRemovedLine(line)) {
                Operation operation = getOperation(line, OperationType.REMOVE, iteratorRemove);
                result.add(operation);
                iteratorRemove++;

            } else {

//                iteratorAdd++;
                iteratorRemove++;

            }
        }

        return result;
    }

    public List<Operation> cluster(List<Operation> operations) {
        List<Operation> result = new ArrayList<>();

        for (int i = 0; i < operations.size(); i++) {

            Operation currentOperation = operations.get(i);
            int currentLine = currentOperation.getLine();
            int currentSize = currentOperation.getSize();

            for (int j = i + 1; j < operations.size(); j++) {
                Operation operation = operations.get(j);

                if (currentOperation.getType() == operation.getType()
                        && operation.getLine() == currentLine) {
                    currentSize++;
                    i++;
                }

            }

            if (currentOperation.getType() == OperationType.ADD) {
                Add add = new Add();
                add.setLine(currentOperation.getLine());
                add.setSize(currentSize);
                result.add(add);
            } else if (currentOperation.getType() == OperationType.REMOVE) {
                Remove remove = new Remove();
                remove.setLine(currentOperation.getLine());
                remove.setSize(currentSize);
                result.add(remove);
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

        if (kind == OperationType.ADD) {
            result = new Add();
            result.setLine(iterator);
            result.setSize(1);

        } else if (kind == OperationType.REMOVE) {
            result = new Remove();
            result.setLine(iterator);
            result.setSize(1);

        }

        return result;
    }
    
    public static Operation getOperations(OperationType kind, int size, int iterator) {

        Operation result = null;

        if (kind == OperationType.ADD) {
            result = new Add();
            result.setLine(iterator);
            result.setSize(size);

        } else if (kind == OperationType.REMOVE) {
            result = new Remove();
            result.setLine(iterator);
            result.setSize(size);

        }

        return result;
    }


}
