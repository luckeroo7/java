import java.nio.file.Path;

import pkg.*;

public class Main {
    public static void main(String[] args) throws Exception {
        Path p = Path.of(args.length > 0 ? args[0] : ".");

        Node root = TreeBuilder.build(p);
        Visitor visitor = new PrinterVis();
        root.accept(visitor);
    }
}
