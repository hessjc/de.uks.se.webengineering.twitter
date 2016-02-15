package de.uks.webengineering.twitter.persistence;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/*
 * Methods for accessing users (User-objects) in the database.
 *
 * @author Jan-Christopher Hess (jan-chr.hess@student.uni-kassel.de)
 */
@Repository
@Transactional
public interface UserRepository extends CrudRepository<User, Long>
{
   User findByUsername(String username);

   User findByEmail(String email);

   List<User> findByFollowers(Long id);

   List<User> findByFollowing(Long id);
}
