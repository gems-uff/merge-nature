/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.mergeguider.languageConstructs;

import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;

/**
 *
 * @author gleiph
 */
public class MyAnnotationUsage {

    private MarkerAnnotation markerAnnotation;
    private NormalAnnotation normalAnnotation;
    private SingleMemberAnnotation singleMemberAnnotation;
    
    private Location location;

    public MyAnnotationUsage(MarkerAnnotation markerAnnotation, Location location) {
        this.markerAnnotation = markerAnnotation;
        this.location = location;
    }

    public MyAnnotationUsage(NormalAnnotation normalAnnotation, Location location) {
        this.normalAnnotation = normalAnnotation;
        this.location = location;
    }

    public MyAnnotationUsage(SingleMemberAnnotation singleMemberAnnotation, Location location) {
        this.singleMemberAnnotation = singleMemberAnnotation;
        this.location = location;
    }

    /**
     * @return the markerAnnotation
     */
    public MarkerAnnotation getMarkerAnnotation() {
        return markerAnnotation;
    }

    /**
     * @param markerAnnotation the markerAnnotation to set
     */
    public void setMarkerAnnotation(MarkerAnnotation markerAnnotation) {
        this.markerAnnotation = markerAnnotation;
    }

    /**
     * @return the normalAnnotation
     */
    public NormalAnnotation getNormalAnnotation() {
        return normalAnnotation;
    }

    /**
     * @param normalAnnotation the normalAnnotation to set
     */
    public void setNormalAnnotation(NormalAnnotation normalAnnotation) {
        this.normalAnnotation = normalAnnotation;
    }

    /**
     * @return the singleMemberAnnotation
     */
    public SingleMemberAnnotation getSingleMemberAnnotation() {
        return singleMemberAnnotation;
    }

    /**
     * @param singleMemberAnnotation the singleMemberAnnotation to set
     */
    public void setSingleMemberAnnotation(SingleMemberAnnotation singleMemberAnnotation) {
        this.singleMemberAnnotation = singleMemberAnnotation;
    }

    /**
     * @return the location
     */
    public Location getLocation() {
        return location;
    }

    /**
     * @param location the location to set
     */
    public void setLocation(Location location) {
        this.location = location;
    }
    
}
