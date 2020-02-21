package com.pnedelko;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class WMWriter {
    private PrintWriter writer;

    public WMWriter(File file) throws FileNotFoundException {
        writer = new PrintWriter(file);
    }

    public void writePush(String segment, int index) {
        writer.println("push " + segment + " " + index);
    }

    public void writePop(String segment, int index) {
        writer.println("pop " + segment + " " + index);
    }

    public void writeArithmetic(char command) {
        // '+', '-', '*', '/', '&', '|', '<', '>', '='

        // "add",
        // "sub",
        // "neg",
        // "eq",
        // "gt",
        // "lt",
        // "and",
        // "or",
        // "not"

        switch (command) {
            case '+':
                writer.println("add");
                break;
            case '-':
                writer.println("sub");
                break;
            case '*':
                writeCall("Math.multiply", 2);
                break;
            case '/':
                writeCall("Math.divide", 2);
                break;
            case '&':
                writer.println("and");
                break;
            case '|':
                writer.println("or");
                break;
            case '<':
                writer.println("lt");
                break;
            case '>':
                writer.println("gt");
                break;
            case '=':
                writer.println("eq");
                break;
            default:
                throw new RuntimeException("Unknown arithmetic operation: " + command);
        }
    }

    public void writeSinglePrependCommand(char command) {
        switch (command) {
            case '-':
                writer.println("neg");
                break;
            case '~':
                writer.println("not");
                break;
            default:
                throw new RuntimeException("Unknown single prepend command");
        }
    }

    public void writeLabel(String label) {
        writer.println("label " + label);
    }

    public void writeGoto(String label) {
        writer.println("goto " + label);
    }

    public void writeIf(String label) {
        writer.println("if-goto " + label);
    }

    public void writeCall(String name, int nArgs) {
        writer.println("call " + name + " " + nArgs);
    }

    public void writeFunction(String name, int nLocals) {
        writer.println("function " + name + " " + nLocals);
    }

    public void writeReturn() {
        writer.println("return");
    }

    public void close() {
        writer.close();
    }
}
