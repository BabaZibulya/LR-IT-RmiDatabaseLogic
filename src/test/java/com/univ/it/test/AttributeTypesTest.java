package com.univ.it.test;

import com.univ.it.types.*;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class AttributeTypesTest {

    // AttributeCharInterval
    @Test(expected = IllegalArgumentException.class)
    public void charIntervalEmplyString() {
        Attribute charInterval = new AttributeCharInterval("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void charIntervalWrongString() {
        Attribute charInterval = new AttributeCharInterval("[][][");
    }

    @Test(expected = IllegalArgumentException.class)
    public void charIntervalWrongOrder() {
        Attribute charInterval = new AttributeCharInterval("[c:a]");
    }

    @Test
    public void charIntervalIncludes() {
        AttributeCharInterval attributeCharInterval = new AttributeCharInterval("[a:c]");
        assertEquals(true, attributeCharInterval.includes('a'));
        assertEquals(true, attributeCharInterval.includes('b'));
        assertEquals(true, attributeCharInterval.includes('c'));
        assertEquals(false, attributeCharInterval.includes('d'));
    }

    // AttributeChar
    @Test(expected = StringIndexOutOfBoundsException.class)
    public void charEmptyString() {
        Attribute a = new AttributeChar("");
    }

    // AttributeInteger
    @Test(expected = NumberFormatException.class)
    public void realEmptyString() {
        Attribute a = new AttributeReal("");
    }

    // AttributeInteger
    @Test(expected = NumberFormatException.class)
    public void intEmptyString() {
        Attribute a = new AttributeInteger("");
    }
}
