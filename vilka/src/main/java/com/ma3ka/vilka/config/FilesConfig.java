package com.ma3ka.vilka.config;

import lombok.Data;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Data
@Configuration
@PropertySource("classpath:files.properties")
public class FilesConfig {

    private String pathToFile;

    private String pathToSaveFile;
}
