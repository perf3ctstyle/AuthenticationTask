package com.epam.esm.config;

import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.nio.charset.StandardCharsets;
import java.util.EnumSet;

public class DispatcherServletInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    private static final String CHARACTER_ENCODING = "characterEncoding";
    private static final String HIDDEN_HTTP_METHOD_FILTER = "hiddenHttpMethodFilter";
    private static final String ANY_URL_PATTERN = "/*";
    private static final String SPRING_PROFILES_ACTIVE = "spring.profiles.active";
    private static final String PROD_PROFILE = "prod";
    private static final String DEV_PROFILE = "dev";

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return null;
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class[]{ApplicationConfig.class};
    }

    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }

    @Override
    public void onStartup(ServletContext aServletContext) throws ServletException {
        super.onStartup(aServletContext);
        registerCharacterEncodingFilter(aServletContext);
        registerHiddenFieldFilter(aServletContext);
        aServletContext.setInitParameter(SPRING_PROFILES_ACTIVE, PROD_PROFILE);
    }

    private void registerCharacterEncodingFilter(ServletContext aContext) {
        EnumSet<DispatcherType> dispatcherTypes = EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD);

        CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
        characterEncodingFilter.setEncoding(StandardCharsets.UTF_8.name());
        characterEncodingFilter.setForceEncoding(true);

        FilterRegistration.Dynamic characterEncoding = aContext.addFilter(CHARACTER_ENCODING, characterEncodingFilter);
        characterEncoding.addMappingForUrlPatterns(dispatcherTypes, true, ANY_URL_PATTERN);
    }

    private void registerHiddenFieldFilter(ServletContext aContext) {
        aContext.addFilter(HIDDEN_HTTP_METHOD_FILTER, new HiddenHttpMethodFilter())
                .addMappingForUrlPatterns(null ,true, ANY_URL_PATTERN);
    }
}
