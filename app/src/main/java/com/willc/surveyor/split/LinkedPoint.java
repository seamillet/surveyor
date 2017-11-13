package com.willc.surveyor.split;

import srs.Geometry.IPoint;

/**
 * Created by stg on 17/11/6.
 */
public class LinkedPoint {

    private IPoint mPoint = null;
    private boolean isIntersection = false;
    private boolean isNextIntersection = false;
    private boolean isPreIntersection = false;
    private int index = -1;
    private int next = -1;
    private int pre = -1;
    private int nextIntsectIndex = -1;
    private int preIntsectIndex = -1;

    public void setPoint(IPoint point) {
        mPoint = point;
    }

    public IPoint getPoint() {
        return mPoint;
    }

    public double getX() {
        return mPoint.X();
    }

    public double getY() {
        return mPoint.Y();
    }

    /**
     * @return the index
     */
    public int getIndex() {
        return index;
    }

    /**
     * @param index
     *            the index to set
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * @return the next
     */
    public int getNext() {
        return next;
    }

    /**
     * @param next
     *            the next to set
     */
    public void setNext(int next) {
        this.next = next;
    }

    /**
     * @return the pre
     */
    public int getPre() {
        return pre;
    }

    /**
     * @param pre
     *            the pre to set
     */
    public void setPre(int pre) {
        this.pre = pre;
    }

    /**
     * @return the isIntersect
     */
    public boolean isIntersection() {
        return isIntersection;
    }

    /**
     * @param isIntersect
     *            the isIntersect to set
     */
    public void setIsIntersection(boolean isIntersect) {
        this.isIntersection = isIntersect;
    }

    /**
     * @return the isNextIntersection
     */
    public boolean isNextIntersection() {
        return isNextIntersection;
    }

    /**
     * @param isNextIntersection
     *            the isNextIntersection to set
     */
    public void setIsNextIntersection(boolean isNextIntersection) {
        this.isNextIntersection = isNextIntersection;
    }

    /**
     * @return the isPreIntersection
     */
    public boolean isPreIntersection() {
        return isPreIntersection;
    }

    /**
     * @param isPreIntersection
     *            the isPreIntersection to set
     */
    public void setIsPreIntersection(boolean isPreIntersection) {
        this.isPreIntersection = isPreIntersection;
    }

    /**
     * @return the nextIntsectIndex
     */
    public int getNextIntsectIndex() {
        return nextIntsectIndex;
    }

    /**
     * @param nextIntsectIndex
     *            the nextIntsectIndex to set
     */
    public void setNextIntsectIndex(int nextIntsectIndex) {
        this.nextIntsectIndex = nextIntsectIndex;
    }

    /**
     * @return the preIntsectIndex
     */
    public int getPreIntsectIndex() {
        return preIntsectIndex;
    }

    /**
     * @param preIntsectIndex
     *            the preIntsectIndex to set
     */
    public void setPreIntsectIndex(int preIntsectIndex) {
        this.preIntsectIndex = preIntsectIndex;
    }

}
