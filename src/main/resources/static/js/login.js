document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("loginForm");

    if (!form) return;

    form.addEventListener("submit", async (e) => {
        e.preventDefault(); // Ngăn reload nhưng vẫn giữ kiểm tra hợp lệ

        if (!form.reportValidity()) return;

        const email = document.getElementById("email").value.trim();
        const password = document.getElementById("password").value;
        const rememberMe = document.getElementById("remember-me").checked;

        try {
            await axios.post("/api/auth/login", {
                email,
                password,
                rememberMe
            }, {
                withCredentials: true
            });

            Swal.fire({
                icon: 'success',
                title: 'Đăng nhập thành công!',
                showConfirmButton: false,
                timer: 1500,
                timerProgressBar: true
            }).then(async () => {
                let redirectUrl = localStorage.getItem("redirectAfterLogin") || "/index";
                if(redirectUrl ==="/opulentia_user/checkout"){
                    redirectUrl="/opulentia/cart";
                    await mergeCartLocalStorageAndServer();
                    clearCartLocalStorage();
                    document.dispatchEvent(new Event('cartUpdated'));
                }

                    localStorage.removeItem("redirectAfterLogin");
                    window.location.href = redirectUrl;


            });

        } catch (error) {
            console.error("❌ Đăng nhập thất bại:", error);

            Swal.fire({
                icon: 'error',
                title: 'Đăng nhập thất bại',
                text: 'Email hoặc mật khẩu không đúng. Vui lòng kiểm tra lại.',
                confirmButtonText: 'Thử lại',
                timer: 2500,
                timerProgressBar: true,
                customClass: {
                    confirmButton: 'swal2-confirm-dark'
                },
                didOpen: () => {
                    const style = document.createElement('style');
                    style.innerHTML = `
                        .swal2-confirm-dark {
                            background-color: #000 !important;
                            color: #fff !important;
                            border: none !important;
                            box-shadow: none !important;
                            border-radius: 6px;
                        }
                        .swal2-confirm-dark:hover {
                            background-color: #222 !important;
                        }
                    `;
                    document.head.appendChild(style);
                }
            });
        }
    });


});


document.querySelectorAll('.oauth-link').forEach(link => {
    link.addEventListener("click", () => {
        const remember = localStorage.getItem("rememberMeChecked") === "true";
        document.cookie = `rememberMe=${remember}; path=/; max-age=300`;
    });
});

document.querySelectorAll("a[href^='/oauth2']").forEach(anchor => {
    anchor.addEventListener("click", () => {
        const redirectUrl = localStorage.getItem("redirectAfterLogin");
        if (redirectUrl) {
            document.cookie = `redirectAfterLogin=${encodeURIComponent(redirectUrl)}; path=/`;
        }
    });
});



