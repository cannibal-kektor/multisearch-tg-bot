package lubos.multisearch.telegrambot.conf;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class StartupHealthCheck implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        File healthCheckFile = new File("/tmp/healthy");
        healthCheckFile.createNewFile();
        Runtime.getRuntime().addShutdownHook(new Thread(healthCheckFile::delete));
    }
}
