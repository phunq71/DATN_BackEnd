// <<<<<<< HEAD
// // document.addEventListener('DOMContentLoaded', async function() {
// //     getCartsFromLocalStorage();
// //     miniCarts = await getMiniCart(carts);
// //     console.log('miniCart',miniCarts);
// //     updateMiniCart();
// // });
// //
// // document.addEventListener('cartUpdated', async function() {
// //     console.log("bắt sự kiện cập nhật");
// //     miniCarts = await getMiniCart(carts);
// //     updateMiniCart();
// // });
// //
// // let miniCarts=[];
// //
// // function getCartsFromLocalStorage(){
// //     let dataCartString= localStorage.getItem('carts');
// //     let dataCarts= JSON.parse(dataCartString);
// //     if(!dataCarts || dataCarts.length>0)
// //
// //     {
// //         carts= JSON.parse(localStorage.getItem('carts'));
// //     }
// //
// // }
// //
// // function getMiniCart(carts){
// //         return axios.post("/opulentia/rest/cart/miniCart", carts)
// //             .then(response => {
// //                 console.log(response.data);
// //                 return response.data; // ✅ TRẢ DỮ LIỆU RA NGOÀI
// //             })
// //             .catch(error => {
// //                 console.log(error);
// //                 return []; // 👈 nếu lỗi thì trả mảng rỗng hoặc giá trị mặc định
// //             });
// // }
// //
// //
// // function updateMiniCart() {
// //     const cartItems = document.getElementById("cart-items");
// //     cartItems.innerHTML = "";
// //
// //     // Kiểm tra nếu giỏ hàng trống
// //     if (miniCarts.length === 0) {
// //         const emptyRow = document.createElement("tr");
// //         emptyRow.innerHTML = `
// //             <td colspan="2" class="empty-minicart-message">
// //                 Giỏ hàng trống
// //             </td>
// //         `;
// //         cartItems.appendChild(emptyRow);
// //     } else {
// //         // Hiển thị sản phẩm nếu có
// //         miniCarts.forEach((item) => {
// //             const row = document.createElement("tr");
// //             row.innerHTML = `
// //                 <td>
// //                     <div class="cart-item-content">
// //                         <img src="/uploads/${item.image}" alt="${item.name}" />
// //                         <span>${item.name}</span>
// //                     </div>
// //                 </td>
// //                 <td>${formatPrice(item.price)}</td>
// //             `;
// //             cartItems.appendChild(row);
// //         });
// //     }
// //
// //     // Cập nhật số lượng sản phẩm
// //     document.querySelector('.cart-count-badge').innerText = miniCarts.length;
// // }
// //
// // function formatPrice(price) {
// //     return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(price);
// // }
// =======

document.addEventListener('DOMContentLoaded', async function() {
    auth = await isLogin();
    if(!auth) {
        console.log("chưa có đăng nhập nè");
        getCartsFromLocalStorage();
        miniCarts = await getMiniCart(carts);
        updateMiniCart();
    }
    else {
        console.log("đăng nhập rồi nè")
        const itemCarts= await getItemCartsFromServer();
        carts = getCartsFromItemCarts(itemCarts);
        miniCarts = await getMiniCart(carts);
        updateMiniCart();
    }
});

let auth= false;

document.addEventListener('cartUpdated', async function() {
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

function isLogin(){
    return axios.get("/opulentia/isLogin").then(response => {
        return response.data;
    }).catch(error => {
        console.log(error);
        return false;
    });
}

function getItemCartsFromServer(){
    return axios.get('/opulentia/user/rest/cart')
        .then(response =>{
            return response.data;
        }
    ).catch(error =>{
        console.log(error);
        return [];
    });
}

function getCartsFromItemCarts(itemCarts) {
    const result = [];

    for (const group of itemCarts) {
        for (const item of group) {
            if (item.quantity > 0) {
                result.push({
                    customerID: null,
                    itemID: item.itemID,
                    quantity: item.quantity,
                    latestDate: item.latestDate
                });
            }
        }
    }

    return result;
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

//
// >>>>>>> 1551dc25cb72b8eb01b7a7c670ac69558d5062f7
