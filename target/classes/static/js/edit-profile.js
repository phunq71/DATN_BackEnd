document.addEventListener('DOMContentLoaded', async function() {
    account= await loadAccount();
    setForm(account);
    const changePasswordBtn = document.getElementById('changePassword22');

    if (account.isOAuth2 && changePasswordBtn) {
        changePasswordBtn.style.display = 'none'; // Ẩn nút
    }
    document.getElementById("saveBtn").addEventListener('click', save);
});

let account={};
let fullAddress=[];


//Input
const displayName= document.getElementById('displayName');
const fullName = document.getElementById('fullName');
const male= document.getElementById('male');
const female= document.getElementById('female');
const address= document.getElementById('address');
const imageAvt= document.getElementById('imageAvt');
const imageInput =document.getElementById('imageInput');
const dob=document.getElementById('dob');
function loadAccount(){
   return axios.get("/opulentia_user/getCustomer")
        .then(response => {
            console.log(response.data);
            return response.data;
        })
        .catch(error => {
            console.log(error);
            return null;
        });
}

function setForm(account){
    if(account!=null){
        displayName.innerText=account.fullName;
        fullName.value=account.fullName;
        if(account.gender == null){
            male.checked=false;
            female.checked=false;
        }else if(account.gender){
            male.checked =true
        }
        else female.checked=true
        address.value=account.addressDetail;
        setAddressID(account.province, account.district, account.ward);
        if(account.isOAuth2) {
            imageAvt.src=account.imageAvt;
            imageInput.disabled = true;
            imageInput.style.display='none';
            imageAvt.title="Bạn không thể thay ảnh đại diện khi đăng nhập bằng Google hoặc Facebook";
        }
        else {
            if(account.imageAvt==null || account.imageAvt==="") imageAvt.src="/uploads/default-avt.jpg";
            else imageAvt.src=`/uploads/${account.imageAvt}`;
        }
        dob.value=account.dob;
    }
}

/**
 * Truyền vào:
 * @param {String} provinceName - tên tỉnh/thành phố
 * @param {String} districtName - tên quận/huyện
 * @param {String} wardName - tên xã/phường/thị trấn
 * @returns {Array} [provinceID, districtID, wardCode] hoặc [null, null, null] nếu không thấy
 */
function setAddressID(provinceName, districtName, wardName) {
    let provinceID = null;
    let districtID = null;
    let wardCode = null;

    // Tìm tỉnh
    const province = dataAddress.find(p => p.ProvinceName === provinceName);
    if (province) {
        provinceID = province.ProvinceID;

        // Tìm quận/huyện trong tỉnh
        const district = province.Districts.find(d => d.DistrictName === districtName);
        if (district) {
            districtID = district.DistrictID;

            // Tìm xã/phường trong quận/huyện
            const ward = district.Wards.find(w => w.WardName === wardName);
            if (ward) {
                wardCode = ward.WardCode;
            }
        }
    }
    provincesSelect.value=provinceID;
    provincesSelect.dispatchEvent(new Event('change'));
    districtsSelect.value=districtID;
    districtsSelect.dispatchEvent(new Event('change'));
    wardsSelect.value=wardCode;



    return [provinceID, districtID, wardCode];
}

function save() {
    // Validate required fields and collect missing fields
    const missingFields = [];

    if (!fullName.value.trim()) {
        missingFields.push('Họ và tên');
    }

    if (!male.checked && !female.checked) {
        missingFields.push('Giới tính');
    }

    if (!dob.value) {
        missingFields.push('Ngày sinh');
    }

    if (provincesSelect.value === "") {
        missingFields.push('Tỉnh/Thành phố');
    }

    if (districtsSelect.value === "") {
        missingFields.push('Quận/Huyện');
    }

    if (wardsSelect.value === "") {
        missingFields.push('Phường/Xã');
    }

    if (!address.value.trim()) {
        missingFields.push('Địa chỉ chi tiết');
    }

    // Validate input formats
    const formatErrors = [];

    // FullName validation - no numbers or special characters
    const nameRegex = /^[a-zA-ZÀ-ỹ\s]+$/;
    if (fullName.value.trim() && !nameRegex.test(fullName.value.trim())) {
        formatErrors.push('Họ và tên không được chứa số hoặc ký tự đặc biệt');
    }

    // Address validation - no special characters or icons
    const addressRegex = /^[a-zA-ZÀ-ỹ0-9\s,./-]+$/;
    if (address.value.trim() && !addressRegex.test(address.value.trim())) {
        formatErrors.push('Địa chỉ không được chứa ký tự đặc biệt hoặc biểu tượng');
    }

    // If there are validation errors, show them
    if (missingFields.length > 0 || formatErrors.length > 0) {
        let errorMessage = '';

        if (missingFields.length > 0) {
            errorMessage += `<b>Các trường bắt buộc còn thiếu:</b><br>• ${missingFields.join('<br>• ')}<br><br>`;
        }

        if (formatErrors.length > 0) {
            errorMessage += `<b>Lỗi định dạng:</b><br>• ${formatErrors.join('<br>• ')}`;
        }

        Swal.fire({
            title: 'Vui lòng kiểm tra lại thông tin',
            html: errorMessage,
            icon: 'error',
            confirmButtonText: 'OK',
            confirmButtonColor: '#000000',
        });
        return;
    }

    // If validation passes, proceed with saving
    const formData = new FormData();
    formData.append('fullName', fullName.value);
    formData.append('gender', male.checked);
    formData.append('dob', dob.value);
    formData.append('province', provincesSelect.options[provincesSelect.selectedIndex].text);
    formData.append('district', districtsSelect.options[districtsSelect.selectedIndex].text);
    formData.append('ward', wardsSelect.options[wardsSelect.selectedIndex].text);
    formData.append('addressDetail', address.value);
    formData.append('addressIdGHN', getAddressIdGHN());
    console.log(getAddressIdGHN());
    if (!account.isOAuth2) {
        formData.append('imageAvt', imageInput.files[0]);
    }

    axios.put('/opulentia_user/saveCustomer', formData)
        .then(response => {
            account=response.data;
            Swal.fire({
                title: 'Cập nhật thành công',
                icon: 'success',
                timer: 1000,
                timerProgressBar: true,
                confirmButtonText: 'OK',
                confirmButtonColor: '#000000'
            });
            displayName.innerText=fullName.value;
        })
        .catch(error => {
            console.log(error);
            Swal.fire({
                title: 'Đã xảy ra lỗi',
                text: 'Có lỗi xảy ra khi cập nhật thông tin. Vui lòng thử lại sau.',
                icon: 'error',
                confirmButtonText: 'OK',
                confirmButtonColor: '#000000'
            });
        });
}

function getAddressIdGHN(){
    if(provincesSelect.value!=="" && districtsSelect.value!=="" && wardsSelect.value!=="" && address.value !==""){
        return provincesSelect.value+"/"+districtsSelect.value+"/"+wardsSelect.value+"/"+address.value;
    }
    return "";
}


