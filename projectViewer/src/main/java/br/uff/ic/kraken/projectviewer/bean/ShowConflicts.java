/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.kraken.projectviewer.bean;

import br.uff.ic.kraken.projectviewer.utils.Navigation;
import java.io.Serializable;
import javax.faces.bean.RequestScoped;
import javax.inject.Named;

/**
 *
 * @author gleiph
 */

@Named(value = "showConflictingChunkBean")
@RequestScoped
public class ShowConflicts extends Navigation implements Serializable {
 
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
}
