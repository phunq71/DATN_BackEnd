document.addEventListener('DOMContentLoaded', async function(){
    const orderId = new URL(window.location.href).pathname.split("/").pop();
    const order= await getOrder(orderId);
    console.log("orderId: "+orderId, order);



    // Fill in basic information
    document.getElementById('shippingName').textContent = order.shippingName;
    document.getElementById('shippingPhone').textContent = order.shippingPhone;
    document.getElementById('shippingAddress').textContent = order.shippingAddress;
    document.getElementById('paymentMethod').textContent = order.paymentMethod;
    document.getElementById('orderDate').textContent = formatDate(order.orderDate);
    document.getElementById('transactionDate').textContent = formatDate(order.transactionDate);
    document.getElementById('orderId').textContent = formatOrderId(order.orderID);

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
    document.getElementById("discountProductPrice").textContent =order.discountProductPrice>0? "- "+formatCurrency(order.discountProductPrice) : formatCurrency(0);
    document.getElementById("discountVoucherPrice").textContent=order.discountVoucherPrice>0 ? "- "+ formatCurrency(order.discountVoucherPrice) : formatCurrency(0);

    document.getElementById("shippingCost").textContent=formatCurrency(order.shippingCost);

    document.querySelector('.od-summary-total span:last-child').textContent = formatCurrency(order.finalPrice);



    function getOrder(orderId) {
        return axios.get(`/opulentia_user/orders/${orderId}`)
            .then(response => {
                return response.data;
            })
            .catch(error => {
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
                        window.location.href="/index";
                    });
                } else {
                    console.log(error);
                }
            });
    }
    function calculateTotalPriceWithoutDiscount(order) {
        let total = 0;

        order.items.forEach(item => {
            total += item.price * item.quantity;
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
});

