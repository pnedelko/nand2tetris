package com.pnedelko;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class JackCompiler {
    private final String path;

    public JackCompiler(String path) {
        this.path = path;
    }

    public void compile() throws IOException {
        var inputPath = new File(path);
        System.out.println("Input path: " + inputPath.getAbsolutePath());

        if (inputPath.isDirectory()) {
            File[] files = inputPath.listFiles(pathname -> pathname.getName().endsWith(".jack"));
            assert files != null; //is it ok?
            for (File file : files) {
                System.out.println("Processing file: " + file.getAbsolutePath());
                processFile(file);
            }
        } else {
            processFile(inputPath);
        }
    }

    private void processFile(File inputFile) throws IOException {
        if (!inputFile.getPath().endsWith(".jack")) {
            throw new IllegalArgumentException("Only .jack files are accepted");
        }

        var tokenizer = new JackTokenizer(inputFile);

        var outputFilePathXml = inputFile.getAbsolutePath()
                .substring(0, inputFile.getAbsolutePath().length() - ".jack".length()) + "2.xml";
        var outputFilePathVm = inputFile.getAbsolutePath()
                .substring(0, inputFile.getAbsolutePath().length() - ".jack".length()) + ".vm";

        System.out.println("Output file xml: " + outputFilePathXml);
        System.out.println("Output file vm: " + outputFilePathVm);

        WMWriter wmWriter = new WMWriter(new File(outputFilePathVm));
        PrintWriter xmlWriter = new PrintWriter(new File(outputFilePathXml));

        try {
            var compEngine = new CompilationEngine(tokenizer, xmlWriter, wmWriter);

            if (tokenizer.hasMoreTokens()) {
                tokenizer.advance();
            }

            if (tokenizer.tokenType() != TokenType.KEYWORD || tokenizer.keyWord() != KeyWord.CLASS) {
                throw new RuntimeException("Expected `class` keyword, but got " + tokenizer.getCurrentToken());
            }

            compEngine.compileClass();
        } catch (IOException e) {
            throw e;
        } finally {
            wmWriter.close();
            xmlWriter.close();
        }
    }
}
