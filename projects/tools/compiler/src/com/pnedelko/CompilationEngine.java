package com.pnedelko;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

public class CompilationEngine {
    private final List<Character> op = Arrays.asList('+', '-', '*', '/', '&', '|', '<', '>', '=');
    private final JackTokenizer tokenizer;
    private final PrintWriter writer;
    private boolean reachedEnd = false;
    private final SymbolTable symbolTable;
    private final WMWriter wmWriter;
    private String className;
    private int ifIndex = 0;
    private int whileIndex = 0;

    public CompilationEngine(JackTokenizer tokenizer, PrintWriter writer, WMWriter wmWriter) {
        this.tokenizer = tokenizer;
        this.writer = writer;
        this.symbolTable = new SymbolTable();
        this.wmWriter = wmWriter;
    }

    public void compileClass() throws IOException {
        this.writer.println("<class>");
        compileKeyword("class");
        className = tokenizer.identifier();
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

            if (tokenizer.symbol() == ',') {
                compileSymbol(',');
            }
        }

        compileSymbol(';');

        this.writer.println("</classVarDec>");
    }

    public void compileSubroutine() throws IOException {
        symbolTable.startSubroutine();

        //each new subroutine resets if and while indexes
        this.ifIndex = 0;
        this.whileIndex = 0;

        this.writer.println("<subroutineDec>");

        //subroutine type
        var subroutineType = tokenizer.keyWord();
        compileKeyword(tokenizer.getCurrentToken());

        //return type
        compileReturnType();

        //subroutine name
        var subroutineName = tokenizer.identifier();
        compileIdentifier();

        //first argument of methods is "this"
        if (subroutineType == KeyWord.METHOD) {
            symbolTable.define("this", className, SymbolTableKind.ARG);
        }

        //params list
        compileParameterList();

        //subroutine body
        var nLocals = 0;
        this.writer.println("<subroutineBody>");
        compileSymbol('{');

        //var declarations
        while (tokenizer.tokenType() == TokenType.KEYWORD && tokenizer.keyWord() == KeyWord.VAR) {
            nLocals += compileVarDec();
        }

        wmWriter.writeFunction(className + "." + subroutineName, nLocals);
        if (subroutineType == KeyWord.CONSTRUCTOR) {
            wmWriter.writePush("constant", symbolTable.varCount(SymbolTableKind.FIELD));
            wmWriter.writeCall("Memory.alloc", 1);
            wmWriter.writePop("pointer", 0);
        } else if (subroutineType == KeyWord.METHOD) {
            wmWriter.writePush("argument", 0);
            wmWriter.writePop("pointer", 0);
        }

        //statements
        compileStatements();

        compileSymbol('}');
        this.writer.println("</subroutineBody>");

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

    public int compileVarDec() throws IOException {
        this.writer.println("<varDec>");
        int nVars = 0;

        var identifierKind = SymbolTableKind.VAR;
        compileKeyword("var");
        var identifierType = tokenizer.getCurrentToken();
        compileVarType();

        while (!(tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol() == ';')) {
            nVars++;
            var identifierName = tokenizer.getCurrentToken();
            symbolTable.define(identifierName, identifierType, identifierKind);
            compileIdentifier();

            if (tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol() == ',') {
                compileSymbol(',');
            }
        }
        compileSymbol(';');

        this.writer.println("</varDec>");

        return nVars;
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

        var identifierName = tokenizer.identifier();
        var identifierKind = symbolTable.kindOf(identifierName);
        var identifierType = symbolTable.typeOf(identifierName);
        var identifierIndex = symbolTable.indexOf(identifierName);

        compileIdentifier();

        if (tokenizer.symbol() == '(') {
            compileSymbol('(');

            int nArgs = 1;
            wmWriter.writePush("pointer", 0); //pushing this
            nArgs += compileExpressionList();
            var functionName = className + "." + identifierName;
            wmWriter.writeCall(functionName, nArgs);

            compileSymbol(')');
        } else if (tokenizer.symbol() == '.') {
            compileSymbol('.');
            int nArgs = 0;

            var functionName = identifierName + "." + tokenizer.identifier();

            if (identifierKind != SymbolTableKind.NONE) {
                var segment = getSegmentByIdentifierKind(identifierKind);
                wmWriter.writePush(segment, identifierIndex);
                functionName = identifierType + "." + tokenizer.identifier();
                nArgs++;
            }

            compileIdentifier();
            compileSymbol('(');

            nArgs += compileExpressionList();
            wmWriter.writeCall(functionName, nArgs);

            compileSymbol(')');
        } else {
            throwExpectation("( | .");
        }
        compileSymbol(';');

        // "do" statement doesn't return anything, therefore we ignore the returned value
        wmWriter.writePop("temp", 0);

        this.writer.println("</doStatement>");
    }

    public void compileLet() throws IOException {
        this.writer.println("<letStatement>");
        compileKeyword("let");

        var varKind = symbolTable.kindOf(tokenizer.identifier());
        var varIndex = symbolTable.indexOf(tokenizer.identifier());
        var varName = tokenizer.identifier();
        String segment = getSegmentByIdentifierKind(varKind);

        compileIdentifier();

        if (tokenizer.symbol() == '[') {
            compileSymbol('[');
            compileExpression();
            compileSymbol(']');
            wmWriter.writePush(segment, varIndex);
            wmWriter.writeArithmetic('+');

            compileSymbol('=');
            compileExpression();
            compileSymbol(';');

            wmWriter.writePop("temp", 0); //pop result of exp
            wmWriter.writePop("pointer", 1); //set pointer to arr[exp] address
            wmWriter.writePush("temp", 0); //put result of exp on stack
            wmWriter.writePop("that", 0); //pop exp result into the address app[exp]
        } else {
            compileSymbol('=');
            compileExpression();
            compileSymbol(';');

            wmWriter.writePop(segment, varIndex);
        }

        this.writer.println("</letStatement>");
    }

    public void compileWhile() throws IOException {
        var whileIndex = this.whileIndex++;
        this.writer.println("<whileStatement>");

        compileKeyword("while");

        wmWriter.writeLabel("WHILE_EXP" + whileIndex);

        compileSymbol('(');
        compileExpression();
        compileSymbol(')');

        wmWriter.writeSinglePrependCommand('~'); //not
        wmWriter.writeIf("WHILE_END" + whileIndex);

        compileSymbol('{');
        compileStatements();
        compileSymbol('}');

        wmWriter.writeGoto("WHILE_EXP" + whileIndex);
        wmWriter.writeLabel("WHILE_END" + whileIndex);

        this.writer.println("</whileStatement>");
    }

    public void compileReturn() throws IOException {
        this.writer.println("<returnStatement>");
        compileKeyword("return");

        if (tokenizer.tokenType() != TokenType.SYMBOL || tokenizer.symbol() != ';') {
            compileExpression();
        } else {
            wmWriter.writePush("constant", 0);
        }

        compileSymbol(';');

        wmWriter.writeReturn();

        this.writer.println("</returnStatement>");
    }

    public void compileIf() throws IOException {
        var ifIndex = this.ifIndex++;

        this.writer.println("<ifStatement>");
        compileKeyword("if");

        compileSymbol('(');
        compileExpression();
        compileSymbol(')');

        wmWriter.writeIf("IF_TRUE" + ifIndex);
        wmWriter.writeGoto("IF_FALSE" + ifIndex);

        wmWriter.writeLabel("IF_TRUE" + ifIndex);
        compileSymbol('{');
        compileStatements();
        compileSymbol('}');

        if (tokenizer.tokenType() == TokenType.KEYWORD && tokenizer.keyWord() == KeyWord.ELSE) {
            wmWriter.writeGoto("IF_END" + ifIndex); // when if statements ended -> go to the end of the if-else
            compileKeyword("else");

            wmWriter.writeLabel("IF_FALSE" + ifIndex);
            compileSymbol('{');
            compileStatements();
            compileSymbol('}');

            wmWriter.writeLabel("IF_END" + ifIndex);
        } else {
            // if without else
            wmWriter.writeLabel("IF_FALSE" + ifIndex);
        }

        this.writer.println("</ifStatement>");
    }

    public void compileExpression() throws IOException {
        this.writer.println("<expression>");
        compileTerm();

        if (op.contains(tokenizer.symbol())) {
            var arithmeticOperation = tokenizer.symbol();
            compileSymbol(tokenizer.symbol());
            compileTerm();
            wmWriter.writeArithmetic(arithmeticOperation);
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
                        wmWriter.writePush("constant", 0);
                        wmWriter.writeSinglePrependCommand('~');
                        break;
                    case FALSE:
                        compileKeyword("false");
                        wmWriter.writePush("constant", 0);
                        break;
                    case NULL:
                        compileKeyword("null");
                        wmWriter.writePush("constant", 0);
                        break;
                    case THIS:
                        compileKeyword("this");
                        wmWriter.writePush("pointer", 0);
                        break;
                    default:
                        throwExpectation("true | false | null | this");
                }
                break;
            case IDENTIFIER:
                var identifierName = tokenizer.identifier();
                var kindOfIdentifier = symbolTable.kindOf(identifierName);
                var identifierType = symbolTable.typeOf(identifierName);
                var identifierIndex = symbolTable.indexOf(identifierName);

                // if (kindOfIdentifier != SymbolTableKind.NONE) {
                //     String segment = getSegmentByIdentifierKind(kindOfIdentifier);
                //     wmWriter.writePush(segment, symbolTable.indexOf(identifierName));
                // }
                compileIdentifier();

                if (tokenizer.tokenType() == TokenType.SYMBOL) {
                    switch (tokenizer.symbol()) {
                        // subroutineName(expressionsList)
                        case '(':
                            compileSymbol('(');
                            var nArgs = compileExpressionList();
                            wmWriter.writeCall(identifierName, nArgs);
                            compileSymbol(')');
                            break;
                        // varName[expression]
                        case '[':
                            compileSymbol('[');
                            compileExpression();
                            compileSymbol(']');

                            var sector = getSegmentByIdentifierKind(kindOfIdentifier);
                            wmWriter.writePush(sector, identifierIndex);
                            wmWriter.writeArithmetic('+');

                            wmWriter.writePop("pointer", 1); //pop address of arr element to the pointer
                            wmWriter.writePush("that", 0); //push on stack a value of arr[exp]

                            break;
                        // varName.subroutineName(expressionsList)
                        case '.':
                            var nArgs2 = 0;
                            compileSymbol('.');

                            String functionName;

                            // ClassName.subroutineName
                            if (kindOfIdentifier == SymbolTableKind.NONE) {
                                functionName = identifierName + "." + tokenizer.identifier();
                            }
                            // varName.subroutineName
                            else {
                                String segment = getSegmentByIdentifierKind(kindOfIdentifier);
                                wmWriter.writePush(segment, symbolTable.indexOf(identifierName));
                                nArgs2++;
                                functionName = identifierType + "." + tokenizer.identifier();
                            }

                            compileIdentifier();
                            compileSymbol('(');
                            nArgs2 += compileExpressionList();
                            wmWriter.writeCall(functionName, nArgs2);
                            compileSymbol(')');
                            break;
                        default:
                            //normal variable
                            if (kindOfIdentifier != SymbolTableKind.NONE) {
                                String segment = getSegmentByIdentifierKind(kindOfIdentifier);
                                wmWriter.writePush(segment, symbolTable.indexOf(identifierName));
                            }
                    }
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
                        var command = tokenizer.symbol();
                        compileSymbol(tokenizer.symbol());
                        compileTerm();
                        wmWriter.writeSinglePrependCommand(command);
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


    /**
     * Compiles expressions list
     * @return int nExpr Number of expressions
     * @throws IOException
     */
    public int compileExpressionList() throws IOException {
        this.writer.println("<expressionList>");

        int nExpr = 0;
        while (tokenizer.symbol() != ')') {
            compileExpression();
            nExpr++;

            if (tokenizer.symbol() == ',') {
                compileSymbol(',');
            }
        }

        this.writer.println("</expressionList>");
        return nExpr;
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

        wmWriter.writePush("constant", tokenizer.intVal());

        movePointer();
    }

    private void compileStringConst() throws IOException {
        var str = tokenizer.stringVal();
        this.writer.println("<stringConstant> " + str + " </stringConstant>");

        wmWriter.writePush("constant", tokenizer.stringVal().length());
        wmWriter.writeCall("String.new", 1);

        for (int i = 0; i < tokenizer.stringVal().length(); i++) {
            wmWriter.writePush("constant", str.charAt(i));
            wmWriter.writeCall("String.appendChar", 2);
        }

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

    private String getSegmentByIdentifierKind(SymbolTableKind kind) {
        switch (kind) {
            case STATIC:
                return "static";
            case FIELD:
                return "this";
            case ARG:
                return  "argument";
            case VAR:
                return "local";
            default:
                throw new RuntimeException("Unexpected identifier kind: " + kind);
        }
    }
}
