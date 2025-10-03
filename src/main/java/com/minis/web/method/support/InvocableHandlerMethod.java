package com.minis.web.method.support;


import com.minis.web.WebDataBinderFactory;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;

@NoArgsConstructor
public class InvocableHandlerMethod extends HandlerMethod {
    private static final Object[] EMPTY_ARGS = new Object[0];
    //private HandlerMethodArgumentResolverComposite resolvers = new HandlerMethodArgumentResolverComposite();
    //private ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();
    private WebDataBinderFactory dataBinderFactory;
    public InvocableHandlerMethod(Object bean, Method method) {
        super(method, bean);
    }
    public InvocableHandlerMethod(HandlerMethod handlerMethod) {
        super(handlerMethod);
    }

    //执行方法并获取初始的返回值(后续会经过ServletInvocableHandlerMethod中的解析器来解析)
    public Object invokeForRequest(Object... providedArgs) throws Exception {
        return doInvoke(providedArgs);
    }

    /**
     * Get the method argument values for the current request, checking the provided
     * argument values and falling back to the configured argument resolvers.
     * <p>The resulting array will be passed into {@link #doInvoke}.
     * @since 5.1.2
     */



    protected Object doInvoke(Object... args) throws Exception {
        Method method = getBridgedMethod();
        Object bean=getBean();
        if(args==null){
            args=EMPTY_ARGS;
        }
        return method.invoke(bean, args);
    }



}
