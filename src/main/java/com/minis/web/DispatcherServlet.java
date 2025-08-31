package com.minis.web;

import com.minis.beans.BeansException;
import com.minis.web.Utils.WebApllicationContextUtil;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class DispatcherServlet extends HttpServlet {
    public static final String WEB_APPLICATION_CONTEXT_ATTRIBUTE = DispatcherServlet.class.getName() + ".CONTEXT";
    private HandlerMapping handlerMapping;
    private HandlerAdapter handlerAdapter;

    private WebApplicationContext webApplicationContext;
    //private WebApplicationContext parentApplicationContext;

    private static final long   serialVersionUID = 1L;
    private String              sContextConfigLocation;
    private List<String>        packageNames        = new ArrayList<>();

    private Map<String,Object>  controllerObjs      = new HashMap<>();
    private List<String>        controllerNames     = new ArrayList<>();
    private Map<String,Class<?>>controllerClasses  = new HashMap<>();



    public DispatcherServlet() {
        super();
    }

    public void init(ServletConfig config) throws ServletException {          super.init(config);
        super.init(config);
        //this.parentApplicationContext = (WebApplicationContext) this.getServletContext().getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
        sContextConfigLocation = config.getInitParameter("contextConfigLocation");
        URL xmlPath = null;
        try {
            xmlPath = this.getServletContext().getResource(sContextConfigLocation);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        this.packageNames = XmlScanComponentHelper.getNodeValue(xmlPath);
        WebApplicationContext parentApplicationContext= WebApllicationContextUtil.getWebApplicationContext(this.getServletContext(),WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
        this.webApplicationContext = new AnnotationConfigWebApplicationContext(sContextConfigLocation,parentApplicationContext);
        Refresh();
    }

    protected void Refresh() {
        initController();
        initHandlerMappings(this.webApplicationContext);
        initHandlerAdapters(this.webApplicationContext);
    }

    //完成对controllerClasses,controllerNames,controllerObjs的初始化
    protected void initController() {
        this.controllerNames= Arrays.asList(this.webApplicationContext.getBeanDefinitionNames());
        for(String controllerName : this.controllerNames){
            //初始化ControllerClass
            controllerClasses.put(controllerName,webApplicationContext.getType(controllerName));
            //初始化ControllerObj
            try {
                controllerObjs.put(controllerName,webApplicationContext.getBean(controllerName));
            } catch (BeansException e) {
                throw new RuntimeException(e);
            }
        }
    }



    protected void initHandlerMappings(WebApplicationContext wac) {
        this.handlerMapping = new RequestMappingHandlerMapping(wac);
    }
    protected void initHandlerAdapters(WebApplicationContext wac) {
        this.handlerAdapter = new RequestMappingHandlerAdapter(wac);
    }

    protected void service(HttpServletRequest request, HttpServletResponse response) {
        request.setAttribute(WEB_APPLICATION_CONTEXT_ATTRIBUTE, this.webApplicationContext);
        try {
            doDispatch(request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
        }
    }
    protected void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception{
        HttpServletRequest processedRequest = request;
        HandlerMethod handlerMethod = null;
        handlerMethod = this.handlerMapping.getHandler(processedRequest);
        if (handlerMethod == null) {
            return;
        }
        HandlerAdapter ha = this.handlerAdapter;
        ha.handle(processedRequest, response, handlerMethod);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
