document.addEventListener('DOMContentLoaded', async function() {
    getCartsFromLocalStorage();
    miniCarts = await getMiniCart(carts);
    // console.log('miniCart',miniCarts);
    updateMiniCart();
});

document.addEventListener('cartUpdated', async function() {
    // console.log("bắt sự kiện cập nhật");
    miniCarts = await getMiniCart(carts);
    updateMiniCart();
});

let miniCarts=[];

function getCartsFromLocalStorage(){
    let dataCartString= localStorage.getItem('carts');
    let dataCarts= JSON.parse(dataCartString);
    if(dataCarts.length>0)

    {
        carts= JSON.parse(localStorage.getItem('carts'));
    }

}

function getMiniCart(carts){
        return axios.post("/opulentia/rest/cart/miniCart", carts)
            .then(response => {
                // console.log(response.data);
                return response.data; // ✅ TRẢ DỮ LIỆU RA NGOÀI
            })
            .catch(error => {
                console.log(error);
                return []; // 👈 nếu lỗi thì trả mảng rỗng hoặc giá trị mặc định
            });
}


function updateMiniCart() {
    const cartItems = document.getElementById("cart-items");
    cartItems.innerHTML = "";

    // Kiểm tra nếu giỏ hàng trống
    if (miniCarts.length === 0) {
        const emptyRow = document.createElement("tr");
        emptyRow.innerHTML = `
            <td colspan="2" class="empty-minicart-message">
                Giỏ hàng trống
            </td>
        `;
        cartItems.appendChild(emptyRow);
    } else {
        // Hiển thị sản phẩm nếu có
        miniCarts.forEach((item) => {
            const row = document.createElement("tr");
            row.innerHTML = `
                <td>
                    <div class="cart-item-content">
                        <img src="/uploads/${item.image}" alt="${item.name}" />
                        <span>${item.name}</span>
                    </div>
                </td>
                <td>${formatPrice(item.price)}</td>
            `;
            cartItems.appendChild(row);
        });
    }

    // Cập nhật số lượng sản phẩm
    document.querySelector('.cart-count-badge').innerText = miniCarts.length;
}

function formatPrice(price) {
    return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(price);
}


