package com.meitan.lubov.model.util;

/**
 * @author denis_k
 *         Date: 14.03.2010
 *         Time: 23:55:21
 */
public class PersistentOrderableImpl implements PersistentOrderable {
    private int order;

    @Override
    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
