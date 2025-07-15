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
import com.main.entity.Membership;
import com.main.repository.AccountRepository;
import com.main.repository.CustomerRepository;
import com.main.repository.MembershipRepository;
import com.main.service.CustomerService;
import com.main.utils.AddressUtil;
import com.main.utils.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

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
            cus.setImageAvt("/avatar.png");
            cus.setPhone("0000000000");
            customerRepository.save(cus);
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }
    @Override
    public byte[] generateQRCode(String content, int width, int height) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            return outputStream.toByteArray();
        } catch (WriterException | IOException e) {
            throw new RuntimeException("Lỗi khi tạo mã QR", e);
        }
    }
}


