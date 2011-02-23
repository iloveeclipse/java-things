/*******************************************************************************
 * Copyright (c) 2011 Andrei Loskutov.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Andrei Loskutov - initial API and implementation
 *******************************************************************************/
package de.loskutov.io;

import java.io.PrintStream;
import java.util.concurrent.TimeUnit;


/**
 * @author andrei
 */
public class SummaryThread extends Thread {

    private final IOTester ioTester;

    public SummaryThread(IOTester ioTester) {
        super();
        this.ioTester = ioTester;
    }

    @Override
    public void run() {
        report();
    }

    @SuppressWarnings("boxing")
    public void report() {
        long totalTime = TimeUnit.MILLISECONDS.convert(
                System.nanoTime() - ioTester.startTimeNano.longValue(), TimeUnit.NANOSECONDS);
        PrintStream pw = System.out;
        long filesCount = ioTester.filesRead.longValue();
        long dirsCount = ioTester.dirsRead.longValue();
        long bytesRead = ioTester.bytesRead.longValue();
        double readSpeed = totalTime > 0? bytesRead * 1000 / totalTime: 0;
        double bytesPerFile = filesCount > 0? bytesRead / filesCount : 0;
        pw.printf("\n");
        pw.printf("Total files / dirs    : %1$ 10d / %2$ 7d \n", filesCount, dirsCount);
        pw.printf("Total bytes read      : %1$ 20d (%2$ 6.1f MB)\n", bytesRead, (double)bytesRead / (1024 * 1024));
        pw.printf("Elapsed time (msec)   : %1$ 20d (%2$ 7d sec)\n", totalTime, totalTime / 1000);
        pw.printf("Read bytes/sec        : %1$ 20.0f (%2$ 5.1f MB/s)\n", readSpeed, readSpeed / (1024 * 1024));
        pw.printf("Avg. bytes per file   : %1$ 20.0f \n", bytesPerFile);
    }
}
