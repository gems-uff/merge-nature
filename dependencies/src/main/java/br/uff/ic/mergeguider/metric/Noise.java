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
public class Noise {
    
    private static final int INVALID = -1;
    
    public static double noise(List<ConflictingChunkInformation> sequence, MergeDependency dependencies){
        double noise = 0;
        double counter = 0;
        
        for (ConflictingChunkInformation chunk : sequence) {
            
            if(Assistance.chunkAssistance(chunk, sequence, dependencies) == 0)
                continue;
            
            double noiseChunk = noiseChunks(chunk, sequence, dependencies);
            if(noiseChunk != INVALID){
                noise += noiseChunk;
                counter += 1;
            }
        }
        
        if(counter > 0)
            return noise/counter;
        else return INVALID;
        
    }
    
    public static double chunkNoise(ConflictingChunkInformation chunk,
            List<ConflictingChunkInformation> sequence, MergeDependency dependencies) {

        if(Assistance.chunkAssistance(chunk, sequence, dependencies) == 0)
            return INVALID;
        
        return noiseChunks(chunk, sequence, dependencies)/
                (double)(chunkPosition(chunk, sequence)-positionFirstChunk(chunk, sequence, dependencies));
        

    }
    
    private static int positionFirstChunk(ConflictingChunkInformation chunk,
            List<ConflictingChunkInformation> sequence, MergeDependency dependencies){
        for (int i = 0; i < sequence.size(); i++) {
            if(dependencies.getDependencies(chunk).contains(sequence.get(i)))
                return i;
        }
        
        return INVALID;
    }
    
    private static int chunkPosition(ConflictingChunkInformation chunk,
            List<ConflictingChunkInformation> sequence){
        return sequence.indexOf(chunk);
    }
 
    private static double noiseChunks(ConflictingChunkInformation chunk,
            List<ConflictingChunkInformation> sequence, MergeDependency dependencies){
        int result = 0;
        
        int positionFirstChunk = positionFirstChunk(chunk, sequence, dependencies);
        int chunkPosition = chunkPosition(chunk, sequence);
        
        for (int i = positionFirstChunk; i < chunkPosition; i++) {
            if(!dependencies.getDependencies(chunk).contains(sequence.get(i)))
                result++;
        }
        return (double)result/(chunkPosition-positionFirstChunk);
    }
    
}
