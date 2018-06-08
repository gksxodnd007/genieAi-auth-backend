package com.finder.genie_ai.dao.mysql;

import com.finder.genie_ai.model.user.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserModel, Integer> {

    Optional<UserModel> findByUserId(String userId);
    Optional<UserModel> findByEmail(String email);

    List<UserModel> findByUserIdOrEmail(String userId, String mail);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE users" +
            "SET user_name = :userName, email = :email, birth = :birth, introduce = :introduce " +
            "WHERE user_id = :userId", nativeQuery = true)
    int updateUserInfo(@Param("userName") String userName,
                       @Param("email") String email,
                       @Param("birth") LocalDate birth,
                       @Param("introduce") String introduce,
                       @Param("userId") String userId);

    @Modifying
    @Query(value = "DELETE FROM users WHERE user_id = :userId", nativeQuery = true)
    void deleteByUserId(@Param("userId") String userId);

}
