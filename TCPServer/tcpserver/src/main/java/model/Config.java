package model;

import java.io.InputStream;
import java.util.Properties;

public class Config {

    public static int serverPort() {
        Properties p = new Properties();
        try (InputStream in = Config.class.getResourceAsStream("/server.properties")) {
            if (in != null) p.load(in);
        } catch (Exception ignored) {}
        return Integer.parseInt(p.getProperty("port", "3000").trim());
    }
}
