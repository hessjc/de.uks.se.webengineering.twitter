package de.uks.webengineering.twitter.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

/**
 * ExceptionConfiguration class for global error handling.
 *
 * @author Jan-Christopher Hess (jan-chr.hess@student.uni-kassel.de)
 */
@ControllerAdvice
public class ExceptionConfiguration
{
   private static final Logger LOG = LoggerFactory.getLogger(ExceptionConfiguration.class);

   /*
    * global ExceptionHandler class for error-handling.
    */
   @ExceptionHandler(Exception.class)
   public String handleError(Exception e, HttpServletRequest request)
   {
      LOG.error("Uncaught exception. msg={}, url={}", e.getMessage(), request.getRequestURL(), e);
      return "error";
   }
}