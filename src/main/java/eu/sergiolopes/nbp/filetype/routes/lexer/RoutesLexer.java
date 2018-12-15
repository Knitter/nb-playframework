package eu.sergiolopes.nbp.filetype.routes.lexer;

import eu.sergiolopes.nbp.filetype.routes.jcclexer.JavaCharStream;
import eu.sergiolopes.nbp.filetype.routes.jcclexer.JavaParserTokenManager;
import eu.sergiolopes.nbp.filetype.routes.jcclexer.Token;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerRestartInfo;

public class RoutesLexer implements Lexer<RoutesTokenId> {

    private LexerRestartInfo<RoutesTokenId> info;
    private JavaParserTokenManager javaParserTokenManager;

    RoutesLexer(LexerRestartInfo<RoutesTokenId> info) {
        this.info = info;
        JavaCharStream stream = new JavaCharStream(info.input());
        javaParserTokenManager = new JavaParserTokenManager(stream);
    }

    @Override
    public org.netbeans.api.lexer.Token<RoutesTokenId> nextToken() {
        Token token = javaParserTokenManager.getNextToken();
        if (info.input().readLength() < 1) {
            return null;
        }
        return info.tokenFactory().createToken(RoutesLanguageHierarchy.getToken(token.kind));
    }

    @Override
    public Object state() {
        return null;
    }

    @Override
    public void release() {
    }
}
