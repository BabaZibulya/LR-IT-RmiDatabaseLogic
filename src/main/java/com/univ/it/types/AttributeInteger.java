package com.univ.it.types;

import java.io.Serializable;

public class AttributeInteger extends Attribute implements Serializable {
    private int val;
    public AttributeInteger(String s) {
        val = Integer.parseInt(s);
    }

    @Override
    public String toString() {
        return Integer.toString(val);
    }
}
