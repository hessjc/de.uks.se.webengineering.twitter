package de.uks.webengineering.twitter.persistence;

import org.hibernate.validator.constraints.Email;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

/*
 * User object.
 *
 * @author Jan-Christopher Hess (jan-chr.hess@student.uni-kassel.de)
 */
@Entity(name = "USERS")
@Table(name = "USERS")
public class User
{
   @Id
   @GeneratedValue
   private Long id;

   @Version
   private Long version;

   @Column(unique = true, nullable = false)
   private String username;

   @Email
   @Column(unique = true, nullable = false)
   private String email;

   @Column(nullable = false)
   private String password;

   @Column(columnDefinition = "TEXT", length = 1024)
   private String description;

   private String header;

   @ManyToMany(cascade = CascadeType.ALL, mappedBy = "following")
   private Set<User> followers;

   @ManyToMany(cascade = CascadeType.ALL)
   private Set<User> following;

   @OneToMany(mappedBy = "user")
   private Set<Tweet> tweets;

   public Long getId()
   {
      return id;
   }

   public Long getVersion()
   {
      return version;
   }

   public String getUsername()
   {
      return username;
   }

   public void setUsername(String username)
   {
      this.username = username;
   }

   public String getEmail()
   {
      return email;
   }

   public void setEmail(String email)
   {
      this.email = email;
   }

   public String getPassword()
   {
      return password;
   }

   public void setPassword(String password)
   {
      this.password = password;
   }

   public String getDescription()
   {
      return description;
   }

   public void setDescription(String description)
   {
      this.description = description;
   }

   public String getHeader()
   {
      return header;
   }

   public void setHeader(String header)
   {
      this.header = header;
   }

   public Set<User> getFollowers()
   {
      if (followers == null)
      {
         followers = new LinkedHashSet<>();
      }
      return followers;
   }

   public void addFollower(User follower)
   {
      getFollowers().add(follower);
   }

   public void removeFollower(User follower)
   {
      getFollowers().remove(follower);
   }

   public Set<User> getFollowing()
   {
      if (following == null)
      {
         following = new LinkedHashSet<>();
      }
      return following;
   }

   public void addFollowing(User following)
   {
      getFollowing().add(following);
   }

   public void removeFollowing(User following)
   {
      getFollowing().remove(following);
   }

   public Set<Tweet> getTweets()
   {
      if (tweets == null)
      {
         tweets = new LinkedHashSet<>();
      }
      return tweets;
   }

   public void addTweet(Tweet tweet)
   {
      getTweets().add(tweet);
   }

   @Override
   public String toString()
   {
      return "User{" +
            "id=" + id +
            ", version=" + version +
            ", username='" + username + '\'' +
            ", email='" + email + '\'' +
            ", password='" + password + '\'' +
            '}';
   }

   @Override
   public boolean equals(Object o)
   {
      if (this == o) return true;
      if (o == null || o instanceof User == false) return false;

      User user = (User)o;

      return !(id != null ? !id.equals(user.id) : user.id != null);
   }

   @Override
   public int hashCode()
   {
      return id != null ? id.hashCode() : 0;
   }
}