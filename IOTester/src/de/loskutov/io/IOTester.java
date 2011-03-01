/*******************************************************************************
 * Copyright (c) 2011 Andrei Loskutov.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributor:  Andrei Loskutov - initial API and implementation
 *******************************************************************************/
package de.loskutov.io;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author andrei
 */
public class IOTester {

    private static final int KB = 1024;
    private static final long MB = KB * KB;
    final ThreadLocal<byte[]> myByteBuf;

    final File root;
    final AtomicLong bytesRead;
    final AtomicLong filesRead;
    final AtomicLong dirsRead;
    final AtomicLong startTimeNano;
    private final SummaryThread reporter;
    private final int progressStep;
    private final long maxBytes;
    private final int bufferSize;
    private final long interval;

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            errorAndExit("Arguments: <directory path> [progress per ? MB][stop after ? MB][buffer size ? KB]\n" +
            "\t(default is reporting each 10 MB and buffer size 4 KB)");
        }
        File file = new File(args[0]).getCanonicalFile();
        if (!file.exists()) {
            errorAndExit("File does not exist: " + file);
        }
        new IOTester(file, args).run();
    }

    static void errorAndExit(String message) throws IllegalStateException {
        System.err.println(message);
        System.exit(-1);
    }

    public IOTester(File root, String[] args) {
        this.root = root;
        bytesRead = new AtomicLong();
        filesRead = new AtomicLong();
        dirsRead = new AtomicLong();
        startTimeNano = new AtomicLong();

        if(args.length > 1) {
            progressStep = Integer.parseInt(args[1]);
        } else {
            progressStep = 10;
        }
        if(args.length > 2) {
            maxBytes = MB * Integer.parseInt(args[2]);
        } else {
            maxBytes = MB * MB;
        }
        if(args.length > 3) {
            bufferSize = KB * Integer.parseInt(args[3]);
        } else {
            bufferSize = 4 * KB;
        }
        interval = MB * progressStep;
        myByteBuf = new ThreadLocal<byte[]>() {

            @Override
            protected byte[] initialValue() {
                return new byte[bufferSize];
            }
        };
        reporter = new SummaryThread(this);
        Runtime.getRuntime().addShutdownHook(reporter);
    }

    void run() {
        startTimeNano.set(System.nanoTime());
        List<File> dirs = new ArrayList<File>();
        dirs.add(root);
        traverse(dirs);
    }

    private void traverse(List<File> toDoList) {
        while (!toDoList.isEmpty()) {
            File dir = toDoList.remove(0);
            File[] files = dir.listFiles();
            if (files == null) {
                dirsRead.incrementAndGet();
                continue;
            }

            for (File file : files) {
                if (file.isFile() && file.canRead()) {
                    read(file);
                } else if (file.isDirectory()) {
                    try {
                        file = file.getCanonicalFile();
                    } catch (IOException e) {
                        System.err.println("Error while resolving file path: " + file);
                        e.printStackTrace();
                        continue;
                    }
                    if (file.getAbsolutePath().startsWith(dir.getAbsolutePath()) && !toDoList.contains(file)) {
                        toDoList.add(file);
                    }
                }
            }
            dirsRead.incrementAndGet();
        }
    }

    private void read(File file) {
        BufferedInputStream bis = null;
        try {
            bis = new BufferedInputStream(new FileInputStream(file));
            int length = (int) file.length();
            if(length == 0){
                filesRead.incrementAndGet();
                return;
            }
            if(length < 0 || length > 10 * MB){
                length = 10 * (int)MB;
            }
            ByteArrayOutputStream bos = new ByteArrayOutputStream(length);

            int read = 0;
            byte buf[] = myByteBuf.get();
            long total = bytesRead.longValue();
            long count = total / interval;
            while ((read = bis.read(buf, 0, bufferSize)) > 0) {
                total = bytesRead.addAndGet(read);
                if(total > maxBytes) {
                    System.exit(0);
                }
                if(total / interval > count) {
                    count = total / interval;
                    reportProgress(total, System.nanoTime());
                }
                bos.write(buf, 0, read);
            }

            total = bytesRead.longValue();
            if(total / interval > count) {
                reportProgress(total, System.nanoTime());
            }

        } catch (IOException e) {
            System.err.println("Error while reading file: " + file);
            e.printStackTrace();
        } finally {
            if(bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        filesRead.incrementAndGet();
    }

    void reportProgress(long roundedTotal, long nanos) {
        reporter.report();
    }

}
