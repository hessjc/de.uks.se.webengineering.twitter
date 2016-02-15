package de.uks.webengineering.twitter.controller;

import de.uks.webengineering.twitter.persistence.User;
import de.uks.webengineering.twitter.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * SettingController class for setting request handling.
 *
 * @author Jan-Christopher Hess (jan-chr.hess@student.uni-kassel.de)
 */
@Controller
public class SettingController
{
   private static Logger LOGGER = LoggerFactory.getLogger(SettingController.class);

   @Autowired
   private UserService userService;

   @RequestMapping(value = "/settings", method = RequestMethod.GET)
   public ModelAndView settings()
   {
      LOGGER.info("GET-Request to \"/settings\" for user={}.", userService.getUser());

      ModelAndView mav = new ModelAndView("welcome");

      return authenticateHelper(mav);
   }

   @RequestMapping(value = "/settings/account", method = RequestMethod.GET)
   public String saveAccountSettingsGETRequest()
   {
      LOGGER.debug("GET-Request to \"/settings/account\" - redirecting to \"/settings\".");
      return "redirect:/settings";
   }

   @RequestMapping(value = "/settings/account", method = RequestMethod.POST)
   public ModelAndView saveAccountSettingsPOSTRequest(@RequestParam("username") String username,
         @RequestParam("email") String email, @RequestParam("description") String description,
         @RequestParam("header") String header)
   {
      User user = userService.getUser();
      LOGGER.info(
            "POST-Request to \"/settings/account\" for user={} with parameter: username={}, email={}, description={}, header={}.",
            user, username, email, description, header);

      ModelAndView mav = new ModelAndView("settings");

      boolean authenticated = userService.isAuthenticated();

      if (authenticated)
      {
         boolean error = false;

         if ((userService.userExists(username) && StringUtils.equals(username, user.getUsername()) == false)
               || username.length() < 1)
         {
            LOGGER.warn("User={}, username={} already exists or username is not allowed.", user, username);
            addErrorProperty(mav, "userExists");
            error = true;
         }
         else
         {
            mav.addObject("username", username);
         }

         if ((userService.emailExists(email) && StringUtils.equals(email, user.getEmail()) == false)
               || email.length() < 1)
         {
            LOGGER.warn("User={}, email={} already exists or email is not allowed.", user, email);
            addErrorProperty(mav, "emailExists");
            error = true;
         }
         else
         {
            mav.addObject("email", email);
         }

         if (description.length() > 1024)
         {
            LOGGER.warn("\"User={}, description is too long.", user);
            addErrorProperty(mav, "descriptionLength");
            error = true;
         }
         else
         {
            mav.addObject("description", description);
         }

         if (error == false)
         {
            LOGGER.debug("Input data for username={}, email={}, description and header succeeded.", username, email);
            user = userService.changeUser(user, username, email, description, header);
            userService.login(user);
            addErrorProperty(mav, "accountSuccess");
         }
      }

      return authenticateHelper(mav);
   }

   @RequestMapping(value = "/settings/password", method = RequestMethod.GET)
   public String savePasswordSettingsGETRequest()
   {
      LOGGER.debug("GET-Request to \"/settings/password\" - redirecting to \"/settings\".");
      return "redirect:/settings";
   }

   @RequestMapping(value = "/settings/password", method = RequestMethod.POST)
   public ModelAndView savePasswordSettings(@RequestParam("password") String password,
         @RequestParam("password1") String password1, @RequestParam("password2") String password2)
   {
      User user = userService.getUser();
      LOGGER.info("Request to \"/settings/password\" for user={}", user);

      ModelAndView mav = new ModelAndView("settings");

      boolean authenticated = userService.isAuthenticated();

      if (authenticated)
      {
         boolean error = false;

         if (StringUtils.equals(new ShaPasswordEncoder(256).encodePassword(password, null), user.getPassword())
               == false)
         {
            LOGGER.warn("User={}, current password is not confirmed.", user);
            addErrorProperty(mav, "passwordNotConfirmed");
            error = true;
         }

         if (StringUtils.equals(password1, password2) == false || password1.length() < 1)
         {
            LOGGER.warn("User={}, passwords are not equal.", user);
            addErrorProperty(mav, "passwordsNotEqual");
            error = true;
         }

         if (error == false)
         {
            LOGGER.debug("User={}, input data for current password and new passwords succeeded.", user);
            user = userService.changePassword(user, password);
            userService.login(user);
            addErrorProperty(mav, "passwordSuccess");
         }
      }

      return authenticateHelper(mav);
   }

   private ModelAndView authenticateHelper(ModelAndView mav)
   {
      boolean authenticated = userService.isAuthenticated();
      mav.addObject("auth", authenticated);

      if (authenticated)
      {
         LOGGER.debug("User={} is authenticated", userService.getUser());
         mav.setViewName("settings");
         mav.addObject("template", "settings");

         mav.addObject("user", userService.getUser());
         mav.addObject("gravatar", DigestUtils.md5DigestAsHex(userService.getUser().getEmail().getBytes()));
      }
      return mav;
   }

   private void addErrorProperty(ModelAndView mav, String key)
   {
      mav.addObject(key, true);
   }
}