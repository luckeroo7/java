import java.util.HashMap;
import java.util.Map;

interface Expression
{
	int Interpret(Map<String, Integer> context);
}

//Терминальное выражение: Константа
class NumberExpression implements Expression
{
	private int number;
	
	public NumberExpression(int num)
	{
		this.number = num;
	}
	
	@Override
	public int Interpret(Map<String, Integer> context)
	{
		return number;
	}
}

//Терминальное выражение: Переменная
class VariableExpression implements Expression
{
	private String name;
	
	public VariableExpression(String name)
	{
		this.name = name;
	}
	
	@Override
	public int Interpret(Map<String, Integer> context)
	{
		return context.get(name);
	}
}

//Сложение
class AddExpression implements Expression
{
	private Expression left;
	private Expression right;
	
	public AddExpression(Expression l, Expression r)
	{
		this.left = l;
		this.right = r;
	}
	
	@Override
	public int Interpret(Map<String, Integer> context)
	{
		return left.Interpret(context) + right.Interpret(context);
	}
}

//Вычитание
class SubtractExpression implements Expression
{
	private Expression left;
	private Expression right;
	
	public SubtractExpression(Expression l, Expression r)
	{
		this.left = l;
		this.right = r;
	}
	
	@Override
	public int Interpret(Map<String, Integer> context)
	{
		return left.Interpret(context) - right.Interpret(context);
	}
}

//Умножение
class MultiplyExpression implements Expression
{
	private Expression left;
	private Expression right;
	
	public MultiplyExpression(Expression l, Expression r)
	{
		this.left = l;
		this.right = r;
	}
	
	@Override
	public int Interpret(Map<String, Integer> context)
	{
		return left.Interpret(context) * right.Interpret(context);
	}
}

//Деление
class DivideExpression implements Expression
{
	private Expression left;
	private Expression right;
	
	public DivideExpression(Expression l, Expression r)
	{
		this.left = l;
		this.right = r;
	}
	
	@Override
	public int Interpret(Map<String, Integer> context)
	{
		return left.Interpret(context) / right.Interpret(context);
	}
}

public class InterpreterDemo
{
    public static void main(String[] args)
	{
		//Задаём контекст
		Map<String, Integer> context = new HashMap<>();
		context.put("x", 10);
		context.put("y", 5);

		//Пример выражения: (x + 3) * (y - 2)
		Expression expression = new MultiplyExpression(
			new AddExpression(new VariableExpression("x"), new NumberExpression(3)), 
			new SubtractExpression(new VariableExpression("y"), new NumberExpression(2))
		);

		int result = expression.Interpret(context);

		System.out.println("Результат выражения: " + result);//Ожидается 39
	}
}
