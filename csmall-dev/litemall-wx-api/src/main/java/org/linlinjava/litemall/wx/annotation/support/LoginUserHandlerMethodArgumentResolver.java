package org.linlinjava.litemall.wx.annotation.support;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.linlinjava.litemall.db.dao.LitemallUserMapper;
import org.linlinjava.litemall.db.domain.LitemallUser;
import org.linlinjava.litemall.db.service.LitemallUserService;
import org.linlinjava.litemall.wx.annotation.LoginUser;
import org.linlinjava.litemall.wx.service.UserTokenManager;
import org.linlinjava.litemall.wx.web.WxAuthController;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;


public class LoginUserHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

    private final Log logger = LogFactory.getLog(LoginUserHandlerMethodArgumentResolver.class);
    public static final String LOGIN_TOKEN_KEY = "X-Litemall-Token";

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().isAssignableFrom(Integer.class) && parameter.hasParameterAnnotation(LoginUser.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer container,
                                  NativeWebRequest request, WebDataBinderFactory factory) throws Exception {

//        return new Integer(1);
        String token = request.getHeader(LOGIN_TOKEN_KEY);
        if (token == null || token.isEmpty()) {
            return null;
        }
        Object userId=  UserTokenManager.getUserId(token);
        if (null==userId){
            return null;
        }
        logger.info("解析token后，userId"+userId+"，校验userId可用性");
        LitemallUserService userService=getDao(LitemallUserService.class, request.getNativeRequest(HttpServletRequest.class));
        LitemallUser user=userService.checkById(Integer.valueOf((Integer) userId));
        if (null==user){
            logger.info("当前用户userid"+userId+"已被禁用或者被删除了");
            return null;
        }
        logger.info("userId"+userId+"可用");
        return user.getId();
    }


    //获取dao,拦截器中只能通过webapplicationcontextutils获取spring boot中的bean.
    private <T> T  getDao(Class<T> t, HttpServletRequest request){
        BeanFactory beanFactory = WebApplicationContextUtils.getRequiredWebApplicationContext(request.getServletContext());
        return beanFactory.getBean(t);
    }
}
