/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.uff.ic.github.mergeviewer.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Gleiph
 */
public class ReadFile {
    
    public static List<String> read(String path) throws FileNotFoundException, IOException {
        
        List<String> result = new ArrayList<>();
        
        BufferedReader bufferedReader = new BufferedReader(new FileReader(path));
        String line = null;
        
        while ((line = bufferedReader.readLine()) != null) {            
            result.add(line);
        }
        return result;
    }
    
}
