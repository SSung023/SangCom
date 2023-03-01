package Project.SangCom.util.exception;

import Project.SangCom.security.service.JwtTokenProviderService;
import Project.SangCom.user.dto.UserLoginResponse;
import Project.SangCom.util.response.dto.CommonResponse;
import Project.SangCom.util.response.dto.SingleResponse;
import Project.SangCom.util.response.service.ResponseService;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;

import static Project.SangCom.security.service.JwtTokenProvider.AUTHORIZATION_HEADER;

/**
 * @Author : Jeeseob
 * @CreateAt : 2022/12/02
 */
@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    private final ResponseService responseService;
    private final JwtTokenProviderService tokenProviderService;

    @ExceptionHandler(Exception.class)
    protected CommonResponse globalExceptionHandler(Exception e) {
        log.info("예외처리되지 않은 Exception, 수정 필요");

        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        String exceptionAsString = sw.toString();
        log.info(exceptionAsString);

        log.info("[Error]" + e.getMessage());
        return responseService.failResult(e.getMessage());
    }

    @ExceptionHandler(JwtException.class)
    protected ResponseEntity<SingleResponse<UserLoginResponse>> globalExceptionHandler
                (HttpServletResponse response, Exception e) {

        String accessToken = response.getHeader(AUTHORIZATION_HEADER);
        UserLoginResponse loginResponse= tokenProviderService.getRequestUserInfo(accessToken);

        return ResponseEntity.ok().body
                (new SingleResponse<>(SuccessCode.SUCCESS.getStatus(), SuccessCode.SUCCESS.getMessage(), loginResponse));
    }
}

/**
 * 비지니스 예외 외에 다른 Exception을 처리할 수 있게끔
 * 많이 신경안써도 되고, 로그에 예외처리되지 않은 Exception이 뜨면 익셉션을 추가하자
 */
