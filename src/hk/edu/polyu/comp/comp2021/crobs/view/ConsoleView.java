package hk.edu.polyu.comp.comp2021.crobs.view;

/** Writes to the standard output. It makes no decision and formats nothing. */
public class ConsoleView implements View {

    /**
     * Shows a normal result on the standard output.
     *
     * @param message the text to show
     */
    @Override
    public void print(String message) { System.out.println(message); }

    /**
     * Shows an error on the standard output.
     *
     * @param message the text to show
     */
    @Override
    public void printError(String message) { System.out.println(message); }
}
