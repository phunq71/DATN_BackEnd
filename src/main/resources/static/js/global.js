

let carts=[];

async function isLoggedIn() {
    let log = "🟡 [isLoggedIn] Bắt đầu kiểm tra trạng thái đăng nhập...\n";

    try {
        // Kiểm tra access token còn hạn
        log += "🔍 Gọi /api/auth/check-login...\n";
        await axios.get("/api/auth/check-login", { withCredentials: true });

        log += "✅ Access token hợp lệ. Đã đăng nhập.\n";
        localStorage.setItem("isLoggedInLog", log);
        localStorage.removeItem("redirectAfterLogin");
        return true;
    } catch (err) {
        const status = err.response?.status;
        log += `❌ Access token không hợp lệ (status: ${status})\n`;

        if (status === 401 || status === 302) {
            const rememberMe = localStorage.getItem("rememberMeChecked") === "true";
            log += `🔁 Cố gắng refresh token (rememberMe = ${rememberMe})...\n`;

            try {
                await axios.post("/api/auth/refresh", { rememberMe }, {
                    withCredentials: true
                });

                log += "✅ Refresh token thành công. Gọi lại check-login...\n";

                await axios.get("/api/auth/check-login", {
                    withCredentials: true
                });

                log += "✅ Đăng nhập lại thành công sau khi refresh.\n";
                localStorage.setItem("isLoggedInLog", log);
                return true;
            } catch (refreshErr) {
                const refreshStatus = refreshErr.response?.status;
                log += `❌ Refresh token thất bại (status: ${refreshStatus})\n`;
                localStorage.setItem("isLoggedInLog", log);
                return false;
            }
        }

        // Lỗi khác (mạng, server...)
        log += "❌ Lỗi không xác định khi kiểm tra đăng nhập.\n";
        localStorage.setItem("isLoggedInLog", log);
        return false;
    }
}




document.addEventListener("DOMContentLoaded", () => {
    const protectedPrefixes = ["/opulentia_user"]; // Link cần bảo vệ, khi chưa login thì redirect

    document.querySelectorAll("a[href]").forEach(anchor => {
        anchor.addEventListener("click", async (event) => {
            const href = anchor.getAttribute("href");

            // Bỏ qua nếu là link ngoài hoặc là trang /auth
            if (!href.startsWith("/") || href.startsWith("/auth")|| href.startsWith("/oauth2") ) return;

            const loggedIn = await isLoggedIn();
            if (!loggedIn) {
                // Luôn lưu lại URL định vào, nếu chưa đăng nhập
                localStorage.setItem("redirectAfterLogin", href);
                console.log("📌 Saved from <a> click:", href);

                const needsAuth = protectedPrefixes.some(path => href.startsWith(path));
                if (needsAuth) {
                    event.preventDefault(); // Ngăn chuyển trang mặc định
                    window.location.href = "/auth"; // Chuyển sang trang login

                }
                // 👉 Nếu không thuộc protected thì cho đi tiếp, không redirect
            }
        });
    });
});


function getCookie(name) {
    const value = `; ${document.cookie}`;
    const parts = value.split(`; ${name}=`);
    if (parts.length === 2) return parts.pop().split(';').shift();
}

// Hàm xử lý flag
function checkLoginFlag() {
    const flag = getCookie("flag");

    if (flag === "true") {
        // Hiển thị thông báo
        Swal.fire({
            icon: 'success',
            title: 'Đăng nhập thành công!',
            showConfirmButton: false,
            timer: 1500,
            timerProgressBar: true
        });

        // Reset flag về false (bằng cách ghi đè lại cookie)
        document.cookie = "flag=false; path=/";
    }
}

// Gọi hàm sau khi trang load
window.addEventListener("DOMContentLoaded", checkLoginFlag);
// =======
// const token = document.querySelector('meta[name="_csrf"]').getAttribute('content');
// const header = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');
// // >>>>>>> d64357fc32bca51dd3229c1e30dd20b5c1cb91fe
// >>>>>>> 1551dc25cb72b8eb01b7a7c670ac69558d5062f7


// thêm yêu thích
document.addEventListener("DOMContentLoaded", async function () {
    const favoriteButtons = document.querySelectorAll(".favorite-btn, .favorite-btnt");

    favoriteButtons.forEach(button => {
        button.addEventListener("click", async function (event) {
            event.preventDefault();

            const icon = this.querySelector("i");
            const productId = this.dataset.idpro;
            const isFavorite = icon.classList.contains("fas");

            if (isFavorite) {
                axios.delete(`/opulentia_user/api/favorites/${productId}`)
                    .then(response => {
                        console.log("🗑️ Đã xóa khỏi yêu thích");
                        icon.classList.remove("fas", "text-dark");
                        icon.classList.add("far");
                    })
                    .catch(error => {
                        console.error("❌ Lỗi khi xóa yêu thích:", error);
                    });
            } else {
                const loggedIn = await isLoggedIn(); // ✅ dùng được await
                if (!loggedIn) {
                    window.location.href = "/auth";
                    return;
                }

                axios.post('/opulentia_user/api/favorites', null, {
                    params: { idPro: productId },
                    withCredentials: true
                })
                    .then(response => {
                        console.log("❤️ Đã thêm vào yêu thích");
                        icon.classList.remove("far");
                        icon.classList.add("fas", "text-dark");
                    })
                    .catch(error => {
                        console.error("❌ Lỗi khi thêm yêu thích:", error);
                    });
            }
        });
    });
});

// Gọi khi web load
window.addEventListener('DOMContentLoaded', () => {
    attemptAutoLogin();
});

async function attemptAutoLogin() {
    try {
        const res = await fetch('/api/auth/check-auth', {
            method: 'GET',
            credentials: 'include'
        });

        if (res.ok) {
            console.log('✅ Đã tự đăng nhập lại thành công');
        } else {
            console.log('❌ Không thể đăng nhập lại');
        }
    } catch (e) {
        console.error('⚠️ Lỗi khi kiểm tra đăng nhập:', e);
    }
}

document.addEventListener('DOMContentLoaded', async function () {
   const isMergeCart = getCookie("isMergeCart");
   deleteCookie("isMergeCart");
  if(isMergeCart){
      await mergeCartLocalStorageAndServer();
      clearCartLocalStorage();
      document.dispatchEvent(new Event('cartUpdated'));
      window.location.href = "/opulentia/cart";
  }
});


function deleteCookie(name) {
    document.cookie = `${name}=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;`;
}


async function mergeCartLocalStorageAndServer(){
    const carts= JSON.parse(localStorage.getItem('carts'));
    await checkMergeCart(carts);

}

function checkMergeCart(carts){
    return axios.post("/opulentia_user/mergeCartLocalStorageAndServer", carts)
        .then(response => {
            return response.data;
        }).catch(error => {
            console.log(error);
            return false;
        })
}

function clearCartLocalStorage(){
    localStorage.removeItem('carts');
}



const replaceUploadImages = () => {
    document.querySelectorAll('img').forEach(img => {
        const originalSrc = img.getAttribute('src');
        if (originalSrc && originalSrc.startsWith('/uploads/')) {
            const filename = originalSrc.split('/uploads/')[1];
            img.src = `https://phudatn.blob.core.windows.net/uploads/${filename}`;
        }
    });
};

document.addEventListener("DOMContentLoaded", () => {
    console.log("Image fix script is running...");
    replaceUploadImages();

    // Theo dõi thay đổi DOM
    const observer = new MutationObserver(mutations => {
        mutations.forEach(() => {
            replaceUploadImages(); // Kiểm tra và thay lại mỗi khi có element mới
        });
    });

    observer.observe(document.body, {
        childList: true,
        subtree: true
    });
});


