package br.uff.ic.github.github;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Gleiph
 * @since Jul 18, 2013
 *
 */
public class CMDOutput {

    private List<String> output;
    private List<String> errors;

    public CMDOutput() {
        output = new ArrayList<String>();
        errors = new ArrayList<String>();
    }

    
    
    /**
     * @return the output
     */
    public List<String> getOutput() {
        return output;
    }

    /**
     * @param output the output to set
     */
    public void setOutput(List<String> output) {
        this.output = output;
    }

    /**
     * @return the errors
     */
    public List<String> getErrors() {
        return errors;
    }

    /**
     * @param errors the errors to set
     */
    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
    
    public void addOutput(String line){
        this.output.add(line);
    }
    
    public void addErrors(String line){
        this.errors.add(line);
    }
    
    
    
    
}
