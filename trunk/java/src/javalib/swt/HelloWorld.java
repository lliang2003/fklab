package fk.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

public class HelloWorld {
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	// TODO Auto-generated method stub
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setText("Hello world");
		
		//Layout layout = new FillLayout(SWT.VERTICAL);
		//RowLayout layout = new RowLayout(SWT.VERTICAL);
		GridLayout layout = new GridLayout(4, true);
		//layout.marginHeight = 10;
		//layout.marginWidth = 100;
		//layout.marginLeft = 200;
		//layout.spacing = 50;
		//layout.fill = true;
		shell.setLayout(layout);
		
		Menu mbar = new Menu(shell, SWT.BAR);
		shell.setMenuBar(mbar);
		MenuItem mfile = new MenuItem(mbar, SWT.CASCADE);
		mfile.setText("&File");
		Menu mmfile = new Menu(shell, SWT.DROP_DOWN);
		MenuItem mopen = new MenuItem(mmfile, SWT.CASCADE);
		mopen.setText("Open");
		mfile.setMenu(mmfile);
		Menu mmopen = new Menu(shell, SWT.DROP_DOWN);
		MenuItem mopenp = new MenuItem(mmopen, SWT.PUSH);
		mopenp.setText("Open Project");
		mopen.setMenu(mmopen);
		MenuItem medit = new MenuItem(mbar, SWT.CASCADE);
		medit.setText("&Edit");
		MenuItem mhelp = new MenuItem(mbar, SWT.CASCADE);
		mhelp.setText("&Help");
		
		//Label sl = new Label(shell, SWT.SEPARATOR|SWT.HORIZONTAL|SWT.SHADOW_IN);
		//sl.setLayoutData(new GridData(SWT.FILL, SWT.UP, true, false, 4, 0));
		
		ToolBar bar = new ToolBar(shell, SWT.RIGHT);
		ToolItem item1 = new ToolItem(bar, SWT.PUSH|SWT.RIGHT);
		item1.setText("toolitem1");
		ToolItem item2 = new ToolItem(bar, SWT.SEPARATOR);
		ToolItem item3 = new ToolItem(bar, SWT.CHECK|SWT.RIGHT);
		item3.setText("toolitem3");
		bar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1));

		Label sll = new Label(shell, SWT.SEPARATOR|SWT.HORIZONTAL|SWT.SHADOW_OUT);
		sll.setLayoutData(new GridData(SWT.FILL, SWT.UP, true, false, 4, 0));
		
		Label label = new Label(shell, SWT.WRAP|SWT.CENTER);
		label.setText("Fuck the world Fuck the world Fuck the world");
		
		for (int i = 0; i < 3; ++i) {
			Button button = new Button(shell, SWT.CENTER);
			button.setText("Hello " + i);
			GridData gdata = new GridData();
			gdata.horizontalAlignment = SWT.FILL;
			gdata.verticalAlignment = SWT.FILL;
			gdata.verticalSpan = i+1;
			gdata.horizontalSpan = i + 1;
			if (i == 1) {
				gdata.grabExcessHorizontalSpace = true;
				gdata.grabExcessVerticalSpace = true;
			}
			button.setLayoutData(gdata);
		}
		Button button1 = new Button(shell, SWT.ARROW);
		button1.setText("button1");
		button1.setAlignment(SWT.RIGHT);
		final Button button2 = new Button(shell, SWT.CHECK);
		button2.setText("button2");
		button2.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				System.out.println("button2 default selected");
			}
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				if (button2.getSelection() == true)
					System.out.println("button2 selected.");
				else
					System.out.println("button2 unselected.");
			}
		});
		Button button3 = new Button(shell, SWT.PUSH|SWT.RIGHT);
		button3.setText("button3");
		GridData gdata = new GridData();
		gdata.horizontalAlignment = SWT.FILL;
		button3.setLayoutData(gdata);
		button3.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				System.out.println("button3 default selected");
			}
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				System.out.println("button3 selected.");
				
			}
		});
		Button button4 = new Button(shell, SWT.RADIO);
		Button button44 = new Button(shell, SWT.RADIO);
		button4.setText("button4");
		button4.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
			}
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
					System.out.println("button4 selected.");
			}
		});
		Button button5 = new Button(shell, SWT.TOGGLE);
		button5.setText("button5");
		Button button6 = new Button(shell, SWT.FLAT);
		button4.setText("button4");
		
		Text text = new Text(shell, SWT.MULTI|SWT.H_SCROLL|SWT.V_SCROLL);
		text.setText("Text  ");
		gdata.verticalSpan = 5;
		gdata.verticalAlignment = SWT.FILL;
		text.setLayoutData(gdata);
		text.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				System.out.println("Text modeified.");
			}
		});
		text.setSelection(4, 6);
		text.insert("joke");
		text.append(" ...");
		text.forceFocus();
		//text.setVisible(false);
		//text.setEnabled(false);
		
		
		List list = new List(shell, SWT.MULTI|SWT.BORDER|SWT.V_SCROLL|SWT.H_SCROLL);
		for (int i = 0; i < 10; ++i)
			list.add("item " + i);
		list.setSize(200, 300);
		
		final Combo combo = new Combo(shell, SWT.DROP_DOWN|SWT.READ_ONLY);
		combo.add("hello");
		combo.add("joke");
		combo.add("kick");
		combo.select(2);
		combo.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}

			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				System.out.println(combo.getSelectionIndex());
			}
			
		});
		
		Group group = new Group(shell, SWT.NONE);
		group.setLayout(new FillLayout(SWT.VERTICAL));
		group.setText("group");
		for (int i = 0; i < 4; i++) {
			Button b = new Button(group, SWT.RADIO);
			b.setText("Choice " + i);
		}
		
		Table table = new Table(shell, SWT.FULL_SELECTION);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);	
		for (int i = 0; i < 3; ++i) {
			TableColumn tc = new TableColumn(table, SWT.NONE);
			tc.setText("column " + i);
			tc.setWidth(100);
		}
		for (int i = 0; i < 10; ++i) {
			TableItem ti = new TableItem(table, SWT.NULL);
			for (int j = 0; j < 3; ++j) {
				ti.setText(j, "item " + i + "-" +j);
			}
		}
		for (int i =0; i < 3; ++i)
			table.getColumn(i).pack();
		
		final Scale scale = new Scale(shell, SWT.HORIZONTAL);
		scale.setMinimum(11);
		scale.setMaximum(111);
		scale.addListener(SWT.Selection, new Listener(){

			public void handleEvent(Event event) {
				// TODO Auto-generated method stub
				System.out.println(scale.getSelection());
			}
			
		});
		
		TabFolder tab = new TabFolder(shell, SWT.UP);
		TabItem tab1 = new TabItem(tab, SWT.NONE);
		tab1.setText("Tab1");
		TabItem tab2 = new TabItem(tab, SWT.NONE);
		tab2.setText("Tab2");
		Group g = new Group(tab, SWT.NONE);
		g.setLayout(new FillLayout());
		for (int i = 0; i < 3; ++i) {
			Button b = new Button(g, SWT.PUSH);
			b.setText("b " + i);
		}
		tab1.setControl(g);
		tab2.setControl(new Button(tab, SWT.FLAT));

		
		
		shell.pack();
		shell.open();
		MessageBox mb = new MessageBox(shell, SWT.OK);
		mb.setText("Hello");
		mb.setMessage("ready?");
		mb.open();

		while ( !shell.isDisposed()) {
            if (!display.readAndDispatch()) {  // process next message
                display.sleep();              // wait for next message
            }
        }
        display.dispose();  // must always clean up

	}

}
