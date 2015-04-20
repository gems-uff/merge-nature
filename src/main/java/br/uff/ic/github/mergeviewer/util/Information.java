/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.github.mergeviewer.util;

import java.io.File;

/**
 *
 * @author Gleiph
 */
public class Information {

    public static String BASE_REVISION = null;
    public static String LEFT_REVISION = null;
    public static String RIGHT_REVISION = null;
    public static String DEVELOPER_MERGE_REVISION = null;

    public static String STATUS = null;

    public static final String BASE_REPOSITORY_SUFIX = "clone" + File.separator + "base";
    public static final String LEFT_REPOSITORY_SUFIX = "clone" + File.separator + "left";
    public static final String RIGHT_REPOSITORY_SUFIX = "clone" + File.separator + "right";
    public static final String DEVELOPER_MERGE_REPOSITORY_SUFIX = "clone" + File.separator + "developerMerge";

}
