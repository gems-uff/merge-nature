/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.uff.ic.github.mergeviewer.util;

import java.util.List;

/**
 *
 * @author Gleiph
 */
public class OutputParser {
    
    public static boolean isConflict(List<String> mergeMessage) {
        String MERGE_FAIL_MESSAGE = "Automatic merge failed";

        for (String line : mergeMessage) {
            if (line.contains(MERGE_FAIL_MESSAGE)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isFastForward(List<String> mergeMessage) {
        String MERGE_FAST_FORWARD_MESSAGE = "Fast-forward";

        for (String line : mergeMessage) {
            if (line.contains(MERGE_FAST_FORWARD_MESSAGE)) {
                return true;
            }
        }
        return false;
    }
}
