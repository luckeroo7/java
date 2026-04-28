public abstract class PizzaDecorator implements Pizza {
    protected Pizza decoratedPizza;

    public PizzaDecorator(Pizza pizza) {
        this.decoratedPizza = pizza;
    }

    @Override
    public double getCost() {
        return decoratedPizza.getCost();
    }

    @Override
    public String getDescription() {
        return decoratedPizza.getDescription();
    }
}