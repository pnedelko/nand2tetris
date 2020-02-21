package com.pnedelko;

public class SymbolTableTest {
    public static void main(String[] args) {
        var table = new SymbolTable();

        table.define("nAccounts", "int", SymbolTableKind.STATIC);
        table.define("bankCommission", "int", SymbolTableKind.STATIC);
        table.define("id", "int", SymbolTableKind.FIELD);
        table.define("owner", "String", SymbolTableKind.FIELD);
        table.define("balance", "int", SymbolTableKind.FIELD);

        System.out.println("nAccounts:" + table.indexOf("nAccounts") + " " + table.kindOf("nAccounts"));
        System.out.println("bankCommission:" + table.indexOf("bankCommission") + " " + table.kindOf("bankCommission"));
        System.out.println("id:" + table.indexOf("id") + " " + table.kindOf("id"));
        System.out.println("owner:" + table.indexOf("owner") + " " + table.kindOf("owner"));
        System.out.println("balance:" + table.indexOf("balance") + " " + table.kindOf("balance"));

        System.out.println("-------------------------");

        table.define("bankCommission", "int", SymbolTableKind.VAR);
        table.define("owner", "int", SymbolTableKind.VAR);
        System.out.println("bankCommission:" + table.indexOf("bankCommission") + " " + table.kindOf("bankCommission"));
        System.out.println("owner:" + table.indexOf("owner") + " " + table.kindOf("owner"));
    }
}
