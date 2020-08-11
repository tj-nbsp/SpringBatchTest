package org.example.support;

import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.support.AopUtils;

import java.lang.reflect.Field;

/**
 * 代理工具
 */
public class ProxyUtil {

    /**
     * 从代理类获取到实际的对象
     */
    public static Object getTarget(Object proxy) throws Exception {
        if (!AopUtils.isAopProxy(proxy)) {
            return proxy;
        }

        if (AopUtils.isJdkDynamicProxy(proxy)) {
            Field f = proxy.getClass().getSuperclass().getDeclaredField("h");
            f.setAccessible(true);
            AopProxy aopProxy = (AopProxy) f.get(proxy);
            Field advised = aopProxy.getClass().getDeclaredField("advised");
            advised.setAccessible(true);
            Object target = ( (AdvisedSupport) advised.get(aopProxy) ).getTargetSource().getTarget();
            return target;
        }
        if (AopUtils.isCglibProxy(proxy)) {
            Field f = proxy.getClass().getDeclaredField("CGLIB$CALLBACK_0");
            f.setAccessible(true);
            Object dynamicAdvisedInterceptor = f.get(proxy);
            Field advised = dynamicAdvisedInterceptor.getClass().getDeclaredField("advised");
            advised.setAccessible(true);
            Object target = ( (AdvisedSupport) advised.get(dynamicAdvisedInterceptor) ).getTargetSource().getTarget();
            return target;
        }
        throw new RuntimeException("get target error");
    }

}
