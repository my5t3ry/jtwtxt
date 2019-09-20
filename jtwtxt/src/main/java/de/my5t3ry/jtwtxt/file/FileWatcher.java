package de.my5t3ry.jtwtxt.file;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * User: my5t3ry
 * Date: 20.09.19 04:36
 */
@Slf4j
@Component
public class FileWatcher {
    private File file;
    private IHandleTwTxtFileChanges service;
    private AtomicBoolean stop = new AtomicBoolean(false);

    public boolean isStopped() {
        return stop.get();
    }

    public void stopThread() {
        stop.set(true);
    }

    private void doOnChange() {
        this.service.handle(file);
    }

    @Async
    public Future<Boolean> run(final File file, final IHandleTwTxtFileChanges fileChangeHandler) {
        this.file = file;
        this.service = fileChangeHandler;
        try (WatchService watcher = FileSystems.getDefault().newWatchService()) {
            Path path = this.file.toPath().getParent();
            path.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);
            while (!isStopped()) {
                WatchKey key;
                key = watcher.poll(30000, TimeUnit.MILLISECONDS);
                if (key == null) {
                    continue;
                }
                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();
                    @SuppressWarnings("unchecked")
                    WatchEvent<Path> ev = (WatchEvent<Path>) event;
                    Path filename = ev.context();
                    if (kind == StandardWatchEventKinds.OVERFLOW) {
                        continue;
                    } else if (kind == java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY
                            && filename.toString().equals(this.file.getName())) {
                        doOnChange();
                    }
                    boolean valid = key.reset();
                    if (!valid) {
                        break;
                    }
                }
            }
        } catch (Throwable e) {
            log.error("could not create watch service for ['" + this.file.getAbsolutePath() + "']", e);
        }
        return new AsyncResult<>(true);

    }
}
