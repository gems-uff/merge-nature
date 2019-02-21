/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.coupling;

import br.uff.ic.gems.resources.ast.ASTTranslator;
import br.uff.ic.gems.resources.ast.ASTTypes;
import br.uff.ic.gems.resources.data.KindConflict;
import br.uff.ic.gems.resources.data.LanguageConstruct;
import br.uff.ic.gems.resources.operation.Operation;
import br.uff.ic.gems.resources.repositioning.Repositioning;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.OneToOne;

/**
 *
 * @author Gleiph, Cristiane
 */
@Entity
public class Chunk implements Serializable {

    @Id
    private Long id;
    private String identifier;
    private List<Operation> operations;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Operation> getOperations() {
        return operations;
    }

    public void setOperations(List<Operation> operations) {
        this.operations = operations;
    }
    
    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
}
