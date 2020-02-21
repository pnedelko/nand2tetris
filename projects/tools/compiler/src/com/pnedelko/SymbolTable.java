package com.pnedelko;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    private final HashMap<String, SymbolTableItem> classScope = new HashMap<>();
    private HashMap<String, SymbolTableItem> methodScope = new HashMap<>();

    public void startSubroutine() {
        methodScope = new HashMap<>();
    }

    public void define(String name, String type, SymbolTableKind kind) {
        var item = new SymbolTableItem(type, kind, varCount(kind));
        if (kind == SymbolTableKind.STATIC || kind == SymbolTableKind.FIELD) {
            classScope.put(name, item);
        } else {
            methodScope.put(name, item);
        }
    }

    public int varCount(SymbolTableKind kind) {
        HashMap<String, SymbolTableItem> scope;

        if (kind == SymbolTableKind.STATIC || kind == SymbolTableKind.FIELD) {
            scope = classScope;
        } else {
            scope = methodScope;
        }

        var count = 0;
        for (Map.Entry item : scope.entrySet()) {
            if (((SymbolTableItem)item.getValue()).getKind() == kind) {
                count++;
            }
        }
        return count;
    }

    public SymbolTableKind kindOf(String name) {
        var item = methodScope.get(name);
        if (item != null) {
            return item.getKind();
        }

        item = classScope.get(name);
        if (item != null) {
            return item.getKind();
        }

        return SymbolTableKind.NONE;
    }

    public String typeOf(String name) {
        var item = methodScope.get(name);
        if (item != null) {
            return item.getType();
        }

        item = classScope.get(name);
        if (item != null) {
            return item.getType();
        }

        return null;
    }

    public int indexOf(String name) {
        var item = methodScope.get(name);
        if (item != null) {
            return item.getIndex();
        }

        item = classScope.get(name);
        if (item != null) {
            return item.getIndex();
        }

        return -1;
    }
}
