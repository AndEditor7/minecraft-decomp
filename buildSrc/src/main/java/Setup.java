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

        Utils.rm(j); Utils.mkdir(j);
        Utils.cp(d.resolve("com"), j.resolve("com"));
        Utils.cp(d.resolve("net"), j.resolve("net"));

        Utils.rm(r); Utils.mkdir(r);
        Utils.cp(d.resolve("assets"), r.resolve("assets"));
        Utils.cp(d.resolve("data"), r.resolve("data"));
        Utils.cp(d.resolve("META-INF"), r.resolve("META-INF"));

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
}
