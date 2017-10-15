package com.univ.it.table;

import com.univ.it.types.Attribute;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.StringJoiner;

public class Row implements Serializable {
    private ArrayList<Attribute> values;

    public Row() {
        values = new ArrayList<>();
    }

    public Row(int n) {
        values = new ArrayList<>(n);
    }

    public void replaceAt(int ind, Attribute newValue) {
        values.set(ind, newValue);
    }

    public void pushBack(Attribute a) {
        values.add(a);
    }

    public int size() {
        return values.size();
    }

    public Attribute getAt(int ind) {
        return values.get(ind);
    }

    public boolean equals(Row other) {
        if (other.size() != size()) {
            return false;
        }
        for (int i = 0; i < size(); ++i) {
            if (!values.get(i).toString().equals(other.getAt(i).toString())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        StringJoiner result = new StringJoiner("\t");
        for (Attribute attribute : values) {
            result.add(attribute.toString());
        }
        return result.toString();
    }
}
