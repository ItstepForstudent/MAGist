package ua.com.arturmamedov.magist.service;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.progress.util.ProgressIndicatorBase;
import org.jetbrains.plugins.github.api.GithubApiRequest;
import org.jetbrains.plugins.github.api.GithubApiRequestExecutorManager;
import org.jetbrains.plugins.github.authentication.accounts.GithubAccount;
import ua.com.arturmamedov.magist.entities.Gist;
import ua.com.arturmamedov.magist.entities.GistFile;


import java.io.IOException;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class GistService {
    private static ReentrantLock lock = new ReentrantLock();

    private Runnable onStartLoading;
    private Runnable onEndLoading;

    public void setOnStartLoading(Runnable onStartLoading) {
        this.onStartLoading = onStartLoading;
    }

    public void setOnEndLoading(Runnable onEndLoading) {
        this.onEndLoading = onEndLoading;
    }

    public static GistService getInstance() {
        return ServiceManager.getService(GistService.class);
    }

    private Map<String, Gist> cache = new HashMap<>();

    public void getGist(GithubAccount account, String gistId, OnResult<Gist> onResult) {
        if(cache.containsKey(gistId)){
            onResult.onResult(cache.get(gistId));
            return;
        }
        new Thread(()-> Optional.ofNullable(account)
                .filter(a -> lock.tryLock())
                .map(ac -> {
                    Optional.ofNullable(onStartLoading).ifPresent(Runnable::run);
                    ProgressIndicatorBase pib = new ProgressIndicatorBase();
                    pib.setText("Getting gist ...");
                    GithubApiRequestExecutorManager executorManager = GithubApiRequestExecutorManager.getInstance();

                    try {
                        Gist g = executorManager.getExecutor(ac).execute(pib,
                                new GithubApiRequest.Get.Json<>("https://api.github.com/gists/" + gistId,
                                        Gist.class, "application/vnd.github.v3+json"));
                        cache.put(gistId,g);
                        return g;
                    } catch (Exception e) {
                        alertError(e);
                        return null;
                    } finally {
                        lock.unlock();
                        Optional.ofNullable(onEndLoading).ifPresent(Runnable::run);
                    }
                }).ifPresent(onResult::onResult)
        ).start();

    }

    public void getGists(GithubAccount account,OnResult<List<Gist>> onResult) {
        cache.clear();
        new Thread(()->Optional.ofNullable(account)
                    .filter(a -> lock.tryLock())
                    .map(ac -> {
                        Optional.ofNullable(onStartLoading).ifPresent(Runnable::run);
                        ProgressIndicatorBase pib = new ProgressIndicatorBase();
                        pib.setText("Getting gists ...");
                        GithubApiRequestExecutorManager executorManager = GithubApiRequestExecutorManager.getInstance();

                        try {
                            return new ArrayList<>(executorManager.getExecutor(ac).execute(pib,
                                    new GithubApiRequest.Get.JsonList<>("https://api.github.com/gists",
                                            Gist.class, "application/vnd.github.v3+json")));
                        } catch (IOException e) {
                            alertError(e);
                            return null;
                        } finally {
                            lock.unlock();
                            Optional.ofNullable(onEndLoading).ifPresent(Runnable::run);
                        }
                    })
                    .ifPresent(onResult::onResult)
        ).start();

    }

    public void getGistFile(GithubAccount account,String id, String filename,OnResult<String> onResult){
        getGist(account,id,g->{
            Optional
                    .ofNullable(g.getFiles().get(filename))
                    .map(GistFile::getContent)
                    .ifPresent(onResult::onResult);
        });
    }

    private void alertError(Exception e1) {
        Notification notification = new Notification(
                "MAGist",
                "Get Gists",
                "Unable to connect to GitHub !\n" + e1.getMessage(),
                NotificationType.ERROR);

        Notifications.Bus.notify(notification);
    }

    public interface OnResult<T>{
        void onResult(T data);
    }
}
