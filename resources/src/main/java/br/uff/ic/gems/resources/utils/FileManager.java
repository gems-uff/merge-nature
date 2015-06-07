/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.gems.resources.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Gleiph
 */
public class FileManager {

    public static Writer createWriter(String path) {

        Writer out = null;
        try {
            FileOutputStream fileOutput = new FileOutputStream(path, false);
            OutputStreamWriter outputWriter = new OutputStreamWriter(fileOutput, Charset.forName("UTF-8"));

            out = new BufferedWriter(outputWriter);
        } catch (IOException ex) {
            Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        return out;
    }

    public static boolean write(String text, Writer out) {
        try {

            out.write(text);
        } catch (IOException ex) {
            Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

        return true;
    }

    public static boolean write(List<Integer> text, Writer out) {
        try {

            for (Integer i : text) {

                out.write(i + " ");
            }
            out.write("\n");
        } catch (IOException ex) {
            Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

        return true;
    }

    public static boolean closeWriter(Writer out) {
        try {
            out.close();
        } catch (IOException ex) {
            Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

        return true;
    }

    public static Reader createReader(String path) {
        try {
            FileReader fileReader = new FileReader(path);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            return bufferedReader;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public static int numberOfLines(String path) {
        int numberOfLines = 0;

        BufferedReader bufferedReader = null;

        try {
            bufferedReader = new BufferedReader(new FileReader(path));
            while (bufferedReader.readLine() != null) {
                numberOfLines++;
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                bufferedReader.close();
            } catch (IOException ex) {
                Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return numberOfLines;
    }

    public static List<Integer> nextLine(BufferedReader reader) throws IOException {
        List<Integer> currentLine = null;

        String lineContent = reader.readLine();
        if (lineContent != null && !lineContent.equals("\n")) {
            String[] split = lineContent.split(" ");
            currentLine = new ArrayList<Integer>();
            for (String position : split) {
                currentLine.add(Integer.parseInt(position));
            }
        }

        return currentLine;
    }

}
