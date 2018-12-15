package eu.sergiolopes.nbp.filetype.scalatemplate.lexer;

import org.netbeans.api.lexer.Language;
import org.netbeans.api.lexer.TokenId;

public class ScalaTemplateTokenId implements TokenId {

    private final String name;
    private final String primaryCategory;
    private final int id;

    ScalaTemplateTokenId(String name, String primaryCategory, int id) {
        this.name = name;
        this.primaryCategory = primaryCategory;
        this.id = id;
    }

    public static Language<ScalaTemplateTokenId> getLanguage() {
        return new ScalaTemplateLanguageHierarchy().language();
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
