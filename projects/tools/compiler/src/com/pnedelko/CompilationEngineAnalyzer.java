package com.pnedelko;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

public class CompilationEngineAnalyzer {
    private final List<Character> op = Arrays.asList('+', '-', '*', '/', '&', '|', '<', '>', '=');
    private final JackTokenizer tokenizer;
    private final PrintWriter writer;
    private boolean reachedEnd = false;
    private final SymbolTable symbolTable;

    public CompilationEngineAnalyzer(JackTokenizer tokenizer, PrintWriter writer) {
        this.tokenizer = tokenizer;
        this.writer = writer;
        this.symbolTable = new SymbolTable();
    }

    public void compileClass() throws IOException {
        this.writer.println("<class>");
        compileKeyword("class");
        compileIdentifier();
        compileSymbol('{');

        while (!(tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol() == '}')) {
            if (tokenizer.tokenType() == TokenType.KEYWORD) {
                switch (tokenizer.keyWord()) {
                    case STATIC:
                    case FIELD:
                        compileClassVarDec();
                        break;
                    case CONSTRUCTOR:
                    case FUNCTION:
                    case METHOD:
                        compileSubroutine();
                        break;
                    default:
                        throwExpectation("class var declaration or subroutine");
                }
            } else {
                break;
            }
        }

        compileSymbol('}');

        if (tokenizer.hasMoreTokens()) {
            throwExpectation("end of input");
        }

        this.writer.println("</class>");
    }

    public void compileClassVarDec() throws IOException {
        this.writer.println("<classVarDec>");

        var keyWord = tokenizer.keyWord();
        var identifierKind = keyWord == KeyWord.STATIC ? SymbolTableKind.STATIC : SymbolTableKind.FIELD;
        compileKeyword(tokenizer.getCurrentToken());

        var identifierType = tokenizer.getCurrentToken();
        compileVarType();

        while (tokenizer.symbol() != ';') {
            var identifierName = tokenizer.getCurrentToken();

            symbolTable.define(identifierName, identifierType, identifierKind);
            compileIdentifier();
            System.out.println(identifierName + ": " + identifierType + " " + identifierKind + " " + symbolTable.indexOf(identifierName));

            if (tokenizer.symbol() == ',') {
                compileSymbol(',');
            }
        }

        compileSymbol(';');

        this.writer.println("</classVarDec>");
    }

    public void compileSubroutine() throws IOException {
        this.writer.println("<subroutineDec>");

        symbolTable.startSubroutine();

        compileKeyword(tokenizer.getCurrentToken());
        compileReturnType();
        compileIdentifier();
        compileParameterList();
        compileSubroutineBody();

        this.writer.println("</subroutineDec>");
    }

    public void compileParameterList() throws IOException {
        compileSymbol('(');
        this.writer.println("<parameterList>");

        // this implementation allows (int a, )
        while (!(tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol() == ')')) {
            compileOneParam();

            if (tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol() == ',') {
                compileSymbol(tokenizer.symbol());
            }
        }

        this.writer.println("</parameterList>");
        compileSymbol(')');
    }

    private void compileOneParam() throws IOException {
        var identifierType = tokenizer.getCurrentToken();
        var identifierKind = SymbolTableKind.ARG;
        compileVarType();

        var identifierName = tokenizer.identifier();
        symbolTable.define(identifierName, identifierType, identifierKind);
        System.out.println(identifierName + ": " + identifierType + " " + identifierKind + " " + symbolTable.indexOf(identifierName));

        compileIdentifier();
    }

    private void compileVarType() throws IOException {
        switch (tokenizer.tokenType()) {
            case KEYWORD:
                switch (tokenizer.keyWord()) {
                    case INT:
                    case CHAR:
                    case BOOLEAN:
                        compileKeyword(tokenizer.getCurrentToken());
                        break;
                    default:
                        throwExpectation("int|char|boolean");
                }
                break;
            case IDENTIFIER:
                compileIdentifier();
                break;
            default:
                throwExpectation("int|char|boolean");
        }
    }

    private void compileSubroutineBody() throws IOException {
        this.writer.println("<subroutineBody>");
        compileSymbol('{');

        while (tokenizer.tokenType() == TokenType.KEYWORD && tokenizer.keyWord() == KeyWord.VAR) {
            compileVarDec();
        }
        compileStatements();

        compileSymbol('}');
        this.writer.println("</subroutineBody>");
    }

    public void compileReturnType() throws IOException {
        switch (tokenizer.tokenType()) {
            case KEYWORD:
                switch (tokenizer.keyWord()) {
                    case VOID:
                    case INT:
                    case CHAR:
                    case BOOLEAN:
                        compileKeyword(tokenizer.getCurrentToken());
                        break;
                    default:
                        throwExpectation("void|int|char|boolean");
                }
                break;
            case IDENTIFIER:
                compileIdentifier();
                break;
            default:
                throwExpectation("void|int|char|boolean");
        }
    }

    public void compileVarDec() throws IOException {
        this.writer.println("<varDec>");

        var identifierKind = SymbolTableKind.VAR;
        compileKeyword("var");
        var identifierType = tokenizer.getCurrentToken();
        compileVarType();

        while (!(tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol() == ';')) {
            var identifierName = tokenizer.getCurrentToken();
            symbolTable.define(identifierName, identifierType, identifierKind);
            System.out.println(identifierName + ": " + identifierType + " " + identifierKind + " " + symbolTable.indexOf(identifierName));
            compileIdentifier();

            if (tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol() == ',') {
                compileSymbol(',');
            }
        }
        compileSymbol(';');

        this.writer.println("</varDec>");
    }

    public void compileStatements() throws IOException {
        this.writer.println("<statements>");
        while(tokenizer.tokenType() == TokenType.KEYWORD) {
            switch (tokenizer.keyWord()) {
                case LET:
                    compileLet();
                    break;
                case WHILE:
                    compileWhile();
                    break;
                case IF:
                    compileIf();
                    break;
                case DO:
                    compileDo();
                    break;
                case RETURN:
                    compileReturn();
                    break;
                default:
                    throwExpectation("expressions keyword");
            }
        }
        this.writer.println("</statements>");
    }

    public void compileDo() throws IOException {
        this.writer.println("<doStatement>");
        compileKeyword("do");
        compileIdentifier();

        if (tokenizer.symbol() == '(') {
            compileSymbol('(');
            compileExpressionList();
            compileSymbol(')');
        } else if (tokenizer.symbol() == '.') {
            compileSymbol('.');
            compileIdentifier();
            compileSymbol('(');
            compileExpressionList();
            compileSymbol(')');
        } else {
            throwExpectation("( | .");
        }
        compileSymbol(';');

        this.writer.println("</doStatement>");
    }

    public void compileLet() throws IOException {
        this.writer.println("<letStatement>");
        compileKeyword("let");
        compileIdentifier();

        if (tokenizer.symbol() == '[') {
            compileSymbol('[');
            compileExpression();
            compileSymbol(']');
        }

        compileSymbol('=');
        compileExpression();
        compileSymbol(';');

        this.writer.println("</letStatement>");
    }

    public void compileWhile() throws IOException {
        this.writer.println("<whileStatement>");

        compileKeyword("while");

        compileSymbol('(');
        compileExpression();
        compileSymbol(')');

        compileSymbol('{');
        compileStatements();
        compileSymbol('}');

        this.writer.println("</whileStatement>");
    }

    public void compileReturn() throws IOException {
        this.writer.println("<returnStatement>");
        compileKeyword("return");

        if (tokenizer.tokenType() != TokenType.SYMBOL || tokenizer.symbol() != ';') {
            compileExpression();
        }

        compileSymbol(';');
        this.writer.println("</returnStatement>");
    }

    public void compileIf() throws IOException {
        this.writer.println("<ifStatement>");
        compileKeyword("if");

        compileSymbol('(');
        compileExpression();
        compileSymbol(')');

        compileSymbol('{');
        compileStatements();
        compileSymbol('}');

        if (tokenizer.tokenType() == TokenType.KEYWORD && tokenizer.keyWord() == KeyWord.ELSE) {
            compileKeyword("else");

            compileSymbol('{');
            compileStatements();
            compileSymbol('}');
        }

        this.writer.println("</ifStatement>");
    }

    public void compileExpression() throws IOException {
        this.writer.println("<expression>");
        compileTerm();

        if (op.contains(tokenizer.symbol())) {
            compileSymbol(tokenizer.symbol());
            compileTerm();
        }

        this.writer.println("</expression>");
    }

    public void compileTerm() throws IOException {
        this.writer.println("<term>");

        switch (tokenizer.tokenType()) {
            case INT_CONST:
                compileIntegerConst();
                break;
            case STRING_CONST:
                compileStringConst();
                break;
            case KEYWORD:
                switch (tokenizer.keyWord()) {
                    case TRUE:
                        compileKeyword("true");
                        break;
                    case FALSE:
                        compileKeyword("false");
                        break;
                    case NULL:
                        compileKeyword("null");
                        break;
                    case THIS:
                        compileKeyword("this");
                        break;
                    default:
                        throwExpectation("true | false | null | this");
                }
                break;
            case IDENTIFIER:
                compileIdentifier();

                if (tokenizer.tokenType() == TokenType.SYMBOL) {
                    switch (tokenizer.symbol()) {
                        // subroutineName(expressionsList)
                        case '(':
                            compileSymbol('(');
                            compileExpressionList();
                            compileSymbol(')');
                            break;
                        // varName[expression]
                        case '[':
                            compileSymbol('[');
                            compileExpression();
                            compileSymbol(']');
                            break;
                        // varName.subroutineName(expressionsList)
                        case '.':
                            compileSymbol('.');
                            compileIdentifier();
                            compileSymbol('(');
                            compileExpressionList();
                            compileSymbol(')');
                            break;
                    }
                    break;
                }

                break;
            case SYMBOL:
                switch (tokenizer.symbol()) {
                    case '(':
                        compileSymbol('(');
                        compileExpression();
                        compileSymbol(')');
                        break;
                    case '-':
                    case '~':
                        compileSymbol(tokenizer.symbol());
                        compileTerm();
                        break;
                    default:
                        throwExpectation("( | -");
                }
                break;
            default:
                throwExpectation("valid token type for term");
        }

        this.writer.println("</term>");
    }


    // todo maybe its worth to move ( and ) compilation inside this method
    public void compileExpressionList() throws IOException {
        this.writer.println("<expressionList>");

        while (tokenizer.symbol() != ')') {
            compileExpression();

            if (tokenizer.symbol() == ',') {
                compileSymbol(',');
            }
        }

        this.writer.println("</expressionList>");
    }

    private void compileIdentifier() throws IOException {
        if (tokenizer.tokenType() != TokenType.IDENTIFIER) {
            throwExpectation("identifier");
        }
        this.writer.println("<identifier> " + tokenizer.identifier() + " </identifier>");
        movePointer();
    }

    private void compileSymbol(char symbol) throws IOException {
        if (tokenizer.tokenType() != TokenType.SYMBOL || tokenizer.symbol() != symbol) {
            throwExpectation(symbol);
        }
        this.writer.println("<symbol> " + symbol + " </symbol>");
        movePointer();
    }

    private void compileIntegerConst() throws IOException {
        this.writer.println("<integerConstant> " + tokenizer.intVal() + " </integerConstant>");
        movePointer();
    }

    private void compileStringConst() throws IOException {
        this.writer.println("<stringConstant> " + tokenizer.stringVal() + " </stringConstant>");
        movePointer();
    }

    // todo: add check for exact keyword
    private void compileKeyword(String keyword) throws IOException {
        if (tokenizer.tokenType() != TokenType.KEYWORD) {
            throwExpectation(keyword);
        }
        this.writer.println("<keyword> " + keyword + " </keyword>");

        movePointer();
    }

    private void throwExpectation(String expected) {
        throw new RuntimeException("Expected " + expected + " but got " + tokenizer.getCurrentToken());
    }

    private void throwExpectation(char expected) {
        throwExpectation(Character.toString(expected));
    }

    private void movePointer() throws IOException {
        if (!tokenizer.hasMoreTokens()) {
            if (reachedEnd) {
                throw new IOException("Unexpected end of input");
            }
            reachedEnd = true;
        }
        tokenizer.advance();
    }
}
