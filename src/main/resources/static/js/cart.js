document.addEventListener('DOMContentLoaded', async function() {
    getCartsFromLocalStorage();
    console.log(carts);
    const itemCarts = await getItem(carts);
    showCarts(itemCarts);
});

let carts =[];
let itemCarts= [];
/*
* Lấy thông tin giỏ hàng từ LocalStorage
* Nếu chưa có thì lấy dữ liệu mẫu để test -- sẽ bỏ sau
* */
function getCartsFromLocalStorage(){
    //Dữ liệu mẫu
    carts= [
        {customerID: null, itemID: 7, quantity: 2, latestDate: new Date().toISOString()},
        {customerID: null, itemID: 21, quantity: 3, latestDate: new Date().toISOString()},
        {customerID: null, itemID: 5, quantity: 3, latestDate: new Date().toISOString()}
    ]

    let dataCarts= localStorage.getItem('carts');

    if(!dataCarts){
        localStorage.setItem('carts', JSON.stringify(carts));
    }
    else{
        carts= JSON.parse(localStorage.getItem('carts'));
    }

}

function getItem(carts) {
    return axios.post("/opulentia/rest/cart", carts)
        .then(response => {
            console.log(response.data);
            return response.data; // ✅ TRẢ DỮ LIỆU RA NGOÀI
        })
        .catch(error => {
            console.log(error);
            return []; // 👈 nếu lỗi thì trả mảng rỗng hoặc giá trị mặc định
        });
}

function showCarts(items) {
    const cartContainer = document.getElementById('cart-container');

    // Clear existing content
    cartContainer.innerHTML = '';

    // Loop through each item and create HTML
    items.forEach(item => {
        const row = document.createElement('tr');
        row.className = 'c-cart-item';

        // Generate color options
        const colorOptions = item.availableOption.colors.map(color =>
            `<option value="${color}" ${item.Color === color ? 'selected' : ''}>Màu ${color}</option>`
        ).join('');

        // Generate size options
        const sizeOptions = item.availableOption.sizes.map(size =>
            `<option value="${size.sizeID}" ${item.sizeID === size.sizeID ? 'selected' : ''}>Size ${size.code}</option>`
        ).join('');

        // Format price (assuming it's in VND)
        const formattedPrice = new Intl.NumberFormat('vi-VN', {
            style: 'currency',
            currency: 'VND'
        }).format(item.price);

        row.innerHTML = `
            <td><input type="checkbox" class="c-cart-checkbox" checked></td>
            <td>
                <div class="c-product-info">
                    <img src="/uploads/${item.mainImage}" alt="${item.productName}" class="c-product-image">
                    <div>
                        <strong>${item.productName}</strong>
                        <div class="c-variant-container">
                            <select class="c-variant-select" data-item-id="${item.itemID}" data-attribute="color">
                                ${colorOptions}
                            </select>
                            <select class="c-variant-select" data-item-id="${item.itemID}" data-attribute="size">
                                ${sizeOptions}
                            </select>
                        </div>
                    </div>
                </div>
            </td>
            <td class="c-price">${formattedPrice}</td>
            <td class="c-actions">
                <div class="c-quantity-container">
                    <button class="c-quantity-btn">-</button>
                    <input type="number" class="c-quantity-input" value="1" min="1">
                    <button class="c-quantity-btn">+</button>
                </div>
            </td>
            <td class="c-item-total">${formattedPrice}</td>
            <td><button class="c-delete-single" data-item-id="${item.itemID}" title="Xóa"><i class="fa-solid fa-trash"></i></button></td>
        `;

        cartContainer.appendChild(row);
    });

    // Add event listeners for dynamic elements
    addCartEventListeners();
}

function addCartEventListeners() {
    // Quantity buttons
    document.querySelectorAll('.c-quantity-btn').forEach(button => {
        button.addEventListener('click', function() {
            const input = this.parentNode.querySelector('.c-quantity-input');
            let value = parseInt(input.value);

            if (this.textContent === '+' || this.textContent.includes('+')) {
                input.value = value + 1;
            } else {
                if (value > 1) {
                    input.value = value - 1;
                }
            }
            updateItemTotal(this.closest('.c-cart-item'));
        });
    });

    // Quantity input change
    document.querySelectorAll('.c-quantity-input').forEach(input => {
        input.addEventListener('change', function() {
            if (this.value < 1) this.value = 1;
            updateItemTotal(this.closest('.c-cart-item'));
        });
    });

    // Variant select changes
    document.querySelectorAll('.c-variant-select').forEach(select => {
        select.addEventListener('change', function() {
            const itemId = this.dataset.itemId;
            const attribute = this.dataset.attribute;
            const newValue = this.value;

            // Here you would typically make an API call to update the variant
            console.log(`Updating item ${itemId} - ${attribute} to ${newValue}`);
        });
    });

    // Delete buttons
    document.querySelectorAll('.c-delete-single').forEach(button => {
        button.addEventListener('click', function() {
            const itemId = this.dataset.itemId;
            // Here you would typically make an API call to remove the item
            console.log(`Removing item ${itemId}`);
            this.closest('.c-cart-item').remove();
        });
    });
}

function updateItemTotal(row) {
    const price = parseFloat(row.querySelector('.c-price').textContent.replace(/[^\d.]/g, ''));
    const quantity = parseInt(row.querySelector('.c-quantity-input').value);
    const total = price * quantity;

    row.querySelector('.c-item-total').textContent =
        new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(total);
}