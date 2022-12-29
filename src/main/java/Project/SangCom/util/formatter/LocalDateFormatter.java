package Project.SangCom.util.formatter;

import org.springframework.format.Formatter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * @Author : Jeeseob
 * @CreateAt : 2022/11/25
 */
@Component
public class LocalDateFormatter implements Formatter<LocalDate> {

    @Override
    public LocalDate parse(String text, Locale locale) {
        return LocalDate.parse(text, DateTimeFormatter.ISO_LOCAL_DATE);
    }

    @Override
    public String print(LocalDate object, Locale locale) {
        return DateTimeFormatter.ISO_LOCAL_DATE.format(object);
    }
}
/**
 * LocalDate랑 LocalDateTime을 보내면 이 형식으로 보내면 알아서 parsing을 해라
 * 프엔이랑 협업 시 필요한 것
 */