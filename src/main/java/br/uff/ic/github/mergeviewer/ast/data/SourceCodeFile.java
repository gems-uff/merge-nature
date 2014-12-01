/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.github.mergeviewer.ast.data;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gleiph
 */
public class SourceCodeFile {
 
    List<SourceCodeArea> areas;

    public SourceCodeFile() {
        this.areas = new ArrayList<>();
    }
    
    public void add(SourceCodeArea sca){
        this.areas.add(sca);
    }
    
    
    public void add(String structure, int begin, int end){
        
        SourceCodeArea sca = new SourceCodeArea();
        sca.setStructure(structure);
        sca.setBegin(begin);
        sca.setEnd(end);
        
        this.areas.add(sca);
    }
    
    
}
