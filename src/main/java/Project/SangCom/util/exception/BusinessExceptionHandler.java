package Project.SangCom.util.exception;

import Project.SangCom.util.response.dto.CommonResponse;
import Project.SangCom.util.response.service.ResponseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @Author : Jeeseob
 * @CreateAt : 2022/11/25
 */
@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class BusinessExceptionHandler {
    private final ResponseService responseService;

    @ExceptionHandler(BusinessException.class)
    protected CommonResponse globalBusinessExceptionHandler(BusinessException e) {
        log.info("[Error]" + e.getMessage());


        return new CommonResponse(e.getStatus(), e.getMessage());
//        return ResponseEntity.badRequest().body(new CommonResponse(e.getStatus(), e.getMessage()));
//        return responseService.failResult(e.getMessage());
    }
}

/**
 * 비지니스 익센셥이 발생하면 어떤 에러가 났는지 알려주는 것
 */
