package eu.sergiolopes.nbp.filetype.sbt.lexer;

import org.netbeans.api.lexer.Language;
import org.netbeans.api.lexer.TokenId;

public class SBTTokenId implements TokenId {

    private final String name;
    private final String primaryCategory;
    private final int id;

    SBTTokenId(String name, String primaryCategory, int id) {
        this.name = name;
        this.primaryCategory = primaryCategory;
        this.id = id;
    }

    public static Language<SBTTokenId> getLanguage() {
        return new SBTLanguageHierarchy().language();
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
