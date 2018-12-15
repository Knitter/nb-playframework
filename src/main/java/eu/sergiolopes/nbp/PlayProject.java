package eu.sergiolopes.nbp;

import eu.sergiolopes.nbp.actions.ActionsProcessor;
import eu.sergiolopes.nbp.actions.ActionsProcessor.ActionsEnum;
import eu.sergiolopes.nbp.actions.PlayIDEActionProvider;
import eu.sergiolopes.nbp.classpath.ClassPathProviderImpl;
import eu.sergiolopes.nbp.nodes.sbtdependencies.SBTDependenciesParentNode;
import eu.sergiolopes.nbp.project.specific.ProjectCustomizerProvider;
import static eu.sergiolopes.nbp.util.MiscUtil.Language.JAVA;
import static eu.sergiolopes.nbp.util.MiscUtil.Language.SCALA;
import java.awt.Image;
import java.beans.PropertyChangeListener;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.event.ChangeListener;
import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.spi.project.support.GenericSources;
import org.netbeans.spi.project.ui.LogicalViewProvider;
import org.netbeans.spi.project.ui.support.CommonProjectActions;
import org.netbeans.spi.project.ui.support.NodeFactorySupport;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.nodes.NodeNotFoundException;
import org.openide.nodes.NodeOp;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;

public class PlayProject implements Project {

    private Lookup lookup;
    private SBTDependenciesParentNode sbtDependenciesParentNode;

    private final FileObject projectDir;
    private final ClassPathProviderImpl classPathProviderImpl;

    public static final String PLUGIN_NAME = "NetBeans Play";
    public static final boolean IS_PRODUCTION = true;

    PlayProject(FileObject dir) {
        projectDir = dir;
        classPathProviderImpl = new ClassPathProviderImpl(this);
    }

    @Override
    public FileObject getProjectDirectory() {
        return projectDir;
    }

    public String getProjectName() {
        return projectDir.getName();
    }

    public ClassPathProviderImpl getClassPathProviderImpl() {
        return classPathProviderImpl;
    }

    @Override
    public Lookup getLookup() {
        if (lookup == null) {
            lookup = Lookups.fixed(new Object[]{
                this,
                new Info(),
                new PlayProjectLogicalView(this),
                new PlayIDEActionProvider(this),
                classPathProviderImpl,
                new ProjectCustomizerProvider(this),
                new PlayProjectSources(this)
            });
        }

        return lookup;
    }

    private final class Info implements ProjectInformation {

        @StaticResource()
        public static final String ICON = "eu/sergiolopes/nbp/play_icon.png";

        @Override
        public Icon getIcon() {
            return new ImageIcon(ImageUtilities.loadImage(ICON));
        }

        @Override
        public String getName() {
            return getProjectDirectory().getName();
        }

        @Override
        public String getDisplayName() {
            return getName();
        }

        @Override
        public void addPropertyChangeListener(PropertyChangeListener pcl) {
            //do nothing, won't change 
        }

        @Override
        public void removePropertyChangeListener(PropertyChangeListener pcl) {
            //do nothing, won't change 
        }

        @Override
        public Project getProject() {
            return PlayProject.this;
        }
    }

    class PlayProjectLogicalView implements LogicalViewProvider {

        private final PlayProject project;

        public PlayProjectLogicalView(PlayProject project) {
            this.project = project;
        }

        @Override
        public Node createLogicalView() {
            try {
                //Obtain the project directory's node: 
                FileObject projectDirectory = project.getProjectDirectory();
                DataFolder projectFolder = DataFolder.findFolder(projectDirectory);
                Node nodeOfProjectFolder = projectFolder.getNodeDelegate();

                //Decorate the project directory's node: 
                return new ProjectNode(nodeOfProjectFolder, project);
            } catch (DataObjectNotFoundException donfe) {
                Exceptions.printStackTrace(donfe);
                //Fallback-the directory couldn't be created - //read-only filesystem or something evil happened 
                return new AbstractNode(Children.LEAF);
            }
        }

        private final class ProjectNode extends FilterNode {

            final PlayProject project;
            private final ActionsProcessor actionsProcessor;

            public ProjectNode(Node node, PlayProject project) throws DataObjectNotFoundException {
                super(node,
                        NodeFactorySupport.createCompositeChildren(project, "Projects/eu-sergiolopes-nbp/Nodes"),
                        new ProxyLookup(new Lookup[]{Lookups.singleton(project), node.getLookup()})
                );

                this.project = project;
                this.actionsProcessor = new ActionsProcessor(project);
            }

            @Override
            public Action[] getActions(boolean arg0) {
                return new Action[]{
                    CommonProjectActions.newFileAction(),
                    CommonProjectActions.deleteProjectAction(),
                    CommonProjectActions.setAsMainProjectAction(),
                    null,
                    actionsProcessor.createAction(ActionsEnum.RUN_AUTOCOMPILE),
                    actionsProcessor.createAction(ActionsEnum.RUN),
                    null,
                    actionsProcessor.createAction(ActionsEnum.CLEAN),
                    actionsProcessor.createAction(ActionsEnum.COMPILE),
                    actionsProcessor.createAction(ActionsEnum.CLEAN_BUILD),
                    null,
                    actionsProcessor.createAction(ActionsEnum.DEBUG_AUTOCOMPILE),
                    actionsProcessor.createAction(ActionsEnum.DEBUG),
                    null,
                    actionsProcessor.createAction(ActionsEnum.TEST_AUTOCOMPILE),
                    actionsProcessor.createAction(ActionsEnum.TEST),
                    actionsProcessor.createAction(ActionsEnum.JACOCO4SBT),
                    actionsProcessor.createAction(ActionsEnum.SBT_SCOVERAGE),
                    null,
                    actionsProcessor.createAction(ActionsEnum.RELOAD_PROJECT),
                    null,
                    CommonProjectActions.closeProjectAction(),
                    null,
                    CommonProjectActions.customizeProjectAction()
                };

            }

            @Override
            public Image getIcon(int type) {
                return ImageUtilities.loadImage(Info.ICON);
            }

            @Override
            public Image getOpenedIcon(int type) {
                return getIcon(type);
            }

            @Override
            public String getDisplayName() {
                return project.getProjectDirectory().getName();
            }
        }

        @Override
        public Node findPath(Node root, Object target) {
            //This functionality was taken from 
            //org.netbeans.modules.php.project.ui.logicalview.PhpLogicalViewProvider.findPath
            Project p = root.getLookup().lookup(Project.class);
            if (p == null) {
                return null;
            }

            // Check each child node in turn.
            Node[] children = root.getChildren().getNodes(true);
            for (Node node : children) {
                if (target instanceof DataObject || target instanceof FileObject) {
                    FileObject kidFO = node.getLookup().lookup(FileObject.class);
                    if (kidFO == null) {
                        continue;
                    }

                    // Copied from org.netbeans.spi.java.project.support.ui.TreeRootNode.PathFinder.findPath:
                    FileObject targetFO;
                    if (target instanceof DataObject) {
                        targetFO = ((DataObject) target).getPrimaryFile();
                    } else {
                        targetFO = (FileObject) target;
                    }

                    Project owner = FileOwnerQuery.getOwner(targetFO);
                    if (!p.equals(owner)) {
                        return null;
                    }

                    if (kidFO == targetFO) {
                        return node;
                    }

                    if (FileUtil.isParentOf(kidFO, targetFO)) {
                        String relPath = FileUtil.getRelativePath(kidFO, targetFO);

                        // first path without extension (more common case)
                        String[] path = relPath.split("/"); // NOI18N
                        path[path.length - 1] = targetFO.getName();

                        // first try to find the file without extension (more common case)
                        Node found = findNode(node, path);
                        if (found == null) {
                            // file not found, try to search for the name with the extension
                            path[path.length - 1] = targetFO.getNameExt();
                            found = findNode(node, path);
                        }

                        if (found == null) {
                            // can happen for tests that are underneath sources directory
                            continue;
                        }

                        if (hasObject(found, target)) {
                            return found;
                        }

                        Node parent = found.getParentNode();
                        Children kids = parent.getChildren();
                        children = kids.getNodes();
                        for (Node child : children) {
                            if (hasObject(child, target)) {
                                return child;
                            }
                        }
                    }
                }
            }

            return null;
        }

        private boolean hasObject(Node node, Object obj) {
            if (obj == null) {
                return false;
            }

            FileObject fileObject = node.getLookup().lookup(FileObject.class);
            if (fileObject == null) {
                return false;
            }

            if (obj instanceof DataObject) {
                DataObject dataObject = node.getLookup().lookup(DataObject.class);
                if (dataObject == null) {
                    return false;
                }

                if (dataObject.equals(obj)) {
                    return true;
                }

                return hasObject(node, ((DataObject) obj).getPrimaryFile());
            }

            if (obj instanceof FileObject) {
                return obj.equals(fileObject);
            }

            return false;
        }

        private Node findNode(Node start, String[] path) {
            try {
                return NodeOp.findPath(start, path);
            } catch (NodeNotFoundException ex) {
                //TODO: Log exception or add validation to avoid it
            }

            return null;
        }
    }

    public SBTDependenciesParentNode getSbtDependenciesParentNode() {
        return sbtDependenciesParentNode;
    }

    public void setSbtDependenciesParentNode(SBTDependenciesParentNode sbtDependenciesParentNode) {
        this.sbtDependenciesParentNode = sbtDependenciesParentNode;
    }

    class PlayProjectSources implements Sources {

        final PlayProject project;

        public PlayProjectSources(PlayProject project) {
            this.project = project;
        }

        @Override
        public SourceGroup[] getSourceGroups(String type) {
            String displayName = ProjectUtils.getInformation(project).getDisplayName();
            if (JAVA.getExtention().equals(type) || SCALA.getExtention().equals(type)) {
                return new SourceGroup[]{
                    GenericSources.group(project, project.getProjectDirectory().getFileObject("app"), type, displayName, null, null),
                    GenericSources.group(project, project.getProjectDirectory().getFileObject("test"), type, displayName, null, null),};
            }

            return new SourceGroup[]{
                GenericSources.group(project, project.getProjectDirectory(), type, displayName, null, null)
            };
        }

        @Override
        public void addChangeListener(ChangeListener listener) {
        }

        @Override
        public void removeChangeListener(ChangeListener listener) {
        }

    }

}
