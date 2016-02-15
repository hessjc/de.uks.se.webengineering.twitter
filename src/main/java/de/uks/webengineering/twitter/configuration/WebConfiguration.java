package de.uks.webengineering.twitter.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.List;
import java.util.Locale;

/**
 * WebConfiguration class for simplifying url-template pattern.
 *
 * @author Jan-Christopher Hess (jan-chr.hess@student.uni-kassel.de)
 */
@Configuration
public class WebConfiguration extends WebMvcConfigurerAdapter
{
   /*
    *  routes url-view-pattern
    */
   @Override
   public void addViewControllers(ViewControllerRegistry registry)
   {
      registry.addViewController("/login").setViewName("login");
      registry.addViewController("/about").setViewName("about");
      registry.addViewController("/help").setViewName("help");
      registry.addViewController("/impressum").setViewName("impressum");
   }

   /*
    * predefining project arguments
    */
   @Override
   public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers)
   {
      PageableHandlerMethodArgumentResolver resolver = new PageableHandlerMethodArgumentResolver();
      resolver.setFallbackPageable(new PageRequest(0, 10));
      argumentResolvers.add(resolver);
      super.addArgumentResolvers(argumentResolvers);
   }

   /*
    * i18n switching language support
    */
   @Bean
   public LocaleResolver localeResolver()
   {
      SessionLocaleResolver slr = new SessionLocaleResolver();
      slr.setDefaultLocale(Locale.ENGLISH);
      return slr;
   }

   /*
    * i18n switching language support
    */
   @Bean
   public LocaleChangeInterceptor localeChangeInterceptor()
   {
      LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
      lci.setParamName("lang");
      return lci;
   }

   @Override
   public void addInterceptors(InterceptorRegistry registry)
   {
      registry.addInterceptor(localeChangeInterceptor());
   }
}