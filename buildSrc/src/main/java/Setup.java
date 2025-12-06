import groovy.json.JsonSlurper;
import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;

@SuppressWarnings("unchecked")
public class Setup {

    public static void copySrc(File root, String side) throws IOException {
        Path d = root.toPath().resolve("decompSrc/" + side);
        Path j = root.toPath().resolve(side + "/src/main/java");
        Path r = root.toPath().resolve(side + "/src/main/resources");

        rm(j); mkdir(j);
        cp(d.resolve("com"), j.resolve("com"));
        cp(d.resolve("net"), j.resolve("net"));

        rm(r); mkdir(r);
        cp(d.resolve("assets"), r.resolve("assets"));
        cp(d.resolve("data"), r.resolve("data"));
        cp(d.resolve("META-INF"), r.resolve("META-INF"));

        for (File f : d.toFile().listFiles((dir, name) -> name.endsWith(".json") || name.endsWith(".jfc")))
            Files.copy(f.toPath(), r.resolve(f.getName()));

        if (side.equals("client")) {
            for (File f : d.toFile().listFiles((dir, name) -> name.endsWith(".png") || name.endsWith(".icns")))
                Files.copy(f.toPath(), r.resolve(f.getName()));
        }
    }

    public static List<String> vfArgs(Set<String> libs) {
        List<String> args = new ArrayList<>(Arrays.asList(
            "java", "-jar", "vineflower.jar",
            "-din=1", "-rbr=1", "-dgs=1", "-asc=1", "-rsy=1",
            "--excluded-classes=.*package-info", "--indent-string=    "
        ));
        for (String lib : libs) args.add("-e=libs/" + lib);

        return args;
    }

    public static void assets(File run, String version, String url) throws Exception {
        File dir = new File(run, "assets/indexes");
        dir.mkdirs();

        File idx = new File(dir, version + ".json");
        if (!idx.exists())
            Files.copy(URI.create(url).toURL().openStream(), idx.toPath());

        File objs = new File(run, "assets/objects");
        objs.mkdirs();

        Map<String, Map<String, Object>> objects = (Map) ((Map) new JsonSlurper().parse(idx)).get("objects");
        for (Map<String, Object> m : objects.values()) {
            String h = (String) m.get("hash");
            long size = ((Number) m.get("size")).longValue();

            File f = new File(objs, h.substring(0, 2) + "/" + h);
            if (!f.exists() || f.length() != size) {
                f.getParentFile().mkdirs();
                Files.copy(URI.create("https://resources.download.minecraft.net/" + h.substring(0, 2) + "/" + h)
                        .toURL().openStream(), f.toPath());
            }
        }
    }

    private static void rm(Path p) throws IOException {
        if (!Files.exists(p)) return;

        Files.walk(p).sorted(Comparator.reverseOrder()).forEach(f -> {
            try {
                Files.delete(f);
            } catch (IOException e) {}
        });
    }

    private static void mkdir(Path p) throws IOException { Files.createDirectories(p); }

    private static void cp(Path src, Path dst) throws IOException {
        if (!Files.exists(src)) return;
        Files.walk(src).forEach(s -> {
            try {
                Files.copy(s, dst.resolve(src.relativize(s)), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
    }
}
