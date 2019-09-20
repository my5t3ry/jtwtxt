package de.my5t3ry.jtwtxt.post;

import de.my5t3ry.jtwtxt.file.FileWatcher;
import de.my5t3ry.jtwtxt.file.IHandleTwTxtFileChanges;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;

/**
 * User: my5t3ry
 * Date: 20.09.19 04:23
 */
@Component
@Slf4j
public class PostImportController {
    @Autowired
    private FileWatcher fileWatcher;

    @Autowired
    private IHandleTwTxtFileChanges fileChangeHandler;
    @Value("${config.twtxt-dir}")
    private File twtxtDir;


    @PostConstruct
    public void init() {
        fileWatcher.run(new File(twtxtDir, "twtxt.txt"), fileChangeHandler);
    }

}
