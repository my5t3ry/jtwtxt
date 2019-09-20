package de.my5t3ry.jtwtxt.utils;

import de.my5t3ry.jtwtxt.post.PostContentType;
import de.my5t3ry.jtwtxt.post.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.QueryBuilder;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * User: my5t3ry
 * Date: 20.09.19 15:51
 */
@Component
@Slf4j
public class WebsitePreviewService {
    private final int timeOutInSeconds = 30;
    private final int maxPreviewHeight = 720;
    @Autowired
    private PostRepository postRepository;

    @Value("${config.twtxt-dir}")
    private File twtxtDir;
    @Value("${webdriver.chrome.driver}")
    private String chromeDriverPath;
    private File webPreviewDir;
    private ChromeDriver chromeDriver;

    @PostConstruct
    public void init() {
        initWebdriver();
        if (!this.twtxtDir.exists()) {
            throw new IllegalStateException("twtxt dir does not exist please configure application.properties ['" + twtxtDir.getAbsolutePath() + "']");
        }
        this.webPreviewDir = new File(twtxtDir, "webpreviews");
        if (!webPreviewDir.exists()) {
            webPreviewDir.mkdir();
        }
    }

    private void initWebdriver() {
        System.setProperty("webdriver.chrome.driver", chromeDriverPath);
        ChromeOptions options = new ChromeOptions();
        options.setHeadless(true);
        options.addArguments("--window-size=1200x600", "--log-level=3");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        chromeDriver = new ChromeDriver(options);
    }

    @Async
    public void fetchMissingPreviews() {
        QueryBuilder builder = nestedQuery("content", boolQuery().must(termQuery("content.type", PostContentType.WEBSITE_EXTERNAL.toString().toLowerCase())), ScoreMode.None);
        postRepository.search(builder).forEach(curPost -> {
            curPost.getContent().forEach(curContent -> {
                if (curContent.getType().equals(PostContentType.WEBSITE_EXTERNAL)) {
                    final File preview = new File(webPreviewDir, curContent.getHashFromUrl() + ".jpg");
                    if (!preview.exists()) {
                        log.info("->> fetching preview ['" + curContent.getUrl() + "']");
                        chromeDriver.get(curContent.getUrl());
                        waitForPageLoadComplete(chromeDriver);
                        final Screenshot screenshot = new AShot()
                                .shootingStrategy(ShootingStrategies.viewportPasting(100))
                                .takeScreenshot(chromeDriver);
                        try {
                            ImageIO.write(screenshot.getImage().getSubimage(0, 0, screenshot.getImage().getWidth(), screenshot.getImage().getHeight() < maxPreviewHeight ? screenshot.getImage().getHeight() : maxPreviewHeight), "jpg", preview);
                        } catch (Exception e) {
                            log.warn("could not create preview for page ['" + curContent.getUrl() + "']", e);
                        }
                    }
                }
            });
        });
    }

    public void waitForPageLoadComplete(WebDriver driver) {
        Wait<WebDriver> wait = new WebDriverWait(driver, timeOutInSeconds);
        wait.until(driver1 -> String
                .valueOf(((JavascriptExecutor) driver1).executeScript("return document.readyState"))
                .equals("complete"));
    }
}
