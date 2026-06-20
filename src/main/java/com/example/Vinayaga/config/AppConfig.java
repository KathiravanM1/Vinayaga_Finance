package com.example.Vinayaga.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app")
@Getter
@Setter
public class AppConfig {

    private Project project = new Project();
    private Report report = new Report();
    private Pagination pagination = new Pagination();

    @Getter
    @Setter
    public static class Project {
        private String codePrefix = "PRJ";
        private String defaultStatus = "ACTIVE";
    }

    @Getter
    @Setter
    public static class Report {
        private int maxDays = 366;
    }

    @Getter
    @Setter
    public static class Pagination {
        private int defaultPage = 0;
        private int defaultSize = 10;
        private int maxSize = 100;
    }
}
