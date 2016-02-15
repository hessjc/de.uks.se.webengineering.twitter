package de.uks.webengineering.twitter.controller;

import de.uks.webengineering.twitter.persistence.Tweet;
import de.uks.webengineering.twitter.persistence.User;
import de.uks.webengineering.twitter.service.TweetService;
import de.uks.webengineering.twitter.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * MainController class for index entry points.
 *
 * @author Jan-Christopher Hess (jan-chr.hess@student.uni-kassel.de)
 */
@Controller
public class MainController
{
   private static Logger LOGGER = LoggerFactory.getLogger(MainController.class);

   @Autowired
   private UserService userService;

   @Autowired
   private TweetService tweetService;

   /*
    * focus on entry point "/".
    */
   @RequestMapping(value = "/", method = RequestMethod.GET)
   public ModelAndView index(Pageable pageable, @RequestParam(value = "show", defaultValue = "all") String show)
   {
      LOGGER.info("Request to \"/\".");

      ModelAndView mav = new ModelAndView("welcome");
      mav.addObject("template", "welcome");

      boolean authenticated = userService.isAuthenticated();
      mav.addObject("auth", authenticated);

      if (authenticated)
      {
         LOGGER.debug("User={} is authenticated", userService.getUser());

         mav.setViewName("index");
         mav.addObject("template", "index");

         mav.addObject("user", userService.getUser());
         mav.addObject("users", userService.findUsers());
         mav.addObject("gravatar", DigestUtils.md5DigestAsHex(userService.getUser().getEmail().getBytes()));

         Page<Tweet> tweets;
         switch (show)
         {
            case "own":
               tweets = tweetService.getOwnTweets(pageable);
               break;
            case "following":
               tweets = tweetService.getFollowingTweets(pageable);
               break;
            default:
               tweets = tweetService.getTweets(pageable);
               break;
         }
         mav.addObject("selection", show);

         mav.addObject("page", tweets);
         mav.addObject("tweets", tweets);
      }
      return mav;
   }

   /*
    * focus on following users "/following".
    */
   @RequestMapping(value = "/following", method = RequestMethod.GET)
   public ModelAndView following()
   {
      LOGGER.info("Request to \"/following\".");

      boolean authenticated = userService.isAuthenticated();
      ModelAndView mav = new ModelAndView("welcome");
      mav.addObject("auth", authenticated);

      if (authenticated)
      {
         LOGGER.debug("User={} is authenticated.", userService.getUser());

         mav.setViewName("following");
         mav.addObject("template", "following");

         mav.addObject("user", userService.getUser());
         mav.addObject("gravatar", DigestUtils.md5DigestAsHex(userService.getUser().getEmail().getBytes()));
      }
      return mav;
   }

   /*
    * focus on follower users "/followers".
    */
   @RequestMapping(value = "/follower", method = RequestMethod.GET)
   public ModelAndView follower()
   {
      LOGGER.info("Request to \"/follower\".");

      boolean authenticated = userService.isAuthenticated();
      ModelAndView mav = new ModelAndView("welcome");
      mav.addObject("auth", authenticated);

      if (authenticated)
      {
         LOGGER.debug("User={} is authenticated.", userService.getUser());

         mav.setViewName("follower");
         mav.addObject("template", "follower");

         mav.addObject("user", userService.getUser());
         mav.addObject("gravatar", DigestUtils.md5DigestAsHex(userService.getUser().getEmail().getBytes()));
      }
      return mav;
   }

   /*
    * several users profiles focus.
    */
   @RequestMapping(value = "/profile/{username}", method = RequestMethod.GET)
   public ModelAndView profile(Pageable pageable, @PathVariable("username") String username)
   {
      LOGGER.info("Request to \"/profile/" + username + "\".");

      ModelAndView mav = new ModelAndView("error");
      mav.addObject("template", "error");

      boolean authenticated = userService.isAuthenticated();
      mav.addObject("auth", authenticated);

      User user = userService.findUser(username);
      mav.addObject("user", user);

      if (user != null)
      {
         mav.setViewName("profile");
         mav.addObject("template", "profile");

         Page<Tweet> tweets = tweetService.getUserTweets(pageable, user);
         mav.addObject("tweets", tweets);
         mav.addObject("page", tweets);

         mav.addObject("gravatar", DigestUtils.md5DigestAsHex(user.getEmail().getBytes()));
      }
      else
      {
         LOGGER.error("User={} not found.", username);
      }

      return mav;
   }
}