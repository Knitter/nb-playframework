package eu.sergiolopes.nbp.filetype.scalatemplate.format;

import eu.sergiolopes.nbp.filetype.scalatemplate.ScalaTemplateLanguage;
import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.modules.editor.indent.spi.Context;
import org.netbeans.modules.editor.indent.spi.ReformatTask;

@MimeRegistration(mimeType = ScalaTemplateLanguage.MIME_TYPE, service = ReformatTask.Factory.class)
public class ScalaTemplateReformatTaskFactory implements ReformatTask.Factory {

    @Override
    public ReformatTask createTask(Context context) {
        return new ScalaTemplateReformatTask(context);
    }
}
