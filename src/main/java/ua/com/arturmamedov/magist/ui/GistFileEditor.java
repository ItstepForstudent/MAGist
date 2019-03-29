package ua.com.arturmamedov.magist.ui;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.project.Project;

import javax.swing.*;
import java.awt.*;

public class GistFileEditor extends JPanel {
    private Editor editor;
    Project project;

    public GistFileEditor(Project project) {
        this.project = project;
        setLayout(new BorderLayout());
    }

    private void recreateEditor(String filename,String content){
        EditorFactory editorFactory = EditorFactory.getInstance();
        FileTypeManager fileTypeManager = FileTypeManager.getInstance();

        editor = editorFactory.createEditor(
                editorFactory.createDocument(content),
                project,
                fileTypeManager.getFileTypeByFileName(filename),
                true
        );
        removeAll();
        add(editor.getComponent());
    }

    public void changeContent(String filename,String content){
        recreateEditor(filename,content);
        revalidate();
        repaint();
    }

}
