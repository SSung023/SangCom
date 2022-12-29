package Project.SangCom.util.config;

import Project.SangCom.util.formatter.LocalDateFormatter;
import Project.SangCom.util.formatter.LocalDateTimeFormatter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author : Jeeseob
 * @CreateAt : 2022/11/25
 *
 */

@Configuration
public class AppConfig {

    @Bean
    public LocalDateFormatter localDateFormatter() {
        return new LocalDateFormatter();
    }

    @Bean
    public LocalDateTimeFormatter localDateTimeFormatter() {
        return new LocalDateTimeFormatter();
    }
}
