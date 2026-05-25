package pkg;

import java.util.ArrayList;

public class DirNode implements Node{
    String name;
    long size = 0;
    ArrayList<Node> contents= new ArrayList<Node>();

    public DirNode(String inName){
        name = inName;
    }

    void addContent(Node node){
        contents.add(node);
    }

    @Override
    public void accept(Visitor visitor) {visitor.visit(this);}
}
