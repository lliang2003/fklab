package fk.swt;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class Test {
	static class MyData {

	    String string1, string2;

	    public MyData(String string1, String string2) {

	        this.string1 = string1;

	        this.string2 = string2;

	    }

	}

	@SuppressWarnings("unchecked")
	static void sort(

		    Table table,

		    final int column,

		    final boolean descend) {



		    int count = table.getItemCount();

		    MyData[] list = new MyData[count];

		    for (int i = 0; i < count; i++) {

		        Object data = table.getItem(i).getData();

		        list[i] = (MyData) data;

		    }

		    Arrays.sort(list, new Comparator() {

		        public int compare(Object a, Object b) {

		            MyData d1 = (MyData) (descend ? b : a);

		            MyData d2 = (MyData) (descend ? a : b);

		            switch (column) {

		                case 0 :

		                   return d1.string1.compareTo(d2.string1);

		                case 1 :

		                   return d1.string2.compareTo(d2.string2);

		            }

		            return 0;

		        }

		    });

		    for (int i = 0; i < list.length; i++) {

		        TableItem item = table.getItem(i);

		        item.setText(0, list[i].string1);

		        item.setText(1, list[i].string2);

		        item.setData(list[i]);

		    }

		}

	static int ROWS = 1000;

	public static void main(String[] args) {

	    Display display = new Display();

	    Shell shell = new Shell(display);

	    final Table table = new Table(shell, SWT.BORDER);

	    table.setHeaderVisible(true);

	    for (int i = 0; i < 2; i++) {

	        TableColumn column =

	            new TableColumn(table, SWT.NONE);

	        column.setText("Column " + i);

	        column.setData(new Boolean(false));

	    }

	    Random r = new Random();

	    table.setRedraw(false);

	    for (int i = 0; i < ROWS; i++) {

	        TableItem item = new TableItem(table, SWT.NULL);

	        MyData data =

	            new MyData(

	                "A" + r.nextInt(1000),

	                "B" + r.nextInt(1000));

	        item.setText(0, data.string1);

	        item.setText(1, data.string2);

	        item.setData(data);

	    }

	    sort(table, 0, false);

	    table.setRedraw(true);

	    for (int i = 0; i < table.getColumnCount(); i++) {

	        final TableColumn column = table.getColumn(i);

	        column.pack();

	        column.addListener(SWT.Selection, new Listener() {

	            public void handleEvent(Event event) {

	                int index = table.indexOf(column);

	                if (index != -1) {

	                    Boolean b = (Boolean) column.getData();

	                    boolean value = b.booleanValue();

	                    sort(table, index, !value);

	                    column.setData(new Boolean(!value));

	                }

	            }

	        });

	    }

	    table.setSize(200, 200);

	    shell.pack();

	    shell.open();

	    while (!shell.isDisposed()) {

	        if (!display.readAndDispatch()) display.sleep();

	    }

	    display.dispose();

	}



}
