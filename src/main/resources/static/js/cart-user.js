const token = document.querySelector('meta[name="_csrf"]').getAttribute('content');
const header = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

document.addEventListener('DOMContentLoaded', async function() {

    itemCarts = await getItemCartsFromServer();
    console.log('cart-user', itemCarts);
});

let itemCarts=[];
let carts=[];

function getItemCartsFromServer(){
    return axios.get('/opulentia/user/rest/cart', {
        headers: {
            [header]: token
        }
    }).then(response =>{
        return response.data;
        }
    ).catch(error =>{
        console.log(error);
        return [];
    });
}
