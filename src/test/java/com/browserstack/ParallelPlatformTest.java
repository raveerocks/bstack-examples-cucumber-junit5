package com.browserstack;

import com.browserstack.webdriver.config.Platform;
import com.browserstack.webdriver.core.WebDriverFactory;
import io.cucumber.core.cli.CommandlineOptions;
import io.cucumber.core.cli.Main;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ParallelPlatformTest {

    public static ThreadLocal<Platform> threadLocalValue = new ThreadLocal<>();
    public static WebDriverFactory webDriverFactory = WebDriverFactory.getInstance();

    public static void main(String[] args) {
        int threadCount = Integer.parseInt(System.getProperty("parallel.threads", "1"));
        ExecutorService pool = Executors.newFixedThreadPool(threadCount, new PlatformThreadFactory());
        webDriverFactory.getPlatforms().forEach(platform -> pool.submit(new Task(platform, threadLocalValue)));
        pool.shutdown();
    }

    private static class Task implements Runnable {

        private Platform platform;
        private ThreadLocal<Platform> threadLocal;

        public Task(Platform platform, ThreadLocal<Platform> threadLocal) {
            this.platform = platform;
            this.threadLocal = threadLocal;
        }

        @Override
        public void run() {
            threadLocal.set(platform);
            String[] argv = new String[]{
                    CommandlineOptions.GLUE, "", "src/test/resources/com/browserstack"};
            ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            Main.run(argv, contextClassLoader);
            threadLocal.remove();
        }
    }
}
