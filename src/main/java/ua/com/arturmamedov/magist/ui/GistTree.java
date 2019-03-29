package ua.com.arturmamedov.magist.ui;

import com.intellij.ui.TreeSpeedSearch;
import com.intellij.ui.treeStructure.SimpleTree;
import ua.com.arturmamedov.magist.entities.Gist;
import ua.com.arturmamedov.magist.entities.GistFile;
import ua.com.arturmamedov.magist.entities.GistTreeNode;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class GistTree extends SimpleTree {
    private Optional<GistSelectFileListener> gistSelectFileListener = Optional.empty();

    private DefaultMutableTreeNode rootNode;
    private DefaultTreeModel treeModel;

    public GistTree() {
        rootNode = new DefaultMutableTreeNode("Gists");
        treeModel = new DefaultTreeModel(rootNode);
        setModel(treeModel);
        addTreeSelectionListener(e->onItemSelected());
        new TreeSpeedSearch(this);
    }

    private void onItemSelected() {
        Object lastSelected = getLastSelectedPathComponent();
        if (lastSelected instanceof GistTreeNode) {
            GistTreeNode gistFile = (GistTreeNode) lastSelected;
            gistSelectFileListener.ifPresent(listener->listener.onSelect(gistFile.getId(),gistFile.getGistFile().getFilename()));
        }
    }

    public void changeGists(List<Gist> gists){
        if (!gists.isEmpty()) {
            rootNode.removeAllChildren();

            for (Gist gist : gists) {
                DefaultMutableTreeNode gistNode = new DefaultMutableTreeNode(gist.getDescription());

                Map<String, GistFile> files = gist.getFiles();
                for (String key : files.keySet()) {
                    gistNode.add(new GistTreeNode(files.get(key), gist.getId()));
                }

                rootNode.add(gistNode);
            }

            treeModel.reload();
        }
    }


    public interface GistSelectFileListener{
        void onSelect(String gistFileId,String gistFileName);
    }

    public void setGistSelectFileListener(GistSelectFileListener gistSelectFileListener) {
        this.gistSelectFileListener = Optional.ofNullable(gistSelectFileListener);
    }
}
