package com.univ.it.types;

import java.io.Serializable;

public class AttributeReal extends Attribute implements Serializable {
    private double val;
    public AttributeReal(String s) {
        val = Double.parseDouble(s);
    }

    @Override
    public String toString() {
        return Double.toString(val);
    }
}
