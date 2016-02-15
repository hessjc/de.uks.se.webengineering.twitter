package de.uks.webengineering.twitter.service;

import de.uks.webengineering.twitter.persistence.Tweet;
import de.uks.webengineering.twitter.persistence.TweetRepository;
import de.uks.webengineering.twitter.persistence.User;
import de.uks.webengineering.twitter.utils.FormatHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * TweetService class for providing methods to handle Tweet (Tweet-class) objects.
 *
 * @author Jan-Christopher Hess (jan-chr.hess@student.uni-kassel.de)
 */
@Service
public class TweetService
{
   private static Logger LOGGER = LoggerFactory.getLogger(TweetService.class);

   @Autowired
   private TweetRepository tweetRepository;

   @Autowired
   private UserService userService;

   /**
    * Creates a new tweet and stores it.
    *
    * @param message the tweet' message
    * @return stored 'tweet object
    */
   @Transactional
   public Tweet createTweet(String message)
   {
      try
      {
         byte[] b = message.getBytes("UTF-8");

         if(FormatHelper.isUTF8Interpreted(message) == false) {
            throw new UnsupportedEncodingException();
         }

         if (message.length() > 141 || FormatHelper.charLength(b) > 141)
         {
            LOGGER.error("Error while parsing tweet message (message is longer then 141 characters)");
            return null;
         }
      }
      catch (UnsupportedEncodingException e)
      {
         LOGGER.error("Error while parsing tweet message (message is not UTF-8 encoded)");
         return null;
      }

      Tweet tweet = new Tweet();
      tweet.setDate(new Date());
      tweet.setMessage(message);

      User user = userService.getUser();
      tweet.setUser(user);

      return storeTweet(tweet);
   }

   /**
    * Search for a tweet tag.
    *
    * @param query the tweet' searching tag
    * @return list of 'tweet objects
    * @first search for all hashtag matching expressions
    * @second remove all non-exactly-matching expressions.
    */
   @Transactional
   public List<Tweet> search(String query)
   {
      query = query.toLowerCase();
      String regex = "#\\w\\w+";
      List<Tweet> tweets = new ArrayList<>();
      if (query.matches(regex))
      {
         tweets = tweetRepository.search(query);

         List<Tweet> removableTweets = new ArrayList<>();
         for (Tweet tweet : tweets)
         {
            boolean flag = false;
            String[] words = tweet.getMessage().split(" ");
            for (int i = 0; i < words.length; i++)
            {
               if (words[i].toLowerCase().equals(query))
               {
                  flag = true;
               }
            }
            if (flag == false)
            {
               removableTweets.add(tweet);
            }
         }
         tweets.removeAll(removableTweets);
      }
      Collections.sort(tweets, new Comparator<Tweet>()
      {
         @Override public int compare(Tweet o1, Tweet o2)
         {
            return o2.getDate().compareTo(o1.getDate());
         }
      });
      return tweets;
   }

   /**
    * Stores a tweet.
    *
    * @param tweet the tweet' object
    * @return stored 'tweet object
    */
   @Transactional
   public Tweet storeTweet(Tweet tweet)
   {
      try
      {
         tweetRepository.save(tweet);
      }
      catch (DataIntegrityViolationException e)
      {
         LOGGER.warn("Error while storing tweet (DataIntegrityViolationException)", e);
         return null;
      }

      LOGGER.info("Tweet tweet={} stored.", tweet);
      return tweet;
   }

   public Page<Tweet> getOwnTweets(Pageable pageable)
   {
      User user = userService.getUser();
      return tweetRepository.findTweetsByUserOrderByDateDesc(pageable, user);
   }

   public Page<Tweet> getUserTweets(Pageable pageable, User user)
   {
      return tweetRepository.findTweetsByUserOrderByDateDesc(pageable, user);
   }

   public Page<Tweet> getFollowingTweets(Pageable pageable)
   {
      User user = userService.getUser();
      List<Tweet> followingList = new ArrayList<Tweet>();

      for (User following : user.getFollowing())
      {
         List<Tweet> list = tweetRepository.findTweetsByUserOrderByDateDesc(following);
         followingList.addAll(list);
      }

      Collections.sort(followingList, new Comparator<Tweet>()
      {
         @Override public int compare(Tweet o1, Tweet o2)
         {
            return o2.getDate().compareTo(o1.getDate());
         }
      });

      Page<Tweet> foundPage = new PageImpl<Tweet>(followingList);

      return foundPage;
   }

   public Page<Tweet> getTweets(Pageable pageable)
   {
      return tweetRepository.findAllByOrderByDateDesc(pageable);
   }
}