import java.util.ArrayList;
import java.util.List;

abstract class Shape {
    public abstract void accept(ShapeVisitor visitor);
}

class Circle extends Shape {
    private final double radius;
    
    public Circle(double radius) {
        this.radius = radius;
    }
    
    public double getRadius() {
        return radius;
    }
    
    @Override
    public void accept(ShapeVisitor visitor) {
        visitor.visit(this);
    }
}

class Rectangle extends Shape {
    private final double width;
    private final double height;
    
    public Rectangle(double width, double height) {
        this.width = width;
        this.height = height;
    }
    
    public double getWidth() {
        return width;
    }
    
    public double getHeight() {
        return height;
    }
    
    @Override
    public void accept(ShapeVisitor visitor) {
        visitor.visit(this);
    }
}

interface ShapeVisitor {
    void visit(Circle circle);
    void visit(Rectangle rectangle);
}


class AreaCalculator implements ShapeVisitor {
    private double totalArea = 0.0;
    
    @Override
    public void visit(Circle circle) {
        totalArea += Math.PI * circle.getRadius() * circle.getRadius();
    }
    
    @Override
    public void visit(Rectangle rectangle) {
        totalArea += rectangle.getWidth() * rectangle.getHeight();
    }
    
    public double getTotalArea() {
        return totalArea;
    }
}


public class VisitorPatternDemo {
    public static void main(String[] args) {
        List<Shape> shapes = new ArrayList<>();
        shapes.add(new Circle(5.0));
        shapes.add(new Rectangle(4.0, 6.0));
        shapes.add(new Circle(2.0));

        AreaCalculator areaCalc = new AreaCalculator();
        for (Shape shape : shapes) {
            shape.accept(areaCalc);
        }
        System.out.println("Общая площадь: " + areaCalc.getTotalArea());
    }
}