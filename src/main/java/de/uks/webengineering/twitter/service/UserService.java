package de.uks.webengineering.twitter.service;

import de.uks.webengineering.twitter.persistence.User;
import de.uks.webengineering.twitter.persistence.UserRepository;
import org.apache.commons.collections4.IteratorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * UserService class for providing methods to handle User (User-class) objects.
 *
 * @author Jan-Christopher Hess (jan-chr.hess@student.uni-kassel.de)
 */
@Service
public class UserService
{
   private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

   @Autowired
   private UserRepository userRepository;

   /**
    * is user authenticated?
    *
    * @return user authenticated?
    */
   public boolean isAuthenticated()
   {
      Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
      return (principal instanceof String) == false;
   }

   /**
    * Creates a new tweet and stores it.
    *
    * @return authenticated 'user object
    */
   public User getUser()
   {
      Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

      if (principal instanceof String)
      {
         return null;
      }

      org.springframework.security.core.userdetails.User userDetails = (org.springframework.security.core.userdetails.User)principal;
      User user = userRepository.findByUsername(userDetails.getUsername());
      if (user == null)
      {
         LOGGER.warn("User from session not found. username={}.", userDetails.getUsername());
         return null;
      }

      return user;
   }

   public boolean userExists(String username)
   {
      return username != null && userRepository.findByUsername(username) != null;
   }

   public boolean emailExists(String email)
   {
      return email != null && userRepository.findByEmail(email) != null;
   }

   /**
    * Registers a user and stores it.
    *
    * @param username the user' username
    * @param email    the user' email
    * @param password the user' password
    * @return stored 'user object
    */
   @Transactional
   public User registerUser(String username, String email, String password)
   {
      if (userExists(username) || emailExists(email))
      {
         LOGGER.warn("User username={}, email={} already exists.", username, email);
         return null;
      }

      User user = new User();
      user.setUsername(username);
      user.setEmail(email);
      user.setHeader("https://pbs.twimg.com/profile_banners/3334325555/1434703658/1500x500");
      user.setDescription("I am who I am.");
      user.setPassword(new ShaPasswordEncoder(256).encodePassword(password, null));

      return storeUser(user);
   }

   /**
    * Login a user manually/programmatically.
    *
    * @param user the user' object
    */
   @Transactional
   public void login(User user)
   {
      org.springframework.security.core.userdetails.User authUser = new org.springframework.security.core.userdetails.User(
            user.getUsername(),
            user.getPassword(),
            AuthorityUtils.createAuthorityList("ROLE_USER"));
      Authentication auth = new UsernamePasswordAuthenticationToken(authUser, authUser.getPassword(),
            authUser.getAuthorities());
      SecurityContextHolder.getContext().setAuthentication(auth);
      LOGGER.info("Programmatically logged in user={}", user);
   }

   /**
    * Creates a new User -> Following association and stores it.
    *
    * @param id the following' id attribute
    * @return stored 'user object
    */
   @Transactional
   public User followUser(long id)
   {
      User user = getUser();
      User following = findUser(id);

      if (following == null)
      {
         LOGGER.error("User by id={} not found.", id);
         return null;
      }

      user.addFollowing(following);
      LOGGER.info("User user={} is following user={}", user, following);
      return storeUser(user);
   }

   /**
    * Removes the User -> Following association and stores it.
    *
    * @param id the following' id attribute
    * @return stored 'user object
    */
   @Transactional
   public User unfollowUser(long id)
   {
      User user = getUser();
      User following = findUser(id);

      if (following == null)
      {
         LOGGER.error("User by id={} not found.", id);
         return null;
      }

      user.removeFollowing(following);
      LOGGER.info("User user={} is not following user={} anymore.", user, following);
      return storeUser(user);
   }

   /**
    * Removes the User -> Follower association and stores it.
    *
    * @param id the follower' id attribute
    * @return stored 'user object
    */
   @Transactional
   public User blockUser(long id)
   {
      User user = getUser();
      User follower = findUser(id);

      if (follower == null)
      {
         LOGGER.error("User by id={} not found.", id);
         return null;
      }

      user.removeFollower(follower);
      follower.removeFollowing(user);
      LOGGER.info("User user={} has blocked user={}.", user, follower);
      return storeUser(user);
   }

   /**
    * Changes user details and stores it.
    *
    * @param user        the user' object
    * @param username    the user' username attribute
    * @param email       the user' email attribute
    * @param description the user' description attribute
    * @param header      the user' header attribute
    * @return stored 'user object
    */
   @Transactional
   public User changeUser(User user, String username, String email, String description, String header)
   {
      user.setUsername(username);
      user.setEmail(email);
      user.setDescription(description);
      user.setHeader(header);
      return storeUser(user);
   }

   /**
    * Changes password of parsed user and stores it.
    *
    * @param user     the user' object
    * @param password the user' password attribute
    * @return stored 'user object
    */
   @Transactional
   public User changePassword(User user, String password)
   {
      user.setPassword(new ShaPasswordEncoder(256).encodePassword(password, null));
      return storeUser(user);
   }

   @Transactional
   public User storeUser(User user)
   {
      try
      {
         userRepository.save(user);
      }
      catch (DataIntegrityViolationException e)
      {
         LOGGER.error("Error while storing user (DataIntegrityViolationException).", e);
         return null;
      }
      LOGGER.info("User user={} stored.", user);
      return user;
   }

   public List<User> findUsers()
   {
      return IteratorUtils.toList(userRepository.findAll().iterator());
   }

   public User findUser(long id)
   {
      return userRepository.findOne(id);
   }

   public User findUser(String username)
   {
      return userRepository.findByUsername(username);
   }
}