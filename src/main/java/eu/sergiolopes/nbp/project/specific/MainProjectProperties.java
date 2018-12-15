package eu.sergiolopes.nbp.project.specific;

import static eu.sergiolopes.nbp.project.specific.ProjectSpecificSettings.KEYS_COMMAND_PARAMETERS;
import static eu.sergiolopes.nbp.project.specific.ProjectSpecificSettings.KEYS_DEBUG_PORT;
import static eu.sergiolopes.nbp.project.specific.ProjectSpecificSettings.KEYS_RUN_PORT;
import eu.sergiolopes.nbp.util.ExceptionManager;
import java.awt.event.ActionEvent;
import java.io.IOException;
import javax.swing.JComponent;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ui.support.ProjectCustomizer;
import org.netbeans.spi.project.ui.support.ProjectCustomizer.Category;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

public class MainProjectProperties implements ProjectCustomizer.CompositeCategoryProvider {

    private static final String GENERAL = "General";

    @ProjectCustomizer.CompositeCategoryProvider.Registration(projectType = "eu-sergiolopes-nbp", position = 10)
    public static MainProjectProperties createGeneral() {
        return new MainProjectProperties();
    }

    @NbBundle.Messages("LBL_Config_General=General")
    @Override
    public Category createCategory(Lookup lkp) {
        return ProjectCustomizer.Category.create(GENERAL, Bundle.LBL_Config_General(), null);
    }

    @Override
    public JComponent createComponent(Category category, Lookup lkp) {
        Project project = Utilities.actionsGlobalContext().lookup(Project.class);
        PanelMainProperties panelMainProperties = new PanelMainProperties(project);
        category.setOkButtonListener((ActionEvent e) -> {
            try {
                String runPort = panelMainProperties.getRunPort();
                String debugPort = panelMainProperties.getDebugPort();
                String parameters = panelMainProperties.getParameters();

                ProjectSpecificSettings settings = new ProjectSpecificSettings(project.getProjectDirectory().getPath());
                settings.setValue(KEYS_RUN_PORT, runPort, false);
                settings.setValue(KEYS_DEBUG_PORT, debugPort, false);
                settings.setValue(KEYS_COMMAND_PARAMETERS, parameters, false);

                settings.store();
            } catch (IOException ex) {
                ExceptionManager.logException(ex);
            }
        });

        return panelMainProperties;
    }
}
