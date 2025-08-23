package com.main.repository;

import com.main.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {
    @Query("SELECT a FROM Account a WHERE a.email = :email")
    Optional<Account> findByEmail(@Param("email") String email);

    @Query("SELECT MAX(a.accountId) FROM Account a WHERE a.accountId LIKE 'ACC%'")
    String findMaxAccountId();

    Optional<Account> findByProviderAndProviderIdAndStatusTrue(String provider, String providerId);

    // Hàm check email cho tài khoản thường
    @Query("SELECT a FROM Account a WHERE a.email = :email AND a.provider IS NULL AND a.status = true")
    Optional<Account> findByEmailAndProviderIsNull(String email);

    boolean existsByEmailAndProviderIsNotNull(String email);

    Optional<Account> findByAccountId(String accountId);

    @Query("""
        SELECT a.email
        FROM Account a
        WHERE a.role ='ADMIN'
        """)
    List<String> getEmailAdmin();

    @Query("""
        SELECT a.email
        FROM Facility f
        JOIN f.manager m
        JOIN m.account a
        WHERE f.facilityId = :areaId
        """)
    String getEmailArea(@Param("areaId") String areaId);


}
