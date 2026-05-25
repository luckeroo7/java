public class Calculator {
    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Usage: java Calculator <num1> <operator> <num2>");
            System.out.println("Example: java Calculator 10 + 5");
            System.out.println("Operators: + - * /");
            return;
        }

        try {
            double a = Double.parseDouble(args[0]);
            String op = args[1];
            double b = Double.parseDouble(args[2]);
            double result = 0;

            switch (op) {
                case "+": result = a + b; break;
                case "-": result = a - b; break;
                case "x": result = a * b; break;
                case "/":
                    if (b == 0) {
                        System.out.println("Error: Division by zero!");
                        return;
                    }
                    result = a / b;
                    break;
                default:
                    System.out.println("Error: Unknown operator '" + op + "'");
                    return;
            }
            System.out.println(a + " " + op + " " + b + " = " + result);
        } catch (NumberFormatException e) {
            System.out.println("Error: Please enter valid numbers");
        }
    }
}