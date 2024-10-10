package ru.skyshine.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public SpringTemplateEngine templateResolvers() {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        Set<ITemplateResolver> templateResolvers = new LinkedHashSet<>();
        List<String> dirNames = getDirNames();
        templateResolvers.add(addMainTemplateResolver());
        for (int i = 1; i <= dirNames.size(); i++) {
            templateResolvers.add(resolver(dirNames.get(i - 1), i));
        }
        templateEngine.setTemplateResolvers(templateResolvers);
        return templateEngine;
    }

    private ClassLoaderTemplateResolver addMainTemplateResolver() {
        ClassLoaderTemplateResolver secondaryTemplateResolver = new ClassLoaderTemplateResolver();
        secondaryTemplateResolver.setPrefix("templates/");
        secondaryTemplateResolver.setSuffix(".html");
        secondaryTemplateResolver.setTemplateMode(TemplateMode.HTML);
        secondaryTemplateResolver.setCharacterEncoding("UTF-8");
        secondaryTemplateResolver.setOrder(0);
        secondaryTemplateResolver.setCheckExistence(true);
        return secondaryTemplateResolver;
    }

    private ClassLoaderTemplateResolver resolver(String directoryName, int index) {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("templates/" + directoryName + "/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding("UTF-8");
        templateResolver.setOrder(index);
        templateResolver.setCheckExistence(true);
        return templateResolver;
    }

    private List<String> getDirNames() {
        try (Stream<Path> paths = Files.list(Paths.get("src/main/resources/templates"))) {
            return paths
                    .filter(Files::isDirectory)
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .toList();

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

}
