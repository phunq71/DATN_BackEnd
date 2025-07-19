document.addEventListener('DOMContentLoaded', initPage);

async function initPage(){
    getOrderDetailIdFromUrl();
    const review= await getReview(orderDetailID);

    if(statusResponse===200){
        if(review.status==="NEW") {
            const item =review.data;
            initReviewItem(item);
            document.querySelector(".rv-submit-btn").innerText="Gửi bài đánh giá";
        }else if (review.status=== "EDIT" || review.status=== "VIEW"){
            const item ={
                imageItem: review.data.imageItem,
                productName: review.data.productName,
                color: review.data.color,
                size: review.data.size
            };
            initReviewItem(item);

            initReviewForm(review.data);
            document.querySelector(".rv-submit-btn").innerText="Sửa đáng giá";
            if(review.status === "VIEW") lockReviewForm();
            if(review.status === "EDIT") {
                const deleteBtn = document.createElement("button");
                deleteBtn.textContent="Xóa bài đánh giá";
                deleteBtn.className="rv-btn-del";
                deleteBtn.addEventListener('click', () => deleteReview(review.data.reviewID));
                document.querySelector(".rv-review-card").appendChild(deleteBtn);

            }
        }
        // else if(review.status==="")
    }else if(statusResponse===403) {
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
    }

    // Gắn sự kiện click cho nút gửi đánh giá
    document.querySelector('.rv-submit-btn').addEventListener('click', async function(e) {
        e.preventDefault();
        if(review.status === "NEW") {
            // Lấy dữ liệu từ form
            const rating = rvCurrentRating;
            const content = document.querySelector('.rv-textarea').value.trim();

            // Validate form
            if (!validateReviewForm(rating, content)) {
                return; // Dừng nếu validate không thành công
            }

            try {
                await createReview();
            } catch (error) {
                console.error('Error submitting review:', error);
            }
        } else if(review.status ==="EDIT") {
            // Lấy dữ liệu từ form
            const rating = rvCurrentRating;
            const content = document.querySelector('.rv-textarea').value.trim();

            // Validate form
            if (!validateReviewForm(rating, content)) {
                return; // Dừng nếu validate không thành công
            }

            try {
                await updateReview();
            } catch (error) {
                console.error('Error submitting review:', error);
            }
        }
    });

// Hàm validate form
    function validateReviewForm(rating, content) {
        // Kiểm tra rating
        if (!rating || rating < 1 || rating > 5) {
            Swal.fire({
                icon: 'error',
                title: 'Thiếu thông tin',
                text: 'Vui lòng chọn số sao đánh giá từ 1 đến 5',
                confirmButtonColor: '#3085d6',
            });
            return false;
        }

        // Kiểm tra nội dung
        if (!content || content.length < 15) {
            Swal.fire({
                icon: 'error',
                title: 'Nội dung quá ngắn',
                html: 'Vui lòng nhập ít nhất <b>15 ký tự</b> cho phần nhận xét<br>Hiện tại: ' + content.length + '/15 ký tự',
                confirmButtonColor: '#3085d6',
            });
            return false;
        }

        return true; // Validate thành công
    }



    // Hàm gửi đánh giá (đã sửa từ code trước)
    async function createReview() {
        const rating = rvCurrentRating;
        const content = document.querySelector('.rv-textarea').value;
        const imageFiles = rvSelectedImages; // Sử dụng mảng đã lưu trữ ảnh

        // Tạo FormData
        const formData = new FormData();
        formData.append("orderDetailID", orderDetailID)
        formData.append('rating', rating);
        formData.append('content', content);

        // Thêm ảnh vào FormData (nếu có)
        if (imageFiles && imageFiles.length > 0) {
            imageFiles.forEach(file => {
                formData.append('images', file);
            });
        }

        try {
            // Hiển thị loading
            const swalInstance = Swal.fire({
                title: 'Đang gửi đánh giá...',
                allowOutsideClick: false,
                didOpen: () => {
                    Swal.showLoading();
                }
            });

            // Gọi API
            const response = await axios.post('/opulentia_user/review/create', formData, {
                headers: {
                    'Content-Type': 'multipart/form-data',
                }
            });

            // Đóng loading
            await swalInstance.close();

            // Thông báo thành công
            await Swal.fire({
                icon: 'success',
                title: 'Thành công!',
                text: 'Chân thành cảm ơn những đánh giá của bạn dành cho OPULENTIA',
                confirmButtonColor: '#3085d6',
            });
            resetReviewForm();
            await initPage();

            return response.data;
        } catch (error) {
            console.error('Lỗi khi gửi đánh giá:', error);
            let errorMessage = error.response?.data?.message || error.message || 'Có lỗi xảy ra khi gửi đánh giá';

            await Swal.fire({
                icon: 'error',
                title: 'Lỗi!',
                text: errorMessage,
                confirmButtonColor: '#3085d6',
            });
            throw error;
        }
    }

    //Xóa review
    async function deleteReview(reviewID) {
        console.log(reviewID);
        const confirmResult = await Swal.fire({
            icon: 'warning',
            title: 'Bạn có chắc muốn xóa không?',
            showCancelButton: true,
            confirmButtonColor: '#000000',
            cancelButtonColor: '#656565',
            confirmButtonText: 'Có, hãy xóa đi!',
            cancelButtonText: 'Hủy, không xóa nữa'
        });

        if (confirmResult.isConfirmed) {
            const deleted = await axios.delete("/opulentia_user/review/delete/" + reviewID)
                .then(response => {
                    console.log("Xóa: ", response.data);
                    return response.data;
                }).catch(error => {
                    console.log("Lỗi: ", error);
                    return false;
                });

            if (deleted) {
                await Swal.fire({
                    icon: 'success',
                    title: 'Đã xóa thành công',
                    timer: 4000,
                    timerProgressBar: true,
                    showConfirmButton: false
                });
                resetReviewForm();
                await initPage();
            } else {
                await Swal.fire({
                    icon: 'error',
                    title: 'Xóa thất bại',
                    confirmButtonColor: '#000000',
                });
            }
        }
    }

    async function updateReview(){
        const rating = rvCurrentRating;
        const content = document.querySelector('.rv-textarea').value;
        const insertedImages = rvSelectedImages; // Sử dụng mảng đã lưu trữ ảnh
        const deletedImages = deletedFiles;

        // Tạo FormData
        const formData = new FormData();
        formData.append("reviewID", review.data.reviewID);
        formData.append('rating', rating);
        formData.append('content', content);

        // Thêm ảnh vào FormData (nếu có)
        if (insertedImages && insertedImages.length > 0) {
            insertedImages.forEach(file => {
                formData.append('insertedImages', file);
            });
        }

        if(deletedImages && deletedImages.length>0) {
            deletedImages.forEach(imageName => {
                formData.append('deletedImages', imageName);
            })
        }


        try {
            // Hiển thị loading
            const swalInstance = Swal.fire({
                title: 'Đang gửi đánh giá...',
                allowOutsideClick: false,
                didOpen: () => {
                    Swal.showLoading();
                }
            });

            // Gọi API
            const response = await axios.put('/opulentia_user/review/update', formData, {
                headers: {
                    'Content-Type': 'multipart/form-data',
                }
            });

            // Đóng loading
            await swalInstance.close();

            // Thông báo thành công
            await Swal.fire({
                icon: 'success',
                title: 'Thành công!',
                text: 'Đánh giá của bạn đã được cập nhật! cảm ơn những đóng góp của bạn dành cho OPULENTIA',
                confirmButtonColor: '#3085d6',
            });
            resetReviewForm();
            await initPage();

            return response.data;
        } catch (error) {
            console.error('Lỗi khi gửi đánh giá:', error);
            let errorMessage = error.response?.data?.message || error.message || 'Có lỗi xảy ra khi gửi đánh giá';

            await Swal.fire({
                icon: 'error',
                title: 'Lỗi!',
                text: errorMessage,
                confirmButtonColor: '#3085d6',
            });
            throw error;
        }
    }

}


let orderDetailID;

const imageItem= document.getElementById("imageItem");
const productName= document.getElementById("productName");
const color= document.getElementById("color");
const size =document.getElementById("size");


function getOrderDetailIdFromUrl() {
    const path = window.location.pathname;
    const parts = path.split('/');
    orderDetailID= parts[parts.length - 1];
}

let statusResponse=0;
function getReview(orderDetailId){
    return axios.get("/opulentia_user/review/get/"+orderDetailId)
        .then (response => {
            console.log(response.data);
            console.log(response.status);
            statusResponse = response.status;
            return response.data;
        })
        .catch(error => {
            console.log("lỗi", error);

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
            return null;
        })
}

function initReviewItem(item){
    imageItem.src= "/uploads/"+item.imageItem;
    productName.textContent= item.productName;
    color.textContent= item.color;
    size.textContent= item.size;
}

function initReviewForm(review) {
    // 1. Hiển thị rating (sao)
    const rvStars = document.querySelectorAll('.rv-star');
    if (review.rating && review.rating > 0) {
        rvCurrentRating = review.rating;
        rvStars.forEach((s, index) => {
            if (index < review.rating) {
                s.classList.add('rv-active');
                s.style.color = '#ffc107';
            } else {
                s.classList.remove('rv-active');
                s.style.color = '#ccc';
            }
        });
    }

    // 2. Hiển thị ảnh đã tải lên (nếu có)
    const rvImagePreviewContainer = document.querySelector('.rv-image-preview-container');
    if (review.reviewImages && review.reviewImages.length > 0) {
        review.reviewImages.forEach((imageUrl, index) => {
            // Tạo wrapper cho ảnh và nút xóa
            const wrapper = document.createElement('div');
            wrapper.className = 'rv-image-preview-wrapper';

            // Tạo ảnh preview
            const img = document.createElement('img');
            img.src ="/uploads/"+ imageUrl; // Giả sử imageUrl là đường dẫn đầy đủ
            img.className = 'rv-image-preview';

            // Tạo nút xóa
            const deleteBtn = document.createElement('button');
            deleteBtn.className = 'rv-delete-image-btn';
            deleteBtn.innerHTML = '×';
            deleteBtn.addEventListener('click', function () {
                const fileName = img.src.split('/').pop();
                addToDeletedFiles(fileName);
                console.log("Ảnh cũ được thêm vào deletedFiles:", fileName);

                wrapper.remove();
                rvUploadedImages--;

                // Hiển thị lại nút thêm ảnh nếu chưa đạt tối đa
                if (rvUploadedImages < rvMaxImages) {
                    rvAddImageBtn.classList.remove('rv-hidden');
                }
            });

            // Thêm ảnh và nút xóa vào wrapper
            wrapper.appendChild(img);
            wrapper.appendChild(deleteBtn);

            // Thêm wrapper vào container
            rvImagePreviewContainer.insertBefore(wrapper, rvAddImageBtn);
            rvUploadedImages++;

            if (rvUploadedImages >= rvMaxImages) {
                rvAddImageBtn.classList.add('rv-hidden');
            }
        });
    }

    // 3. Hiển thị nội dung đánh giá
    const textarea = document.querySelector('.rv-textarea');
    if (review.reviewContent) {
        textarea.value = review.reviewContent;
    }

}

function lockReviewForm() {
    // 1. Khóa chức năng rating (sao)
    const rvStars = document.querySelectorAll('.rv-star');
    rvStars.forEach(star => {
        star.style.pointerEvents = 'none'; // Vô hiệu hóa tương tác
        star.style.cursor = 'default'; // Đổi con trỏ chuột
    });

    // 2. Khóa chức năng thêm ảnh
    const rvAddImageBtn = document.getElementById('rvAddImageBtn');
    if (rvAddImageBtn) {
        rvAddImageBtn.style.display = 'none'; // Ẩn nút thêm ảnh
    }

    // 3. Khóa chức năng xóa ảnh đã tải lên
    const deleteButtons = document.querySelectorAll('.rv-delete-image-btn');
    deleteButtons.forEach(button => {
        button.style.display = 'none'; // Ẩn nút xóa ảnh
    });

    // 4. Khóa textarea nhận xét
    const textarea = document.querySelector('.rv-textarea');
    if (textarea) {
        textarea.readOnly = true; // Chỉ cho phép đọc
        textarea.style.cursor = 'default'; // Đổi con trỏ chuột
        textarea.style.backgroundColor = '#f5f5f5'; // Màu nền khác để thể hiện trạng thái disabled
    }

    // 5. Ẩn nút gửi đánh giá
    const submitBtn = document.querySelector('.rv-submit-btn');
    if (submitBtn) {
        submitBtn.style.display = 'none';
    }

    // 6. Thêm thông báo form đã bị khóa (tuỳ chọn)
    const formContainer = document.querySelector('.rv-review-card');
    if (formContainer) {
        console.log("thêm thông báo khóa")
        const lockMessage = document.createElement('div');
        lockMessage.className = 'rv-lock-message';
        lockMessage.textContent = 'Bạn không thể sửa đánh giá sau 15 ngày kể từ ngày nhận hàng thành công';
        lockMessage.style.marginTop = '15px';
        lockMessage.style.color = '#888';
        lockMessage.style.fontStyle = 'italic';
        formContainer.appendChild(lockMessage);
    }
}

// Hàm reset form
function resetReviewForm() {
    // Reset rating
    rvCurrentRating = 0;
    rvStars.forEach(star => star.classList.remove('rv-active'));

    // Reset content
    document.querySelector('.rv-textarea').value = '';

    // Reset ảnh
    const previewContainer = document.querySelector('.rv-image-preview-container');
    const previewWrappers = previewContainer.querySelectorAll('.rv-image-preview-wrapper');
    previewWrappers.forEach(wrapper => wrapper.remove());
    rvSelectedImages = [];
    rvUploadedImages = 0;
    rvAddImageBtn.classList.remove('rv-hidden')

    document.querySelector(".rv-review-card").removeChild(document.querySelector(".rv-btn-del"));

}





