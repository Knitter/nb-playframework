package eu.sergiolopes.nbp.nodes.testfile;

import eu.sergiolopes.nbp.PlayProject;
import eu.sergiolopes.nbp.actions.ActionsProcessor;
import eu.sergiolopes.nbp.actions.ActionsProcessor.ActionsEnum;
import static eu.sergiolopes.nbp.nodes.testfile.TestFileFilterNode.getFullyQualifyClassName;
import java.awt.event.ActionEvent;
import java.util.Optional;
import javax.swing.AbstractAction;
import org.openide.filesystems.FileObject;

public class TestFileContinuallyAction extends AbstractAction {

    private final PlayProject playProject;
    private final FileObject fileObject;
    private final ActionsProcessor actionsProcessor;

    public TestFileContinuallyAction(PlayProject playProject, FileObject fileObject) {
        this.playProject = playProject;
        this.fileObject = fileObject;
        this.actionsProcessor = new ActionsProcessor(playProject);
        putValue(NAME, "Test File Continually (~testOnly)");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String fullyQualifyClassName = getFullyQualifyClassName(
                playProject.getProjectDirectory().getPath(), fileObject.getPath());

        actionsProcessor.executeAction(ActionsEnum.TEST_ONLY_AUTOCOMPILE, Optional.of(fullyQualifyClassName));
    }

}
