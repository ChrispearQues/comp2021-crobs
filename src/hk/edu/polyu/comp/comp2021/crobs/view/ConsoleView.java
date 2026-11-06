package hk.edu.polyu.comp.comp2021.crobs.view;

public class ConsoleView implements View {

    @Override
    public void print(String message) { System.out.println(message); }

    @Override
    public void printError(String message) { System.out.println(message); }
}
