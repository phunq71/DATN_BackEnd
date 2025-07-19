document.addEventListener("DOMContentLoaded",async function () {
    // Product Variant Size Selection
    // function setupSizeSelection() {
    //     const links = document.querySelectorAll(".size-link");
    //
    //     links.forEach(link => {
    //         link.addEventListener("click", function (e) {
    //             e.preventDefault();
    //             const variantID = link.getAttribute("data-variant-id");
    //             const sizeCode = link.getAttribute("data-size-code");
    //
    //             // Update selected state
    //             document.querySelectorAll('.size-option.selected')
    //                 .forEach(el => el.classList.remove('selected'));
    //
    //             link.querySelector(".size-option")?.classList.add("selected");
    //
    //             // Fetch stock data
    //             axios.get(`/opulentia/variant/${variantID}/size/${sizeCode}`)
    //                 .then(response => {
    //                     const stock = response.data;
    //                     const stockElement = document.getElementById("stock-status");
    //                     if (stock > 0) {
    //                         stockElement.innerText = `Số lượng còn lại: ${stock}`;
    //                         stockElement.style.color = "black";
    //                     } else {
    //                         stockElement.innerText = "Hết hàng";
    //                         stockElement.style.color = "red";
    //                     }
    //                 })
    //                 .catch(console.error);
    //         });
    //     });
    // }
    let sizesData=[];
    let selectedItem=null
    const qtyInput= document.getElementById("qty-input");
    const minusBtn = document.getElementById('minusBtn');
    const plusBtn = document.getElementById('plusBtn');

    const auth = await isLogin();

    function setupSizeSelection() {
        selectedItem=null
        // Get variantId from URL (last part after last slash)
        const url = window.location.href;
        const variantId = url.substring(url.lastIndexOf('/') + 1);

        // Make API call to check available sizes
        axios.get(`/opulentia/productDetail/checkSize/${variantId}`)
            .then(response => {
                const sizeContainer = document.getElementById('size-container');
                const stockStatus = document.getElementById('stock-status');
                sizesData = response.data;

                // Clear existing size options
                sizeContainer.innerHTML = '';

                // Create new size options based on API response
                sizesData.forEach(size => {
                    const sizeOption = document.createElement('div');
                    sizeOption.className = `size-option ${size.isInStock ? '' : 'out-of-stock'}`;
                    sizeOption.textContent = size.sizeCode;
                    sizeOption.dataset.item = size.itemId;

                    // Add click event for size selection
                    sizeOption.addEventListener('click', function () {
                        if (size.isInStock) {
                            // Remove selected class from all options
                            document.querySelectorAll('.size-option').forEach(option => {
                                option.classList.remove('selected');
                            });

                            // Add selected class to clicked option
                            this.classList.add('selected');
                            selectedItem=size.itemId;
                            qtyInput.max=size.stockQuantity;
                            adjustQuantity();
                            // console.log(selectedItem);
                            // Update stock status
                            stockStatus.textContent = `Còn hàng (${size.stockQuantity} sản phẩm)`;
                        } else {
                            stockStatus.textContent = 'Hết hàng';
                        }
                    });

                    sizeContainer.appendChild(sizeOption);
                });

                // Initialize stock status
                if (sizesData.length > 0) {
                    stockStatus.textContent = 'Vui lòng chọn size';
                } else {
                    stockStatus.textContent = 'Sản phẩm tạm hết hàng';
                }
            })
            .catch(error => {
                console.error('Error fetching size data:', error);
                document.getElementById('stock-status').textContent = 'Lỗi khi tải thông tin size';
            });
    }

    function addToCart(itemID, quantity) {
            let stockQuantity = findQuantityStockByItemId(itemID);
            let existingCartItemIndex = carts.findIndex(cart => cart.itemID === itemID);
            let isOverQuantity=false;

            // Case 1: Item not in cart
            if (existingCartItemIndex === -1) {
                // Check if quantity exceeds stock
                if (quantity > stockQuantity) {
                   isOverQuantity=true;
                    quantity = stockQuantity;
                }

                let cart = {
                    customerID: null,
                    itemID: itemID,
                    quantity: quantity,
                    latestDate: new Date().toISOString()
                };

                carts.push(cart);
            }
            // Case 2: Item already in cart
            else {
                let newTotalQuantity = Number( carts[existingCartItemIndex].quantity) + Number(quantity);
                console.log('newQty', newTotalQuantity);
                // Check if total quantity exceeds stock
                if (newTotalQuantity > stockQuantity) {
                    isOverQuantity=true;
                    carts[existingCartItemIndex].quantity = stockQuantity;
                } else {
                    carts[existingCartItemIndex].quantity = newTotalQuantity;
                }

                // Update the latest date
                carts[existingCartItemIndex].latestDate = new Date().toISOString();
            }



        if(!auth){
            localStorage.setItem('carts', JSON.stringify(carts));
            console.log(carts);
        }
        else{
            updateCartsFromServer(carts);
        }
        document.dispatchEvent(new Event('cartUpdated'));

        if(!isOverQuantity){
            Swal.fire({
                title: 'Đã thêm vào giỏ hàng!',
                icon: 'success',
                showCancelButton: true,
                confirmButtonText: 'Đến giỏ hàng',
                cancelButtonText: 'OK',
                confirmButtonColor: '#000000', // Đến giỏ hàng: nền đen
                cancelButtonColor: '#ffffff',  // OK: nền trắng
                timer: 4000,
                timerProgressBar: true,
                reverseButtons: true, // để OK bên trái
                didRender: () => {
                    // Đổi màu chữ nút OK thành đen
                    const cancelBtn = document.querySelector('.swal2-cancel');
                    if (cancelBtn) {
                        cancelBtn.style.color = 'black';
                    }
                }
            }).then((result) => {
                if (result.isConfirmed) {
                    window.location.href = '/opulentia/cart';
                }
            });
        }else{
            Swal.fire({
                title: 'Xin lỗi vì sự bất tiện này',
                text: 'Số lượng sản phẩm trong kho không đủ đáp ứng yêu cầu của bạn. Đã tự điều chỉnh về số lượng nhiều nhất.',
                icon: 'warning',
                showCancelButton: true,
                confirmButtonText: 'Đến giỏ hàng',
                cancelButtonText: 'OK',
                confirmButtonColor: '#000000',
                cancelButtonColor: '#ffffff',
                timer: 4000,
                timerProgressBar: true,
                reverseButtons: true,
                didRender: () => {
                    const cancelBtn = document.querySelector('.swal2-cancel');
                    if (cancelBtn) {
                        cancelBtn.style.color = 'black';
                    }
                }
            }).then((result) => {
                if (result.isConfirmed) {
                    window.location.href = '/opulentia/cart';
                }
            });
        }
    }

    document.getElementById("add-to-cart").addEventListener('click', function (){
        if(selectedItem || selectedItem!=null) {
            addToCart(selectedItem, qtyInput.value);
        }else{
            Swal.fire({
                title: 'Vui lòng chọn size của bạn',
                icon: 'warning', // 'success', 'error', 'warning', 'info', 'question'
                timer:3000,
                timerProgressBar: true,
                confirmButtonColor: '#000000',
            });
        }
    });

    function findQuantityStockByItemId(itemId) {
        const item = sizesData.find(item => item.itemId === itemId);
        return item ? item.stockQuantity : null; // Trả về stockQuantity nếu tìm thấy, ngược lại trả về null
    }

    function updateCartsFromServer(carts){
        carts.sort((a, b) => new Date(a.latestDate) - new Date(b.latestDate));
        return axios.put('/opulentia_user/rest/cart/update',carts)
            .then(response =>{
                console.log(response.data)
                return response.data;
            }).catch(error =>{
                console.log(error);
                return [];
            });
    }



    minusBtn.addEventListener('click', () => {
        let currentValue = Number(qtyInput.value);
        const min = Number(qtyInput.min) || 1;
        if (currentValue > min) {
            qtyInput.value = currentValue - 1;
        }
    });

    plusBtn.addEventListener('click', () => {
        let currentValue = Number(qtyInput.value);
        const max = Number(qtyInput.max) || Infinity;
        if (currentValue < max) {
            qtyInput.value = currentValue + 1;
        }
    });

    // Biến để theo dõi xem có đang chỉnh sửa hay không
    let isEditing = false;
    let timeoutId = null; // Biến để lưu timeout

    qtyInput.addEventListener('input', () => {
        isEditing = true;

        // Hủy timeout trước đó (nếu có)
        if (timeoutId) {
            clearTimeout(timeoutId);
        }

        // Đặt timeout mới (đợi 1 giây sau khi ngừng nhập)
        timeoutId = setTimeout(() => {
            let value = Number(qtyInput.value);
            const max = Number(qtyInput.max) || Infinity;

            if (value > max) {
                Swal.fire({
                    title: 'Xin lỗi vì sự bất tiện này!',
                    text: `Chỉ còn ${max} sản phẩm trong kho. Đã tự động điều chỉnh số lượng`,
                    icon: 'warning', // 'success', 'error', 'warning', 'info', 'question'
                    confirmButtonColor: '#000000'
                });
                qtyInput.value = max; // Tự động sửa lại giá trị
            }
        }, 500); // 1000ms = 1 giây
    });

    // qtyInput.addEventListener('blur', () => {
    //     const max = Number(qtyInput.max) || Infinity;
    //     // Hủy timeout nếu người dùng rời khỏi ô input
    //     if (timeoutId) {
    //         clearTimeout(timeoutId);
    //         timeoutId = null;
    //     }
    //
    //     if (isEditing) {
    //         Swal.fire({
    //             title: 'Xin lỗi vì sự bất tiện này!',
    //             text: `Chỉ còn ${max} sản phẩm trong kho. Đã tự động điều chỉnh số lượng`,
    //             icon: 'warning', // 'success', 'error', 'warning', 'info', 'question'
    //             confirmButtonColor: '#000000'
    //         });
    //         adjustQuantity();
    //         isEditing = false;
    //     }
    // });

// Hàm điều chỉnh số lượng hợp lệ
    function adjustQuantity() {
        let value = Number(qtyInput.value);
        const min = Number(qtyInput.min) || 1;
        const max = Number(qtyInput.max) || Infinity;

        if (value < min) {
            qtyInput.value = min;
            return min;
        }
        if (value > max) {
            qtyInput.value = max;
            return max;
        }
        return value;
    }



    // Product Reviews
    function setupReviews() {
        const pathParts = window.location.pathname.split('/');
        const productId = pathParts[4];
        let currentPage = 0;

        function formatDateToAgo(dateStr) {
            const now = new Date();
            const date = new Date(dateStr);
            const diffDays = Math.floor((now - date) / (1000 * 60 * 60 * 24));
            return diffDays === 0 ? "Hôm nay" :
                diffDays === 1 ? "1 ngày trước" :
                    `${diffDays} ngày trước`;
        }

        function loadReviews(page = 0, filters = {}) {
            const params = new URLSearchParams({page});
            Object.entries(filters).forEach(([key, value]) => {
                if (value) params.append(key, value);
            });

            axios.get(`/opulentia/${productId}?${params.toString()}`)
                .then(res => {
                    const {reviews, totalPages, currentPage: current} = res.data;
                    currentPage = current;
                    renderReviews(reviews);
                    renderPagination(totalPages);
                })
                .catch(console.error);
        }

        function renderReviews(reviews) {
            const reviewContainer = document.querySelector(".customer-reviews");
            let html = `<h3>Đánh giá của khách hàng</h3>`;

            if (!reviews?.length) {
                html += `<p>Chưa có đánh giá nào.</p>`;
            } else {
                html += reviews.map(review => {
                    const stars = '★'.repeat(review.rating) + '☆'.repeat(5 - review.rating);
                    const images = review.reviewImages?.length > 0
                        ? `<div class="review-images">${
                            review.reviewImages.map(img =>
                                `<img src="/uploads/${img.imageUrl}" alt="Review ${review.reviewID}" class="review-image">`
                            ).join('')
                        }</div>`
                        : '';

                    return `
                        <div class="review-item">
                            <div class="review-header">
                                <div class="reviewer-name">${review.customerName}</div>
                                <div class="review-stars">${stars}</div>
                            </div>
                            <div class="review-meta">
                                <span>Màu: ${review.color} | Size: ${review.size}</span>
                                <span class="review-time">${formatDateToAgo(review.createAt)}</span>
                            </div>
                            <div class="review-content">${review.content}</div>
                            ${images}
                        </div>
                    `;
                }).join('');
            }

            reviewContainer.innerHTML = html;
            setupImageZoom();
        }

        function renderPagination(totalPages) {
            const paginationContainer = document.querySelector(".pagination");
            let pageHtml = `
                <button onclick="loadReviews(${currentPage - 1})" ${currentPage === 0 ? 'disabled' : ''}>← Trước</button>
            `;

            for (let i = 0; i < totalPages; i++) {
                pageHtml += `<button onclick="loadReviews(${i})" class="${i === currentPage ? 'active-page' : ''}">${i + 1}</button>`;
            }

            pageHtml += `
                <button onclick="loadReviews(${currentPage + 1})" ${currentPage === totalPages - 1 ? 'disabled' : ''}>Sau →</button>
            `;

            paginationContainer.innerHTML = pageHtml;
        }

        function handleSingleSelect(container) {
            container.querySelectorAll('input[type="checkbox"]').forEach(cb => {
                cb.addEventListener('change', function () {
                    if (this.checked) {
                        container.querySelectorAll('input[type="checkbox"]').forEach(other => {
                            if (other !== this) other.checked = false;
                        });
                    }
                });
            });
        }

        function applyFilters() {
            const filters = {
                rating: document.querySelector('input[name="rating"]:checked')?.value || null,
                color: document.querySelector('input[name="color"]:checked')?.value || null,
                size: document.querySelector('input[name="size"]:checked')?.value || null,
                hasImage: document.querySelector('#hasImage')?.checked || false
            };

            loadReviews(0, filters);
            document.querySelector('.clear-filter').style.display = 'block';
            document.getElementById('filterModal').style.display = 'none';
        }

        function clearFilters() {
            document.querySelectorAll('input[type="checkbox"]').forEach(cb => cb.checked = false);
            loadReviews(0);
            document.querySelector('.clear-filter').style.display = 'none';
        }

        // Initialize
        loadReviews(0);
        document.querySelectorAll('.filter-group').forEach(handleSingleSelect);

        // Expose functions to global scope if needed
        window.loadReviews = loadReviews;
        window.applyFilters = applyFilters;
        window.clearFilters = clearFilters;
    }

    // Image Slider
    function setupImageSlider() {
        const slider = document.querySelector('.image-slider');
        const allImages = slider.querySelectorAll('img');
        let currentImageIndex = 0;
        let currentColorIndex = 0;
        let isDragging = false;
        let startX = 0;

        function showImage(colorIndex, imageIndex) {
            allImages.forEach(img => img.classList.remove('active'));
            const activeImage = slider.querySelector(`img[data-color-index="${colorIndex}"][data-image-index="${imageIndex}"]`);
            activeImage?.classList.add('active');
        }

        function selectColor(colorIndex) {
            currentColorIndex = colorIndex;
            currentImageIndex = 0;
            showImage(currentColorIndex, currentImageIndex);
        }

        function navigateImage(direction) {
            const colorImages = slider.querySelectorAll(`img[data-color-index="${currentColorIndex}"]`);
            currentImageIndex = (currentImageIndex + direction + colorImages.length) % colorImages.length;
            showImage(currentColorIndex, currentImageIndex);
        }

        // Event handlers
        function handleDragStart(e) {
            isDragging = true;
            startX = e.clientX || e.touches[0].clientX;
        }

        function handleDragMove(e) {
            if (!isDragging) return;
            const clientX = e.clientX || e.touches[0].clientX;
            const diff = clientX - startX;
            if (Math.abs(diff) > 50) {
                navigateImage(diff > 0 ? -1 : 1);
                isDragging = false;
            }
        }

        function handleDragEnd() {
            isDragging = false;
        }

        function handleWheel(e) {
            e.preventDefault();
            navigateImage(e.deltaY > 0 ? 1 : -1);
        }

        // Set up event listeners
        slider.addEventListener('mousedown', handleDragStart);
        slider.addEventListener('mousemove', handleDragMove);
        slider.addEventListener('mouseup', handleDragEnd);
        slider.addEventListener('mouseleave', handleDragEnd);
        slider.addEventListener('touchstart', handleDragStart);
        slider.addEventListener('touchmove', handleDragMove);
        slider.addEventListener('touchend', handleDragEnd);
        slider.addEventListener('wheel', handleWheel, {passive: false});

        document.querySelectorAll('.color-option').forEach(option => {
            option.addEventListener('click', () => {
                selectColor(parseInt(option.dataset.colorIndex));
            });
        });

        // Initialize
        showImage(0, 0);
    }

    // Image Zoom (assuming this exists)
    function setupImageZoom() {
        // Your image zoom implementation here
    }

    // Initialize all components
    setupSizeSelection();
    setupReviews();
    setupImageSlider();
    setupImageZoom();

    // Modal handling
    window.openFilterModal = function () {
        document.getElementById('filterModal').style.display = 'block';
    };

    window.onclick = function (event) {
        if (event.target === document.getElementById('filterModal')) {
            document.getElementById('filterModal').style.display = 'none';
        }
    };
});