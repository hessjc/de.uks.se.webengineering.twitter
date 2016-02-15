package de.uks.webengineering.twitter.persistence;

import javax.persistence.*;
import java.util.Date;

/*
 * Tweet object.
 *
 * @author Jan-Christopher Hess (jan-chr.hess@student.uni-kassel.de)
 */
@Entity(name = "TWEETS")
@Table(name = "TWEETS")
public class Tweet
{
   @Id
   @GeneratedValue
   private Long id;

   @Version
   private Long version;

   private Date date;

   @Column(columnDefinition = "TEXT", length = 141)
   private String message;

   @ManyToOne
   private User user;

   public Long getId()
   {
      return id;
   }

   public Long getVersion()
   {
      return version;
   }

   public Date getDate()
   {
      return date;
   }

   public void setDate(Date date)
   {
      this.date = date;
   }

   public String getMessage()
   {
      return message;
   }

   public void setMessage(String message)
   {
      this.message = message;
   }

   public User getUser()
   {
      return user;
   }

   public void setUser(User user)
   {
      this.user = user;
   }

   @Override
   public String toString()
   {
      return "Tweet{" +
            "id=" + id +
            ", version=" + version +
            ", date=" + date +
            ", message='" + message + '\'' +
            ", user=" + user +
            '}';
   }
}