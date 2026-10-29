// ui.js - hành vi giao diện dùng chung (không phụ thuộc phần nào của nhóm)

// 1) Nút ☰ trên màn hình nhỏ: <button data-toggle="id-cua-menu"> mở/đóng phần tử có id đó
document.addEventListener('click', function (e) {
    var btn = e.target.closest('[data-toggle]');
    if (!btn) { return; }
    var target = document.getElementById(btn.getAttribute('data-toggle'));
    if (target) {
        var open = target.classList.toggle('open');
        btn.setAttribute('aria-expanded', open ? 'true' : 'false');
    }
});

// 2) Thông báo thành công (.alert-success) tự ẩn sau 6 giây; thông báo lỗi giữ nguyên
document.addEventListener('DOMContentLoaded', function () {
    document.querySelectorAll('.alert-success').forEach(function (el) {
        setTimeout(function () {
            el.style.transition = 'opacity .4s';
            el.style.opacity = '0';
            setTimeout(function () { el.remove(); }, 450);
        }, 6000);
    });
});
