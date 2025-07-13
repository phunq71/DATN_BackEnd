document.addEventListener('DOMContentLoaded', function () {
    // Lấy các phần tử DOM

    const modal = document.getElementById('cp-changePasswordModal');
    const closeBtn = document.querySelector('.cp-close');
    const checkPasswordBtn = document.getElementById('cp-checkPasswordBtn');
    const oldPasswordInput = document.getElementById('cp-oldPassword');
    const newPasswordSection = document.getElementById('cp-newPasswordSection');
    const newPasswordInput = document.getElementById('cp-newPassword');
    const confirmPasswordInput = document.getElementById('cp-confirmPassword');
    const changePasswordBtnForm = document.getElementById('cp-changePasswordBtn');
    const passwordForm = document.getElementById('cp-passwordForm');
    const changePasswordBtn = document.getElementById('changePassword');


    let isDraggingPassword = false;
    // Mở modal khi click vào nút Đổi mật khẩu
    changePasswordBtn.addEventListener('click', function (e) {
        // account phải là biến toàn cục hoặc lấy từ đâu đó

        e.preventDefault(); // Ngăn không cho thẻ a thực hiện hành vi mặc định
        modal.style.display = 'block';


    });



    // Đóng modal khi click vào nút đóng
    closeBtn.addEventListener('click', function () {
        modal.style.display = 'none';
        resetForm();
    });

    // Đóng modal khi click bên ngoài modal
    window.addEventListener('click', function (event) {
        // Chỉ đóng modal nếu không phải đang kéo chọn text và click bên ngoài modal
        if (event.target === modal && !isDraggingPassword) {
            modal.style.display = 'none';
            resetForm();
        }
    });
// Thêm sự kiện cho việc kéo chọn text
    [oldPasswordInput, newPasswordInput, confirmPasswordInput].forEach(input => {
        input.addEventListener('mousedown', function () {
            isDraggingPassword = true;
        });

        input.addEventListener('mouseup', function () {
            // Delay một chút để tránh conflict với sự kiện click
            setTimeout(() => {
                isDraggingPassword = false;
            }, 100);
        });
    });

    // Kiểm tra mật khẩu cũ
    checkPasswordBtn.addEventListener('click', function () {
        const oldPassword = oldPasswordInput.value.trim();

        if (!oldPassword) {
            showError(oldPasswordInput, 'cp-oldPasswordFeedback', 'Vui lòng nhập mật khẩu hiện tại');
            return;
        }

        // Gửi yêu cầu kiểm tra mật khẩu cũ đến server
        checkOldPassword(oldPassword);
    });

    // Validate mật khẩu mới khi nhập
    newPasswordInput.addEventListener('input', function () {
        validateNewPassword();
        validateConfirmPassword();
        updatePasswordStrength();
        updateChangePasswordBtn();
    });

    // Validate xác nhận mật khẩu khi nhập
    confirmPasswordInput.addEventListener('input', function () {
        validateConfirmPassword();
        updateChangePasswordBtn();
    });

    // Gửi form đổi mật khẩu
    passwordForm.addEventListener('submit', function (e) {
        e.preventDefault();

        if (changePasswordBtnForm.disabled) return;

        const oldPassword = oldPasswordInput.value.trim();
        const newPassword = newPasswordInput.value.trim();
        const confirmPassword = confirmPasswordInput.value.trim();

        // Gửi yêu cầu đổi mật khẩu đến server
        changePassword(oldPassword, newPassword, confirmPassword);
    });

    // Hàm kiểm tra mật khẩu cũ với server
   function checkOldPassword(password) {

        // Code thực tế sẽ giống như sau (bỏ comment khi triển khai):
        axios.post("/opulentia_user/checkPassword", {password: password})
        .then(response => {
            if (response.data) {
                showSuccess(oldPasswordInput, 'cp-oldPasswordFeedback');
                newPasswordSection.style.display = 'block';
                checkPasswordBtn.style.display = 'none';
            } else {
                showError(oldPasswordInput, 'cp-oldPasswordFeedback','Mật khẩu hiện tại không đúng');
            }
        })
        .catch(error => {
            console.log(error)
            showError(oldPasswordInput, 'cp-oldPasswordFeedback', 'Lỗi kết nối server');
        });
    }

    // Hàm đổi mật khẩu
    function changePassword(oldPassword, newPassword, confirmPassword) {
        // // Đây là nơi bạn sẽ gửi AJAX request đến server để đổi mật khẩu
        //
        // // Hiển thị loading
        // changePasswordBtnForm.disabled = true;
        // changePasswordBtnForm.textContent = 'Đang xử lý...';
        //
        // // Giả lập request đến server
        // setTimeout(function () {
        //     alert('Đổi mật khẩu thành công!');
        //     modal.style.display = 'none';
        //     resetForm();
        //
        //     changePasswordBtnForm.disabled = false;
        //     changePasswordBtnForm.textContent = 'Đổi mật khẩu';
        // }, 1000);

        // Code thực tế sẽ giống như sau (bỏ comment khi triển khai):
        axios.put("/opulentia_user/changePassword", {
            oldPassword: oldPassword,
            newPassword: newPassword,
            confirmPassword: confirmPassword
        })
        .then(response => {
            if (response.data) {
                Swal.fire({
                    title: 'Đổi mật khẩu thành công',
                    icon: 'success',
                    timer: 1500,
                    timerProgressBar: true,
                    confirmButtonText: 'OK',
                    confirmButtonColor: '#000000'
                });
                modal.style.display = 'none';
                resetForm();
            } else {
                Swal.fire({
                    title: 'Đổi mật khẩu thành công',
                    icon: 'error',
                    timer: 5000,
                    timerProgressBar: true,
                    confirmButtonText: 'OK',
                    confirmButtonColor: '#000000'
                });
            }
        })
        .catch(error => {
            console.log(error)
            alert('Lỗi kết nối server');
        });

    }

    // Hàm validate mật khẩu mới
    function validateNewPassword() {
        const password = newPasswordInput.value.trim();
        const specialChars = "!@#$%^&*";

        if (!password) {
            showError(newPasswordInput, 'cp-newPasswordFeedback', 'Vui lòng nhập mật khẩu mới');
            return false;
        }

        if (password.length < 8) {
            showError(newPasswordInput, 'cp-newPasswordFeedback', 'Mật khẩu phải có ít nhất 8 ký tự');
            return false;
        }

        if (!/[A-Z]/.test(password)) {
            showError(newPasswordInput, 'cp-newPasswordFeedback', 'Mật khẩu phải có ít nhất 1 chữ hoa');
            return false;
        }

        if (!/[a-z]/.test(password)) {
            showError(newPasswordInput, 'cp-newPasswordFeedback', 'Mật khẩu phải có ít nhất 1 chữ thường');
            return false;
        }

        if (!/[0-9]/.test(password)) {
            showError(newPasswordInput, 'cp-newPasswordFeedback', 'Mật khẩu phải có ít nhất 1 số');
            return false;
        }

        let hasSpecialChar = false;
        for (let char of specialChars) {
            if (password.includes(char)) {
                hasSpecialChar = true;
                break;
            }
        }

        if (!hasSpecialChar) {
            showError(newPasswordInput, 'cp-newPasswordFeedback', `Mật khẩu phải có ít nhất 1 ký tự đặc biệt (${specialChars})`);
            return false;
        }

        showSuccess(newPasswordInput, 'cp-newPasswordFeedback');
        return true;
    }

    // Hàm validate xác nhận mật khẩu
    function validateConfirmPassword() {
        const password = newPasswordInput.value.trim();
        const confirmPassword = confirmPasswordInput.value.trim();

        if (!confirmPassword) {
            showError(confirmPasswordInput, 'cp-confirmPasswordFeedback', 'Vui lòng xác nhận mật khẩu');
            return false;
        }

        if (password !== confirmPassword) {
            showError(confirmPasswordInput, 'cp-confirmPasswordFeedback', 'Mật khẩu xác nhận không khớp');
            return false;
        }

        showSuccess(confirmPasswordInput, 'cp-confirmPasswordFeedback');
        return true;
    }

    // Hàm cập nhật độ mạnh mật khẩu
    function updatePasswordStrength() {
        const password = newPasswordInput.value.trim();
        const strengthText = document.getElementById('cp-passwordStrength');
        const specialChars = "!@#$%^&*";

        strengthText.textContent = '';
        strengthText.className = 'cp-password-strength';

        if (!password) return;

        // Kiểm tra điều kiện cơ bản trước
        const hasMinLength = password.length >= 8;
        const hasUpperCase = /[A-Z]/.test(password);
        const hasLowerCase = /[a-z]/.test(password);
        const hasNumber = /[0-9]/.test(password);
        const hasSpecialChar = [...specialChars].some(char => password.includes(char));

        // Nếu chưa đủ điều kiện cơ bản thì không hiển thị đánh giá độ mạnh
        if (!hasMinLength || !hasUpperCase || !hasLowerCase || !hasNumber || !hasSpecialChar) {
            return;
        }

        // Nếu đủ điều kiện cơ bản thì mới đánh giá độ mạnh
        let strength = 0;

        // Độ dài
        if (password.length >= 8) strength++;
        if (password.length >= 12) strength++;
        if (password.length >= 16) strength++;

        // Độ phức tạp
        if (hasUpperCase && hasLowerCase) strength++;
        if (hasNumber) strength++;
        if (hasSpecialChar) strength++;
        if ((password.match(/[!@#$%^&*]/g) || []).length > 1) strength++;

        // Phân loại độ mạnh
        if (strength <= 4) {
            strengthText.textContent = 'Trung bình';
            strengthText.className = 'cp-password-strength cp-password-medium';
        } else if (strength <= 7) {
            strengthText.textContent = 'Mạnh';
            strengthText.className = 'cp-password-strength cp-password-strong';
        } else {
            strengthText.textContent = 'Rất mạnh';
            strengthText.className = 'cp-password-strength cp-password-very-strong';
        }
    }


    // Hàm cập nhật trạng thái nút đổi mật khẩu
    function updateChangePasswordBtn() {
        const isNewPasswordValid = validateNewPassword();
        const isConfirmPasswordValid = validateConfirmPassword();

        changePasswordBtnForm.disabled = !(isNewPasswordValid && isConfirmPasswordValid);
    }

    // Hàm hiển thị lỗi
    function showError(input, feedbackId, message) {
        const feedback = document.getElementById(feedbackId);
        input.classList.add('cp-is-invalid');
        input.classList.remove('cp-is-valid');
        feedback.textContent = message;
        feedback.classList.add('show'); // Thay vì style.display
    }

    // Hàm hiển thị thành công
    function showSuccess(input, feedbackId) {
        const feedback = document.getElementById(feedbackId);
        input.classList.remove('cp-is-invalid');
        input.classList.add('cp-is-valid');
        feedback.classList.remove('show');
    }

    // Thêm loading state cho nút kiểm tra
    function setButtonLoading(button, isLoading) {
        if (isLoading) {
            button.disabled = true;
            const originalWidth = button.offsetWidth;
            button.style.minWidth = originalWidth + 'px'; // Giữ nguyên kích thước
            button.innerHTML = '<span class="cp-loading-text">Đang kiểm tra...</span>';
        } else {
            button.disabled = false;
            button.style.minWidth = '';
            button.textContent = 'Kiểm tra';
        }
    }


    // Hàm reset form
    function resetForm() {
        oldPasswordInput.value = '';
        newPasswordInput.value = '';
        confirmPasswordInput.value = '';

        oldPasswordInput.classList.remove('cp-is-invalid', 'cp-is-valid');
        newPasswordInput.classList.remove('cp-is-invalid', 'cp-is-valid');
        confirmPasswordInput.classList.remove('cp-is-invalid', 'cp-is-valid');

        document.getElementById('cp-oldPasswordFeedback').style.display = 'none';
        document.getElementById('cp-newPasswordFeedback').style.display = 'none';
        document.getElementById('cp-confirmPasswordFeedback').style.display = 'none';

        document.getElementById('cp-passwordStrength').textContent = '';
        document.getElementById('cp-passwordStrength').className = 'cp-password-strength';

        newPasswordSection.style.display = 'none';
        checkPasswordBtn.style.display = 'block';
        checkPasswordBtn.disabled = false;
        checkPasswordBtn.textContent = 'Kiểm tra';

        changePasswordBtnForm.disabled = true;
        changePasswordBtnForm.textContent = 'Đổi mật khẩu';
    }
});
