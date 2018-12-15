package eu.sergiolopes.nbp.filetype.routes.format;

import eu.sergiolopes.nbp.filetype.routes.RoutesLanguage;
import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.modules.editor.indent.spi.Context;
import org.netbeans.modules.editor.indent.spi.ReformatTask;

@MimeRegistration(mimeType = RoutesLanguage.MIME_TYPE, service = ReformatTask.Factory.class)
public class RoutesReformatTaskFactory implements ReformatTask.Factory {

    @Override
    public ReformatTask createTask(Context context) {
        return new RoutesReformatTask(context);
    }
}
