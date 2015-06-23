/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.kraken.projectviewer.bean;

import br.uff.ic.gems.resources.data.LanguageConstruct;
import br.uff.ic.kraken.projectviewer.utils.Navigation;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.faces.bean.RequestScoped;
import javax.inject.Named;

/**
 *
 * @author gleiph
 */
@Named(value = "showConflictsBean")
@RequestScoped
public class ShowConflictsBean extends Navigation implements Serializable {
 
    private Long id;
    private String dataType;

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the dataType
     */
    public String getDataType() {
        return dataType;
    }

    /**
     * @param dataType the dataType to set
     */
    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
    
    public String navigate(){
        return super.dataNavigation(id, dataType);
    }
    
    public List<String> getGeneralKindOfConflict(List<LanguageConstruct> leftFiltered, List<LanguageConstruct> rightFiltered){
        
        if(leftFiltered == null || rightFiltered == null)
            return new ArrayList<>();
        
        List<String> result = new ArrayList<>();
        
        for (LanguageConstruct lf : leftFiltered) {
            if(!result.contains(lf.getName()))
                result.add(lf.getName());
        }
        
        for (LanguageConstruct rf : rightFiltered) {
            if(!result.contains(rf.getName()))
                result.add(rf.getName());
        }

        Collections.sort(result);
        
        return result;
    }
}
