package com.pnedelko;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            throw new IllegalArgumentException("Path to .jack file or dir is required as a first argument");
        }

        var compiler = new JackCompiler(args[0]);
        compiler.compile();
    }
}
