package com.miracle.engine.recyclerview;

import java.util.List;
import java.util.ListIterator;

public interface TypedData {

    int getDataType();

    default int contentHashCode(){return 0;}

    default boolean equalsContent(Object obj){
        return equals(obj);
    }

    static boolean equalsContent(TypedData a, TypedData b){
        return a != null && a.equalsContent(b);
    }

    static boolean listEqualsContent(List<?> a, List<?> b) {
        ListIterator<?> e1 = a.listIterator();
        ListIterator<?> e2 = b.listIterator();
        while (e1.hasNext() && e2.hasNext()) {
            Object o1 = e1.next();
            if(!(o1 instanceof TypedData)) return false;
            TypedData t1 = (TypedData) o1;
            Object o2 = e2.next();
            if (o2==null || !t1.equalsContent(o2))
                return false;
        }
        return !(e1.hasNext() || e2.hasNext());
    }
}
