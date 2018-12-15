package eu.sergiolopes.nbp.filetype.conf.completion;

import eu.sergiolopes.nbp.util.ExceptionManager;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.Action;
import org.netbeans.spi.editor.completion.CompletionDocumentation;

public class ConfCompletionDocumentation implements CompletionDocumentation {

    private final ConfigurationItem item;

    public ConfCompletionDocumentation(ConfigurationItem item) {
        this.item = item;
    }

    @Override
    public String getText() {
        return item.documentation;
    }

    @Override
    public URL getURL() {
        if (item.url != null && !item.url.isEmpty()) {
            try {
                return new URL(item.url);
            } catch (MalformedURLException ex) {
                ExceptionManager.logException(ex);
            }
        }

        return null;
    }

    @Override
    public CompletionDocumentation resolveLink(String string) {
        return null;
    }

    @Override
    public Action getGotoSourceAction() {
        return null;
    }
}
