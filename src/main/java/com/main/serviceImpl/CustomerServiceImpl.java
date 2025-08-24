package com.main.serviceImpl;

import com.google.zxing.WriterException;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.main.dto.CustomerDTO;
import com.main.dto.CustomerManagementDTO;
import com.main.dto.CustomerRegisterDTO;
import com.main.entity.*;
import com.main.repository.*;
import com.main.security.CustomOAuth2UserService;
import com.main.service.CustomerService;
import com.main.service.MailService;
import com.main.utils.AddressUtil;
import com.main.utils.AuthUtil;
import com.main.utils.CommonUtils;
import com.main.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.mail.MailSender;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final PasswordEncoder passwordEncoder;

    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final MembershipRepository membershipRepository;


    private final TransactionRepository transactionRepository;

    private final MailService mailService;

    private final VoucherRepository voucherRepository;

    private final PromotionRepository promotionRepository;

    private final UsedVoucherRepository usedVoucherRepository;
    private final OrderRepository orderRepository;
    private final FileUtil fileUtil;


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
                String filename=fileUtil.saveImage(avatar);
                savedCustomer.setImageAvt(filename);
                fileUtil.deleteFile(oldCustomer.getImageAvt());
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
            Customer cus = new Customer();

            cus.setFullName(customerRegisterDTO.getFullname());
            cus.setGender(customerRegisterDTO.getGender());
            cus.setAddress(customerRegisterDTO.getAddress());
            cus.setAddressIdGHN(customerRegisterDTO.getFullAddressID());
            String raw = cus.getAddressIdGHN();
            String formatted = Arrays.stream(raw.split(","))
                    .map(String::trim) // xóa khoảng trắng hai bên
                    .collect(Collectors.joining("/"));

            cus.setAddressIdGHN(formatted);
            cus.setDob(customerRegisterDTO.getDob());
            cus.setMembership(null);
            cus.setImageAvt("avatar.png");
            cus.setPhone("N/A");
            cus.setAccount(acc);
            customerRepository.save(cus);

            Customer cus1 = customerRepository.findByCustomerId(cus.getCustomerId());

            updateRankByCustomerId(cus1);
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

    @Override
    public Boolean updateAddrBoolean(String Address, String AddressIdGHN) {
        Customer customer = customerRepository.findByCustomerId(AuthUtil.getAccountID());
        customer.setAddress(Address);
        customer.setAddressIdGHN(AddressIdGHN);
        try {
            customerRepository.save(customer);
            return true;
        }catch (Exception e){
            e.getMessage();
            return false;
        }
    }

    @Override
    @Transactional
    public Void updateRankByCustomerId(Customer customer) {
        // Tổng chi tiêu
        BigDecimal totalAmount = transactionRepository.sumAmountByCustomer(customer);

        // membership hiện tại của KH (có thể null)
        String membershipId = (customer.getMembership() != null)
                ? customer.getMembership().getMembershipId()
                : null;

        // Hạng phù hợp hiện tại
        List<Membership> memberships = membershipRepository.findMembershipBySpent(totalAmount);
        Membership newMembership = memberships.isEmpty() ? null : memberships.get(0); // lấy hạng cao nhất phù hợp

        // Nếu có hạng mới
        if (newMembership != null) {
            String newMembershipId = newMembership.getMembershipId();
            System.out.println("😚😚😚😚😚😚😚😚😚😚😚😚😚😚😚😚😚😚😚😚😚");
            // So sánh an toàn (nếu khác thì update)
            boolean isDifferent = (membershipId == null)
                    || (!membershipId.equalsIgnoreCase(newMembershipId));

            if (isDifferent) {
                // Cập nhật hạng mới
                customer.setMembership(newMembership);
                customerRepository.save(customer);

                // Gửi mail chúc mừng
                mailService.sendCongratulationRank(customer);

                // Lấy promotions của rank mới
                List<Promotion> promotions = promotionRepository.findByNewRank(newMembershipId);
                if (promotions != null && !promotions.isEmpty()) {
                    promotions.forEach(promotion -> {
                        if (promotion.getVouchers() != null) {
                            promotion.getVouchers().forEach(voucher -> {
                                if (voucher != null) {
                                    UsedVoucher usedVoucher = new UsedVoucher();
                                    usedVoucher.setType(false);
                                    usedVoucher.setCustomer(customer);
                                    usedVoucher.setVoucher(voucher);

                                    UsedVoucherID usedVoucherID = new UsedVoucherID();
                                    usedVoucherID.setCustomer(customer.getCustomerId());
                                    usedVoucherID.setVoucher(voucher.getVoucherID());
                                    usedVoucher.setUsedVoucherID(usedVoucherID);

                                    usedVoucherRepository.save(usedVoucher);

                                    // Trừ số lượng
                                    if (voucher.getQuantityRemaining() != null && voucher.getQuantityRemaining() > 0) {
                                        voucher.setQuantityRemaining(voucher.getQuantityRemaining() - 1);
                                    }
                                    voucher.setQuantityUsed(
                                            (voucher.getQuantityUsed() == null ? 0 : voucher.getQuantityUsed()) + 1
                                    );
                                    voucherRepository.save(voucher);

                                    // Gửi mail voucher
                                    if (customer.getAccount() != null) {
                                        mailService.sendVoucherEmail(
                                                customer.getAccount().getEmail(),
                                                voucher.getVoucherID(),
                                                voucher.getDiscountValue(),
                                                voucher.getEndDate()
                                        );
                                    }
                                }
                            });
                        }
                    });
                }
            }
        }

        return null;
    }


    public Page<CustomerManagementDTO> getAllCustomer(String membershipId, String customerId, int page) {
        Pageable pageable = PageRequest.of(page, 12);
        return customerRepository.getAllCustomer(membershipId, customerId, pageable);
    }
    @Override
    @Transactional
    public Boolean deleteCustomer(String customerId) {
        Optional<Account> acc = accountRepository.findByAccountId(customerId);
        try {
            System.err.println("🔹 Bắt đầu xóa dữ liệu cho customer: " + customerId);
            System.err.println("1️⃣ Xóa ReviewImage...");
            customerRepository.deleteReviewImageByCustomer(customerId);
            System.err.println("2️⃣ Xóa Review...");
            customerRepository.deleteReviewByCustomer(customerId);
            System.err.println("3️⃣ Xóa Favorite...");
            customerRepository.deleteFavouriteByCustomer(customerId);
            System.err.println("4️⃣ Xóa Cart...");
            customerRepository.deleteCartByCustomer(customerId);
            System.err.println("5️⃣ Xóa UsedVoucher...");
            List<Order> orders = orderRepository.getOrdersByCusID(customerId);
            for (Order o : orders) {
                System.err.println("   - Order ID: " + o.getOrderID());
                o.setCustomer(null);
                orderRepository.save(o);
            }
            orderRepository.flush(); //ép xuống db
            customerRepository.deleteUsedVoucherByCustomer(customerId);
            System.err.println("6️⃣ Xóa Customer...");
            customerRepository.deleteCustomerByCustomer(customerId);
            System.err.println("6️⃣.1 Xóa Account...");
            customerRepository.deleteAccountByCustomer(customerId);
            System.err.println("7️⃣ Lấy danh sách Order...");
            System.err.println("✅ Hoàn tất xóa dữ liệu cho customer: " + customerId);
            mailService.sendDeleteAccountEmail(acc.get().getEmail());
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi xóa dữ liệu cho customer: " + customerId);
            e.printStackTrace();
            throw e; // Ném lại lỗi để rollback transaction
        }
        return true;
    }

    @Override
    public Map<String, Object> findAllAddresses() {
        List<String> addresses = customerRepository.findAllAddresses();

        // Đếm số lượng theo tỉnh
        Map<String, Long> grouped = new HashMap<>();
        for (String addr : addresses) {
            String province = "Không xác định";
            if(addr.equals("N/A")){
                addr = province;
            }
            if (addr != null && !addr.isBlank()) {
                String[] parts = addr.split(",");
                province = parts[parts.length - 1].trim();
            }

            // Tăng số lượng cho tỉnh này
            if (grouped.containsKey(province)) {
                grouped.put(province, grouped.get(province) + 1);
            } else {
                grouped.put(province, 1L);
            }
        }

        // Chuyển Map thành List để sắp xếp
        List<Map.Entry<String, Long>> sortedList = new ArrayList<>(grouped.entrySet());
        Collections.sort(sortedList, new Comparator<Map.Entry<String, Long>>() {
            @Override
            public int compare(Map.Entry<String, Long> o1, Map.Entry<String, Long> o2) {
                return o2.getValue().compareTo(o1.getValue()); // giảm dần
            }
        });

        // Tách thành labels và data
        List<String> labels = new ArrayList<>();
        List<Long> data = new ArrayList<>();
        for (Map.Entry<String, Long> entry : sortedList) {
            labels.add(entry.getKey());
            data.add(entry.getValue());
        }
        // In kết quả ra console
        System.err.println("===== THỐNG KÊ KHÁCH HÀNG THEO TỈNH =====");
        for (int i = 0; i < labels.size(); i++) {
            System.err.println(labels.get(i) + " : " + data.get(i));
        }
        System.err.println("========================================");
        // Trả về kết quả
        Map<String, Object> response = new HashMap<>();
        response.put("labels", labels);
        response.put("data", data);
        return response;
    }

}


