package com.univ.it.types;

import java.io.Serializable;

public class AttributeChar extends Attribute implements Serializable {
    private char val;
    public AttributeChar(String s) {
        val = s.charAt(0);
    }

    @Override
    public String toString() {
        return Character.toString(val);
    }
}
