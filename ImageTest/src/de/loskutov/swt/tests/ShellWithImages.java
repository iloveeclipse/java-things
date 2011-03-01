/*******************************************************************************
 * Copyright (c) 2011 Andrei Loskutov.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributor:  Andrei Loskutov - initial API and implementation
 *******************************************************************************/

package de.loskutov.swt.tests;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;

/**
 * Class shows a shell with different UI elements (buttons/labels/table)
 * containing embedded image.
 *
 * @author aloskuto
 */
public class ShellWithImages {
    static String jreHome = System.getProperty("java.home");
    static String imgFolder = "../demo/jfc/Java2D/src/images/";

    public static void main(String[] args) throws Exception {
        Display display = new Display();

        Image image;
        Image image2;

        File file = new File(jreHome, imgFolder + "java-logo.gif");
        if (file.isFile()) {
            InputStream stream = new FileInputStream(file);
            image = new Image(display, stream);
            stream.close();
        } else {
            image = Display.getDefault().getSystemImage(SWT.ICON_WARNING);
        }

        File file2 = new File(jreHome, imgFolder + "print.gif");
        if (file2.isFile()) {
            InputStream stream2 = new FileInputStream(file2);
            image2 = new Image(display, stream2);
            stream2.close();
        } else {
            image2 = Display.getDefault().getSystemImage(SWT.ICON_WORKING);
        }

        Shell shell = new Shell(display);
        shell.setLayout(new GridLayout(2, false));

        if (args.length > 0 && args[0] != null) {
            shell.setText(args[0]);
        }

        Button button = new Button(shell, SWT.PUSH);
        button.setImage(image);
        button.setText("Button");
        button.setToolTipText("\nButton\n");
        button = new Button(shell, SWT.PUSH);
        button.setImage(image2);
        button.setText("Button");
        button.setToolTipText("\nButton\n");

        button = new Button(shell, SWT.CHECK);
        button.setImage(image);
        button.setText("Check");
        button.setToolTipText("\nCheck\n");
        button = new Button(shell, SWT.CHECK);
        button.setImage(image2);
        button.setText("Check");
        button.setToolTipText("\nCheck\n");

        button = new Button(shell, SWT.RADIO);
        button.setImage(image);
        button.setText("Radio");
        button.setToolTipText("\nRadio\n");
        button = new Button(shell, SWT.RADIO);
        button.setImage(image2);
        button.setText("Radio");
        button.setToolTipText("\nRadio\n");

        button = new Button(shell, SWT.FLAT);
        button.setImage(image);
        button.setText("Flat");
        button.setToolTipText("\nFlat\n");
        button = new Button(shell, SWT.FLAT);
        button.setImage(image2);
        button.setText("Flat");
        button.setToolTipText("\nFlat\n");

        Label label = new Label(shell, SWT.NONE);
        label.setImage(image);
        label.setToolTipText("\nLabel\n");
        label = new Label(shell, SWT.NONE);
        label.setImage(image2);
        label.setToolTipText("\nLabel\n");

        Table table = new Table(shell, SWT.BORDER);
        table.setToolTipText("\nTable\n");
        table.setItemCount(2);
        table.getItem(0).setImage(image);
        table.getItem(1).setImage(image2);

        shell.setSize(300, 400);
        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        display.dispose();
    }
}
