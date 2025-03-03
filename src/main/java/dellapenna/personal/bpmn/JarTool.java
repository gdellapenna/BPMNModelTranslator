package dellapenna.personal.bpmn;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

/**
 *
 * https://www.baeldung.com/jar-create-programatically
 */
public class JarTool {

    private Manifest manifest = new Manifest();

    public void startManifest() {
        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
    }

    public void setMainClass(String mainFQCN) {
        if (mainFQCN != null && !mainFQCN.equals("")) {
            manifest.getMainAttributes().put(Attributes.Name.MAIN_CLASS, mainFQCN);
        }
    }

    public void addToManifest(String key, String value) {
        manifest.getMainAttributes().put(new Attributes.Name(key), value);
    }

    public JarOutputStream openJar(String jarFile) throws IOException {
        return new JarOutputStream(new FileOutputStream(jarFile), manifest);
    }

    public void addFileFromPath(JarOutputStream jar, String rootPath, Path source) throws FileNotFoundException, IOException {
        String remaining = "";
        if (rootPath.endsWith(File.separator)) {
            remaining = source.toString().substring(rootPath.length());
        } else {
            remaining = source.toString().substring(rootPath.length() + 1);
        }
        addFileWithPath(jar, remaining, source);

    }

    public void addFileWithPath(JarOutputStream jar, String jarPathName, Path source) throws FileNotFoundException, IOException {
        addEntry(jar, jarPathName, Files.getLastModifiedTime(source).toMillis(), Files.newInputStream(source));
    }

    private void addEntry(JarOutputStream jar, String jarPathName, long lastModified, InputStream source) throws IOException {
        String name = jarPathName.replace("\\", "/");
        JarEntry entry = new JarEntry(name);
        entry.setTime(lastModified);
        jar.putNextEntry(entry);

        try (BufferedInputStream in = new BufferedInputStream(source)) {
            byte[] buffer = new byte[1024];
            while (true) {
                int count = in.read(buffer);
                if (count == -1) {
                    break;
                }
                jar.write(buffer, 0, count);
            }
        }
        jar.closeEntry();
    }
}
