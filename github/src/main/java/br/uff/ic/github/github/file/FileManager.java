package br.uff.ic.github.github.file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Gleiph
 */
public class FileManager {

    private String path;
    private boolean replace;
    private FileWriter fileWriter;
    private BufferedWriter bufferedWriter;

    public FileManager(String path, boolean replace) {
        this.path = path;
        this.replace = replace;
    }

    public void open() throws IOException {
        fileWriter = new FileWriter(new File(path), !replace);
        bufferedWriter = new BufferedWriter(fileWriter);
    }

    public void writeln(String content) {
        try {
            bufferedWriter.write(content);
            bufferedWriter.flush();
            bufferedWriter.newLine();
        } catch (IOException ex) {
            Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void close() {
        try {
            bufferedWriter.close();
            fileWriter.close();
        } catch (IOException ex) {
            Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * @param path the path to set
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * @return the replace
     */
    public boolean isReplace() {
        return replace;
    }

    /**
     * @param replace the replace to set
     */
    public void setReplace(boolean replace) {
        this.replace = replace;
    }

    public List<String> readFile() {
        List<String> result = new ArrayList<>();

        try {
            return FileUtils.readLines(new File(path));
        } catch (IOException ex) {
            Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        return result;
    }
}
