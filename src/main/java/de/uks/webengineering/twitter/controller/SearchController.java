package de.uks.webengineering.twitter.controller;

import de.uks.webengineering.twitter.service.TweetService;
import de.uks.webengineering.twitter.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * SearchController class for searching tweets.
 *
 * @author Jan-Christopher Hess (jan-chr.hess@student.uni-kassel.de)
 */
@Controller
public class SearchController
{
   private static final Logger LOGGER = LoggerFactory.getLogger(SearchController.class);

   @Autowired
   private TweetService tweetService;

   @Autowired
   private UserService userService;

   /*
    * modular search for hashtags "/search".
    */
   @RequestMapping("/search")
   public ModelAndView search(String query)
   {
      LOGGER.info("Request to \"/search\"");
      ModelAndView mav = new ModelAndView("welcome");

      boolean authenticated = userService.isAuthenticated();
      mav.addObject("auth", authenticated);

      if (authenticated)
      {
         mav.setViewName("search");
         mav.addObject("template", "search");

         if (query != null)
         {
            mav.addObject("list", tweetService.search(query));
            mav.addObject("query", query);
         }

         mav.addObject("user", userService.getUser());
         mav.addObject("gravatar", DigestUtils.md5DigestAsHex(userService.getUser().getEmail().getBytes()));
      }

      return mav;
   }
}