package com.qualixium.playnb.filetype.routes.lexer;

import org.netbeans.api.lexer.Language;
import org.netbeans.api.lexer.TokenId;

public class RoutesTokenId implements TokenId {

    private final String name;
    private final String primaryCategory;
    private final int id;

    RoutesTokenId(String name, String primaryCategory, int id) {
        this.name = name;
        this.primaryCategory = primaryCategory;
        this.id = id;
    }

    public static Language<RoutesTokenId> getLanguage() {
        return new RoutesLanguageHierarchy().language();
    }

    @Override
    public String primaryCategory() {
        return primaryCategory;
    }

    @Override
    public int ordinal() {
        return id;
    }

    @Override
    public String name() {
        return name;
    }
}
