package pkg;

import java.io.IOException;
import java.nio.file.*;

public class TreeBuilder {
    public static Node build(Path p) throws IOException{
        if (!Files.exists(p)) throw new IOException("Путь не найден");
        return buildNode(p);
    }

    private static Node buildNode(Path path) throws IOException{

        String name = path.getFileName().toString();

        if(Files.isRegularFile(path))
            return new FileNode(name, Files.size(path));
        if(Files.isDirectory(path)){
            DirNode dir = new DirNode(name);
            try(var stream = Files.list(path)){
                stream.forEach((p) -> {
                    try{
                        dir.addContent(buildNode(p));
                    }catch(IOException | SecurityException e){System.out.println(e.getMessage());}
                });
            }
            SizeCounterVis visitor = new SizeCounterVis();
            dir.accept(visitor);
            dir.size = visitor.getTotalSize();
            return dir;
        }
        return null;
    }

}
