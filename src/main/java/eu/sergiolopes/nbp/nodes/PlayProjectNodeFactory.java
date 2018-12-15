package eu.sergiolopes.nbp.nodes;

import eu.sergiolopes.nbp.PlayProject;
import eu.sergiolopes.nbp.nodes.importantfiles.ImportantFilesParentNode;
import eu.sergiolopes.nbp.nodes.sbtdependencies.SBTDependenciesParentNode;
import eu.sergiolopes.nbp.nodes.sbtdependencies.runtime.RuntimeParentDependencyNode;
import eu.sergiolopes.nbp.util.MiscUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.event.ChangeListener;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ui.support.NodeFactory;
import org.netbeans.spi.project.ui.support.NodeList;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.Node;

@NodeFactory.Registration(projectType = "eu-sergiolopes-nbp", position = -50000)
public class PlayProjectNodeFactory implements NodeFactory {

    @Override
    public NodeList<?> createNodes(Project project) {
        PlayProject p = project.getLookup().lookup(PlayProject.class);
        assert p != null;
        return new PlayProjectNodeList(p);
    }

    private class PlayProjectNodeList implements NodeList<Node> {

        PlayProject project;

        public PlayProjectNodeList(PlayProject project) {
            this.project = project;
        }

        @Override
        public List<Node> keys() {
            FileObject projectDirectory = project.getProjectDirectory();
            List<Node> result = new ArrayList<>();
            if (projectDirectory != null) {
                List<FileObject> listAllFileObjects = Arrays.asList(projectDirectory.getChildren());
                result.addAll(listAllFileObjects.stream()
                        .filter(fo -> fo.isFolder()
                                && (fo.getName().equals("app")
                                || fo.getName().equals("conf")
                                || fo.getName().equals("public")
                                || fo.getName().equals("test")))
                        .sorted(MiscUtil.FILE_OBJECT_COMPARATOR)
                        .map(fo -> {
                            try {
                                return DataObject.find(fo).getNodeDelegate();
                            } catch (DataObjectNotFoundException ex) {
                                return null;
                            }
                        })
                        .collect(Collectors.toList()));
            }

            SBTDependenciesParentNode sbtDependenciesParentNode = new SBTDependenciesParentNode(project);
            project.setSbtDependenciesParentNode(sbtDependenciesParentNode);
            result.add(sbtDependenciesParentNode);

            result.add(new RuntimeParentDependencyNode(project));
            result.add(new ImportantFilesParentNode(project));

            return result;
        }

        @Override
        public Node node(Node node) {
            FileObject fileObject = (FileObject) node.getLookup().lookup(FileObject.class);
            if (fileObject != null) {
                if (fileObject.isFolder()) {
                    switch (fileObject.getName()) {
                        case "app":
                            return new RootSourceFilterNode(node, project);
                        case "conf":
                            return new ConfFolderFilterNode(node);
                        case "test":
                            return new TestFolderFilterNode(node, project);
                        default:
                            return new FolderFilterNode(node);
                    }
                }
            }

            return node;
        }

        @Override
        public void addNotify() {
        }

        @Override
        public void removeNotify() {
        }

        @Override
        public void addChangeListener(ChangeListener cl) {
        }

        @Override
        public void removeChangeListener(ChangeListener cl) {
        }
    }
}
