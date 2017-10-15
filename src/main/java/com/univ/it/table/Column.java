package com.univ.it.table;

import com.univ.it.types.Attribute;

import java.io.Serializable;
import java.lang.reflect.Constructor;

public class Column implements Serializable {
    private String name;
    private Class attributeType;

    public Column(String s) {
        try {
            name = s.substring(s.lastIndexOf(".") + 1);
            attributeType = Class.forName(s);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public Column(Class c) {
        attributeType = c;
        String s = attributeType.getCanonicalName();
        name = s.substring(s.lastIndexOf(".") + 1);
    }

    Class getAttributeClass() {
        return attributeType;
    }

    public Attribute createAttribute(String attrString) throws Exception {
        return (Attribute) findStringConstructor(attributeType).newInstance(attrString);
    }

    public String getName() {
        return name;
    }

    private Constructor findStringConstructor(Class c) throws NoSuchMethodException {
        return c.getConstructor(String.class);
    }

    public boolean equals(Column other) {
        return (attributeType.equals(other.attributeType));
    }

    @Override
    public String toString() {
        return attributeType.getCanonicalName();
    }
}
