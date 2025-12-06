import java.io.*;
import java.net.URI;
import java.nio.file.*;
import java.util.*;
import java.util.zip.*;

public class Jars {

    public static void download(String url, File dest) throws IOException {
        if (dest.exists()) return;

        dest.getParentFile().mkdirs();
        try (InputStream in = URI.create(url).toURL().openStream()) {
            Files.copy(in, dest.toPath());
        }
    }

    public static void unbundle(File bundler, File dest, File libsDir, Set<String> libs) throws IOException {
        if (dest.exists()) return;

        try (ZipFile zip = new ZipFile(bundler)) {
            extract(zip, serverPath(zip), dest);

            for (ZipEntry e : Collections.list(zip.entries())) {
                String path = e.getName();
                if (!path.startsWith("META-INF/libraries/") || !path.endsWith(".jar")) continue;

                String name = path.substring(path.lastIndexOf('/') + 1);
                if (!libs.contains(name)) continue;

                try {
                    extract(zip, path, new File(libsDir, name));
                } catch (IOException ex) {
                    throw new UncheckedIOException(ex);
                }
            }
        }

        bundler.delete();
    }

    private static String serverPath(ZipFile zip) throws IOException {
        ZipEntry versions = zip.getEntry("META-INF/versions.list");
        try (BufferedReader r = new BufferedReader(new InputStreamReader(zip.getInputStream(versions)))) {
            String[] parts = r.readLine().split("\t");
            return "META-INF/versions/" + parts[2];
        }
    }

    private static void extract(ZipFile zip, String entry, File dest) throws IOException {
        if (dest.exists()) return;

        dest.getParentFile().mkdirs();
        try (InputStream in = zip.getInputStream(zip.getEntry(entry))) {
            Files.copy(in, dest.toPath());
        }
    }
}
