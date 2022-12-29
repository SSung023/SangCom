package Project.SangCom.util.exception;

import Project.SangCom.util.response.dto.CommonResponse;
import Project.SangCom.util.response.service.ResponseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @Author : Jeeseob
 * @CreateAt : 2022/12/02
 */
@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    private final ResponseService responseService;

    @ExceptionHandler(Exception.class)
    protected CommonResponse globalExceptionHandler(Exception e) {
        log.info("예외처리되지 않은 Exception, 수정 필요");
        log.info("[Error]" + e.getMessage());
        return responseService.failResult(e.getMessage());
    }
}

/**
 * 비지니스 예외 외에 다른 Exception을 처리할 수 있게끔
 * 많이 신경안써도 되고, 로그에 예외처리되지 않은 Exception이 뜨면 익셉션을 추가하자
 */
