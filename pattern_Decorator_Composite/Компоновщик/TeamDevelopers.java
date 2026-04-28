package composite;

import java.util.ArrayList;
import java.util.List;

public class TeamDevelopers implements DeveloperGroup {

    private List<Developer> developers = new ArrayList<>();

    @Override
    public void writeCode() {
        for (Developer developer : developers) {
            developer.writeCode();
        }
    }

    @Override
    public void addDeveloper(Developer developer) {
        developers.add(developer);
    }

    @Override
    public void removeDeveloper(Developer developer) {
        developers.remove(developer);
    }
}