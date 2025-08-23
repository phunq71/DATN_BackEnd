package com.main.repository;

import com.main.dto.CustomerDTO;
import com.main.dto.CustomerManagementDTO;
import com.main.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, String > {
    @Query("SELECT MAX(c.customerId) FROM Customer c")
    String findMaxCustomerId();

    Customer findByAccount_AccountId(String accountID);

    Customer findByCustomerId(String customerID);

    Customer findByQrToken(String qrToken);

    @Query("""
    SELECT c.customerId
         , c.fullName
         , c.gender
         , c.address
         , c.membership.rank
         , c.dob
         , c.imageAvt
         , c.addressIdGHN FROM Customer c
             WHERE c.customerId = :accountId
    """)
    CustomerDTO getCustomerByAccountID(@Param("accountId") String accountId);

    @Query("""
    SELECT  c.customerId, c.fullName, c.phone
            , c.gender, c.address, c.addressIdGHN
            , c.dob, c.imageAvt, c.membership.membershipId, c.membership.rank
            , a.createAt , a.updateAt, a.status
        FROM Customer c
        JOIN Account a ON a.accountId = c.account.accountId
       WHERE (:membershipId IS NULL OR c.membership.membershipId = :membershipId)
       AND (:customerId IS NULL OR c.customerId = :customerId)
    """)
    Page<CustomerManagementDTO> getAllCustomer(@Param("membershipId") String membershipId
                            , @Param("customerId") String customerId ,Pageable pageable);
//-------- cac hàm sau dùng để Xóa customer
    @Modifying
    @Query("""
        DELETE FROM ReviewImage ri
        WHERE ri.review.customer.customerId = :customerId
        AND ri.returnItem.returnItemId IS null
        """)
    void deleteReviewImageByCustomer(@Param("customerId") String customerId);

    @Modifying
    @Query("""
    DELETE FROM Review r
    WHERE r.customer.customerId = :customerId
    """)
    void deleteReviewByCustomer(@Param("customerId") String customerId);

    @Modifying
    @Query("""
    DELETE FROM Favorite f
    WHERE f.customer.customerId = :customerId
    """)
    void deleteFavouriteByCustomer(@Param("customerId") String customerId);

    @Modifying
    @Query("""
    DELETE FROM Cart c
    WHERE c.customer.customerId = :customerId
    """)
    void deleteCartByCustomer(@Param("customerId") String customerId);

    @Modifying
    @Query("""
    DELETE FROM UsedVoucher u
    WHERE u.customer.customerId = :customerId
    """)
    void deleteUsedVoucherByCustomer(@Param("customerId") String customerId);

    @Modifying
    @Query("""
    DELETE FROM Account a
    WHERE a.customer.customerId = :customerId
    """)
    void deleteAccountByCustomer(@Param("customerId") String customerId);

    @Modifying
    @Query("""
    DELETE FROM Customer a
    WHERE a.customerId = :customerId
    """)
    void deleteCustomerByCustomer(@Param("customerId") String customerId);
//-------------------
    // Lấy tỉnh thành, phục vụ cho biểu đồ
    @Query("SELECT c.address FROM Customer c")
    List<String> findAllAddresses();

}
