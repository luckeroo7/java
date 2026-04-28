public class DecoratorPatternDemo {

    public static void main(String[] args) {

        Pizza pizza = new PlainPizza();

        //System.out.println(pizza.getDescription() + " $" + pizza.getCost());
        System.out.println(pizza.getDescription());

        // Output: Plain Pizza

        pizza = new CheeseDecorator(pizza);

        System.out.println(pizza.getDescription());

        // Output: Plain Pizza, Cheese

        pizza = new PepperoniDecorator(pizza);

        System.out.println(pizza.getDescription());

        // Output: Plain Pizza, Cheese, Pepperoni




//        Pizza new_pizza = new PlainPizza();
//        new_pizza = new PepperoniDecorator(new_pizza);
//        new_pizza = new CheeseDecorator(new_pizza);
//        System.out.println(new_pizza.getDescription());

        // Output: Plain Pizza, Pepperoni, Cheese
    }
}