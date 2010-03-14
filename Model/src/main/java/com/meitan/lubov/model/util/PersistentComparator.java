package com.meitan.lubov.model.util;

import java.util.Comparator;

/**
 * @author denis_k
 *         Date: 14.03.2010
 *         Time: 23:58:00
 */
public class PersistentComparator implements Comparator<PersistentOrderable>{
    @Override
    public int compare(PersistentOrderable o1, PersistentOrderable o2) {
        boolean equals = o1.getOrder() == o2.getOrder();
        if (equals) {
            return 0;
        }
        if (o1.getOrder() > o2.getOrder()) {
            return 1;
        } else {
            return -1;
        }
    }
}
