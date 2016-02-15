package de.uks.webengineering.twitter.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/*
 * Methods for accessing tweets (Tweet-objects) in database.
 *
 * @author Jan-Christopher Hess (jan-chr.hess@student.uni-kassel.de)
 */
@Repository
@Transactional
public interface TweetRepository extends CrudRepository<Tweet, Long>
{
   Page<Tweet> findAllByOrderByDateDesc(Pageable pageable);

   Page<Tweet> findTweetsByUserOrderByDateDesc(Pageable pageable, User user);

   List<Tweet> findTweetsByUserOrderByDateDesc(User user);

   @Query("select t from TWEETS t where LOWER(t.message) like %?1%")
   List<Tweet> search(String query);
}
