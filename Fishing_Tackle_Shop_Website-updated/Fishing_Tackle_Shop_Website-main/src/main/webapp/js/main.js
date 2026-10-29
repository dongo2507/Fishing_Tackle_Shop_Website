// 1) Mọi <form data-confirm="..."> sẽ hỏi xác nhận trước khi gửi.
document.addEventListener('submit', function (e) {
    var msg = e.target.getAttribute('data-confirm');
    if (msg && !confirm(msg)) {
        e.preventDefault();
    }
});

// 2) Ô chọn file ảnh: kiểm tra số lượng, dung lượng, loại file và xem trước.
//    data-max-files="5"      : số ảnh tối đa mỗi lần chọn (không bắt buộc)
//    data-preview="idDiv"    : id của thẻ div để hiện ảnh xem trước (không bắt buộc)
//    (Đây chỉ để thân thiện với người dùng; server vẫn kiểm tra lại toàn bộ.)
document.addEventListener('change', function (e) {
    var input = e.target;
    if (input.tagName !== 'INPUT' || input.type !== 'file') {
        return;
    }

    var MAX_BYTES = 5 * 1024 * 1024;
    var box = document.getElementById(input.getAttribute('data-preview'));
    var maxFiles = parseInt(input.getAttribute('data-max-files') || '0', 10);
    var files = Array.prototype.slice.call(input.files);

    function reject(message) {
        alert(message);
        input.value = '';
        if (box) { box.innerHTML = ''; }
    }

    if (maxFiles && files.length > maxFiles) {
        reject('Chỉ được chọn tối đa ' + maxFiles + ' ảnh mỗi lần.');
        return;
    }
    for (var i = 0; i < files.length; i++) {
        if (files[i].size > MAX_BYTES) {
            reject('Ảnh "' + files[i].name + '" vượt quá 5MB.');
            return;
        }
        if (!/^image\/(jpeg|png|gif|webp)$/.test(files[i].type)) {
            reject('"' + files[i].name + '" không phải ảnh JPG, PNG, GIF hoặc WebP.');
            return;
        }
    }

    if (box) {
        box.innerHTML = '';
        files.forEach(function (file) {
            var img = document.createElement('img');
            img.src = URL.createObjectURL(file);
            img.alt = '';
            box.appendChild(img);
        });
    }
});
