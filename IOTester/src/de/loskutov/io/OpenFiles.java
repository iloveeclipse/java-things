/*******************************************************************************
 * Copyright (c) 2014 Andrey Loskutov.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Andrey Loskutov - initial API and implementation
 *******************************************************************************/

package de.loskutov.io;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public class OpenFiles {
    public static void main(String[] args) throws IOException {
        int count = 2000;
        if(args.length == 0){
            count = 2000;
        } else {
            count = Integer.valueOf(args[0]);
        }
        Path path = Paths.get(".");
        System.out.println("Will try to open up to " + count + " files in current directory : " + path.toAbsolutePath());

        Path tmpDir;
        try {
            tmpDir = Files.createTempDirectory(path, "open_files_test_");
            tmpDir.toFile().deleteOnExit();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        List<OutputStream> streams = new ArrayList<OutputStream>();
        for (int i = 0; i < count; i++) {
            try {
                Path tmpFile = Files.createTempFile(tmpDir, "_open_", ".tmp");
                tmpFile.toFile().deleteOnExit();
                OutputStream stream = Files.newOutputStream(tmpFile);
                streams.add(stream);
                stream.write(("Hello " + i).getBytes());
                if(i != 0 && i % 100 == 0) {
                    System.out.println(" " + i);
                }
                System.out.print(".");
            } catch (Throwable t){
                System.out.println("\nOpened: " + i + " files, but then crashed with: " + t);
                return;
            }
        }
        System.out.println("\nOpened: " + streams.size() + " files without issues!");
        for (OutputStream os : streams) {
            os.close();
        }
    }
}
