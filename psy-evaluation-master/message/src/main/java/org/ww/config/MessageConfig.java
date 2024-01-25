package org.ww.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.ww.interceptor.HotlineInterceptor;

import javax.validation.MessageInterpolator;

@Configuration
public class MessageConfig implements WebMvcConfigurer {

    @Bean
    public HotlineInterceptor hotlineSeatInterceptor(){
        return new HotlineInterceptor(HotlineInterceptor.TYPE_SEAT);
    }
    @Bean
    public HotlineInterceptor hotlineAdminInterceptor(){
        return new HotlineInterceptor(HotlineInterceptor.TYPE_ADMIN);
    }

    @Bean
    public HotlineInterceptor hotlineBothInterceptor(){
        return new HotlineInterceptor(HotlineInterceptor.TYPE_BOTH);
    }
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(hotlineBothInterceptor())
                .addPathPatterns(
                        "/hotline/call/received",
                        "/hotline/call/missed",
                        "/hotline/call/callInHistoryByPhone",
                        "/hotline/blacklist/**",
                        "/hotline/teacher/**",
                        "/hotline/doc/**",
                        "/hotline/contact/**",
                        "/hotline/statistics/**",
                        "/hotline/listCodes"
                        );
        registry.addInterceptor(hotlineSeatInterceptor())
                .addPathPatterns(
//                        "/hotline/call/sdkUrl",
                        "/hotline/call/note",
                        "/hotline/call/lastCall"
                );
    }
}
