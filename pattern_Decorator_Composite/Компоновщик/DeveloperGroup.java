package composite;

public interface DeveloperGroup extends Developer {

    void addDeveloper(Developer developer);
    void removeDeveloper(Developer developer);
}