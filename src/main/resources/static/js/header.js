document.addEventListener('DOMContentLoaded', async function() {
    getCartsFromLocalStorage();
    miniCarts = await getMiniCart(carts);
    console.log('miniCart',miniCarts);
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
                console.log(response.data);
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

    document.querySelector('.cart-count-badge').innerText = miniCarts.length;
}

