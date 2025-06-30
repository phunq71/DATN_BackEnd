document.addEventListener('DOMContentLoaded', function() {
    getCartsFromLoalStorage();
    console.log(carts);
    getItem(carts);
});

let carts =[];

/*
* Lấy thông tin giỏ hàng từ LocalStorage
* Nếu chưa có thì lấy dữ liệu mẫu để test -- sẽ bỏ sau
* */
function getCartsFromLoalStorage(){
    //Dữ liệu mẫu
    carts= [
        {customerID: null, itemID: 7, quantity: 2, latestDate: new Date().toISOString()},
        {customerID: null, itemID: 21, quantity: 3, latestDate: new Date().toISOString()},
        {customerID: null, itemID: 5, quantity: 3, latestDate: new Date().toISOString()}
    ]

    let dataCarts= localStorage.getItem('carts');

    if(dataCarts){
        localStorage.setItem('carts', JSON.stringify(carts));
    }
    else{
        carts= JSON.parse(localStorage.getItem('carts'));
    }

}


function getItem(carts){
    axios.post("/opulentia/rest/cart", carts)
        .then(response => {
            console.log(response.data);
        }).catch(error => {
            console.log(error)
        }

    )
}
