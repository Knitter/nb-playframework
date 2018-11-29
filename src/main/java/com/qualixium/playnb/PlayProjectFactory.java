package com.qualixium.playnb;

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

    private static final String APP_FOLDER = "app";
    private static final String BUILD_SBT_FILE = "build.sbt";

    private static final ImageIcon PROJECT_ICON = new ImageIcon(ImageUtilities.loadImage("com/qualixium/playnb/play_icon.png"));

    /**
     * Checks if the given directory object contains the required files to be considered an Play project.
     *
     * @param directoryObj
     * @return
     */
    @Override
    public boolean isProject(FileObject directoryObj) {
        return directoryObj.getFileObject(APP_FOLDER) != null && directoryObj.getFileObject(BUILD_SBT_FILE) != null;
    }

    //Specifies when the project will be opened, i.e., if the project exists: 
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
            return new ProjectManager.Result(PROJECT_ICON);
        }

        return null;
    }
}
