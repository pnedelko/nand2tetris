package com.pnedelko;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class JackTokenizer {
    private List<String> keywords = Arrays.asList(
            "class",
            "constructor",
            "function",
            "method",
            "field",
            "static",
            "var",
            "int",
            "char",
            "boolean",
            "void",
            "true",
            "false",
            "null",
            "this",
            "let",
            "do",
            "if",
            "else",
            "while",
            "return"
    );

    private List<Character> symbols = Arrays.asList(
            '{',
            '}',
            '(',
            ')',
            '[',
            ']',
            '.',
            ',',
            ';',
            '+',
            '-',
            '*',
            '/',
            '&',
            '|',
            '<',
            '>',
            '=',
            '-'
    );

    private final Map<String, KeyWord> keyWordMap = Map.ofEntries(
            Map.entry("class", KeyWord.CLASS),
            Map.entry("constructor", KeyWord.CONSTRUCTOR),
            Map.entry("function", KeyWord.FUNCTION),
            Map.entry("method", KeyWord.METHOD),
            Map.entry("field", KeyWord.FIELD),
            Map.entry("static", KeyWord.STATIC),
            Map.entry("var", KeyWord.VAR),
            Map.entry("int", KeyWord.INT),
            Map.entry("char", KeyWord.CHAR),
            Map.entry("boolean", KeyWord.BOOLEAN),
            Map.entry("void", KeyWord.VOID),
            Map.entry("true", KeyWord.TRUE),
            Map.entry("false", KeyWord.FALSE),
            Map.entry("null", KeyWord.NULL),
            Map.entry("this", KeyWord.THIS),
            Map.entry("let", KeyWord.LET),
            Map.entry("do", KeyWord.DO),
            Map.entry("if", KeyWord.IF),
            Map.entry("else", KeyWord.ELSE),
            Map.entry("while", KeyWord.WHILE),
            Map.entry("return", KeyWord.RETURN)
    );

    private String source = "";
    private int index = 0;
    private String currentToken = "";
    private TokenType currentTokenType;
    private String nextToken = "";
    private TokenType nextTokenType;

    public JackTokenizer(File file) throws IOException {
        var scanner = new Scanner(file);

        var stringBuilder = new StringBuilder();
        while (scanner.hasNextLine()) {
            var line = scanner.nextLine().trim();
            var index = line.indexOf("//");
            if (index > 0) {
                line = line.substring(0, index).trim();
                if (line.length() != 0) {
                    stringBuilder.append(line);
                }
            } else if (index == -1) {
                stringBuilder.append(line);
            }
        }
        scanner.close();

        source = stringBuilder.toString().replaceAll("/\\*.+?\\*/", "");
        advanceNextToken();
    }

    public String getNextToken() {
        return nextToken;
    }

    public TokenType getNextTokenType() {
        return nextTokenType;
    }

    public String getCurrentToken() {
        return currentToken;
    }

    public boolean hasMoreTokens() {
        return !nextToken.equals("");
    }

    private String parseSymbol() {
        var ch = getCurrentChar();
        moveCursor();
        return Character.toString(ch);
    }

    private String parseStringLiteral() {
        moveCursor(); //move to the next char after "
        var sb = new StringBuilder();
        while (getCurrentChar() != '"') {
            sb.append(getCurrentChar());
            moveCursor();
        }
        moveCursor(); //move to the next char after "
        return sb.toString();
    }

    private String parseKeywordOrIdentifier() {
        var sb = new StringBuilder();
        while (hasMoreChars() && Character.isAlphabetic(getCurrentChar())) {
            sb.append(getCurrentChar());
            moveCursor();
        }
        return sb.toString();
    }

    private String parseIntegerLiteral() {
        var sb = new StringBuilder();
        while (hasMoreChars() && Character.isDigit(getCurrentChar())) {
            sb.append(getCurrentChar());
            moveCursor();
        }
        return sb.toString();
    }

    private boolean hasMoreChars() {
        return index < source.length();
    }

    private char getCurrentChar() {
        return source.charAt(index);
    }

    private void moveCursor() {
        index++;
    }

    public void advance() throws IOException {
        currentToken = nextToken;
        currentTokenType = nextTokenType;
        advanceNextToken();
    }

    private void advanceNextToken() throws IOException {
        while (hasMoreChars()) {
            var currentChar = getCurrentChar();

            if (currentChar == ' ') {
                moveCursor();
                continue;
            }

            if (Character.isDigit(currentChar)) {
                nextToken = parseIntegerLiteral();
                nextTokenType = TokenType.INT_CONST;
                return;
            }

            if (currentChar == '"') {
                nextToken = parseStringLiteral();
                nextTokenType = TokenType.STRING_CONST;
                return;
            }

            if (Character.isAlphabetic(currentChar)) {
                nextToken = parseKeywordOrIdentifier();
                if (keywords.contains(nextToken)) {
                    nextTokenType = TokenType.KEYWORD;
                } else {
                    nextTokenType = TokenType.IDENTIFIER;
                }
                return;
            }

            nextToken = parseSymbol();
            nextTokenType = TokenType.SYMBOL;
            return;
        }

        nextToken = "";
        nextTokenType = TokenType.EOF;
    }

    public TokenType tokenType() {
        return currentTokenType;
    }

    public KeyWord keyWord() {
        return keyWordMap.get(currentToken);
    }

    public KeyWord nextKeyWord() {
        return keyWordMap.get(nextToken);
    }

    public char symbol() {
        return currentToken.charAt(0);
    }

    public char nextSymbol() {
        return nextToken.charAt(0);
    }

    public String identifier() {
        return currentToken;
    }

    public int intVal() {
        return Integer.parseInt(currentToken);
    }

    public String stringVal() {
        return currentToken;
    }
}
