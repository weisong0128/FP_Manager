package com.fiberhome.fp.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * aop打印日志
 */
@Component
@Aspect
public class WebLogAspect {
    private static Logger logger = LoggerFactory.getLogger(WebLogAspect.class);
    private final ObjectMapper mapper;

    public WebLogAspect(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Pointcut("@annotation(org.springframework.web.bind.annotation.GetMapping)")
    public void getLog() {
    }

    @Pointcut("@annotation(org.springframework.web.bind.annotation.PostMapping)")
    public void Postlog() {
    }

    @Before("getLog()")
    public void exBefor(JoinPoint joinPoint) {
        String name = joinPoint.getSignature().toString().replace("Response", "Request");
        logger.info(name);
        for (Object object : joinPoint.getArgs()) {
            if (object instanceof MultipartFile || object instanceof HttpServletRequest || object instanceof HttpServletResponse) {
                continue;
            }
            try {
                if (logger.isDebugEnabled()) {
                    logger.info(joinPoint.getTarget().getClass().getName() + "." + joinPoint.getSignature().getName() + " : request parameter : " + mapper.writeValueAsString(object));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @AfterReturning(returning = "response", pointcut = "getLog()")
    public void doAfterReturning(Object response) throws IOException {
        if (response != null) {
            logger.info(" response parameter : " + mapper.writeValueAsString(response));
        }
    }

    @AfterReturning(returning = "response", pointcut = "getLog()")
    public void doAfterReturning(JoinPoint joinPoint) throws IOException {
        String name = joinPoint.getSignature().toString();
        logger.info("Response" + name);
    }

    @Before("Postlog()")
    public void exBeforPostlog(JoinPoint joinPoint) {
        String name = joinPoint.getSignature().toString().replace("Response", "Request");
        logger.info(name);
        for (Object object : joinPoint.getArgs()) {
            try {
                if (logger.isDebugEnabled()) {
                    logger.info(joinPoint.getTarget().getClass().getName() + "." + joinPoint.getSignature().getName() + " : request parameter : " + mapper.writeValueAsString(object));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @AfterReturning(returning = "response", pointcut = "Postlog()")
    public void doAfterReturningPostlog(Object response) throws IOException {
        if (response != null) {
            logger.info(" response parameter : " + mapper.writeValueAsString(response));
        }
    }

    @AfterReturning(returning = "response", pointcut = "Postlog()")
    public void doAfterReturningPostlog(JoinPoint joinPoint) throws IOException {
        String name = joinPoint.getSignature().toString();
        logger.info(name);
    }
}
