package de.my5t3ry.jtwtxt.post;

import de.my5t3ry.jtwtxt.file.FileWatcher;
import de.my5t3ry.jtwtxt.file.IHandleTwTxtFileChanges;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

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
    private FileWatcher fileWatcher ;

    @Autowired
    private IHandleTwTxtFileChanges fileChangeHandler;

    @PostConstruct
    public void init() {
        final String twtxtFilePath = System.getProperty("config.twtxt-file");
        if (StringUtils.isEmpty(twtxtFilePath)) {
            throw new IllegalStateException("Please configure the config.twtxt-file vm parameter ['-Dconfig.twtxt-file=/path']");
        }
        fileWatcher.run(new File(twtxtFilePath), fileChangeHandler);
    }

}
