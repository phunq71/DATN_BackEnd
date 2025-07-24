document.addEventListener('DOMContentLoaded', async function(){
    const returnRequestId = new URL(window.location.href).pathname.split("/").pop();
    const  returnRequest= await getReturnRequest(returnRequestId);
    console.log("returnRequest: "+returnRequestId, returnRequest);


    // Get modal elements
    const modal = document.getElementById("img-modal");
    const closeBtn = document.querySelector(".img-close-btn");

    // Close modal
    closeBtn.addEventListener("click", () => {
        modal.style.display = "none";
    });

    // Close when clicking outside modal
    window.addEventListener("click", (event) => {
        if (event.target === modal) {
            modal.style.display = "none";
        }
    });

    // Fill in basic information

    document.getElementById('orderStatus').textContent= returnRequest.statusName;

    // Fill in products table
    const productsTable = document.querySelector('.od-products tbody');
    productsTable.innerHTML = ''; // Clear existing rows

    returnRequest.items.forEach(item => {
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
            <td>${item.reason}</td>
            <td><button class="od-btn od-btn-review" onclick="openImage(${item.returnItemID})">${item.evidenceImages.length}/5</button></td>
        `;
        productsTable.appendChild(row);
    });

    document.getElementById("finalPrice").textContent = formatCurrency(calculateTotalPrice());

    //bạn trạng thái
    const table =document.querySelector(".order-table");
    const tr =document.createElement('tr');
    const td1=document.createElement('td');
    td1.textContent=formatDate(returnRequest.requestDate);
    const td2= document.createElement('td');
    td2.innerHTML=`<b>${returnRequest.statusName}</b>`;

    tr.appendChild(td1); tr.appendChild(td2);

    table.appendChild(tr);

    const last_tr= document.createElement('tr');
    last_tr.innerHTML=`            <td colspan="2" class="last-row">
                <button class="toggle-btn">Xem chi tiết</button>
            </td>`;
    table.appendChild(last_tr);


    //gắn sự kiện chuyển về order
    document.getElementById("action-btn").addEventListener('click', function (){
        window.location.href="/opulentia_user/orderDetail/"+returnRequest.orderId;
    });




    function getReturnRequest(returnRequestId) {
        if (isNaN(returnRequestId) || returnRequestId === null || returnRequestId === '' || Number(returnRequestId) <= 0) {
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


        return axios.get(`/opulentia_user/returnRequestDetail/get/${returnRequestId}`)
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


    function formatCurrency(amount) {
        return amount.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',') + 'đ';
    }

// Function to format date
    function formatDate(dateString) {
        const date = new Date(dateString);
        return date.toLocaleString('vi-VN');
    }


    function calculateTotalPrice() {
        let total = 0;

        // Duyệt qua từng item trong mảng items
        returnRequest.items.forEach(item => {
            // Tính giá sau khi đã giảm
            const discountedPrice = item.price * (1 - item.discountPercent / 100);
            // Cộng dồn vào tổng (giá sau giảm * số lượng)
            total += discountedPrice * item.quantity;
        });

        return total;
    }


    function openImage(returnItemID) {

        const item = returnRequest.items.find(item => item.returnItemID === returnItemID);

        if (!item || !item.evidenceImages || item.evidenceImages.length === 0) {
            console.log("Không tìm thấy ảnh minh chứng cho returnItemID này");
            return;
        }

        // Lấy các phần tử modal
        const modal = document.getElementById("img-modal");
        const modalTitle = document.querySelector(".img-modal-header h2");
        const imageContainer = document.querySelector(".img-container");

        // Xóa ảnh cũ (nếu có)
        imageContainer.innerHTML = '';

        // Thêm ảnh mới vào container
        item.evidenceImages.forEach((imgSrc, index) => {
            const imgElement = document.createElement("img");
            imgElement.src = "/uploads/"+ imgSrc;
            imgElement.alt = `Evidence ${index + 1}`;
            imageContainer.appendChild(imgElement);
        });

        // Cập nhật tiêu đề modal
        modalTitle.textContent = `Ảnh minh chứng - ${item.productName}`;

        // Hiển thị modal
        modal.style.display = "block";
    }

    window.openImage = openImage;

});

