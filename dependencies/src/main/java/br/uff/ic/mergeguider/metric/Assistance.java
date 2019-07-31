/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.mergeguider.metric;

import br.uff.ic.mergeguider.datastructure.ConflictingChunkInformation;
import br.uff.ic.mergeguider.datastructure.MergeDependency;
import java.util.List;

/**
 *
 * @author gleiph
 */
public class Assistance {

    public static double chunkAssistance(ConflictingChunkInformation chunk,
            List<ConflictingChunkInformation> sequence, MergeDependency dependencies) {
        double result = 0;

        List<ConflictingChunkInformation> chunksDependencies = dependencies.getDependencies(chunk);

       if(chunksDependencies.isEmpty())
           return 0;
        
        for (ConflictingChunkInformation chunksDependency : sequence) {
            if (chunksDependency.equals(chunk)) {
                break;
            } else if (chunksDependencies.contains(chunksDependency)) {
                result += 1;
            }
        }

        return result / chunksDependencies.size();
    }

    public static double assistance(List<ConflictingChunkInformation> sequence, MergeDependency dependencies) {

        double result = 0;

        if (sequence.size() < 2) {
            return 0;
        }

        for (int i = 1; i < sequence.size(); i++) {
            result += chunkAssistance(sequence.get(i), sequence, dependencies);
        }

        return result / (sequence.size() - 1);
    }

}
