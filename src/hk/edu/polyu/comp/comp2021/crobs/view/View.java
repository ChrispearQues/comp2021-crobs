package hk.edu.polyu.comp.comp2021.crobs.view;

/** Output abstraction. The console implementation is the only one needed now. */
public interface View {

    /**
     * Shows a normal result.
     *
     * @param message the text to show
     */
    void print(String message);

    /**
     * Shows an error.
     *
     * @param message the text to show
     */
    void printError(String message);
}
