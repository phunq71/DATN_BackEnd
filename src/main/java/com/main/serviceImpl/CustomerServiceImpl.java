package com.main.serviceImpl;

import com.google.zxing.WriterException;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.main.dto.CustomerDTO;
import com.main.dto.CustomerRegisterDTO;
import com.main.entity.Account;
import com.main.entity.Customer;
import com.main.entity.Image;
import com.main.entity.Membership;
import com.main.repository.AccountRepository;
import com.main.repository.CustomerRepository;
import com.main.repository.MembershipRepository;
import com.main.service.CustomerService;
import com.main.utils.AddressUtil;
import com.main.utils.AuthUtil;
import com.main.utils.CommonUtils;
import com.main.utils.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import javax.imageio.ImageIO;

@Service
public class CustomerServiceImpl implements CustomerService {
    @Lazy
    @Autowired
    private PasswordEncoder passwordEncoder;

   private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final MembershipRepository membershipRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository, AccountRepository accountRepository, MembershipRepository membershipRepository) {
        this.customerRepository = customerRepository;
        this.accountRepository = accountRepository;
        this.membershipRepository = membershipRepository;
    }


    @Override
    public String generateCustomerId() {
        String maxId = customerRepository.findMaxCustomerId(); // VD: CUS000000123

        long nextId = 1;
        if (maxId != null && maxId.startsWith("CUS")) {
            try {
                // Tách phần số, ví dụ: từ "CUS000000123" → "000000123" → 123
                long current = Long.parseLong(maxId.substring(3));
                nextId = current + 1;
            } catch (NumberFormatException e) {
                throw new RuntimeException("CustomerID không hợp lệ: " + maxId);
            }
        }

        return String.format("CUS%09d", nextId); // Kết quả: "CUS000000124"
    }

    @Override
    public CustomerDTO getCustomerDTOByaccountId(String accountId) {
        CustomerDTO customerDTO = customerRepository.getCustomerByAccountID(accountId);
        String[] address= AddressUtil.splitAddress(customerDTO.getAddress());
        customerDTO.setAddressDetail(address[0]);
        customerDTO.setWard(address[1]);
        customerDTO.setDistrict(address[2]);
        customerDTO.setProvince(address[3]);
        return customerDTO;
    }

    @Override
    public CustomerDTO saveCustomer(CustomerDTO customerDTO, MultipartFile avatar) {
        try{
            Customer customer = new Customer();
            customer.setCustomerId(customerDTO.getCustomerId());
            customer.setFullName(customerDTO.getFullName());
            customer.setGender(customerDTO.getGender());
            customer.setAddress(customerDTO.getAddressDetail()+", "+ customerDTO.getWard()+", "+ customerDTO.getDistrict()+", "+ customerDTO.getProvince());
            customer.setDob(customerDTO.getDob());
            customer.setImageAvt(customerDTO.getImageAvt());
            customer.setAddressIdGHN(customerDTO.getAddressIdGHN());

            Customer oldCustomer = customerRepository.findById(customer.getCustomerId()).get();
            Customer savedCustomer =mergeCustomer(oldCustomer, customer);
            if(avatar!=null){
                String filename=FileUtil.saveImage(avatar);
                savedCustomer.setImageAvt(filename);
                FileUtil.deleteFile(oldCustomer.getImageAvt());
            }
            savedCustomer.setMembership(oldCustomer.getMembership());
            customerRepository.save(savedCustomer);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public Customer mergeCustomer(Customer oldCustomer, Customer newCustomer) {
        if (oldCustomer == null && newCustomer == null) {
            return null;
        }
        if (oldCustomer == null) {
            return newCustomer;
        }
        if (newCustomer == null) {
            return oldCustomer;
        }

        return new Customer(
                newCustomer.getCustomerId() != null ? newCustomer.getCustomerId() : oldCustomer.getCustomerId(),
                newCustomer.getFullName() != null ? newCustomer.getFullName() : oldCustomer.getFullName(),
                newCustomer.getPhone() != null ? newCustomer.getPhone() : oldCustomer.getPhone(),
                newCustomer.getGender() != null ? newCustomer.getGender() : oldCustomer.getGender(),
                newCustomer.getAddress() != null ? newCustomer.getAddress() : oldCustomer.getAddress(),
                newCustomer.getAddressIdGHN() != null ? newCustomer.getAddressIdGHN() : oldCustomer.getAddressIdGHN(),
                newCustomer.getDob() != null ? newCustomer.getDob() : oldCustomer.getDob(),
                newCustomer.getImageAvt() != null ? newCustomer.getImageAvt() : oldCustomer.getImageAvt()
        );
    }
    @Override
    public Customer findByAccountID(String accountId) {
        return customerRepository.findByAccount_AccountId(accountId);
    }
    @Override
    public Customer save(Customer customer) {
        return customerRepository.save(customer);
    }
    @Override
    public boolean saveCustomerRegister(CustomerRegisterDTO customerRegisterDTO) {
        try {
            Account acc = new Account();
            acc.setAccountId(generateCustomerId());
            acc.setEmail(customerRegisterDTO.getEmail());
            //mã hóa mk
            String hashedPassword = passwordEncoder.encode(customerRegisterDTO.getPassword());
            acc.setPassword(hashedPassword);
            acc.setRole("USER");
            acc.setStatus(true);
            acc.setCreateAt(LocalDate.now());
            accountRepository.save(acc);
            Membership mber = membershipRepository.findById("MB01").get();
            Customer cus = new Customer();
            cus.setCustomerId(acc.getAccountId());
            cus.setFullName(customerRegisterDTO.getFullname());
            cus.setGender(customerRegisterDTO.getGender());
            cus.setAddress(customerRegisterDTO.getAddress());
            cus.setAddressIdGHN(customerRegisterDTO.getFullAddressID());
            cus.setDob(customerRegisterDTO.getDob());
            cus.setMembership(mber);
            cus.setImageAvt("avatar.png");
            cus.setPhone("0000000000");
            customerRepository.save(cus);
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }


    @Override
    public byte[] createQRCode() {
        String customerID, code;
        customerID = AuthUtil.getAccountID();
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[48]; // 48 bytes ≈ 64 ký tự khi mã hóa Base64
        random.nextBytes(bytes);
        code = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        long expireAt = System.currentTimeMillis() / 1000 + 300;
        // Gộp chuỗi raw
        String raw = customerID + ":" + code + ":" + expireAt;
        // Mã hóa toàn bộ chuỗi bằng Base64
        String encodedToken = Base64.getUrlEncoder().withoutPadding().encodeToString(raw.getBytes());
        Customer customer = new Customer();
        customer = customerRepository.findByCustomerId(customerID);
        customer.setQrToken(encodedToken);

        customerRepository.save(customer);


        String fileName = customer.getImageAvt();
        BufferedImage avatar;
        String img = customer.getImageAvt();

        try {
            if (img.startsWith("http://") || img.startsWith("https://")) {
                // Ảnh từ mạng (OAuth2)
                URL imageUrl = new URL(img);
                avatar = ImageIO.read(imageUrl);
            } else {
                // Ảnh từ file hệ thống
                String filePath = "uploads/" + img;
                File imageFile = new File(filePath);
                if (!imageFile.exists()) {
                    throw new RuntimeException("File không tồn tại: " + filePath);
                }
                avatar = ImageIO.read(imageFile);
            }
        } catch (IOException e) {
            throw new RuntimeException("Không thể đọc ảnh avatar: " + img, e);
        }

        return CommonUtils.generateQRCodeWithAvatar(encodedToken, 300, 300, avatar);

    }

    public Customer verifyQRCodeToken(String qrToken) {
        try {
            // 1. Giải mã token từ base64
            byte[] decodedBytes = Base64.getUrlDecoder().decode(qrToken);
            String raw = new String(decodedBytes); // VD: "CUS00000026:randomCode:1721222000"

            // 2. Tách chuỗi thành 3 phần: customerID, code, expireAt
            String[] parts = raw.split(":");
            if (parts.length != 3) {
                throw new IllegalArgumentException("Mã QR không hợp lệ");
            }

            String customerId = parts[0];
            String code = parts[1];
            long expireAt = Long.parseLong(parts[2]);

            // 3. Kiểm tra thời gian hết hạn
            long now = System.currentTimeMillis() / 1000;
            if (now > expireAt) {
                throw new IllegalArgumentException("Mã QR đã hết hạn");
            }

            // 4. Kiểm tra trong DB (chắc chắn token tồn tại và chưa bị reset)
            Customer customer = customerRepository.findByQrToken(qrToken);
            if (customer == null || !customer.getCustomerId().equals(customerId)) {
                throw new IllegalArgumentException("Không tìm thấy khách hàng phù hợp");
            }

            // 5. Xóa QR token sau khi xác thực (1 lần dùng)
            customer.setQrToken(null);
            customerRepository.save(customer);

            return customer;

        } catch (Exception e) {
            // Có thể log ra chi tiết nếu cần
            return null; // Hoặc ném exception cụ thể tùy mục đích dùng
        }
    }




}


