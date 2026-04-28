public class PepperoniDecorator extends PizzaDecorator {
    public PepperoniDecorator(Pizza pizza) {
        super(pizza);
    }

    @Override
    public double getCost() {
        return super.getCost() + 1.0;
    }

    public String addPepperoni() {
        return ", Pepperoni";
    }

    @Override
    public String getDescription() {
        return super.getDescription() + addPepperoni();
    }
}