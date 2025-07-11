// <<<<<<< HEAD
// const token = document.querySelector('meta[name="_csrf"]').getAttribute('content');
// const header = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');
// =======
// <<<<<<< HEAD
// // const token = document.querySelector('meta[name="_csrf"]').getAttribute('content');
// // const header = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');
//
// //
// // axios.interceptors.request.use(config => {
// //     const token = localStorage.getItem("accessToken") || sessionStorage.getItem("accessToken");
// //
// //     if (token) {
// //         config.headers.Authorization = `Bearer ${token}`;
// //         console.log("📤 Gửi request với token:", token); // 👈 In ra token ở đây
// //     } else {
// //         console.warn("⚠️ Không tìm thấy token trong localStorage hoặc sessionStorage");
// //     }
// //
// //     return config;
// // }, error => {
// //     return Promise.reject(error);
// // });
//
//
// // axios.post("/api/auth/login", { email, password }, { withCredentials: true });
// // axios.defaults.withCredentials = true; // nếu dùng toàn cục

let carts=[];

async function isLoggedIn() {
    try {
        const response = await fetch("/api/auth/check-login", {
            method: "GET",
            credentials: "include" // quan trọng: để gửi cookie kèm theo
        });
        return response.ok; // true nếu 200, false nếu 401
    } catch (err) {
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
    const favoriteButtons = document.querySelectorAll(".favorite-btn");

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