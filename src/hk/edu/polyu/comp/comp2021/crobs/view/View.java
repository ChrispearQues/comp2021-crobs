package hk.edu.polyu.comp.comp2021.crobs.view;

/** Output abstraction. The console implementation is the only one needed now. */
public interface View {

    void print(String message);

    void printError(String message);
}
