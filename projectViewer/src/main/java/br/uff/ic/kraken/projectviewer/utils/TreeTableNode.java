/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.kraken.projectviewer.utils;

/**
 *
 * @author gleiph
 */
public class TreeTableNode {

    private String name;
    private Long id;
    private String dataType;

    public TreeTableNode(String name, Long id, String dataType) {
        this.name = name;
        this.id = id;
        this.dataType = dataType;
    }
    
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

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

    
}

