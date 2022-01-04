package de.feu.propra.util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Utility class to find available resources inside a running application. Can
 * be used to e.g. find all available language resources, so a user can select
 * the desired language without the need to hardcode the selection or have a
 * seperate resource file with a list of languages.
 * 
 * @author j-hap 
 *
 */
public class ResourceFinder {
  private static List<String> resources = new ArrayList<>();

  static {
    try {
      findAllResources();
    } catch (IOException | URISyntaxException e) {
      Logger.getLogger("").severe("Unable to find language resource files.");
    }
  }

  private ResourceFinder() {
  }

  private static void findAllResources() throws IOException, URISyntaxException {
    var src = ResourceFinder.class.getProtectionDomain().getCodeSource();
    switch (src.getLocation().getProtocol()) {
    case "file" -> findAllResourcesInFileSystem();
    case "jar" -> findAllResourcesInJar();
    case "rsrc" -> findAllResourcesInRscs();
    default -> System.out.println("ERROR");
    }
  }

  private static void findAllResourcesInRscs() throws IOException {
    var thisClassesResourcePath = ResourceFinder.class.getName().replace('.', '/') + ".class";
    var resource = ClassLoader.getSystemResource(thisClassesResourcePath);
    var path = resource.getPath();
    var jarUrl = new URL(path.substring(0, path.lastIndexOf("jar!") + 3));
    findAllResourcesInJar(jarUrl);
  }

  private static void findAllResourcesInJar(URL jarPath) throws IOException {
    var zip = new ZipInputStream(jarPath.openStream());
    ZipEntry entry;
    while ((entry = zip.getNextEntry()) != null) {
      if (!entry.getName().endsWith("/")) {
        resources.add(entry.getName());
      }
    }
  }

  private static void findAllResourcesInJar() throws IOException {
    var src = ResourceFinder.class.getProtectionDomain().getCodeSource();
    var jarPath = src.getLocation();
    findAllResourcesInJar(jarPath);
  }

  private static void findAllResourcesInFileSystem() throws IOException, URISyntaxException {
    var resource = ResourceFinder.class.getResource("/");
    var root = resource.getPath();
    resources = Files
        .find(Path.of(resource.toURI()), Integer.MAX_VALUE, (filePath, fileAttr) -> fileAttr.isRegularFile())
        .map(e -> e.toUri().getPath()).map(e -> e.replaceFirst(root, "")).collect(Collectors.toList());
  }

  /**
   * Returns all available resource files below a given path. The input must not
   * start with /.
   * 
   * @param subpath Files below this resource path are returned.
   * @return An array of Strings of resources below the given path.
   */
  public static String[] getResourcesBelow(String subpath) {
    return resources.stream().filter(e -> e.startsWith(subpath)).toArray(String[]::new);
  }
}
