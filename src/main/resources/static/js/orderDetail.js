document.addEventListener('DOMContentLoaded', async function(){
    const orderId = new URL(window.location.href).pathname.split("/").pop();
    let lastRendered = new Set();
    const order= await getOrder(orderId);

    console.log("orderId: "+orderId, order);

    updateProgressBar(order.status);

    // Fill in basic information
    document.getElementById('shippingName').textContent = order.shippingName;
    document.getElementById('shippingPhone').textContent = order.shippingPhone;
    document.getElementById('shippingAddress').textContent = order.shippingAddress;
    document.getElementById('paymentMethod').textContent = order.paymentMethod;
    document.getElementById('orderDate').textContent = formatDate(order.orderDate);
    document.getElementById('transactionDate').textContent = formatDate(order.transactionDate);
    document.getElementById('orderId').textContent = formatOrderId(order.orderID);
    document.getElementById('orderStatus').textContent= order.statusName;
    const actionDiv = document.getElementById("actions")

    addActionButtons(order.status);

    // Fill in products table
    const productsTable = document.querySelector('.od-products tbody');
    productsTable.innerHTML = ''; // Clear existing rows

    order.items.forEach(item => {
        const discountedPrice = item.price * (100 - item.discountPercent) / 100;
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>
                <div style="display: flex; gap: 15px;">
                    <img src="/uploads/${item.image}" alt="Product" class="od-product-img">
                    <div>
                        <div class="od-product-name">${item.productName}</div>
                        <div class="od-product-attr">Màu: ${item.color} | Size: ${item.size}</div>
                    </div>
                </div>
            </td>
            <td>
                <div class="od-original-price">${formatCurrency(item.price)}</div>
                <div class="od-discounted-price">${formatCurrency(discountedPrice)}</div>
            </td>
            <td>${item.quantity}</td>
            <td class="od-discounted-price">${formatCurrency(discountedPrice * item.quantity)}</td>
        `;
        productsTable.appendChild(row);
    });

    // Fill in summary
    const totalItemsPrice = order.items.reduce((sum, item) => {
        const discountedPrice = item.price * (100 - item.discountPercent) / 100;
        return sum + (discountedPrice * item.quantity);
    }, 0);

    document.getElementById("totalItemsPrice").textContent = formatCurrency(calculateTotalPriceWithoutDiscount(order));
    const shippingCost = order.shippingCost;
    const discountedShipping = order.shippinngCostDiscountPrice;
    const shippingDiscount = shippingCost - discountedShipping;

    document.getElementById("discountCS").textContent = shippingDiscount > 0
        ? "- " + formatCurrency(shippingDiscount)
        : formatCurrency(0);


    document.getElementById("discountVoucherPrice").textContent=order.discountVoucherPrice>0 ? "- "+ formatCurrency(order.discountVoucherPrice) : formatCurrency(0);

    document.getElementById("shippingCost").textContent=formatCurrency(order.shippingCost);

    document.querySelector('.od-summary-total span:last-child').textContent = formatCurrency(order.finalPrice);


    function getOrder(orderId) {
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
        if (isNaN(orderId) || orderId === null || orderId === '' || Number(orderId) <= 0) {
            Swal.fire({
                icon: 'error',
                title: 'Thông báo',
                text: 'Bạn không có quyền mở trang này!',
                confirmButtonText: 'Quay lại trang chủ',
                confirmButtonColor: '#000000',
                allowOutsideClick: false,
                allowEscapeKey: false,
                allowEnterKey: false
            }).then(() => {
                window.location.href="/index";
            });
            return;
        }

        return axios.get(`/opulentia_user/orders/${orderId}`)
            .then(response => {
                Swal.close();
                const data = response.data;

                // Hiển thị tất cả các trạng thái trong trackingHistory
                if (data.trackingHistory && data.trackingHistory.length>0) {
                    data.trackingHistory.forEach(entry => {
                        renderShippingStatus(entry.status, entry.updatedTime);
                    });
                } else {
                    document.getElementById('shipping-status-table').style.display='none';
                }

                return data;
            })
            .catch(error => {
                Swal.close();
                if (error.response && error.response.status === 400) {
                    Swal.fire({
                        icon: 'error',
                        title: 'Thông báo',
                        text: 'Bạn không có quyền mở trang này!',
                        confirmButtonText: 'Quay lại trang chủ',
                        confirmButtonColor: '#000000',
                        allowOutsideClick: false,
                        allowEscapeKey: false,
                        allowEnterKey: false
                    }).then(() => {
                        window.location.href = "/index";
                    });
                } else {
                    console.log(error);
                }
            });
    }



    function formatVNDateTime(dateString) {
        const date = new Date(dateString);
        const hours = date.getHours().toString().padStart(2, "0");
        const minutes = date.getMinutes().toString().padStart(2, "0");
        const day = date.getDate();
        const month = date.getMonth() + 1;
        const year = date.getFullYear();
        return `${hours}:${minutes} ${day}/${month}/${year}`;
    }

    function renderShippingStatus(status, updateTime) {
        const formattedTime = formatVNDateTime(updateTime);
        const translatedStatus = translateShippingStatus(status);
        const key = formattedTime + translatedStatus;

        // Không thêm trùng dòng đã render
        if (lastRendered.has(key)) return;
        lastRendered.add(key);
        const tbody = document.getElementById("shipping-status-body");
        const row = document.createElement("tr");
        row.innerHTML = `
        <td>${formattedTime}</td>
        <td>${translatedStatus}</td>
    `;
        tbody.appendChild(row);
    }


    function translateShippingStatus(status) {
        const statusMap = {
            "ready_to_pick": "Chờ lấy hàng",
            "picking": "Đang lấy hàng",
            "money_collect_picking": "Lấy hàng và thu tiền",
            "picked": "Đã lấy hàng",
            "storing": "Đang lưu kho",
            "transporting": "Đang vận chuyển",
            "sorting": "Đang phân loại",
            "delivering": "Đang giao hàng",
            "money_collect_delivering": "Giao hàng và thu tiền",
            "delivered": "Đã giao hàng",
            "delivery_fail": "Giao hàng thất bại",
            "cancel": "Đã hủy",
            "return": "Hoàn hàng",
            "returned": "Đã hoàn hàng"
        };

        return statusMap[status] || status; // Nếu không khớp thì trả về nguyên trạng
    }


    function calculateTotalPriceWithoutDiscount(order) {
        let total = 0;

        order.items.forEach(item => {
            total += (item.price * (100 - item.discountPercent) / 100 ) * item.quantity;
        });

        return total;
    }



    function formatCurrency(amount) {
        return amount.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',') + 'đ';
    }

// Function to format date
    function formatDate(dateString) {
        const date = new Date(dateString);
        return date.toLocaleString('vi-VN');
    }

// Function to format order ID
    function formatOrderId(id) {
        return `#OD${String(id).padStart(6, '0')}`;
    }



    function addActionButtons(status){
        if(status=== "DaYeuCauHuy" || status === "ChoGiaoHang") return;

        const btn = document.createElement('button');
        btn.className="od-btn od-btn-cancel";
        if(status==="ChoXacNhan"){
            btn.textContent="Hủy đơn hàng";
        }else if(status==="ChuanBiDon" || status==="SanSangGiao"){
            btn.textContent="Gửi yêu cầu hủy đơn";
        }else if(status === "DaHuy"){
            btn.textContent="Mua lại";
        }
        else if (status === "DaGiao") {
            const updateStatusAt = new Date(order.updateStatusAt);
            const now = new Date();

            const diffTime = now - updateStatusAt; // chênh lệch mili giây
            const diffDays = diffTime / (1000 * 60 * 60 * 24); // đổi ra ngày



            if (diffDays <= 15) {
                console.log("Chưa quá 15 ngày → hiện nút");

                const reviewBtn = document.createElement('button');
                reviewBtn.className = "od-btn od-btn-review";
                reviewBtn.id = "reviewBtn";
                reviewBtn.textContent = "Đánh giá sản phẩm";
                actionDiv.appendChild(reviewBtn);

            } else {
                console.log("Quá 15 ngày");
                // TẤT CẢ isReviewed == false ?
                const allFalse = order.items.every(item => item.isReviewed !== true);

                if (!allFalse) {
                    console.log("Có ÍT NHẤT 1 isReviewed == true → hiện nút");
                    const reviewBtn = document.createElement('button');
                    reviewBtn.className = "od-btn od-btn-review";
                    reviewBtn.id = "reviewBtn";
                    reviewBtn.textContent = "Đánh giá sản phẩm";
                    actionDiv.appendChild(reviewBtn);
                } else {
                    console.log("Tất cả isReviewed == false → KHÔNG hiện nút");
                }
            }

            if (diffDays <=7 ){
                const returnRequest = document.createElement("button");
                returnRequest.className = "od-btn od-btn-cancel";
                returnRequest.id="returnRequestBtn";
                returnRequest.textContent="Gửi yêu cầu hoàn trả";
                returnRequest.addEventListener("click", () => {
                    window.location.href="/opulentia_user/returnRequest/"+orderId;
                });
                actionDiv.appendChild(returnRequest);
            }


            btn.textContent = "Mua lại";
        }

        actionDiv.appendChild(btn);
    }


    // Lấy các phần tử DOM
    const openModalBtn = document.getElementById("reviewBtn");
    const modalOverlay = document.getElementById('rvReviewModal');
    const closeModalBtn = document.querySelector('.rv-close-button');

    // Mở modal khi click nút
    if(openModalBtn)
    openModalBtn.addEventListener('click', function() {
        modalOverlay.style.display = 'flex';
        initItemReview(order.items);
    });

    // Đóng modal khi click nút đóng hoặc bên ngoài modal
    function closeModal() {
        modalOverlay.style.display = 'none';
    }

    closeModalBtn.addEventListener('click', closeModal);
    modalOverlay.addEventListener('click', function(e) {
        if (e.target === modalOverlay) {
            closeModal();
        }
    });

    function initItemReview(items) {
        const container = document.getElementById('rv-items-container');

        let htmlString = '';

        items.forEach(item => {
            htmlString += `
        <div class="rv-product-item">
            <img src="/uploads/${item.image}" alt="Product Image" class="rv-product-image">
            <div class="rv-product-info">
                <div class="rv-product-name">${item.productName}</div>
                <div class="rv-product-details">
                    <span>Màu: ${item.color}</span> |
                    <span>Size: ${item.size}</span>
                </div>
            </div>
            <a href="/opulentia_user/review/${item.orderDetailID}" class="rv-review-button${item.isReviewed ? ' reviewed' : ''}" 
            style="display: ${!item.isReviewed && isOver15days() ? 'none' : 'block'}"
            >
                 ${item.isReviewed ? 'Xem đánh giá' : 'Đánh giá'}
            </a>
        </div>
        `;
        });

        container.innerHTML = htmlString;
    }

    function isOver15days(){
        const updateStatusAt = new Date(order.updateStatusAt);
        const now = new Date();

        const diffTime = now - updateStatusAt; // chênh lệch mili giây
        const diffDays = diffTime / (1000 * 60 * 60 * 24); // đổi ra ngày

        return diffDays > 15;
    }
});

