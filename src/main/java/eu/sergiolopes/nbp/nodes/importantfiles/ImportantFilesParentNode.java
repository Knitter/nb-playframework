package eu.sergiolopes.nbp.nodes.importantfiles;

import eu.sergiolopes.nbp.PlayProject;
import java.awt.Image;
import org.netbeans.api.annotations.common.StaticResource;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.ImageUtilities;

public class ImportantFilesParentNode extends AbstractNode {

    private final PlayProject playProject;

    @StaticResource
    public static final String IMPORTANT_FILES_OPENED_PATH = "eu/sergiolopes/nbp/project/important_folder_open.png";
    public static final Image IMPORTANT_FILES_OPENED_IMAGE = ImageUtilities.loadImage(IMPORTANT_FILES_OPENED_PATH);
    @StaticResource
    public static final String IMPORTANT_FILES_CLOSED_PATH = "eu/sergiolopes/nbp/project/important_folder_closed.png";
    public static final Image IMPORTANT_FILES_CLOSED_IMAGE = ImageUtilities.loadImage(IMPORTANT_FILES_CLOSED_PATH);

    public ImportantFilesParentNode(PlayProject playProject) {
        super(Children.create(new ImportantFilesChildFactory(playProject), true));
        this.playProject = playProject;
        setDisplayName("Important Files");
    }

    @Override
    public Image getOpenedIcon(int type) {
        return IMPORTANT_FILES_OPENED_IMAGE;
    }

    @Override
    public Image getIcon(int type) {
        return IMPORTANT_FILES_CLOSED_IMAGE;
    }

}
