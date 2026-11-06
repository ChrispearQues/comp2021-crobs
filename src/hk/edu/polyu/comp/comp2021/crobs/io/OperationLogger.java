package hk.edu.polyu.comp.comp2021.crobs.io;

/** REQ1: every executed command is logged. */
public interface OperationLogger {

    void log(int index, String command);
}
