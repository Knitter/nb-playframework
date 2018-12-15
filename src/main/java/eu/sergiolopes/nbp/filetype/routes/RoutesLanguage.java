package eu.sergiolopes.nbp.filetype.routes;

import eu.sergiolopes.nbp.filetype.routes.hints.RoutesHintsProvider;
import eu.sergiolopes.nbp.filetype.routes.lexer.RoutesTokenId;
import eu.sergiolopes.nbp.filetype.routes.parser.RoutesParser;
import org.netbeans.api.lexer.Language;
import org.netbeans.modules.csl.api.HintsProvider;
import org.netbeans.modules.csl.spi.DefaultLanguageConfig;
import org.netbeans.modules.csl.spi.LanguageRegistration;
import org.netbeans.modules.parsing.spi.Parser;

@LanguageRegistration(mimeType = RoutesLanguage.MIME_TYPE)
public class RoutesLanguage extends DefaultLanguageConfig {

    public static final String MIME_TYPE = "text/routes-file";

    @Override
    public Language getLexerLanguage() {
        return RoutesTokenId.getLanguage();
    }

    @Override
    public String getDisplayName() {
        return "Routes";
    }

    @Override
    public Parser getParser() {
        return new RoutesParser();
    }

    @Override
    public HintsProvider getHintsProvider() {
        return new RoutesHintsProvider();
    }

    @Override
    public boolean hasHintsProvider() {
        return true;
    }

    @Override
    public String getLineCommentPrefix() {
        return RoutesLanguageHelper.COMMENT_SYMBOL;
    }

}
