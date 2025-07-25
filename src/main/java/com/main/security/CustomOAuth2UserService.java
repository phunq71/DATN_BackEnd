package com.main.security;

import com.main.entity.Account;
import com.main.entity.Customer;
import com.main.entity.Membership;
import com.main.repository.AccountRepository;
import com.main.repository.CustomerRepository;
import com.main.repository.MembershipRepository;
import com.main.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final AccountRepository accountRepo;
    private final CustomerRepository customerRepo;
    private final CustomerService customerService;
    private final MembershipRepository membershipRepository;

    private static final Logger log = LoggerFactory.getLogger(CustomOAuth2UserService.class);

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {
        try {
            OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(request);

            String provider = request.getClientRegistration().getRegistrationId(); // "google" hoặc "facebook"
            String providerId = oAuth2User.getName(); // ID được cấp từ Google/Facebook
            String email = oAuth2User.getAttribute("email");
            String name = oAuth2User.getAttribute("name");

            String picture;
            if ("google".equals(provider)) {
                picture = oAuth2User.getAttribute("picture");
            } else if ("facebook".equals(provider)) {
                String fbId = oAuth2User.getAttribute("id");
                picture = "https://graph.facebook.com/" + fbId + "/picture?type=large";
            } else {
                picture = "https://example.com/default-avatar.png";
            }

            Optional<Account> existingAccount = accountRepo.findByProviderAndProviderIdAndStatusTrue(provider, providerId);
            Account account;

            if (existingAccount.isPresent()) {
                account = existingAccount.get();

                Customer customer = customerRepo.findById(account.getAccountId()).orElse(null);
                if (customer != null && picture != null && !picture.equals(customer.getImageAvt())) {
                    customer.setImageAvt(picture);
                    customerRepo.save(customer);
                }

            } else {
                account = new Account();
                String generatedId = customerService.generateCustomerId();
                account.setAccountId(generatedId);
                account.setEmail(email);
                account.setPassword("oauth"); // dấu hiệu là tài khoản OAuth
                account.setRole("USER");
                account.setStatus(true);
                account.setCreateAt(LocalDate.now());
                account.setProvider(provider);
                account.setProviderId(providerId);
                accountRepo.save(account);

                Customer customer = new Customer();
                customer.setCustomerId(generatedId);
                customer.setFullName(name);
                customer.setAddress("N/A");
                customer.setPhone("N/A");
                customer.setImageAvt(picture);
                customer.setMembership(membershipRepository.findById("MB01").orElse(null));
                customerRepo.save(customer);
            }

            // ✅ Trả về CustomOAuth2User chứa accountId
            return new CustomOAuth2User(oAuth2User, account.getAccountId());

        } catch (Exception e) {
            log.error("❌ Lỗi khi xử lý OAuth2 user: {}", e.getMessage(), e);
            throw new OAuth2AuthenticationException("OAuth2 login failed");
        }
    }
}
