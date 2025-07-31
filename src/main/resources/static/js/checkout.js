
// ====================== MẶC ĐỊNH =======================
window.addEventListener('DOMContentLoaded', () => {

});

// ======================= MODAL =======================

function openModal(id) {
    const modal = document.getElementById(id);
    if (modal) {
        modal.classList.add('active');

        if (id === 'address-modal') {
            setTimeout(prefillAddressModal, 100);
        }
    }
}
function closeModal(id) {
    const modal = document.getElementById(id);
    if (modal) {
        modal.classList.remove('active');
    }
}

window.openModal = openModal;
window.closeModal = closeModal;

// ======================= SỰ KIỆN ĐÓNG MODAL =======================

document.querySelectorAll('.modal-overlay').forEach(overlay => {
    overlay.addEventListener('click', function (e) {
        if (e.target === overlay) {
            overlay.classList.remove('active');
        }
    });
});

document.querySelectorAll('.close-btn').forEach(btn => {
    btn.addEventListener('click', () => {
        const modal = btn.closest('.modal-overlay');
        if (modal) modal.classList.remove('active');
    });
});

// ======================= DỮ LIỆU CHÍNH =======================

const token = '5b993278-3a63-11f0-9b81-222185cb68c8';
const shopId = 196674;

let checkoutInfo = {
    customer: {
        customerId: '',
        customerName: '',
        customerAddress: '',
        customerPhone: '',
        customerAddressIdGHN: '',
        note: '',
        costShip: 0,
        discountCost: 0
    },
    facilities: [],
    facilityId: '',
    lastTime: '',
    paymentMethod: 'sepay',
    voucherId:'',
    type: null,
    discountTotal: 0,
    hauMai1: null,
    hauMai2: null
};

// ======================= FETCH CHECKOUT =======================

async function fetchDataCheckout() {
    const parsedItems = JSON.parse(listItems);

    try {
        const response = await axios.post('/opulentia_user/datacheckout', parsedItems, {
            withCredentials: true,
            headers: {
                'Content-Type': 'application/json'
            }
        });

        const data = response.data;
        const info = data?.customer;
        console.log(data);

        if (!info?.customerPhone || info.customerPhone.trim() === '' || info.customerPhone.trim() === 'N/A') {
            Swal.fire({
                icon: 'warning',
                title: 'Thiếu thông tin',
                text: 'Vui lòng thêm số điện thoại để mua hàng.',
                confirmButtonText: 'OK'
            });
            window.location.href = '/opulentia_user/edit-profile';
            return;
        }

        checkoutInfo.customer = info;
        checkoutInfo.facilities = data?.facilities;

        const isValidFacilities = Array.isArray(checkoutInfo.facilities) && checkoutInfo.facilities.length > 0;

        if (!isValidFacilities) {
            Swal.fire({
                icon: 'info',
                title: 'Không tìm thấy cơ sở phù hợp',
                text: 'Hiện tại không có cơ sở nào có thể đáp ứng tất cả sản phẩm bạn đã chọn. Bạn sẽ được chuyển về giỏ hàng để điều chỉnh.',
                confirmButtonText: 'Quay về giỏ hàng',
                allowOutsideClick: false,
                allowEscapeKey: false
            }).then(() => {
                window.location.href = '/opulentia/cart';
            });
        }


        await fetchLeadtimes();
        renderCheckoutInfo();
        await calculateShippingFee();
        await fetchSuggestedVouchers();
    } catch (error) {
        console.error('Lỗi khi gọi API:', error);
    }
}

fetchDataCheckout();

// ======================= RENDER =======================

function renderCheckoutInfo() {
    const info = checkoutInfo.customer;

    document.querySelectorAll('.info-row .info-value')[0].textContent = info.customerName;
    document.querySelectorAll('.info-row .info-value')[1].textContent = formatPhone(info.customerPhone);

    const addressElement = document.querySelectorAll('.info-row .info-value')[2];
    if (!info?.customerAddress || info.customerAddress.trim() === '' || info.customerAddress === 'N/A') {
        addressElement.innerHTML = '&nbsp;&nbsp;Vui lòng thêm địa chỉ';
        addressElement.style.color = 'red';
    } else {
        addressElement.textContent = info.customerAddress;
        addressElement.style.color = '';
    }

    let note = info.note || '';
    if (note.length > 35) {
        note = note.slice(0, 35) + '...';
    }

    document.getElementById('noteContent').textContent = note;
    calculateShippingFee();

    if (checkoutInfo.lastTime) {
        const fromDate = new Date(checkoutInfo.lastTime);
        const toDate = new Date(fromDate);
        toDate.setDate(toDate.getDate() + 1);

        document.querySelector('.c-shipping-estimate').textContent =
            `Dự kiến giao hàng từ ngày ${formatDate(fromDate)} đến ${formatDate(toDate)}`;
    } else {
        document.querySelector('.c-shipping-estimate').textContent = `Dự kiến giao hàng: N/A`;
    }


}

function formatPhone(phone) {
    return phone.replace(/(\d{3})(\d{3})(\d{4})/, '$1 $2 $3');
}

function formatDate(dateStr) {
    const date = new Date(dateStr);
    const day = String(date.getDate()).padStart(2, '0');
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const year = date.getFullYear();
    return `${day}/${month}/${year}`;
}

// ======================= NOTE =======================

function submitNote(event) {
    event.preventDefault();
    const noteValue = document.getElementById('note-input').value.trim();
    checkoutInfo.customer.note = noteValue;
    closeModal('note-modal');
    renderCheckoutInfo();
}
window.submitNote = submitNote;

// ======================= ĐỊA CHỈ =======================

let provincesData = [];

fetch('/data/Address.json')
    .then(res => res.json())
    .then(data => {
        provincesData = data;
        loadCities();
    });

function loadCities() {
    const citySelect = document.getElementById('city');
    provincesData.forEach(province => {
        const option = document.createElement('option');
        option.value = province.ProvinceID;
        option.textContent = province.ProvinceName;
        citySelect.appendChild(option);
    });
}

document.getElementById('city').addEventListener('change', function () {
    loadDistricts(parseInt(this.value));
});

document.getElementById('district').addEventListener('change', function () {
    const cityId = parseInt(document.getElementById('city').value);
    loadWards(cityId, parseInt(this.value));
});

function loadDistricts(provinceId, selectedDistrictId = null, callback = null) {
    const province = provincesData.find(p => p.ProvinceID === provinceId);
    const districtSelect = document.getElementById('district');
    districtSelect.innerHTML = '<option value="">Chọn quận/huyện</option>';
    document.getElementById('ward').innerHTML = '<option value="">Chọn xã/phường</option>';

    if (province) {
        province.Districts.forEach(d => {
            const option = document.createElement('option');
            option.value = d.DistrictID;
            option.textContent = d.DistrictName;
            districtSelect.appendChild(option);
        });

        if (selectedDistrictId) districtSelect.value = selectedDistrictId;
        if (callback) callback();
    }
}

function loadWards(provinceId, districtId, selectedWardCode = null) {
    const province = provincesData.find(p => p.ProvinceID === provinceId);
    const district = province?.Districts.find(d => d.DistrictID === districtId);
    const wardSelect = document.getElementById('ward');
    wardSelect.innerHTML = '<option value="">Chọn xã/phường</option>';

    if (district) {
        district.Wards.forEach(w => {
            const option = document.createElement('option');
            option.value = w.WardCode;
            option.textContent = w.WardName;
            wardSelect.appendChild(option);
        });

        if (selectedWardCode) wardSelect.value = selectedWardCode;
    }
}

function prefillAddressModal() {
    const ghn = checkoutInfo.customer.customerAddressIdGHN;
    if (!ghn) return;

    const [provinceId, districtId, wardCode, ...rest] = ghn.split('/');
    const detail = rest.join('/').trim();

    document.getElementById('city').value = provinceId;
    loadDistricts(parseInt(provinceId), parseInt(districtId), () => {
        loadWards(parseInt(provinceId), parseInt(districtId), wardCode);
    });

    document.getElementById('address').value = detail;
}

document.querySelector('.submit-address-open')?.addEventListener('click', () => {
    openModal('address-modal');
});

document.querySelector('.submit-btn').addEventListener('click', async () => {
    const cityEl = document.getElementById('city');
    const districtEl = document.getElementById('district');
    const wardEl = document.getElementById('ward');
    const detailEl = document.getElementById('address');
    const errorDiv = document.getElementById('address-error-msg');
    const isDefault = document.getElementById('default-address').checked;

    const city = cityEl.value;
    const district = districtEl.value;
    const ward = wardEl.value;
    const detail = detailEl.value.trim();

    const isValid = city && district && ward && detail;
    const fields = [cityEl, districtEl, wardEl, detailEl];

    if (!isValid) {
        errorDiv.textContent = "Vui lòng chọn đầy đủ Tỉnh/Thành phố, Quận/Huyện, Xã/Phường và nhập địa chỉ chi tiết.";
        errorDiv.style.display = 'block';
        fields.forEach(el => {
            if (!el.value || el.value.trim() === '') {
                el.classList.add('input-error');
            } else {
                el.classList.remove('input-error');
            }
        });
        return;
    }

    errorDiv.textContent = '';
    errorDiv.style.display = 'none';
    fields.forEach(el => el.classList.remove('input-error'));

    const addressIdGHN = `${city}/${district}/${ward}/${detail}`;
    const addressText = `${detail}, ${wardEl.selectedOptions[0].text}, ${districtEl.selectedOptions[0].text}, ${cityEl.selectedOptions[0].text}`;
    const savedNote = document.getElementById('note-input').value.trim();

    if (isDefault) {
        try {
            await axios.post('/opulentia_user/update/address', {
                customerAddressIdGHN: addressIdGHN,
                customerAddress: addressText
            });

            Swal.fire({
                icon: 'success',
                title: 'Cập nhật địa chỉ thành công!',
                showConfirmButton: false,
                timer: 1500
            });

            await fetchDataCheckout();
            checkoutInfo.customer.note = savedNote;
            closeModal('address-modal');
            renderCheckoutInfo();
        } catch (error) {
            alert("❌ Lỗi khi cập nhật địa chỉ");
            console.error(error);
        }
    } else {
        checkoutInfo.customer.customerAddressIdGHN = addressIdGHN;
        checkoutInfo.customer.customerAddress = addressText;
        checkoutInfo.customer.note = savedNote;
        await fetchSuggestedVouchers();
        renderCheckoutInfo();
        fetchLeadtimes();
        closeModal('address-modal');
    }
});


function parseGHNAddressId(id) {
    const parts = id.split('/');
    return {
        district_id: parseInt(parts[1]),
        ward_code: parts[2]
    };
}

async function fetchLeadtimes() {
    const addressIdGHN = checkoutInfo.customer.customerAddressIdGHN;
    if (!addressIdGHN || addressIdGHN.trim() === '') return;

    const { district_id: toDistrictId, ward_code: toWardCode } = parseGHNAddressId(addressIdGHN);

    for (let facility of checkoutInfo.facilities) {
        const { district_id: fromDistrictId, ward_code: fromWardCode } = parseGHNAddressId(facility.addressIdGHN);

        try {
            const res = await fetch('https://dev-online-gateway.ghn.vn/shiip/public-api/v2/shipping-order/leadtime', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'ShopId': shopId.toString(),
                    'Token': token
                },
                body: JSON.stringify({
                    from_district_id: fromDistrictId,
                    from_ward_code: fromWardCode,
                    to_district_id: toDistrictId,
                    to_ward_code: toWardCode,
                    service_id
                })
            });

            const data = await res.json();
            facility.leadtime = data.data?.leadtime_order?.from_estimate_date
                ? new Date(data.data.leadtime_order.from_estimate_date).toISOString()
                : null;


        } catch (err) {
            console.error("❌ Lỗi GHN:", err);
            facility.leadtime = null;
        }
    }

    checkoutInfo.facilities.sort((a, b) => {
        const leadA = new Date(a.leadtime).getTime();
        const leadB = new Date(b.leadtime).getTime();
        return leadA !== leadB ? leadA - leadB : a.totalOrders - b.totalOrders;
    });

    checkoutInfo.facilityId = checkoutInfo.facilities[0]?.id || null;

    if (checkoutInfo.facilities[0]?.leadtime) {
        const date = new Date(checkoutInfo.facilities[0].leadtime);
        date.setDate(date.getDate() + 1);
        checkoutInfo.lastTime = date.toISOString();
    } else {
        checkoutInfo.lastTime = null;
    }

}

// ======================= PAYMENT =======================
function handlePaymentChange() {
    const selectEl = document.getElementById('payment-method');
    const selectedValue = selectEl.value;

    if (selectedValue === 'cod' && totalAmount > 5000000) {
        Swal.fire({
            icon: 'warning',
            title: 'Không hỗ trợ COD',
            text: 'Đơn hàng trên 5.000.000₫ không được thanh toán khi nhận hàng (COD).',
            confirmButtonText: 'OK'
        });
        // Reset lại phương thức thanh toán
        selectEl.value = 'sepay';
        checkoutInfo.paymentMethod = 'sepay';
    } else {
        checkoutInfo.paymentMethod = selectedValue;
    }

    console.log("Phương thức thanh toán đã thay đổi:", checkoutInfo.paymentMethod);
    calculateShippingFee();
}

window.handlePaymentChange = handlePaymentChange;


// 👉 Biến toàn cục
let service_id = null;
let service_type_id = null;

// 👉 Hàm tách riêng để lấy danh sách dịch vụ GHN
async function getGHNService(from_district_id, to_district_id) {
    try {
        const res = await axios.post(
            'https://dev-online-gateway.ghn.vn/shiip/public-api/v2/shipping-order/available-services',
            {
                shop_id: shopId,
                from_district: from_district_id,
                to_district: to_district_id
            },
            {
                headers: {
                    'Content-Type': 'application/json',
                    'Token': token
                }
            }
        );

        const services = res.data?.data;
        console.log("📋 Danh sách dịch vụ GHN:", services);

        if (!services || services.length === 0) {
            console.error("❌ Không tìm thấy dịch vụ vận chuyển phù hợp.");
            return null;
        }

        // Gán vào biến toàn cục
        service_id = services[0].service_id;
        service_type_id = services[0].service_type_id;

        return services[0]; // hoặc trả về cả danh sách nếu muốn
    } catch (err) {
        console.error("❌ Lỗi khi lấy dịch vụ GHN:", err.response?.data || err.message);
        return null;
    }
}

async function calculateShippingFee() {
    const addressIdGHN = checkoutInfo.customer.customerAddressIdGHN;
    const facility = checkoutInfo.facilities.find(f => f.id === checkoutInfo.facilityId);

    if (!addressIdGHN || !facility) {
        console.warn("⚠️ Thiếu thông tin địa chỉ hoặc cơ sở để tính phí.");
        return;
    }

    const { district_id: from_district_id, ward_code: from_ward_code } = parseGHNAddressId(facility.addressIdGHN);
    const { district_id: to_district_id, ward_code: to_ward_code } = parseGHNAddressId(addressIdGHN);

    const parsedItems = JSON.parse(listItems); // lấy danh sách sản phẩm
    const items = parsedItems.map(item => ({
        name: `${item.item_name} x${item.quantity}`,
        quantity: item.quantity,
        height: 50,
        weight: 200,
        length: 20,
        width: 20
    }));

    const isCOD = checkoutInfo.paymentMethod === 'cod';
    const flag = totalAmount < 700000;
    // 🔹 Gọi hàm lấy dịch vụ GHN
    const service = await getGHNService(from_district_id, to_district_id);
    if (!service) return;
    console.log(totalAmount)
    const bodyData = {
        shop_id: shopId,
        from_district_id,
        from_ward_code,
        to_district_id,
        to_ward_code,
        service_id,
        service_type_id,
        height: 50,
        length: 20,
        weight: 200,
        width: 20,
        insurance_value: ( totalAmount - checkoutInfo.discountTotal) >5000000? 5000000 : totalAmount,
        cod_failed_amount: 2000,
        coupon: null,
        items,
        payment_type_id: 1,
        cod_amount: isCOD ? totalAmount - checkoutInfo.discountTotal + checkoutInfo.customer.discountCost : 0
    };

    console.log("📦 Dữ liệu gửi tính phí:", JSON.stringify(bodyData, null, 2));

    try {
        const response = await axios.post(
            'https://dev-online-gateway.ghn.vn/shiip/public-api/v2/shipping-order/fee',
            bodyData,
            {
                headers: {
                    'Content-Type': 'application/json',
                    'Token': token,
                    'ShopId': shopId.toString()
                }
            }
        );

        const shippingFee = response.data?.data?.total || 0;
        checkoutInfo.customer.costShip = shippingFee;

        if (totalAmount >= 500000 && totalAmount < 700000) {
            checkoutInfo.customer.discountCost = shippingFee * 0.5;
        } else if (totalAmount > 700000) {
            checkoutInfo.customer.discountCost = 0;
        } else {
            checkoutInfo.customer.discountCost = shippingFee;
        }

        const finalShippingFee = checkoutInfo.customer.discountCost;
        checkoutInfo.customer.discountCost = Math.floor(checkoutInfo.customer.discountCost);
        checkoutInfo.customer.costShip = Math.floor(checkoutInfo.customer.costShip);

        console.log("✅ Phí giao hàng:", shippingFee);
        console.log("✅ Phí sau khi giảm:", finalShippingFee);

// Hiển thị HTML
        const shipPriceEl = document.querySelector('.c-ship-price');
        const shipPriceEl1 = document.getElementById('ship');
        const originalShip = `₫${checkoutInfo.customer.costShip.toLocaleString('vi-VN')}`;
        const finalShip = `₫${checkoutInfo.customer.discountCost.toLocaleString('vi-VN')}`;

        if (finalShippingFee < shippingFee) {
            // Có giảm
            shipPriceEl.innerHTML = `<span style="text-decoration: line-through; color: gray; font-size: 0.9em;">${originalShip}</span> <span style="color: red;">${finalShip}</span>`;
            shipPriceEl1.innerHTML = `<span style="text-decoration: line-through; color: gray; font-size: 0.9em;">${originalShip}</span> <span style="color: black;">${finalShip}</span>`;
        } else {
            // Không giảm
            shipPriceEl.textContent = originalShip;
            shipPriceEl1.textContent = originalShip;
        }
        const discount = totalAmount -  checkoutInfo.discountTotal + checkoutInfo.customer.discountCost;
        document.getElementById('tongTien').textContent =
            '₫' + discount.toLocaleString('vi-VN');

    } catch (error) {
        if (error.response) {
            console.error("❌ Lỗi GHN API:", error.response.data);
        } else {
            console.error("❌ Lỗi không xác định:", error.message);
        }
        return null;
    }
}

// ======================= CHỌN VOUCHER =======================
function applySelectedVoucher() {
    const selectedRadio = document.querySelector('.c-voucher-radio:checked');
    if (!selectedRadio) return;

    const selectedVoucherId = selectedRadio.value;
    const selectedVoucher = listIVouchers.find(v => v.voucherID === selectedVoucherId);

    if (selectedVoucher) {
        checkoutInfo.voucherId = selectedVoucher.voucherID;
        checkoutInfo.type = selectedVoucher.type;

        // Áp dụng giảm giá
        if (selectedVoucher.discountType === 'Percent') {
            const percent = selectedVoucher.discountValue;
            checkoutInfo.discountTotal = Math.floor(totalAmount * (percent / 100));
        } else if (selectedVoucher.discountType === 'Amount') {
            checkoutInfo.discountTotal = selectedVoucher.discountValue;
        }

        // Gán nội dung giảm giá ra giao diện
        const voucherTextSpan = document.querySelector('.c-voucher-value');
        if (voucherTextSpan) {
            voucherTextSpan.textContent =
                selectedVoucher.discountType === 'Percent'
                    ? `Giảm ${selectedVoucher.discountValue}%`
                    : `Giảm ${selectedVoucher.discountValue.toLocaleString()}đ`;
        }
        fetchSuggestedVouchers();
        calculateShippingFee();
        closeModal('voucherModal');
        const discount = checkoutInfo.discountTotal || 0;
        document.getElementById('giamGia').textContent =
            '₫' + discount.toLocaleString('vi-VN');


    }
}
window.applySelectedVoucher = applySelectedVoucher;

// ======================= Hàm lấy hậu mãi =======================
async function fetchSuggestedVouchers() {
    try {console.log(totalAmount)
        console.log(checkoutInfo.customer.discountCost)

        const response = await axios.get("/opulentia_user/suggested-vouchers", {
            params: {
                totalAmount: (totalAmount - checkoutInfo.discountTotal) + checkoutInfo.customer.discountCost
            },
            withCredentials: true
        });

        const { hauMai1, hauMai2 } = response.data;

        console.log("✅ Hậu mãi gần đạt:", hauMai1);
        console.log("✅ Hậu mãi đã đạt:", hauMai2);
        hienThiHauMai(hauMai1, hauMai2, checkoutInfo);
        return { hauMai1, hauMai2 };
    } catch (error) {
        console.error("❌ Lỗi lấy hậu mãi:", error);
        return { hauMai1: null, hauMai2: null };
    }
}


function hienThiHauMai(hauMai1, hauMai2, checkoutInfo) {
    const hauMaiEl = document.getElementById("hauMai");
    const hauMaiEl2 = document.getElementById("hauMai2");
    let message = "";
    let message1 = "";

    const currentValue = totalAmount - checkoutInfo.discountTotal + checkoutInfo.customer.discountCost;
    console.log("🎯 Giá trị đơn hàng hiện tại:", currentValue);

    // Nếu có hậu mãi đã đạt
    if (hauMai2) {
        const giam = hauMai2.discountType === "Percent"
            ? `${hauMai2.discountValue}%`
            : `${hauMai2.discountValue.toLocaleString()}₫`;
        message += `🎁 Sau đơn hàng này, bạn sẽ nhận được voucher giảm ${giam}. `;
    }

    // Nếu có hậu mãi gần đạt
    if (hauMai1) {
        const conThieu = hauMai1.claimConditions - currentValue;
        if (conThieu > 0) {
            const giam = hauMai1.discountType === "Percent"
                ? `${hauMai1.discountValue}%`
                : `${hauMai1.discountValue.toLocaleString()}₫`;
            message1 += `🛒 Mua thêm ${conThieu.toLocaleString()}₫ để nhận được voucher giảm ${giam}.`;
        }
    }

    hauMaiEl.textContent = message || "Không có hậu mãi nào phù hợp.";
    hauMaiEl2.textContent = message1 || "Không có hậu mãi nào phù hợp.";
}

//=====================================ĐẶT HÀNG =========================================

function datHang() {
    if (checkoutInfo.paymentMethod === 'sepay') {
        Swal.fire({
            icon: 'info',
            title: 'Thanh toán SePay',
            text: 'Tính năng đang phát triển. Vui lòng chọn phương thức khác!',
            confirmButtonColor: '#3085d6',
            confirmButtonText: 'OK'
        });
        return;
    }

    axios.post('/opulentia_user/order/add', checkoutInfo, {
        withCredentials: true,
    })
        .then(function (response) {
            Swal.fire({
                icon: 'success',
                title: 'Đặt hàng thành công!',
                text: response.data.message || 'Cảm ơn bạn đã mua hàng tại Opulentia.',
                confirmButtonColor: '#3085d6',
                confirmButtonText: 'Đến trang đơn hàng'
            }).then(() => {
                // Chuyển hướng sau khi người dùng bấm OK
                window.location.href = 'http://localhost:8989/opulentia_user/allOrder';
            });

            console.log('Đặt hàng thành công:', response.data);
        })

        .catch(function (error) {
            Swal.fire({
                icon: 'error',
                title: 'Lỗi khi đặt hàng',
                text: error.response?.data?.message || 'Vui lòng thử lại sau!',
                confirmButtonColor: '#d33',
                confirmButtonText: 'Thử lại'
            });
            console.error('Lỗi khi đặt hàng:', error.response?.data || error.message);
        });

}


window.datHang = datHang;
checkoutInfo.totalAmount = totalAmount;
checkoutInfo.listItems = JSON.parse(listItems);

