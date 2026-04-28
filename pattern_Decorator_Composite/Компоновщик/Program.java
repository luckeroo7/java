package composite;

public class Program {

    public static void main(String[] args) {

        Developer javaDev1 = new JavaDeveloper();
        Developer javaDev2 = new JavaDeveloper();
        Developer pythonDev = new PythonDeveloper();

        // backend команда
        DeveloperGroup backendTeam = new TeamDevelopers();
        backendTeam.addDeveloper(javaDev1);
        backendTeam.addDeveloper(pythonDev);

        // полная команда (вложенность)
        DeveloperGroup fullTeam = new TeamDevelopers();
        fullTeam.addDeveloper(backendTeam);
        fullTeam.addDeveloper(javaDev2);

        // запуск
        fullTeam.writeCode();
    }
}