/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.gems.merge.operation;

/**
 *
 * @author gleiph
 */
public abstract class Operation {
    private int line;
    private int size;
    private OperationType type;

    /**
     * @return the line
     */
    public int getLine() {
        return line;
    }

    /**
     * @param line the line to set
     */
    public void setLine(int line) {
        this.line = line;
    }

    /**
     * @return the size
     */
    public int getSize() {
        return size;
    }

    /**
     * @param size the size to set
     */
    public void setSize(int size) {
        this.size = size;
    }

    /**
     * @return the type
     */
    public OperationType getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(OperationType type) {
        this.type = type;
    }
    
    @Override
    public abstract String toString();
    
}
