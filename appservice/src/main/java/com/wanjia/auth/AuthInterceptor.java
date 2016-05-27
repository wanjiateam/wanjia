package com.wanjia.auth;

import org.apache.log4j.Logger;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * Created by blake on 2016/5/25.
 */
public class AuthInterceptor extends HandlerInterceptorAdapter {

    Logger logger = Logger.getLogger(AuthInterceptor.class);
    @Override
    public boolean preHandle(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response, Object handler) throws Exception {

        logger.info("login handle .............."+handler.getClass().getName());
        if(handler.getClass().isAssignableFrom(HandlerMethod.class)) {
            AuthPassport authPassport = ((HandlerMethod) handler).getMethodAnnotation(AuthPassport.class);
            //没有声明需要权限,或者声明不验证权限
            if (authPassport == null || authPassport.validate() == false) {
                return true;
            } else {
                String appkey = request.getParameter("appkey");
                if (appkey !=null && appkey.equals("abc")) {
                    logger.info("appkey is right..........");
                    return true;
                } else {
                    logger.error("appkey is wrong");
                    response.getOutputStream().print("auth error");
                    return false;
                }
            }
        }else{
            return false ;
        }

    }

    @Override
    public void postHandle(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response, Object handler, Exception ex) throws Exception {
        super.afterCompletion(request, response, handler, ex);
    }
}
