package pkg;

public class FileNode implements Node{
    String name;
    long size;

    public FileNode(String inName, long inSize){
        name = inName;
        size = inSize;
    }

    public String GetName() {
        return name;
    }

    public long GetSize(){
        return size;
    }

    @Override
    public void accept(Visitor visitor) {visitor.visit(this);}
}
