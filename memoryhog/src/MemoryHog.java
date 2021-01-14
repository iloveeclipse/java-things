import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MemoryHog {

	private static final int WAIT = -1;

	private static final String CHILD = "child";

	private static final int KB = 1024;

	/*
	 * /proc/meminfo
	 * MemTotal:       65688000 kB
	 * MemFree:        19272180 kB
	 * MemAvailable:   49772992 kB
	 */
	private static final Pattern MEM_AVAILABLE_PATTERN = Pattern
			.compile("MemAvailable:\\s+(\\d+) kB");


	public static void main(String[] args) throws Exception {
		long allocateGB = parseInput(args);
		if(allocateGB == WAIT) {
			keepWaiting();
		} else if(allocateGB == 0) {
			// Default: use half of available memory
			allocateGB = kilobytesToGb(parseProcMeminfo() / 2);
			System.out.println("No arguments given, will allocate " + allocateGB + " GB RAM");
		} else if(allocateGB == Long.MIN_VALUE) {
			allocateGB = kilobytesToGb(parseProcMeminfo() / 2);
			System.out.println("Wrong arguments given, will allocate half of available (" + allocateGB + " GB) RAM");
		} else if(allocateGB > 0) {
			System.out.println("Got " + allocateGB + " GB RAM to allocate");
		}
		runFreeAndPrintOutput();
		Process process1 = createMemoryHog(allocateGB);
		process1.waitFor();
	}

	private static Process createMemoryHog(long gb) throws IOException {
		String javaHome = System.getProperty("java.home", "");
		// We start Java with minumum heap size set to requested allocation memory size
		// and let JVM to preallocate all the heap, which will literally
		// write so much memory with zeros
		ProcessBuilder pb = new ProcessBuilder(javaHome + "/bin/java",
				"-Xms" + gb + "g",
				"-Xmx" + gb + "g",
				"-XX:+AlwaysPreTouch",
				getSourceLocation(),
				CHILD);
		pb.redirectErrorStream(true);
		pb.inheritIO();
		StringJoiner commands = new StringJoiner(" ");
		pb.command().forEach(c -> commands.add(c));

		System.out.println("Starting: " + commands.toString() + "\n will take some time to allocate " + gb +" GB RAM...");
		return pb.start();
	}

	private static void keepWaiting() throws Exception {
		reportMemoryUse("Press Ctrl+C to terminate the memory hog (pid: " + getMyPid() + ")");
		while(true) {
			Thread.sleep(10_000);
		}
	}

	private static void reportMemoryUse(String message) throws IOException {
		runFreeAndPrintOutput();
		long total = Runtime.getRuntime().totalMemory();
		//		long max = Runtime.getRuntime().maxMemory();
		//		long free = Runtime.getRuntime().freeMemory();
		//		long used = total - free;
		long systemAvailable = parseProcMeminfo() * KB;
		System.out.println("This program will use " + bytesToGB(total) + " GB, system available memory left: "
				+ bytesToGB(systemAvailable) + " GB");
		System.out.println(message);
	}

	private static void runFreeAndPrintOutput() throws IOException {
		Process process = Runtime.getRuntime().exec(new String []{"free", "-h"});
		InputStream stream = process.getInputStream();
		System.out.println(new String(stream.readAllBytes()));
	}

	private static long bytesToGB(long bytes) {
		return bytes / (1024 * 1024 * 1024);
	}

	private static long kilobytesToGb(long kiloBytes) {
		return kiloBytes / (1024 * 1024);
	}

	private static String getSourceLocation() throws IOException {
		URL location = MemoryHog.class.getProtectionDomain().getCodeSource().getLocation();
		if(location == null) {
			String pid = getMyPid();
			List<String> cmd = getCommandLine(pid);
			if(cmd.size() > 1) {
				String source = cmd.get(1);
				if(source.startsWith("-") && cmd.size() > 2) {
					source = cmd.get(2);
				}
				if(Files.exists(Paths.get(source))){
					return source;
				}
				Path cwd = Files.readSymbolicLink(Paths.get("/proc/" + pid + "/cwd"));
				return cwd + source;
			}
		}

		String dir = location.getFile();
		if(dir.endsWith("bin/")) {
			dir = dir.substring(0, dir.lastIndexOf("bin/")) + "src/";
		}
		return dir + MemoryHog.class.getCanonicalName().replace('.', '/') + ".java";
	}

	private static List<String> getCommandLine(String pid) throws IOException {
		List<String> cmd = new ArrayList<>();
		// Doesn't work, because of null-terminated lines: Files.readAllLines(Paths.get("/proc/" + pidSubstring + "/cmdline"));
		try (InputStream stream = Files.newInputStream(Paths.get("/proc/" + pid + "/cmdline"))){
			byte[] bytes = stream.readAllBytes();
			StringBuilder sb = new StringBuilder();
			for (byte b : bytes) {
				if(b != 0) {
					sb.append((char)b);
				} else {
					cmd.add(sb.toString());
					sb = new StringBuilder();
				}
			}
			if(sb.length() > 0) {
				cmd.add(sb.toString());
			}
		}
		return cmd;
	}

	private static String getMyPid() {
		String jvmName = ManagementFactory.getRuntimeMXBean().getName();
		int indexOfAt = jvmName.indexOf('@');
		return jvmName.substring(0, indexOfAt);
	}

	private static long parseInput(String[] args) {
		if(args.length > 0) {
			try {
				long memory = Long.parseLong(args[0]);
				if(memory <= 0) {
					return Long.MIN_VALUE;
				}
				return memory;
			} catch (Exception e) {
				if(CHILD.equals(args[0])) {
					return WAIT;
				}
				return Long.MIN_VALUE;
			}
		}
		return 0;
	}

	/**
	 * @return system available memory in kB
	 */
	public static long parseProcMeminfo() throws IOException {
		List<String> lines = Files.readAllLines(Paths.get("/proc/meminfo"));
		for (String line : lines) {
			Matcher matcher = MEM_AVAILABLE_PATTERN.matcher(line);
			if (matcher.matches() && matcher.groupCount() > 0) {
				String actualValue = matcher.group(1);
				return Long.parseLong(actualValue.trim());
			}
		}
		return 0;
	}

}
