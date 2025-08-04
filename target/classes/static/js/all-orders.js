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

        const trimmed = input.trim();
        const regex = /^#?OD(\d+)$/i;

        const match = trimmed.match(regex);
        if (match) {
            return match[1]; // Trả về phần số sau OD
        }

        return trimmed; // Nếu không phải dạng OD thì trả về nguyên input
    }

function initOrder(orders) {
    const orderListContainer = document.getElementById('order-list-container');
    if (!orderListContainer) return;

    orderListContainer.innerHTML = ''; // Clear existing content

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

        addActionButtons(actions, order.status);

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
    image.src = "/uploads/"+ item.image || 'https://via.placeholder.com/100';
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

function addActionButtons(actionElement, status){
    if(status=== "DaYeuCauHuy" || status === "ChoGiaoHang") return;

    const btn = document.createElement('button');
    btn.className="od-btn od-btn-outline";
    if(status==="ChoXacNhan"){
        btn.textContent="Hủy đơn hàng";
    }else if(status==="ChuanBiDon" || status==="SanSangGiao"){
        btn.textContent="Gửi yêu cầu hủy đơn";
    }else if(status === "DaGiao" || status === "DaHuy"){
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
    image.src = "/uploads/"+ item.image || 'https://via.placeholder.com/100';
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
