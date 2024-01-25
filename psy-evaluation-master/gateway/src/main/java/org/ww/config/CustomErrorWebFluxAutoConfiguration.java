//package org.ww.config;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.ObjectProvider;
//import org.springframework.boot.autoconfigure.AutoConfigureBefore;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
//import org.springframework.boot.autoconfigure.condition.SearchStrategy;
//import org.springframework.boot.autoconfigure.web.ResourceProperties;
//import org.springframework.boot.autoconfigure.web.ServerProperties;
//import org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration;
//import org.springframework.boot.context.properties.EnableConfigurationProperties;
//import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
//import org.springframework.boot.web.reactive.error.ErrorAttributes;
//import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.annotation.Order;
//import org.springframework.http.codec.ServerCodecConfigurer;
//import org.springframework.web.reactive.config.WebFluxConfigurer;
//import org.springframework.web.reactive.result.view.ViewResolver;
//
//@Configuration
//@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
//@ConditionalOnClass(WebFluxConfigurer.class)
//@AutoConfigureBefore(WebFluxAutoConfiguration.class)
//@EnableConfigurationProperties({ServerProperties.class, ResourceProperties.class})
//@Slf4j
//public class CustomErrorWebFluxAutoConfiguration {
//
//    private final ServerProperties serverProperties;
//
//    private final ApplicationContext applicationContext;
//
//    private final ResourceProperties resourceProperties;
//
//    private final List<ViewResolver> viewResolvers;
//
//    private final ServerCodecConfigurer serverCodecConfigurer;
//
//    public CustomErrorWebFluxAutoConfiguration(ServerProperties serverProperties,
//                                               ResourceProperties resourceProperties,
//                                               ObjectProvider<ViewResolver> viewResolversProvider,
//                                               ServerCodecConfigurer serverCodecConfigurer,
//                                               ApplicationContext applicationContext) {
//        this.serverProperties = serverProperties;
//        this.applicationContext = applicationContext;
//        this.resourceProperties = resourceProperties;
//        this.viewResolvers = viewResolversProvider.orderedStream()
//                .collect(Collectors.toList());
//        this.serverCodecConfigurer = serverCodecConfigurer;
//    }
//
//    @Bean
//    @ConditionalOnMissingBean(value = ErrorWebExceptionHandler.class,
//            search = SearchStrategy.CURRENT)
//    @Order(-1)
//    public ErrorWebExceptionHandler errorWebExceptionHandler(ErrorAttributes errorAttributes) {
//        // TODO 这里完成自定义ErrorWebExceptionHandler实现逻辑
//        log.error("errrrrrrrrrrrrr {}", errorAttributes);
//        return null;
//    }
//
//    @Bean
//    @ConditionalOnMissingBean(value = ErrorAttributes.class, search = SearchStrategy.CURRENT)
//    public DefaultErrorAttributes errorAttributes() {
//        return new DefaultErrorAttributes(this.serverProperties.getError().isIncludeException());
//    }
//}