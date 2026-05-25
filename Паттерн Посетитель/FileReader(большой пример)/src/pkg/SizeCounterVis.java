package pkg;

public class SizeCounterVis implements Visitor{

    private long totalSize = 0;

    public long getTotalSize(){
        return totalSize;
    }

    @Override
    public void visit(DirNode dNode){
        for(var node : dNode.contents)
            node.accept(this);
    }

    @Override
    public void visit(FileNode fNode){
        totalSize += fNode.size;
    }
}
