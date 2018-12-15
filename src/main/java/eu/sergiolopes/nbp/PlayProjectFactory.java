package eu.sergiolopes.nbp;

import java.io.IOException;
import javax.swing.ImageIcon;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.spi.project.ProjectFactory;
import org.netbeans.spi.project.ProjectFactory2;
import org.netbeans.spi.project.ProjectState;
import org.openide.filesystems.FileObject;
import org.openide.util.ImageUtilities;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = ProjectFactory.class, position = -1000)
public class PlayProjectFactory implements ProjectFactory2 {

    private static final String APP = "app";
    private static final String BUILDSBT = "build.sbt";

    private static final ImageIcon ICON = new ImageIcon(ImageUtilities.loadImage("eu/sergiolopes/nbp/play_icon.png"));

    /**
     * Checks if the given directory object contains the required files to be considered an Play project.
     *
     * @param directoryObj
     * @return
     */
    @Override
    public boolean isProject(FileObject directoryObj) {
        return directoryObj.getFileObject(APP) != null && directoryObj.getFileObject(BUILDSBT) != null;
    }

    @Override
    public Project loadProject(FileObject directoryObj, ProjectState state) throws IOException {
        return isProject(directoryObj) ? new PlayProject(directoryObj) : null;
    }

    @Override
    public void saveProject(final Project project) throws IOException, ClassCastException {
        //NOTE: Not supported at the moment
    }

    @Override
    public ProjectManager.Result isProject2(FileObject directoryObj) {
        if (isProject(directoryObj)) {
            return new ProjectManager.Result(ICON);
        }

        return null;
    }
}
