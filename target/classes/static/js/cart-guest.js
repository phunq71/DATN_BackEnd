// const token = document.querySelector('meta[name="_csrf"]').getAttribute('content');
// const header = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');


document.addEventListener('DOMContentLoaded', async function() {
   // getCartsFromLocalStorage();
    // console.log(carts);
    itemCarts = await getItem(carts);
    // console.log(itemCarts);
    initCart();
});

let carts =[];
let itemCarts= [];
const cartContainer = document.getElementById('cart-container');
let checkedItems=[];

/*
* Lấy thông tin giỏ hàng từ LocalStorage
* Nếu chưa có thì lấy dữ liệu mẫu để test -- sẽ bỏ sau
* */
function getCartsFromLocalStorage(){
    //Dữ liệu mẫu
    carts= [
        {customerID: null, itemID: 121, quantity: 2, latestDate: new Date().toISOString()},
        {customerID: null, itemID: 21, quantity: 3, latestDate: new Date().toISOString()},
        {customerID: null, itemID: 46, quantity: 3, latestDate: new Date().toISOString()},
        {customerID: null, itemID: 150 , quantity: 3, latestDate: new Date().toISOString()}
    ]

    // Lấy dữ liệu từ localStorage
    let dataCartString = localStorage.getItem('carts');
    let dataCarts = dataCartString ? JSON.parse(dataCartString) : [];

// Nếu chưa có gì trong localStorage thì set dữ liệu mẫu
//     if (!dataCarts || dataCarts.length < 1) {
//         localStorage.setItem('carts', JSON.stringify(carts));
//     }
//     else{
//         carts= JSON.parse(localStorage.getItem('carts'));
//     }

}



function getItem(carts) {
    return axios.post("/opulentia/rest/cart", carts)
        .then(response => {
            // console.log(response.data);
            return response.data; // ✅ TRẢ DỮ LIỆU RA NGOÀI
        })
        .catch(error => {
            // console.log(error);
            return []; // 👈 nếu lỗi thì trả mảng rỗng hoặc giá trị mặc định
        });
}
// Function to format price with VND currency
function formatPrice(price) {
    return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(price);
}

// Function to find a product by itemID
function findProductByItemID(itemID) {
    for (const productGroup of itemCarts) {
        const product = productGroup.find(p => p.itemID === itemID);
        if (product) return product;
    }
    return null;
}

// Function to get all colors for a product
function getProductColors(productGroup) {
    const colors = [];
    const colorMap = new Map();

    productGroup.forEach(item => {
        if (!colorMap.has(item.color)) {
            colorMap.set(item.color, {
                color: item.color,
                variantID: item.variantID,
                hasStock: productGroup.some(p => p.color === item.color && p.isInStock)
            });
        }
    });

    return Array.from(colorMap.values());
}

// Function to get all sizes for a specific color (variant)
function getSizesForColor(productGroup, variantID) {
    return productGroup
        .filter(item => item.variantID === variantID)
        .map(item => ({
            sizeID: item.sizeID,
            sizeCode: item.sizeCode,
            isInStock: item.isInStock,
            itemID: item.itemID
        }));
}

// Function to create a cart item row
function createCartItemRow(chosenProduct, productGroup) {
    const row = document.createElement('tr');
    row.className = 'c-cart-item';
    row.dataset.itemId = chosenProduct.itemID;

    // Check if all variants are out of stock
    const allOutOfStock = !productGroup.some(item => item.isInStock);

    // Kiểm tra xem item này có trong danh sách checked không
    const isChecked = checkedItems.includes(carts.findIndex(cart =>cart.itemID===chosenProduct.itemID)) && !allOutOfStock;

    // Get all colors and sizes for this product
    const colors = getProductColors(productGroup);
    const currentVariantID = chosenProduct.variantID;
    const sizes = getSizesForColor(productGroup, currentVariantID);

    // Find the current size
    const currentSize = sizes.find(s => s.itemID === chosenProduct.itemID);

    // Determine if current selection is out of stock
    const currentOutOfStock = allOutOfStock || !chosenProduct.isInStock;
    // Create row HTML - checkbox checked nếu có trong checkedItems
    row.innerHTML = `
        <td><input type="checkbox" class="c-cart-checkbox" ${isChecked ? 'checked' : ''} ${currentOutOfStock ? 'disabled' : ''}></td>
        <td>
            <div class="c-product-info">
                <img src="/uploads/${chosenProduct.mainImage}" alt="${chosenProduct.productName}" class="c-product-image">
                <div>
                    <strong>${chosenProduct.productName}</strong>
                    <div class="c-variant-container">
                        <select class="c-variant-select c-color-select" ${allOutOfStock ? 'disabled' : ''}>
                            ${colors.map(color =>
        `<option value="${color.variantID}" 
                                 ${color.variantID === currentVariantID ? 'selected' : ''}
                                 ${!color.hasStock ? 'disabled' : ''}>
                                    ${color.color}${!color.hasStock ? ' (hết hàng)' : ''}
                                </option>`
    ).join('')}
                        </select>
                        <select class="c-variant-select c-size-select" ${allOutOfStock ? 'disabled' : ''}>
                            ${sizes.map(size =>
        `<option value="${size.itemID}" 
                                 ${size.itemID === chosenProduct.itemID ? 'selected' : ''}
                                 ${!size.isInStock ? 'disabled' : ''}>
                                    Size ${size.sizeCode}${!size.isInStock ? ' (hết hàng)' : ''}
                                </option>`
    ).join('')}
                        </select>
                    </div>
                </div>
            </div>
        </td>
        <td class="c-price">${formatPrice(chosenProduct.price)}</td>
        <td class="c-actions">
            <div class="c-quantity-container">
                <button class="c-quantity-btn minus" ${currentOutOfStock ? 'disabled' : ''}>-</button>
                <input type="number" class="c-quantity-input" value="${currentOutOfStock ? 0 : (chosenProduct.quantity || 1)}" min="1" ${currentOutOfStock ? 'disabled' : ''}>
                <button class="c-quantity-btn plus" ${currentOutOfStock ? 'disabled' : ''}>+</button>
            </div>
        </td>
        <td class="c-item-total">${formatPrice(currentOutOfStock ? 0 : (chosenProduct.price * (chosenProduct.quantity || 1)))}</td>
        <td><button class="c-delete-single" title="Xóa"><i class="fa-solid fa-trash"></i></button></td>
    `;

    // Add disabled class if all variants are out of stock
    if (allOutOfStock) {
        row.classList.add('disabled-row');
    }

    return row;
}
// Function to handle color change
async function handleColorChange(event, productGroup) {
    const row = event.target.closest('.c-cart-item');
    const oldItemID = parseInt(row.dataset.itemId);
    const variantID = event.target.value;
    const quantityInput = row.querySelector('.c-quantity-input');
    const currentQuantity = parseInt(quantityInput.value);

    // Get sizes for the new color
    const sizes = getSizesForColor(productGroup, variantID);
    const sizeSelect = row.querySelector('.c-size-select');

    // Update size dropdown
    sizeSelect.innerHTML = sizes.map(size =>
        `<option value="${size.itemID}" 
             ${size.isInStock ? '' : 'disabled'}>
                Size ${size.sizeCode}${size.isInStock ? '' : ' (hết hàng)'}
            </option>`
    ).join('');

    // Select the first available size
    const firstAvailableSize = sizes.find(size => size.isInStock);
    if (firstAvailableSize) {
        sizeSelect.value = firstAvailableSize.itemID;

        // Update the row's itemID
        const newItemID = firstAvailableSize.itemID;
        row.dataset.itemId = newItemID;

        // Find the new item to check stock quantity
        const newItem = findItemByID(newItemID);
        if (newItem) {
            // Check if current quantity exceeds new stock
            if (currentQuantity > newItem.stockQuantity) {
                const newQuantity = newItem.stockQuantity;
                quantityInput.value = newQuantity;
                Swal.fire({
                    title: 'Xin lỗi vì sự bất tiện này!',
                    text: `Chỉ còn ${newItem.stockQuantity} sản phẩm trong kho. Đã tự động điều chỉnh số lượng`,
                    icon: 'warning', // 'success', 'error', 'warning', 'info', 'question'
                    confirmButtonColor: '#000000'
                });
            }
        }

        // Update cart
        carts.forEach(cart => {
            if(cart.itemID===oldItemID) {
                cart.itemID = newItemID;
                // Update quantity if needed
                if (newItem && currentQuantity > newItem.stockQuantity) {
                    cart.quantity = newItem.stockQuantity;
                }
            }
        });

        localStorage.setItem('carts', JSON.stringify(carts));
        itemCarts = await getItem(carts);
        initCart();
    }
}

// Function to handle size change
async function handleSizeChange(event) {
    const row = event.target.closest('.c-cart-item');
    const oldItemID = parseInt(row.dataset.itemId);
    const newItemID = parseInt(event.target.value);
    const quantityInput = row.querySelector('.c-quantity-input');
    const currentQuantity = parseInt(quantityInput.value);

    // Find the new item to check stock quantity
    const newItem = findItemByID(newItemID);
    if (newItem) {
        // Check if current quantity exceeds new stock
        if (currentQuantity > newItem.stockQuantity) {
            const newQuantity = newItem.stockQuantity;
            quantityInput.value = newQuantity;
            Swal.fire({
                title: 'Xin lỗi vì sự bất tiện này!',
                text: `Chỉ còn ${item.stockQuantity} sản phẩm trong kho. Đã tự động điều chỉnh số lượng`,
                icon: 'warning', // 'success', 'error', 'warning', 'info', 'question'
                confirmButtonColor: '#000000'
            });
        }
    }

    // Update the row's itemID
    row.dataset.itemId = newItemID;

    // Update cart
    carts.forEach(cart => {
        if(cart.itemID===oldItemID) {
            cart.itemID = newItemID;
            // Update quantity if needed
            if (newItem && currentQuantity > newItem.stockQuantity) {
                cart.quantity = newItem.stockQuantity;
            }
        }
    });

    localStorage.setItem('carts', JSON.stringify(carts));
    itemCarts = await getItem(carts);
    initCart();
}

// Function to handle quantity change
async function handleQuantityChange(event, input) {
    const row = input.closest('.c-cart-item');
    const itemID = parseInt(row.dataset.itemId);
    const oldQuantity = parseInt(input.dataset.oldValue || input.value);
    let newQuantity = parseInt(input.value);

    // Tìm item trong giỏ hàng
    const item = findItemByID(itemID);
    if (!item) {
        alert('Sản phẩm không tồn tại trong giỏ hàng');
        input.value = oldQuantity;
        return;
    }

    // Kiểm tra số lượng hợp lệ (ít nhất là 1)
    if (newQuantity < 1) {
        alert('Số lượng phải lớn hơn 0');
        input.value = oldQuantity;
        return;
    }

    // Nếu số lượng mới vượt quá tồn kho, set bằng số lượng tồn kho tối đa
    if (newQuantity > item.stockQuantity) {
        newQuantity = item.stockQuantity;
        input.value = newQuantity; // Set lại giá trị input
        Swal.fire({
            title: 'Xin lỗi vì sự bất tiện này!',
            text: `Chỉ còn ${item.stockQuantity} sản phẩm trong kho. Đã tự động điều chỉnh số lượng`,
            icon: 'warning', // 'success', 'error', 'warning', 'info', 'question'
            confirmButtonColor: '#000000'
        });
    }

    // Cập nhật số lượng trong giỏ hàng
    const cartItemIndex = carts.findIndex(cart => cart.itemID === itemID);
    if (cartItemIndex !== -1) {
        carts[cartItemIndex].quantity = newQuantity;
        localStorage.setItem('carts', JSON.stringify(carts));

        // Cập nhật giá trị cũ
        input.dataset.oldValue = newQuantity;

        // Cập nhật tổng giá
        const price = parseInt(row.querySelector('.c-price').textContent.replace(/[^\d]/g, ''));
        row.querySelector('.c-item-total').textContent = formatPrice(price * newQuantity);

        // Lấy giỏ hàng mới nhất và render lại
        itemCarts = await getItem(carts);
        initCart();
    }
}

// Initialize the cart
// Initialize the cart
function initCart() {
    cartContainer.innerHTML = '';

    itemCarts.forEach(productGroup => {
        // Find chosen products in this group
        const chosenProducts = productGroup.filter(item => item.chosen);

        chosenProducts.forEach(chosenProduct => {
            const row = createCartItemRow(chosenProduct, productGroup);
            cartContainer.appendChild(row);

            // Add event listeners
            const checkbox = row.querySelector('.c-cart-checkbox');

            checkbox.addEventListener('change', function() {
                const cartIndex = carts.findIndex(cart => cart.itemID === parseInt(row.dataset.itemId));

                if (this.checked) {
                    // Thêm index vào danh sách checked nếu chưa có
                    if (!checkedItems.includes(cartIndex)) {
                        checkedItems.push(cartIndex);
                    }
                } else {
                    // Xóa khỏi danh sách checked nếu có
                    checkedItems = checkedItems.filter(index => index !== cartIndex);
                }
                updateTotalAmount();
            });

            // Add event listeners
            const colorSelect = row.querySelector('.c-color-select');
            const sizeSelect = row.querySelector('.c-size-select');
            const quantityInput = row.querySelector('.c-quantity-input');
            const minusBtn = row.querySelector('.minus');
            const plusBtn = row.querySelector('.plus');
            const deleteItemBtn= row.querySelector('.c-delete-single')
            //const checkbox = row.querySelector('.c-cart-checkbox');

            colorSelect.addEventListener('change', (e) => handleColorChange(e, productGroup));
            sizeSelect.addEventListener('change', handleSizeChange);

            quantityInput.addEventListener('change', (e) => handleQuantityChange(e, quantityInput));

            // Add checkbox change event
            checkbox.addEventListener('change', () => {
                updateTotalAmount();
                document.getElementById('c-select-all-checkout').checked=false;
            });

            plusBtn.addEventListener('click', async () => {
                const currentQuantity = parseInt(quantityInput.value);
                const item = findItemByID(parseInt(row.dataset.itemId));

                if (item && currentQuantity >= item.stockQuantity) {
                    Swal.fire({
                        title: 'Xin lỗi vì sự bất tiện này!',
                        text: `Chỉ còn ${item.stockQuantity} sản phẩm trong kho`,
                        icon: 'warning',
                        confirmButtonColor: '#000000'
                    });
                    return;
                }

                quantityInput.value = currentQuantity + 1;
                quantityInput.dispatchEvent(new Event('change'));
            });

            minusBtn.addEventListener('click', () => {
                if (parseInt(quantityInput.value) > 1) {
                    quantityInput.value = parseInt(quantityInput.value) - 1;
                    quantityInput.dispatchEvent(new Event('change'));
                }
            });

            deleteItemBtn.addEventListener('click', async () =>{
                // console.log(chosenProduct);
                const itemIndex = carts.findIndex(item => parseInt(item.itemID) === parseInt(chosenProduct.itemID));
                // console.log(itemIndex);
                // console.log(carts)
                carts.splice(itemIndex, 1);
                localStorage.setItem('carts', JSON.stringify(carts));
                // console.log('cart sau khi slice', carts)
                itemCarts= await getItem(carts);
                initCart();
            });


            document.getElementById('c-select-all-checkout').addEventListener('change', function() {
                const isChecked = this.checked;
                const checkboxes = document.querySelectorAll('.c-cart-checkbox:not(:disabled)');
                checkedItems = [];

                checkboxes.forEach((checkbox) => {
                    const row = checkbox.closest('.c-cart-item');
                    if (!row) return; // ⚠️ Bỏ qua nếu không tìm thấy row

                    const itemID = parseInt(row.dataset.itemId); // ✅ Đảm bảo row tồn tại
                    const cartIndex = carts.findIndex(cart => cart.itemID === itemID);

                    checkbox.checked = isChecked;
                    if (isChecked && cartIndex !== -1) {
                        checkedItems.push(cartIndex);
                    }
                });

                // console.log('checkedItems', checkedItems);
                updateTotalAmount();
            });
        });

    });
    document.dispatchEvent(new Event('cartUpdated'));
    updateTotalAmount();
    updateTotalQuantity();
}

// Function to update total amount when checkboxes change
function updateTotalAmount() {
    let total = 0;
    checkedItems.forEach(index => {
        const cartItem = carts[index];
        if (cartItem) {
            const item = findItemByID(cartItem.itemID);
            if (item) {
                total += item.price * cartItem.quantity;
            }
        }
    });

    document.getElementById('total-amount').innerText = formatPrice(total);
}

function findItemByID(itemID) {
    // Duyệt qua từng mảng con trong mảng lớn
    for (const group of itemCarts) {
        // Duyệt qua từng item trong mảng con
        for (const item of group) {
            // Nếu itemID trùng khớp, trả về item đó
            if (item.itemID === itemID) {
                // console.log(item);
                return item;
            }
        }
    }
    // Nếu không tìm thấy, trả về null hoặc undefined
    return null;
}
function calculateTotal() {
    let total = 0;
    document.querySelectorAll('.c-cart-item').forEach(row => {
        const checkbox = row.querySelector('.c-cart-checkbox');
        if (checkbox && checkbox.checked) {
            const price = parseInt(row.querySelector('.c-price').textContent.replace(/[^\d]/g, ''));
            const quantity = parseInt(row.querySelector('.c-quantity-input').value);
            total += price * quantity;
        }
    });
    return total;
}

function deleteCheckedItem(){
    if(checkedItems.length>0){
    Swal.fire({
        title: 'Xóa các hàng được chọn',
        text: 'Bạn có chắc chắn muốn xóa hết các sản phẩm được chọn ra khỏi giỏ hàng',
        icon: 'error',
        showCancelButton: true,
        confirmButtonText: 'Có, xóa sạch hết',
        cancelButtonText: 'Hủy, không xóa nữa',
        confirmButtonColor: '#000000',
        cancelButtonColor: '#656565'
    })
        .then(async (result) =>{
        if (result.isConfirmed) {
            // Sắp xếp giảm dần để khi xóa không ảnh hưởng đến index của các phần tử khác
            checkedItems.sort((a, b) => b - a);

            // Xóa từng item theo index
            checkedItems.forEach(index => {
                carts.splice(index, 1);
            });

            localStorage.setItem('carts', JSON.stringify(carts));
            checkedItems = [];
            itemCarts = await getItem(carts);
            initCart();
        } else if (result.isDismissed) {

        }
    });}
    else Swal.fire({
        title: 'Không có sản phẩm nào được chọn',
        icon: 'info',
        confirmButtonColor: '#000000'
        }
    )
}

function updateTotalQuantity() {
    let totalQuantity = 0;

    // Duyệt qua từng mảng con trong itemCarts
    for (const cartGroup of itemCarts) {
        // Duyệt qua từng item trong mảng con
        for (const item of cartGroup) {
            // Kiểm tra nếu item được chọn và còn hàng
            if (item.chosen === true && item.isInStock === true) {
                totalQuantity += item.quantity;
            }
        }
    }

    document.getElementById('total-quantity').innerText=totalQuantity;
}
