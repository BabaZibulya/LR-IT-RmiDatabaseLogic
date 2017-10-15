package com.univ.it.test;

import com.univ.it.table.Row;
import com.univ.it.table.Table;
import com.univ.it.types.AttributeChar;
import com.univ.it.types.AttributeCharInterval;
import com.univ.it.types.AttributeReal;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import static org.junit.Assert.assertEquals;

public class TableTest {

    private String charVal = "h", realVal = "1.65", charIntervalVal = "[a:n]";
    private String charVal2 = "f", realVal2 = "1.64", charIntervalVal2 = "[a:h]";
    private int sizeOfTable = 10;

    private Table fillTable() {
        Table table = new Table("test");
        for (int i = 0; i < sizeOfTable; ++i) {
            Row newRow = new Row(3);
            newRow.pushBack(new AttributeChar(charVal));
            newRow.pushBack(new AttributeReal(realVal));
            newRow.pushBack(new AttributeCharInterval(charIntervalVal));
            try {
                table.addNewRow(newRow);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        return table;
    }

    @Test
    public void readAndWriteTableTest() throws Exception {
        Table table = fillTable();
        table.saveToFile("/home/bondarenko/");
        Table table2 = Table.readFromFile("/home/bondarenko/test");
        assertEquals(sizeOfTable, table2.size());
        for (int i = 0; i < table2.size(); ++i) {
            Row row = table.getRow(i);
            assertEquals(charVal + "\t" + realVal + "\t" + charIntervalVal, row.toString());
        }
    }

    @Test
    public void differenceTableTest() throws Exception {
        Table table1 = fillTable();
        Table table2 = fillTable();
        Row newRow = new Row();
        newRow.pushBack(new AttributeChar(charVal2));
        newRow.pushBack(new AttributeReal(realVal2));
        newRow.pushBack(new AttributeCharInterval(charIntervalVal2));
        table2.addNewRow(newRow);

        newRow = new Row();
        newRow.pushBack(new AttributeChar(charVal2));
        newRow.pushBack(new AttributeReal(realVal2));
        newRow.pushBack(new AttributeCharInterval(charIntervalVal2));
        table2.addNewRow(newRow);

        Table diff12 = Table.differenceBetween(table1, table2);
        assertEquals(0, diff12.size());
        Table diff21 = Table.differenceBetween(table2, table1);
        assertEquals(2, diff21.size());
        for (int i = 0; i < diff21.size(); ++i) {
            assertEquals(newRow.toString(), diff21.getRow(i).toString());
        }
    }

    @Test
    public void testSerialization() throws Exception {
        FileOutputStream fos = new FileOutputStream("temp.out");
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        Table table1 = fillTable();
        oos.writeObject(table1);
        oos.flush();
        oos.close();

        FileInputStream fis = new FileInputStream("temp.out");
        ObjectInputStream oin = new ObjectInputStream(fis);
        Table ts = (Table) oin.readObject();
        assertEquals(ts.size(), sizeOfTable);
    }
}
