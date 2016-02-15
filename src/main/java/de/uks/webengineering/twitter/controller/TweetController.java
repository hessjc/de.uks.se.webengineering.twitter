package de.uks.webengineering.twitter.controller;

import de.uks.webengineering.twitter.persistence.Tweet;
import de.uks.webengineering.twitter.service.TweetService;
import de.uks.webengineering.twitter.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * TweetController class for tweet request handling.
 *
 * @author Jan-Christopher Hess (jan-chr.hess@student.uni-kassel.de)
 */
@Controller
public class TweetController
{
   private static Logger LOGGER = LoggerFactory.getLogger(TweetController.class);

   @Autowired
   private UserService userService;

   @Autowired
   private TweetService tweetService;

   @RequestMapping(value = "/tweet")
   public String tweet(@RequestParam("message") String message)
   {
      LOGGER.info("Request to \"/tweet\"");
      boolean authenticated = userService.isAuthenticated();

      if (authenticated)
      {
         Tweet tweet = tweetService.createTweet(message);
      }
      return "redirect:/";
   }
}