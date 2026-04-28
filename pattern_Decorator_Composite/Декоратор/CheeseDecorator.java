public class CheeseDecorator extends PizzaDecorator {
    public CheeseDecorator(Pizza pizza) {
        super(pizza);
    }

    @Override
    public double getCost() {
        return super.getCost() + 2.0;
    }

    public String addCheese() {
        return ", Cheese";
    }

    @Override
    public String getDescription() {
        return super.getDescription() + addCheese();
    }
}