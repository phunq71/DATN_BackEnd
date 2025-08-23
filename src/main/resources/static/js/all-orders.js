    document.addEventListener("DOMContentLoaded",async function() {
   orders= await getOrders("ALL");
    initOrder(orders);


    allTag.addEventListener('click', async function(){
        orders= await getOrders("ALL");
        initOrder(orders)
    });

    choXacNhan.addEventListener('click', async function(){
        orders= await getOrders("ChoXacNhan");
        initOrder(orders)
    });

    choLayHang.addEventListener('click', async function(){
        orders= await getOrders("ChoLayHang");
        initOrder(orders)
    });

    choGiaoHang.addEventListener('click', async function(){
        orders= await getOrders("ChoGiaoHang");
        initOrder(orders)
    });

    daGiao.addEventListener('click', async function(){
        orders= await getOrders("DaGiao");
        initOrder(orders)
    });

    daHuy.addEventListener('click', async function(){
        orders= await getOrders("DaHuy");
        initOrder(orders)
    });

    yeuCauHuy.addEventListener('click', async function(){
        orders= await getOrders("YeuCauHuy");
        initOrder(orders)
    });

    traHang.addEventListener('click',  async function (){
        const returnRequest= await getReturnRequest();
        console.log(returnRequest);
        initReturnRequest(returnRequest);
    });
});

const allTag= document.getElementById("ALL");
const choXacNhan = document.getElementById("cho-xac-nhan");
const choLayHang = document.getElementById("cho-lay-hang");
const choGiaoHang= document.getElementById("cho-giao-hang");
const daGiao = document.getElementById("da-giao");
const daHuy = document.getElementById("da-huy");
const traHang = document.getElementById("tra-hang");
const yeuCauHuy = document.getElementById("yeu-cau-huy");

const yearSelect = document.getElementById("year-select");

yearSelect.addEventListener('change', function(){
    triggerClickOnActiveTab();
});

const searchBtn =document.getElementById("search-order-btn");
searchBtn.addEventListener('click',async function () {
   const keyword= document.getElementById("search-order-input").value;
   if(keyword.trim() !==""){
       orders = await getOrdersByKeyword(keyword);
       if(orders.length<1){
           Swal.fire({
               icon: 'info',
               title: 'Thông báo',
               text: 'Không tìm thấy đơn hàng với từ khóa của bạn',
               confirmButtonText: 'OK',
               confirmButtonColor: '#000000',
           });

       }
       initOrder(orders);
   }
   else{
       Swal.fire({
           icon: 'error',
           title: 'Thông báo',
           text: 'Bạn vui lòng nhập id đơn hàng hoặc tên sản phẩm để tìm kiếm',
           confirmButtonText: 'OK',
           confirmButtonColor: '#000000',
       });
   }
});

function triggerClickOnActiveTab() {
    const activeTab = document.querySelector('.od-tab.active');
    if (activeTab) {
        activeTab.click();
    } else {
        console.log('Không tìm thấy tab nào đang active.');
    }
}

let orders=[];

function getOrders(status){
    let timerInterval;
    Swal.fire({
        title: 'Đang lấy dữ liệu',
        html: 'Vui lòng chờ trong giây lát...',
        timerProgressBar: true,
        allowOutsideClick: false,
        allowEscapeKey: false,
        allowEnterKey: false,
        didOpen: () => {
            Swal.showLoading();
        },
        willClose: () => {
            clearInterval(timerInterval);
        }
    });
    let year = yearSelect.value;
    return axios.get(`/opulentia_user/orders/allOrders/${status}/${year}`)
        .then(response =>{
            Swal.close();
            console.log('👈👈👈👈👈👈👈👈');
            console.log(status, response.data);
            return response.data
        }).catch(error => {
            Swal.close();
            console.log(error);
            return [];
        });
}

function getReturnRequest(){
    let year = yearSelect.value;
    return axios.get("/opulentia_user/returnRequest/get/"+year)
        .then(response => {
            return response.data
        })
        .catch(error => {
            console.log(error);
            return [];
        })
}

function getOrdersByKeyword(keyword){

    return axios.get(`/opulentia_user/orders/findByKeyword/${extractKeyword(keyword)}`)
        .then(response =>{
            console.log(keyword, response.data);
            return response.data
        }).catch(error => {
            console.log(error);
            return [];
        });
}

    function extractKeyword(input) {
        if (typeof input !== 'string') return input;

        // Xóa khoảng trắng
        let trimmed = input.trim();

        // Bỏ ký tự '#' nếu có
        trimmed = trimmed.replace(/^#/, '');

        // Bỏ tiền tố "OD" (không phân biệt hoa thường)
        trimmed = trimmed.replace(/^OD/i, '');

        // Bỏ số 0 ở đầu
        trimmed = trimmed.replace(/^0+/, '');

        // Nếu sau khi xử lý vẫn trống thì trả về null
        console.log('👉👉'+trimmed)
        return trimmed || null;
    }


    function initOrder(orders) {
    const orderListContainer = document.getElementById('order-list-container');
    if (!orderListContainer) return;

    orderListContainer.innerHTML = '';

    // ✅ Nếu không có đơn thì hiển thị thông báo
    if (!orders || orders.length === 0) {
        const emptyMsg = document.createElement('p');
        emptyMsg.textContent = 'Không có đơn nào!';
        emptyMsg.className = 'od-empty-msg';
        orderListContainer.appendChild(emptyMsg);
        return;
    }// Clear existing content

    orders.forEach(order => {
        // Format order ID and date
        const orderIdText = `#OD${order.orderID.toString().padStart(6, '0')}`;
        const orderDate = new Date(order.orderDate);
        const formattedDate = `${orderDate.getDate()}/${orderDate.getMonth() + 1}/${orderDate.getFullYear()}`;

        // Create order card
        const orderCard = document.createElement('div');
        orderCard.className = 'od-order-card';

        // Create header
        const header = document.createElement('div');
        header.className = 'od-order-header';

        const orderIdSpan = document.createElement('span');
        orderIdSpan.className = 'od-order-id';
        orderIdSpan.textContent = orderIdText;

        const orderDateSpan = document.createElement('span');
        orderDateSpan.className = 'od-order-date';
        orderDateSpan.textContent = `Ngày đặt: ${formattedDate}`;

        const statusSpan = document.createElement('span');
        statusSpan.className = `od-order-status`;
        statusSpan.textContent = order.statusName;

        header.appendChild(orderIdSpan);
        header.appendChild(orderDateSpan);
        header.appendChild(statusSpan);

        // Create first product item (visible by default)
        const firstItem = order.items[0];
        const firstProductItem = createProductItem(firstItem);

        // Create expand button if there are more items
        let expandSection = '';
        if (order.items.length > 1) {
            expandSection = `
                <div class="od-expand-products">
                    <button class="od-expand-btn" onclick="openModal(${order.orderID})" data-order-id="${order.orderID}">▼ Xem thêm ${order.items.length - 1} sản phẩm khác</button>
                </div>
            `;
        }

        // Create summary
        const summary = document.createElement('div');
        summary.className = 'od-order-summary';

        const totalLabel = document.createElement('span');
        totalLabel.className = 'od-total-label';
        totalLabel.textContent = `Tổng tiền (${order.items.reduce((sum, item) => {
            return sum + (item.quantity || 0);}, 0)} sản phẩm):`;

        const totalAmount = document.createElement('span');
        totalAmount.className = 'od-total-amount';
        totalAmount.textContent = `₫${order.totalPrice.toLocaleString('vi-VN')}`;

        summary.appendChild(totalLabel);
        summary.appendChild(totalAmount);

        // Create actions
        const actions = document.createElement('div');
        actions.className = 'od-order-actions';

        const trackButton = document.createElement('button');
        trackButton.className = 'od-btn od-btn-primary';
        trackButton.textContent = 'Theo dõi đơn';
        trackButton.addEventListener('click', function (){
            window.location.href="/opulentia_user/orderDetail/"+ order.orderID;
        });

        addActionButtons(actions, order.status, order.orderID);

        actions.appendChild(trackButton);

        // Assemble the card
        orderCard.appendChild(header);
        orderCard.appendChild(firstProductItem);
        if (expandSection) orderCard.innerHTML += expandSection;
        orderCard.appendChild(summary);
        orderCard.appendChild(actions);

        orderListContainer.appendChild(orderCard);
    });
}

function createProductItem(item) {
    const productItem = document.createElement('div');
    productItem.className = 'od-product-item';

    const image = document.createElement('img');
    image.src = item.image ? "https://phudatn.blob.core.windows.net/images/" + item.image : 'https://via.placeholder.com/100';
    image.alt = item.productName;
    image.className = 'od-product-image';

    const info = document.createElement('div');
    info.className = 'od-product-info';

    const name = document.createElement('h3');
    name.className = 'od-product-name';
    name.textContent = item.productName;

    const variants = document.createElement('div');
    variants.className = 'od-product-variants';

    const colorVariant = document.createElement('span');
    colorVariant.className = 'od-variant';
    colorVariant.textContent = `Màu: ${item.color}`;

    const sizeVariant = document.createElement('span');
    sizeVariant.className = 'od-variant';
    sizeVariant.textContent = `Size: ${item.size}`;

    variants.appendChild(colorVariant);
    variants.appendChild(sizeVariant);

    const prices = document.createElement('div');
    prices.className = 'od-product-prices';

    const originalPrice = item.discountPercent > 0 ?
        `<span class="od-original-price">₫${item.price.toLocaleString('vi-VN')}</span>` : '';

    const salePrice = item.discountPercent > 0 ?
        Math.round(item.price * (100 - item.discountPercent) / 100) :
        item.price;

    prices.innerHTML = `
        ${originalPrice}
        <span class="od-sale-price">₫${salePrice.toLocaleString('vi-VN')}</span>
        <span class="od-quantity">x${item.quantity}</span>
    `;

    info.appendChild(name);
    info.appendChild(variants);
    info.appendChild(prices);

    productItem.appendChild(image);
    productItem.appendChild(info);

    return productItem;
}

// Updated modal functions
const modal = document.getElementById('odProductsModal');
const modalClose = document.querySelector('.od-modal-close');
const modalTitle = document.querySelector('.od-modal-title');
const modalProducts = document.querySelector('.od-modal-products');


function openModal(orderID) {
    // Find the order with the matching ID
    const order = orders.find(o => o.orderID === orderID);
    if (!order) return;

    // Update modal title
    modalTitle.textContent = `Tất cả sản phẩm trong đơn #OD${orderID.toString().padStart(6, '0')}`;

    // Clear existing products
    modalProducts.innerHTML = '';

    // Add all products from the order
    order.items.forEach(item => {
        modalProducts.appendChild(createProductItem(item));
    });

    // Show modal
    modal.style.display = 'flex';
}

// Close modal when click close button
modalClose.addEventListener('click', () => {
    modal.style.display = 'none';
});

// Close modal when click outside
window.addEventListener('click', (e) => {
    if (e.target === modal) {
        modal.style.display = 'none';
    }
});

function formatPrice(price) {
    return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(price);
}

function addActionButtons(actionElement, status, orderId){
    if(status=== "DaYeuCauHuy" || status === "ChoGiaoHang") return;

    const btn = document.createElement('button');
    btn.className="od-btn od-btn-outline";
    if(status==="ChoXacNhan"){
        btn.textContent="Hủy đơn hàng";
        attachCancelOrderHandler(orderId, btn);
    }else if(status==="ChuanBiDon" || status==="SanSangGiao"){
        btn.textContent="Gửi yêu cầu hủy đơn";
        btn.addEventListener("click", () => {
            handleCancelOrder(orderId, btn);
        });
    }else if(status === "DaGiao" || status === "DaHuy" || status === "YeuCauHuy"){
        btn.textContent="Mua lại";
    }
    actionElement.appendChild(btn);
}



function initReturnRequest(returnRequests) {
    const orderListContainer = document.getElementById('order-list-container');
    if (!orderListContainer) return;

    orderListContainer.innerHTML = ''; // Clear existing content

    returnRequests.forEach(returnRequest => {
        // Format order ID and date
        const orderIdText = `#RR${returnRequest.returnRequestId.toString().padStart(6, '0')}`;
        const orderDate = new Date(returnRequest.requestDate);
        const formattedDate = `${orderDate.getDate()}/${orderDate.getMonth() + 1}/${orderDate.getFullYear()}`;

        // Create order card
        const orderCard = document.createElement('div');
        orderCard.className = 'od-order-card';

        // Create header
        const header = document.createElement('div');
        header.className = 'od-order-header';

        const orderIdSpan = document.createElement('span');
        orderIdSpan.className = 'od-order-id';
        orderIdSpan.textContent = orderIdText;

        const orderDateSpan = document.createElement('span');
        orderDateSpan.className = 'od-order-date';
        orderDateSpan.textContent = `Ngày yêu cầu: ${formattedDate}`;

        const statusSpan = document.createElement('span');
        statusSpan.className = `od-order-status`;
        statusSpan.textContent = returnRequest.statusName;

        header.appendChild(orderIdSpan);
        header.appendChild(orderDateSpan);
        header.appendChild(statusSpan);

        // Create first product item (visible by default)
        const firstItem = returnRequest.items[0];
        const firstProductItem = createReturnItem(firstItem);

        // Create expand button if there are more items
        // let expandSection = '';
        // if (returnRequest.items.length > 1) {
        //     expandSection = `
        //         <div class="od-expand-products">
        //             <button class="od-expand-btn" onclick="openModal(${order.orderID})" data-order-id="${order.orderID}">▼ Xem thêm ${order.items.length - 1} sản phẩm khác</button>
        //         </div>
        //     `;
        // }

        // Create summary
        const summary = document.createElement('div');
        summary.className = 'od-order-summary';

        const totalLabel = document.createElement('span');
        totalLabel.className = 'od-total-label';
        totalLabel.textContent = `Tổng tiền hoàn (${returnRequest.items.reduce((sum, item) => {
            return sum + (item.quantity || 0);}, 0)} sản phẩm):`;

        const totalAmount = document.createElement('span');
        totalAmount.className = 'od-total-amount';
        totalAmount.textContent = `₫${returnRequest.totalPrice.toLocaleString('vi-VN')}`;

        summary.appendChild(totalLabel);
        summary.appendChild(totalAmount);

        // Create actions
        const actions = document.createElement('div');
        actions.className = 'od-order-actions';

        const trackButton = document.createElement('button');
        trackButton.className = 'od-btn od-btn-primary';
        trackButton.textContent = 'Xem chi tiết';
        trackButton.addEventListener('click', function (){
            window.location.href="/opulentia_user/returnRequestDetail/"+ returnRequest.returnRequestId;
        });

        actions.appendChild(trackButton);

        // Assemble the card
        orderCard.appendChild(header);
        orderCard.appendChild(firstProductItem);
       // if (expandSection) orderCard.innerHTML += expandSection;
        orderCard.appendChild(summary);
        orderCard.appendChild(actions);

        orderListContainer.appendChild(orderCard);
    });
}

function createReturnItem(item) {
    const productItem = document.createElement('div');
    productItem.className = 'od-product-item';

    const image = document.createElement('img');
    image.src = "https://phudatn.blob.core.windows.net/images/"+ item.image || 'https://via.placeholder.com/100';
    image.alt = item.productName;
    image.className = 'od-product-image';

    const info = document.createElement('div');
    info.className = 'od-product-info';

    const name = document.createElement('h3');
    name.className = 'od-product-name';
    name.textContent = item.productName;

    const variants = document.createElement('div');
    variants.className = 'od-product-variants';

    const colorVariant = document.createElement('span');
    colorVariant.className = 'od-variant';
    colorVariant.textContent = `Màu: ${item.color}`;

    const sizeVariant = document.createElement('span');
    sizeVariant.className = 'od-variant';
    sizeVariant.textContent = `Size: ${item.size}`;

    variants.appendChild(colorVariant);
    variants.appendChild(sizeVariant);

    const prices = document.createElement('div');
    prices.className = 'od-product-prices';

    const originalPrice = item.discountPercent > 0 ?
        `<span class="od-original-price">₫${item.price.toLocaleString('vi-VN')}</span>` : '';

    const salePrice = item.discountPercent > 0 ?
        Math.round(item.price * (100 - item.discountPercent) / 100) :
        item.price;

    prices.innerHTML = `
        ${originalPrice}
        <span class="od-sale-price">₫${salePrice.toLocaleString('vi-VN')}</span>
        <span class="od-quantity">x${item.quantity}</span>
    `;

    info.appendChild(name);
    info.appendChild(variants);
    info.appendChild(prices);

    productItem.appendChild(image);
    productItem.appendChild(info);

    return productItem;
}

// Hủy đơn hàng
    function attachCancelOrderHandler(orderId, btn) {
        if (!btn) return;

        btn.addEventListener("click", async () => {
            const result = await Swal.fire({
                title: "Xác nhận hủy đơn?",
                text: "Bạn có chắc muốn hủy đơn hàng này không?",
                icon: "warning",
                showCancelButton: true,
                confirmButtonText: "Hủy đơn",
                cancelButtonText: "Thoát",
                reverseButtons: true
            });

            if (!result.isConfirmed) return;

            try {
                const response = await fetch(`/api/orders/${orderId}/cancel`, {
                    method: "PUT"
                });

                if (response.ok) {
                    await Swal.fire({
                        title: "Thành công!",
                        text: "Hủy đơn hàng thành công!",
                        icon: "success",
                        confirmButtonText: "OK"
                    });
                    // Render lại danh sách đơn
                    orders= await getOrders("DaHuy");
                    initOrder(orders)
                    // Sau khi render xong, chuyển tab
                    document.getElementById("cho-xac-nhan").classList.remove("active");
                    document.getElementById("da-huy").classList.add("active");
                    btn.textContent = "Đã hủy";
                    btn.disabled = true;
                } else {
                    const errorText = await response.text();
                    Swal.fire({
                        title: "Thất bại!",
                        text: "Không thể hủy đơn: " + errorText,
                        icon: "error",
                        confirmButtonText: "OK"
                    });
                }
            } catch (err) {
                console.error("Lỗi khi hủy đơn:", err);
                Swal.fire({
                    title: "Lỗi!",
                    text: "Có lỗi xảy ra khi hủy đơn hàng.",
                    icon: "error",
                    confirmButtonText: "OK"
                });
            }
        });
    }

    // Hàm hiển thị dialog chọn lý do
    function showCancelReasonDialog(callback) {
        Swal.fire({
            title: 'Chọn lý do hủy đơn',
            input: 'select',
            inputOptions: {
                'Tôi đổi ý': 'Tôi đổi ý',
                'Đặt nhầm sản phẩm': 'Đặt nhầm sản phẩm',
                'Muốn thay đổi địa chỉ': 'Muốn thay đổi địa chỉ',
                'Khác': 'Khác'
            },
            inputPlaceholder: 'Chọn lý do',
            showCancelButton: true,
            confirmButtonText: 'Xác nhận',
            cancelButtonText: 'Hủy'
        }).then((result) => {
            if (result.isConfirmed && result.value) {
                callback(result.value);
            }
        });
    }

    function handleCancelOrder(orderId, btn) {
        if (!btn) return;

        showCancelReasonDialog(async function(reason) {
            console.log("Lý do đã chọn:", reason);

            try {
                const response = await fetch(`/api/orders/${orderId}/requestCancel`, {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify({ reason: reason })
                });

                if (response.ok) {
                    // Hiển thị alert và sau đó update UI
                    await Swal.fire({
                        title: "Thành công",
                        text: "Đơn đã được gửi yêu cầu hủy!",
                        icon: "success",
                        confirmButtonText: "OK"
                    });

                    // Render lại danh sách đơn

                    orders= await getOrders("YeuCauHuy");
                    initOrder(orders)

                    // Sau khi render xong, chuyển tab
                    document.getElementById("cho-lay-hang").classList.remove("active");
                    document.getElementById("yeu-cau-huy").classList.add("active");

                    // Cập nhật nút
                    btn.textContent = "Yêu cầu hủy";
                    btn.disabled = true;
                } else {
                    const errorText = await response.text();
                    Swal.fire({
                        title: "Thất bại!",
                        text: "Không thể hủy đơn: " + errorText,
                        icon: "error",
                        confirmButtonText: "OK"
                    });
                }
            } catch (err) {
                console.error("Lỗi khi hủy đơn:", err);
                Swal.fire({
                    title: "Lỗi!",
                    text: "Có lỗi xảy ra khi hủy đơn hàng.",
                    icon: "error",
                    confirmButtonText: "OK"
                });
            }
        });
    }





