package de.uks.webengineering.twitter.controller;

import de.uks.webengineering.twitter.persistence.User;
import de.uks.webengineering.twitter.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * UserController class for user (user, login, registration) request handling.
 *
 * @author Jan-Christopher Hess (jan-chr.hess@student.uni-kassel.de)
 */
@Controller
public class UserController
{
   private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

   @Autowired
   private UserService userService;

   @Autowired
   private MainController mainController;

   @RequestMapping("/signup")
   public String signup()
   {
      LOGGER.info("Request to \"/signup\".");
      return ("signup");
   }

   @RequestMapping(value = "/register", method = RequestMethod.GET)
   public String registerGETRequest()
   {
      LOGGER.debug("GET-Request to \"/register\" - redirecting to \"/signup\".");
      return "redirect:/signup";
   }

   @RequestMapping(value = "/register", method = RequestMethod.POST)
   public ModelAndView registerPOSTRequest(String username, String email, String password1, String password2)
   {
      LOGGER.info("POST-Request to \"/register\" for username={}, email={}.", username, email);
      ModelAndView mav = new ModelAndView("signup");

      boolean error = username == null;

      if (StringUtils.equals(password1, password2) == false || password1.length() < 1)
      {
         LOGGER.warn("Passwords for username={}, email={} not equal.", username, email);
         addErrorProperty(mav, "passwordsNotEqual");
         error = true;
      }

      if (userService.userExists(username) || username.length() < 1)
      {
         LOGGER.warn("User for username={} already exists or username is not allowed.", username);
         addErrorProperty(mav, "userExists");
         error = true;
      }
      else
      {
         mav.addObject("username", username);
      }

      if (userService.emailExists(email) || email.length() < 1)
      {
         LOGGER.warn("User for email={} already exists or email is not allowed.", email);
         addErrorProperty(mav, "emailExists");
         error = true;
      }
      else
      {
         mav.addObject("email", email);
      }

      if (error == false)
      {
         LOGGER.debug("Input data for username={}, email={} succeeded.", username, email);
         User user = userService.registerUser(username, email, password1);
         userService.login(user);
         return mainController.index(new PageRequest(0, 10), "all");
      }

      return mav;
   }

   @RequestMapping(value = "/follow", method = RequestMethod.GET)
   public String followGETRequest()
   {
      LOGGER.debug("GET-Request to \"/follow\" - redirecting to \"/\".");
      return "redirect:/";
   }

   @RequestMapping(value = "/follow", method = RequestMethod.POST)
   public String followPOSTRequest(@RequestParam("id") long id)
   {
      LOGGER.info("POST-Request to \"/follow\" for id={}", id);

      boolean authenticated = userService.isAuthenticated();

      if (authenticated)
      {
         userService.followUser(id);
      }
      return "redirect:/";
   }

   @RequestMapping(value = "/unfollow", method = RequestMethod.GET)
   public String unfollowGETRequest()
   {
      LOGGER.debug("GET-Request to \"/unfollow\" - redirecting to \"/\".");
      return "redirect:/";
   }

   @RequestMapping(value = "/unfollow", method = RequestMethod.POST)
   public String unfollowPOSTRequest(@RequestParam("id") long id)
   {
      LOGGER.info("POST-Request to \"/unfollow\" for id={}", id);

      boolean authenticated = userService.isAuthenticated();

      if (authenticated)
      {
         userService.unfollowUser(id);
      }
      return "redirect:/following";
   }

   @RequestMapping(value = "/block", method = RequestMethod.GET)
   public String blockGETRequest()
   {
      LOGGER.debug("GET-Request to \"/block\" - redirecting to \"/\".");
      return "redirect:/";
   }

   @RequestMapping(value = "/block", method = RequestMethod.POST)
   public String blockPOSTRequest(@RequestParam("id") long id)
   {
      LOGGER.info("POST-Request to \"/block\" for id={}", id);
      boolean authenticated = userService.isAuthenticated();

      if (authenticated)
      {
         userService.blockUser(id);
      }
      return "redirect:/follower";
   }

   private void addErrorProperty(ModelAndView mav, String key)
   {
      mav.addObject(key, true);
   }
}