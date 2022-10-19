/*
 *  Copyright 2019-2020 Zheng Jie
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package plus.extvos.logging.aspect;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import plus.extvos.common.utils.ThrowableUtil;
import plus.extvos.logging.annotation.Log;
import plus.extvos.logging.domain.LogObject;
import plus.extvos.logging.helpers.RequestContext;
import plus.extvos.logging.service.LogDispatchService;

import java.lang.reflect.Method;
import java.sql.Timestamp;

/**
 * @author Mingcai SHEN
 */
@Component
@Aspect
public class LogAspect {

    private static final Logger log = LoggerFactory.getLogger(LogAspect.class);

    @Autowired(required = false)
    private LogDispatchService logDispatchService;

    ThreadLocal<Long> currentTime = new ThreadLocal<>();

    public LogAspect() {

    }

    /**
     * 配置切入点
     */
    @Pointcut("@annotation(plus.extvos.logging.annotation.Log)")
    public void logPointcut() {
        // 该方法无方法体,主要为了让同类中其他方法使用此切入点
    }

    /**
     * 配置环绕通知,使用在方法logPointcut()上注册的切入点
     *
     * @param joinPoint join point for advice
     */
    @Around("logPointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result;
        currentTime.set(System.currentTimeMillis());
        result = joinPoint.proceed();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Log l = method.getAnnotation(Log.class);
        LogObject logObject = new LogObject(l);
        logObject.setDuration(System.currentTimeMillis() - currentTime.get());
        logObject.setCreated(new Timestamp(System.currentTimeMillis()));
        logObject.setComment(l.comment());
        logObject.setUsername(getUsername());
        if(l.model().isEmpty()){
            logObject.setModel(method.getClass().getName());
        } else {
            logObject.setModel(l.model());
        }
        RequestContext ctx = RequestContext.probe();
        logObject.setAgent(ctx.getAgent());
        logObject.setRequestIp(ctx.getIpAddress());
        logObject.setRequestUri(ctx.getRequestURI());
        logObject.setMethod(signature.getDeclaringTypeName() + "::" + method.getName());
        currentTime.remove();
        if (logDispatchService != null) {
            logDispatchService.dispatch(logObject);
        } else {
            log.info("logAround:> {}", logObject);
        }
        return result;
    }

    /**
     * 配置异常通知
     *
     * @param joinPoint join point for advice
     * @param e         exception
     */
    @AfterThrowing(pointcut = "logPointcut()", throwing = "e")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        currentTime.set(System.currentTimeMillis());
        Log l = method.getAnnotation(Log.class);
        LogObject logObject = new LogObject(l);
        logObject.setAction("ERROR");
        logObject.setDuration(System.currentTimeMillis() - currentTime.get());
        logObject.setExceptionDetail(ThrowableUtil.getStackTrace(e).getBytes());
        RequestContext ctx = RequestContext.probe();
        logObject.setCreated(new Timestamp(System.currentTimeMillis()));
        logObject.setComment(l.comment());
        logObject.setUsername(getUsername());
        logObject.setAgent(ctx.getAgent());
        logObject.setRequestIp(ctx.getIpAddress());
        logObject.setRequestUri(ctx.getRequestURI());
        logObject.setMethod(signature.getDeclaringTypeName() + "::" + method.getName());
        currentTime.remove();
        if (logDispatchService != null) {
            logDispatchService.dispatch(logObject);
        } else {
            log.info("logAfterThrowing:> {}", logObject);
        }

    }

    public String getUsername() {
        try {
            Subject subject = SecurityUtils.getSubject();
            if (null != subject && subject.isAuthenticated()) {
                return "User:" + subject.getPrincipal().toString();
            } else {
                return "Session:" + subject.getSession().getId().toString();
            }
        } catch (Exception e) {
            return "";
        }
    }
}
