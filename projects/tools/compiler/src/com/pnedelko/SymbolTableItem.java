package com.pnedelko;

public class SymbolTableItem {
    private String type;
    private SymbolTableKind kind;
    private int index;

    public SymbolTableItem(String type, SymbolTableKind kind, int index) {
        this.type = type;
        this.kind = kind;
        this.index = index;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public SymbolTableKind getKind() {
        return kind;
    }

    public void setKind(SymbolTableKind kind) {
        this.kind = kind;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
