package pkg;

public class PrinterVis implements Visitor{

    private int depth = 0;
    private final String ident = " ";

    @Override
    public void visit(DirNode dNode){
        System.out.println(ident.repeat(2*depth) + "/[" + dNode.name + " " + formatSize(dNode.size) + "]");
        depth++;
        for(var node : dNode.contents)
            node.accept(this);
        --depth;
        System.out.println(ident.repeat(2*depth) + "/[" + dNode.name + "]");
    }

    @Override
    public void visit(FileNode fNode){
        System.out.println(ident.repeat(2*depth) + "-" + fNode.name + " " + formatSize(fNode.size));
    }

    private String formatSize(long bytes){
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        return String.format("%.1f MB", bytes / (1024.0 * 1024));
    }

}
