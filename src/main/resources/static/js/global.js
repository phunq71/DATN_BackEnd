// const token = document.querySelector('meta[name="_csrf"]').getAttribute('content');
// const header = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

//
// axios.interceptors.request.use(config => {
//     const token = localStorage.getItem("accessToken") || sessionStorage.getItem("accessToken");
//
//     if (token) {
//         config.headers.Authorization = `Bearer ${token}`;
//         console.log("📤 Gửi request với token:", token); // 👈 In ra token ở đây
//     } else {
//         console.warn("⚠️ Không tìm thấy token trong localStorage hoặc sessionStorage");
//     }
//
//     return config;
// }, error => {
//     return Promise.reject(error);
// });


// axios.post("/api/auth/login", { email, password }, { withCredentials: true });
// axios.defaults.withCredentials = true; // nếu dùng toàn cục

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
    const protectedPrefixes = ["/edit-profile"]; // Link cần bảo vệ, khi chưa login thì redirect

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










