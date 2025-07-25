document.addEventListener('DOMContentLoaded', async function(){
    const orderID = getOrderIDFromCurrentURL();
    //console.log(orderID);
    const returnItems = await getReturnItem(orderID);
    //console.log(returnItems);

    initReturnItem(returnItems);
});

// Thêm biến toàn cục để lưu trữ file
const uploadedFilesMap = new Map(); // key: productId, value: array of files

function getOrderIDFromCurrentURL() {
    const path = window.location.pathname; // Lấy đường dẫn URL hiện tại
    const parts = path.split('/');
    const lastPart = parts.pop(); // Hoặc parts[parts.length - 1]
    const id = parseInt(lastPart, 10);
    if (isNaN(id)) {
        Swal.fire({
            icon: 'error',
            title: 'Lỗi',
            text: 'OrderID không hợp lệ!',
            confirmButtonText: 'OK',
            confirmButtonColor: '#000000'
        });
        throw new Error("OrderID không hợp lệ!");
    }
    return id;
}

function getReturnItem(orderID){
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
    return axios.get("/opulentia_user/returnItem/get/"+orderID)
        .then(response => {
                Swal.close();
                return response.data;
        }) .catch(error => {
            Swal.close();

            if (error.response) {
                const status = error.response.status;

                if (status === 403) {
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
                } else if (status === 404) {
                    Swal.fire({
                        icon: 'warning',
                        title: 'Đơn hàng đã được gửi yêu cầu hoàn trả',
                        confirmButtonText: 'Kiểm tra đơn hoàn trả của bạn',
                        confirmButtonColor: '#000000',
                        allowOutsideClick: false,
                        allowEscapeKey: false,
                        allowEnterKey: false
                    }).then( () => {
                        window.location.href ="/opulentia_user/allOrder";
                    });
                } else {
                    Swal.fire({
                        icon: 'error',
                        title: 'Lỗi',
                        text: 'Đã xảy ra lỗi không xác định!',
                        confirmButtonText: 'Đóng',
                        confirmButtonColor: '#000000'
                    });
                }
            } else {
                Swal.fire({
                    icon: 'error',
                    title: 'Lỗi',
                    text: 'Không thể kết nối đến máy chủ!',
                    confirmButtonText: 'Đóng',
                    confirmButtonColor: '#000000'
                });
            }
        });
}

// Initialize return items with event listeners
function initReturnItem(returnItems) {
    const container = document.querySelector('.r-form-container-choose');

    // Clear existing products (except the first note and select-all)
    const existingElements = container.querySelectorAll('.r-product, .r-product-details-form');
    existingElements.forEach(el => el.remove());

    // Generate HTML for each return item
    returnItems.forEach((item, index) => {
        const productId = item.orderDetailID;
        const discountedPrice = item.price * (1 - item.discountPercent / 100);

        // Product HTML
        const productHTML = `
        <div class="r-product" data-product-id="${productId}" data-product-name="${item.productName}">
            <input type="checkbox" class="r-checkbox product-checkbox">
            <img class="r-product-img" src="/uploads/${item.image}" alt="${item.productName}">
            <div class="r-product-info">
                <div class="r-product-name">${item.productName}</div>
                <div class="r-product-loai">Size: ${item.size} | Màu: ${item.color}</div>
                <div class="r-product-price">
                    <del>₫${item.price.toLocaleString()}</del><span>₫${discountedPrice.toLocaleString()}</span>
                </div>
            </div>
            <div class="r-product-qty">
                <span>Số lượng:</span>
                <div class="r-qty-control">
                    <button class="r-qty-btn minus" type="button">-</button>
                    <input type="number" class="r-qty-input" value="1" min="1" max="${item.quantity}">
                    <button class="r-qty-btn plus" type="button">+</button>
                </div>
            </div>
        </div>`;

        // Product details form HTML
        const formHTML = `
    <div class="r-product-details-form" id="product-details-${productId}">
        <div class="r-description-group">
            <label class="r-description-label">
                <span class="r-required">*</span>Lý do trả hàng:
            </label>
            <select class="r-description-select-box return-reason" required>
                <option value="" disabled selected>-- Chọn lý do --</option>
                <option value="Sản phẩm bị hư hỏng">Sản phẩm bị hư hỏng</option>
                <option value="Giao sai sản phẩm">Giao sai sản phẩm</option>
                <option value="Không giống mô tả">Không giống mô tả</option>
                <option value="Sai kích cỡ">Sai kích cỡ</option>
                <option value="other">Lý do khác</option>
            </select>
        </div>

        <div class="r-description-group other-reason-group" style="display: none;">
            <label class="r-description-label">
                <span class="r-required">*</span>Mô tả lý do khác:
            </label>
            <textarea class="r-description-textarea other-reason" maxlength="500" oninput="updateCharCount(this, 'other-reason-count-${productId}')"
                      placeholder="Vui lòng mô tả chi tiết lý do trả hàng..."></textarea>
            <div class="r-description-count"><span id="other-reason-count-${productId}">0</span>/500 ký tự</div>
        </div>

        <div class="r-upload-title">Hình ảnh minh chứng</div>
        <p style="color: #666; font-size: 14px; margin-bottom: 20px;">Tải lên hình ảnh sản phẩm hiện trạng để hỗ trợ yêu cầu của bạn (tối thiểu 1 ảnh, tối đa 5 ảnh)</p>

        <div class="r-upload-grid" id="upload-grid-${productId}">
            <div class="r-upload-box" id="upload-box-${productId}">
                <span class="r-upload-plus">+</span>
                <img>
                <input type="file" accept="image/*" id="r-upload-input-${productId}" class="r-upload-hidden"
                       onchange="handleImagePreview(this, 'upload-grid-${productId}')">
            </div>
        </div>
        <div class="r-upload-error" id="upload-error-${productId}">Vui lòng tải lên ít nhất 1 ảnh</div>
    </div>`;

        // Thêm sự kiện click cho box upload


        // Append to container
        container.insertAdjacentHTML('beforeend', productHTML);
        container.insertAdjacentHTML('beforeend', formHTML);
        document.getElementById(`upload-box-${productId}`).onclick = function() {
            document.getElementById(`r-upload-input-${productId}`).click();
        };
    });

    // Initialize event listeners for the new elements
    initReturnItemEvents();
}




// Initialize all event listeners for return items
function initReturnItemEvents() {
    // Quantity buttons
    document.querySelectorAll('.r-qty-btn').forEach(button => {
        button.addEventListener('click', function() {
            const input = this.parentNode.querySelector('.r-qty-input');
            let value = parseInt(input.value);
            const max = parseInt(input.max);

            if (this.classList.contains('minus') && value > 1) {
                input.value = value - 1;
            } else if (this.classList.contains('plus') && value < max) {
                input.value = value + 1;
            }

            // Update total refund amount
            updateTotalRefund();
        });
    });

    // Quantity input validation
    document.querySelectorAll('.r-qty-input').forEach(input => {
        input.addEventListener('change', function() {
            let value = parseInt(this.value);
            const max = parseInt(this.max);
            if (isNaN(value)) value = 1;
            if (value < 1) value = 1;
            if (value > max) value = max;
            this.value = value;

            // Update total refund amount
            updateTotalRefund();
        });
    });

    // Product checkbox toggle
    document.querySelectorAll('.product-checkbox').forEach(checkbox => {
        checkbox.addEventListener('change', function() {
            const productId = this.closest('.r-product').getAttribute('data-product-id');
            const detailsForm = document.getElementById(`product-details-${productId}`);

            if (this.checked) {
                detailsForm.style.display = 'block';
            } else {
                detailsForm.style.display = 'none';
            }

            // Update "Select all" checkbox
            updateSelectAllCheckbox();

            // Update total refund amount
            updateTotalRefund();
        });
    });

    // "Select all" checkbox
    const selectAll = document.getElementById('select-all');
    if (selectAll) {
        selectAll.addEventListener('change', function() {
            const checkboxes = document.querySelectorAll('.product-checkbox');
            checkboxes.forEach(checkbox => {
                checkbox.checked = this.checked;
                const productId = checkbox.closest('.r-product').getAttribute('data-product-id');
                const detailsForm = document.getElementById(`product-details-${productId}`);
                detailsForm.style.display = this.checked ? 'block' : 'none';
            });

            // Update total refund amount
            updateTotalRefund();
        });
    }

    // Return reason select change
    document.querySelectorAll('.return-reason').forEach(select => {
        select.addEventListener('change', function() {
            const otherReasonGroup = this.closest('.r-description-group').nextElementSibling;
            if (this.value === 'other') {
                otherReasonGroup.style.display = 'block';
            } else {
                otherReasonGroup.style.display = 'none';
            }
        });
    });

    // Submit button
    const submitBtn = document.getElementById('submit-request');
    if (submitBtn) {
        submitBtn.addEventListener('click', function(e) {
            e.preventDefault();
            submitReturnRequest();
        });
    }
}

// Submit return request to server
async function submitReturnRequest() {
    if (!validateForm()) {
        return;
    }

    // Show loading progress
    let timerInterval;
    Swal.fire({
        title: 'Đang gửi yêu cầu',
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

    try {
        const formData = new FormData();
        const returnItems = [];
        let totalFiles = 0;

        //console.log("Bắt đầu chuẩn bị dữ liệu gửi đi...");

        // Xử lý từng sản phẩm được chọn
        document.querySelectorAll('.product-checkbox:checked').forEach(checkbox => {
            const productElement = checkbox.closest('.r-product');
            const productId = productElement.getAttribute('data-product-id');
            const detailsForm = document.getElementById(`product-details-${productId}`);

            //console.log(`\nXử lý sản phẩm ID: ${productId}`);

            // Lấy lý do trả hàng
            const returnReason = detailsForm.querySelector('.return-reason').value;
            let reasonText = returnReason;

            if (returnReason === 'other') {
                reasonText = detailsForm.querySelector('.other-reason').value.trim();
                //console.log(`- Lý do trả hàng: "${reasonText}" (tùy chọn khác)`);
            } else {
                //console.log(`- Lý do trả hàng: "${reasonText}"`);
            }

            // Lấy số lượng
            const quantity = parseInt(productElement.querySelector('.r-qty-input').value);
            //console.log(`- Số lượng: ${quantity}`);

            // Lấy file đã upload cho sản phẩm này
            const uploadGrid = document.getElementById(`upload-grid-${productId}`);
            const fileInput = document.getElementById(`r-upload-input-${productId}`);


            // //console.log(`- Kiểm tra file input:`, fileInput);
            //
            // if (fileInput.files && fileInput.files.length > 0) {
            //     //console.log(`- Tìm thấy ${fileInput.files.length} file trong input`);
            //
            //     // Lưu thông tin file
            //     for (let i = 0; i < fileInput.files.length; i++) {
            //         const file = fileInput.files[i];
            //         productFiles.push(file);
            //         fileNames.push(file.name);
            //         formData.append(`files`, file); // Thêm file vào FormData
            //         totalFiles++;
            //         //console.log(`  + File ${i+1}: ${file.name} (${file.type}, ${file.size} bytes)`);
            //     }
            // } else {
            //     //console.log(`- Không có file nào được chọn cho sản phẩm này`);
            // }


            // Lấy file từ bộ nhớ thay vì từ input
            const fileNames = [];
            const productFiles = uploadedFilesMap.get(productId) || [];

            //console.log(`Files cho sản phẩm ${productId}:`, productFiles);

            productFiles.forEach(file => {
                fileNames.push(file.name);
                formData.append(`files`, file);
            });


            // Thêm vào danh sách sản phẩm trả hàng
            returnItems.push({
                orderDetailID: parseInt(productId),
                quantity: quantity,
                reason: reasonText,
                fileNames: fileNames
            });
        });

        // Thêm thông tin sản phẩm vào FormData dưới dạng JSON
        formData.append('returnItems', JSON.stringify(returnItems));

        //console.log("\nTổng kết dữ liệu sẽ gửi:");
        //console.log("- Danh sách sản phẩm trả hàng:", returnItems);
        //console.log("- Tổng số file đính kèm:", totalFiles);
        //console.log("- FormData content:");

        // Hiển thị nội dung FormData (chỉ để debug)
        for (let [key, value] of formData.entries()) {
            if (key === 'returnItems') {
                //console.log(`  ${key}:`, JSON.parse(value));
            } else {
                //console.log(`  ${key}:`, value.name || value);
            }
        }

        // Hiển thị trạng thái loading
        const submitBtn = document.getElementById('submit-request');
        submitBtn.disabled = true;
        submitBtn.textContent = 'Đang gửi...';

        //console.log("\nĐang gửi yêu cầu đến server...");

        // Gửi request đến server
        const response = await axios.post('/opulentia_user/returnItem/save/'+getOrderIDFromCurrentURL(), formData, {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        });

        Swal.close();

        await Swal.fire({
            icon: 'success',
            title: 'Thành công',
            text: 'Yêu cầu trả hàng/hoàn tiền của bạn đã được gửi thành công!',
            confirmButtonText: 'Đến trang đơn hàng',
            confirmButtonColor: '#000000',
            allowOutsideClick: false,
            allowEscapeKey: false,
            allowEnterKey: false
        }).then(() => {
            window.location.href = "/opulentia_user/allOrder";
        });


    } catch (error) {
        Swal.close();
        await Swal.fire({
            icon: 'error',
            title: 'Lỗi',
            text: 'Có lỗi xảy ra khi gửi yêu cầu. Vui lòng thử lại sau.',
            confirmButtonText: 'OK',
            confirmButtonColor: '#000000'
        });
        console.log(error);
    } finally {
        // Reset nút gửi
        const submitBtn = document.getElementById('submit-request');
        if (submitBtn) {
            submitBtn.disabled = false;
            submitBtn.textContent = 'Gửi yêu cầu';
        }
    }
}


// Validate form before submission
function validateForm() {
    let isValid = true;

    // Check at least one product is selected
    const selectedProducts = document.querySelectorAll('.product-checkbox:checked');
    if (selectedProducts.length === 0) {
        Swal.fire({
            icon: 'error',
            title: 'Lỗi',
            text: 'Vui lòng chọn ít nhất một sản phẩm để trả hàng/hoàn tiền',
            confirmButtonText: 'OK',
            confirmButtonColor: '#000000',
        });
        return false;
    }

    // Validate each selected product
    selectedProducts.forEach(checkbox => {
        const productId = checkbox.closest('.r-product').getAttribute('data-product-id');
        const productName = checkbox.closest(".r-product").getAttribute("data-product-name");
        const detailsForm = document.getElementById(`product-details-${productId}`);

        // Check return reason is selected
        const returnReason = detailsForm.querySelector('.return-reason');
        if (!returnReason.value) {
            Swal.fire({
                icon: 'error',
                title: 'Lỗi',
                text: `Vui lòng chọn lý do trả hàng cho sản phẩm ${productName}`,
                confirmButtonText: 'OK',
                confirmButtonColor: '#000000'
            });
            isValid = false;
            return;
        }

        // Check other reason is filled if selected
        if (returnReason.value === 'other') {
            const otherReason = detailsForm.querySelector('.other-reason');
            if (!otherReason.value.trim()) {
                Swal.fire({
                    icon: 'error',
                    title: 'Lỗi',
                    text: `Vui lòng nhập lý do trả hàng cho sản phẩm ${productName}`,
                    confirmButtonText: 'OK',
                    confirmButtonColor: '#000000',
                });
                isValid = false;
                return;
            }
        }

        // Check at least one image is uploaded
        // Check at least one image is uploaded for this product
        const uploadGrid = document.getElementById(`upload-grid-${productId}`);
        const uploadedImages = uploadGrid.querySelectorAll('img[src]');

        if (uploadedImages.length === 0) {
            // Show error message
            const errorElement = document.getElementById(`upload-error-${productId}`);
            if (errorElement) {
                errorElement.style.display = 'block';
                errorElement.scrollIntoView({ behavior: 'smooth', block: 'center' });
            }

            // Highlight the upload area
            uploadGrid.style.border = '1px solid red';

            isValid = false;

            // Get product name for error message
            const productName = checkbox.closest('.r-product').querySelector('.r-product-name').textContent;
            Swal.fire({
                icon: 'error',
                title: 'Lỗi',
                text: `Sản phẩm "${productName}" cần tải lên ít nhất 1 ảnh minh chứng`,
                confirmButtonText: 'OK',
                confirmButtonColor: '#000000',
            });
        } else {
            // Reset error display if valid
            const errorElement = document.getElementById(`upload-error-${productId}`);
            if (errorElement) {
                errorElement.style.display = 'none';
            }
            uploadGrid.style.border = '';
        }
    });

    return isValid;
}

// Update character count for textareas
function updateCharCount(el, countId) {
    document.getElementById(countId).innerText = el.value.length;
}



// Hàm xử lý xóa ảnh
function removeImage(uploadBox, gridId) {
    const uploadGrid = document.getElementById(gridId);
    const productId = gridId.split('-')[2];

    // Xóa file khỏi bộ nhớ
    if (uploadedFilesMap.has(productId)) {
        const files = uploadedFilesMap.get(productId);
        const boxIndex = Array.from(uploadGrid.children).indexOf(uploadBox);
        if (boxIndex >= 0 && boxIndex < files.length) {
            files.splice(boxIndex, 1);
            uploadedFilesMap.set(productId, files);
        }
    }

    // Xóa box ảnh
    uploadGrid.removeChild(uploadBox);

    // Kiểm tra nếu không còn box upload nào thì thêm lại box mặc định
    if (uploadGrid.children.length === 0) {
        addUploadBox(gridId);
    }

    // Ẩn/hiện thông báo lỗi
    toggleErrorMsg(productId);
}

// Hàm thêm box upload mới
function addUploadBox(gridId) {
    const uploadGrid = document.getElementById(gridId);
    const productId = gridId.split('-')[2];

    if (uploadGrid.children.length >= 5) return;

    const uploadBox = document.createElement('div');
    uploadBox.className = 'r-upload-box';
    uploadBox.innerHTML = `
        <span class="r-upload-plus">+</span>
        <img>
        <input type="file" accept="image/*" class="r-upload-hidden"
               onchange="handleImagePreview(this, '${gridId}')">
    `;

    uploadBox.onclick = function() {
        uploadBox.querySelector('input').click();
    };

    uploadGrid.appendChild(uploadBox);
}

// Hàm xử lý preview ảnh
function handleImagePreview(input, gridId) {
    const file = input.files[0]; // Chỉ lấy 1 file
    if (!file) return;

    const uploadGrid = document.getElementById(gridId);
    const productId = gridId.split('-')[2];

    // Khởi tạo mảng file nếu chưa có
    if (!uploadedFilesMap.has(productId)) {
        uploadedFilesMap.set(productId, []);
    }

    const files = uploadedFilesMap.get(productId);

    // Kiểm tra đã đạt tối đa chưa
    if (files.length >= 5) {
        alert('Bạn chỉ có thể tải lên tối đa 5 ảnh cho mỗi sản phẩm');
        return;
    }

    // Thêm file vào mảng
    files.push(file);
    uploadedFilesMap.set(productId, files);

    // Tạo preview ảnh
    const reader = new FileReader();
    reader.onload = function(e) {
        // Biến box hiện tại thành box hiển thị ảnh
        const uploadBox = input.closest('.r-upload-box');
        uploadBox.innerHTML = `
            <img src="${e.target.result}" style="display: block;">
            <div class="r-upload-delete">×</div>
        `;

        // Thêm sự kiện xóa
        uploadBox.querySelector('.r-upload-delete').onclick = function(e) {
            e.stopPropagation();
            removeImage(uploadBox, gridId);
        };

        // Thêm box upload mới nếu chưa đạt tối đa
        if (files.length < 5) {
            addUploadBox(gridId);
        }

        // Ẩn thông báo lỗi
        toggleErrorMsg(productId);
    };
    reader.readAsDataURL(file);

    // Reset input
    input.value = '';
}

// Hàm ẩn/hiện thông báo lỗi
function toggleErrorMsg(productId) {
    const errorElement = document.getElementById(`upload-error-${productId}`);
    if (!errorElement) return;

    const hasImages = uploadedFilesMap.get(productId)?.length > 0;
    errorElement.style.display = hasImages ? 'none' : 'block';
}
// Update "Select all" checkbox
function updateSelectAllCheckbox() {
    const checkboxes = document.querySelectorAll('.product-checkbox');
    const selectAll = document.getElementById('select-all');
    if (!selectAll) return;

    const checkedCount = Array.from(checkboxes).filter(cb => cb.checked).length;

    if (checkedCount === checkboxes.length) {
        selectAll.checked = true;
        selectAll.indeterminate = false;
    } else if (checkedCount > 0) {
        selectAll.checked = false;
        selectAll.indeterminate = true;
    } else {
        selectAll.checked = false;
        selectAll.indeterminate = false;
    }
}

// Update total refund amount
function updateTotalRefund() {
    let total = 0;

    document.querySelectorAll('.product-checkbox:checked').forEach(checkbox => {
        const productElement = checkbox.closest('.r-product');
        const priceText = productElement.querySelector('.r-product-price span').textContent;
        const price = parseInt(priceText.replace(/[^\d]/g, ''));
        const quantity = parseInt(productElement.querySelector('.r-qty-input').value);

        total += price * quantity;
    });

    const totalElement = document.getElementById('total-refund-amount');
    if (totalElement) {
        totalElement.textContent = `₫${total.toLocaleString('vi-VN')}`;
    }
}
