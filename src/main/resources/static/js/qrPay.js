
    // Countdown timer
    function startTimer(duration, display) {
    let timer = duration, minutes, seconds;
    const interval = setInterval(function () {
    minutes = parseInt(timer / 60, 10);
    seconds = parseInt(timer % 60, 10);

    minutes = minutes < 10 ? "0" + minutes : minutes;
    seconds = seconds < 10 ? "0" + seconds : seconds;

    display.textContent = minutes + ":" + seconds;

    if (--timer < 0) {
    clearInterval(interval);
    display.textContent = "Hết hạn";
    document.querySelector('.q-status').className = 'q-status q-expired';
    document.querySelector('.q-status').textContent = 'Mã đã hết hạn';
}
}, 1000);
}

    // Copy order ID function
    function copyOrderId() {
    const orderId = 'DH20230615001';
    navigator.clipboard.writeText(orderId).then(() => {
    // alert('Đã copy ID đơn hàng: ' + orderId);
}).catch(err => {
    // console.error('Lỗi khi copy: ', err);
});
}

    // Toggle bank transfer section
    function toggleBankTransfer() {
    const bankTransfer = document.getElementById('bankTransfer');
    const btn = document.querySelector('.q-alternative-btn');

    bankTransfer.classList.toggle('active');
    if (bankTransfer.classList.contains('active')) {
    btn.innerHTML = 'Phương án thanh toán khác ▲';
} else {
    btn.innerHTML = 'Phương án thanh toán khác ▼';
}
}

    window.onload = function () {
    const fiveMinutes = 60 * 5,
    display = document.querySelector('#q-countdown');
    startTimer(fiveMinutes, display);
};



    // Simulate payment status check
    // document.getElementById('q-checkStatus').addEventListener('click', function() {
    //   alert('Đang kiểm tra trạng thái thanh toán...');
    // });


    document.addEventListener("DOMContentLoaded", () => {
        const intervalId = setInterval(async () => {
            const orderIdElement = document.getElementById("orderId");
            if (!orderIdElement) return;
            console.log("Bắt đầu tìm orderId")
            const maDH = orderIdElement.textContent;
            console.log(maDH)
            const statusDiv = document.querySelector(".q-status");

            axios.get(`/opulentia_user/result?maDH=${maDH}`, {
                withCredentials: true
            })
                .then(response => {
                    if (response.data === true) {
                        clearInterval(intervalId); // Dừng polling

                        // Cập nhật giao diện
                        statusDiv.textContent = "Đã thanh toán 👌";
                        statusDiv.classList.remove("q-pending");
                        statusDiv.classList.add("q-success"); // nhớ định nghĩa css q-success (xanh)

                        // Redirect sau 2 giây
                        setTimeout(() => {
                            window.location.href = "https://datn1-eubngvadfdb4crgk.southeastasia-01.azurewebsites.net/opulentia_user/allOrder";
                        }, 2000);
                    } else {
                        console.log("Chưa thanh toán...");
                    }
                })
                .catch(error => {
                    console.error("Lỗi khi gọi API:", error);
                });
        }, 1000); // check mỗi giây thay vì 500ms cho nhẹ server
    });
