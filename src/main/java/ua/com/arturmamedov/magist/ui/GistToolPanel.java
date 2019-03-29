package ua.com.arturmamedov.magist.ui;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.ui.JBSplitter;
import com.intellij.ui.components.JBScrollPane;
import org.jetbrains.plugins.github.authentication.GithubAuthenticationManager;
import org.jetbrains.plugins.github.authentication.accounts.GithubAccount;
import org.jetbrains.plugins.github.authentication.ui.GithubChooseAccountDialog;
import ua.com.arturmamedov.magist.service.GistService;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class GistToolPanel extends SimpleToolWindowPanel {

    private GistTree gistTree;
    private GistService gistService;
    private Project project;
    private GistFileEditor gistFileEditor;
    private JBSplitter splitPane;
    private GithubAccount account = null;


    GistToolPanel(Project project) {
        super(false, true);
        this.project = project;
        gistService = GistService.getInstance();
        createView();
        gistService.getGists(account, g -> gistTree.changeGists(g));
    }

    private void changeDefaultGitAccount() {
        GithubAuthenticationManager instance = GithubAuthenticationManager.getInstance();
        ArrayList<GithubAccount> accountList = new ArrayList<>(instance.getAccounts());
        if(!accountList.isEmpty()){
            account = accountList.get(0);
        }
    }

    private void createView() {
        changeDefaultGitAccount();

        splitPane = new JBSplitter("MamedovGistsJB_SPLITER_KEY", 0.25f);
        splitPane.setSecondComponent(new JPanel());

        createToolbar();

        gistTree = new GistTree();
        gistTree.setGistSelectFileListener(this::onSelectGistFile);

        gistFileEditor = new GistFileEditor(project);

        JBScrollPane jbScrollPane = new JBScrollPane(gistTree);
        splitPane.setFirstComponent(jbScrollPane);
        splitPane.setSecondComponent(gistFileEditor);
        add(splitPane);

        gistService.setOnStartLoading(this::onStartLoading);
        gistService.setOnEndLoading(this::onEndLoading);
    }

    private void onStartLoading(){
        gistTree.setEnabled(false);
        gistTree.setForeground(new Color(0,0,0,10));
    }

    private void onEndLoading(){
        gistTree.setEnabled(true);
        gistTree.setForeground(getForeground());
    }

    private void onSelectGistFile(String id, String filename) {
        gistService.getGistFile(account, id, filename, s -> {
            ApplicationManager.getApplication().invokeLater(()->{
                gistFileEditor.changeContent(filename,s);
            });
        });
    }

    private void createToolbar() {
        ActionManager actionManager = ActionManager.getInstance();

        DefaultActionGroup group = new DefaultActionGroup();
        group.add(new SelectAccountWindow());
        group.add(new RefreshBtn());

        ActionToolbar toolbar = actionManager.createActionToolbar("Mamedov Gists", group, false);
        JPanel buttonsPanel = new JPanel(new BorderLayout());
        buttonsPanel.add(toolbar.getComponent(), BorderLayout.NORTH);
        setToolbar(buttonsPanel);
    }


    private class SelectAccountWindow extends AnAction {
        private SelectAccountWindow() {
            super(AllIcons.General.User);
        }

        @Override
        public void actionPerformed(AnActionEvent e) {
            GithubAuthenticationManager instance = GithubAuthenticationManager.getInstance();
            GithubChooseAccountDialog githubChooseAccountDialog = new GithubChooseAccountDialog(
                    e.getProject(),
                    null,
                    instance.getAccounts(),
                    "Select GitHub account to use for gists",
                    false,
                    false,
                    "Select GitHub account",
                    "Change");
            githubChooseAccountDialog.show();

            try {
                account = githubChooseAccountDialog.getAccount();
                gistService.getGists(account, g -> gistTree.changeGists(g));
            } catch (IllegalStateException ise) {

            }
        }

    }



    private class RefreshBtn extends AnAction {
        private RefreshBtn() {
            super(AllIcons.Actions.Refresh);
        }

        @Override
        public void actionPerformed(AnActionEvent e) {
            gistService.getGists(account, g -> gistTree.changeGists(g));
        }
    }
}
