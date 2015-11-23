/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.mergeguider.languageConstructs;

/**
 *
 * @author gleiph
 */
public class Location {

    private final int INVALID_VALUE = -1;

    private int elementLineBegin;
    private int elementLineEnd;
    private int elementColumnBegin;
    private int elementColumnEnd;

    private int bodyLineBegin;
    private int bodyLineEnd;
    private int bodyColumnBegin;
    private int bodyColumnEnd;

    public Location(int elementLineBegin, int elementLineEnd, int elementColumnBegin, int elementColumnEnd) {
        this.elementLineBegin = elementLineBegin;
        this.elementLineEnd = elementLineEnd;
        this.elementColumnBegin = elementColumnBegin;
        this.elementColumnEnd = elementColumnEnd;

        this.bodyLineBegin = INVALID_VALUE;
        this.bodyLineEnd = INVALID_VALUE;
        this.bodyColumnBegin = INVALID_VALUE;
        this.bodyColumnEnd = INVALID_VALUE;
    }

    public Location(int elementLineBegin, int elementLineEnd, int elementColumnBegin, int elementColumnEnd,
            int bodyLineBegin, int bodyLineEnd, int bodyColumnBegin, int bodyColumnEnd) {

        this.elementLineBegin = elementLineBegin;
        this.elementLineEnd = elementLineEnd;
        this.elementColumnBegin = elementColumnBegin;
        this.elementColumnEnd = elementColumnEnd;
        this.bodyLineBegin = bodyLineBegin;
        this.bodyLineEnd = bodyLineEnd;
        this.bodyColumnBegin = bodyColumnBegin;
        this.bodyColumnEnd = bodyColumnEnd;
    }

    /**
     * @return the elementLineBegin
     */
    public int getElementLineBegin() {
        return elementLineBegin;
    }

    /**
     * @param elementLineBegin the elementLineBegin to set
     */
    public void setElementLineBegin(int elementLineBegin) {
        this.elementLineBegin = elementLineBegin;
    }

    /**
     * @return the elementLineEnd
     */
    public int getElementLineEnd() {
        return elementLineEnd;
    }

    /**
     * @param elementLineEnd the elementLineEnd to set
     */
    public void setElementLineEnd(int elementLineEnd) {
        this.elementLineEnd = elementLineEnd;
    }

    /**
     * @return the elementColumnBegin
     */
    public int getElementColumnBegin() {
        return elementColumnBegin;
    }

    /**
     * @param elementColumnBegin the elementColumnBegin to set
     */
    public void setElementColumnBegin(int elementColumnBegin) {
        this.elementColumnBegin = elementColumnBegin;
    }

    /**
     * @return the elementColumnEnd
     */
    public int getElementColumnEnd() {
        return elementColumnEnd;
    }

    /**
     * @param elementColumnEnd the elementColumnEnd to set
     */
    public void setElementColumnEnd(int elementColumnEnd) {
        this.elementColumnEnd = elementColumnEnd;
    }

    /**
     * @return the bodyLineBegin
     */
    public int getBodyLineBegin() {
        return bodyLineBegin;
    }

    /**
     * @param bodyLineBegin the bodyLineBegin to set
     */
    public void setBodyLineBegin(int bodyLineBegin) {
        this.bodyLineBegin = bodyLineBegin;
    }

    /**
     * @return the bodyLineEnd
     */
    public int getBodyLineEnd() {
        return bodyLineEnd;
    }

    /**
     * @param bodyLineEnd the bodyLineEnd to set
     */
    public void setBodyLineEnd(int bodyLineEnd) {
        this.bodyLineEnd = bodyLineEnd;
    }

    /**
     * @return the bodyColumnBegin
     */
    public int getBodyColumnBegin() {
        return bodyColumnBegin;
    }

    /**
     * @param bodyColumnBegin the bodyColumnBegin to set
     */
    public void setBodyColumnBegin(int bodyColumnBegin) {
        this.bodyColumnBegin = bodyColumnBegin;
    }

    /**
     * @return the bodyColumnEnd
     */
    public int getBodyColumnEnd() {
        return bodyColumnEnd;
    }

    /**
     * @param bodyColumnEnd the bodyColumnEnd to set
     */
    public void setBodyColumnEnd(int bodyColumnEnd) {
        this.bodyColumnEnd = bodyColumnEnd;
    }

    @Override
    public String toString() {

        if (bodyColumnBegin == INVALID_VALUE) {
            return "[(" + elementLineBegin + ", " + elementColumnBegin + "), (" + elementLineEnd + ", " + elementColumnEnd + ")]";
        } else {
            return "[(" + elementLineBegin + ", " + elementColumnBegin + "), (" + elementLineEnd + ", " + elementColumnEnd + ")], "
                    + "[(" + bodyLineBegin + ", " + bodyColumnBegin + "), (" + bodyLineEnd + ", " + bodyColumnEnd + ")]";
        }
    }

    public boolean contains(Location location) {

        if (this.elementLineBegin < location.getElementLineBegin()
                && this.elementLineEnd > location.getElementLineEnd()) {
            return true;
        } else if (elementLineBegin == location.getElementLineBegin()
                && elementColumnBegin <= location.getElementColumnBegin()
                && elementLineEnd == location.getElementLineEnd()
                && elementColumnEnd >= location.getElementColumnEnd()) {
            return true;
        } else if (elementLineBegin < location.getElementLineBegin()
                && elementLineEnd == location.getElementLineEnd()
                && elementColumnEnd >= location.getElementColumnEnd()) {
            return true;
        } else if (elementLineBegin == location.getElementLineBegin()
                && elementColumnBegin <= location.getElementColumnBegin()
                && elementLineEnd > location.getElementLineEnd()) {
            return true;
        }
        return false;
    }

}
