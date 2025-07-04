package com.main.repository;

import com.main.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {
    @Query("SELECT a FROM Account a WHERE a.email = :email")
    Optional<Account> findByEmail(@Param("email") String email);

    @Query("SELECT MAX(a.accountId) FROM Account a WHERE a.accountId LIKE 'ACC%'")
    String findMaxAccountId();

    Optional<Account> findByProviderAndProviderId(String provider, String providerId);

    Optional<Account> findByEmailAndProviderIsNull(String email);
    boolean existsByEmailAndProviderIsNotNull(String email);

}
