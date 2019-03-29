package ua.com.arturmamedov.magist.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.swing.tree.DefaultMutableTreeNode;

@Getter @AllArgsConstructor
public class GistTreeNode extends DefaultMutableTreeNode {

    private final GistFile gistFile;
    private final String id;

    @Override
    public String toString() {
        return gistFile.getFilename();
    }
}
