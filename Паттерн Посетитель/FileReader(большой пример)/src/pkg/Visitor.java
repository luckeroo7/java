package pkg;

public interface Visitor {
    void visit(FileNode fNode);
    void visit(DirNode dNode);
}
