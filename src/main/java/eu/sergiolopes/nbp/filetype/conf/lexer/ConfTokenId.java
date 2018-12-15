package eu.sergiolopes.nbp.filetype.conf.lexer;

import org.netbeans.api.lexer.Language;
import org.netbeans.api.lexer.TokenId;

public class ConfTokenId implements TokenId {

    private final String name;
    private final String primaryCategory;
    private final int id;

    ConfTokenId(String name, String primaryCategory, int id) {
        this.name = name;
        this.primaryCategory = primaryCategory;
        this.id = id;
    }

    public static Language<ConfTokenId> getLanguage() {
        return new ConfLanguageHierarchy().language();
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
