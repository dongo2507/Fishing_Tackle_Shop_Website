// Mọi <form data-confirm="..."> sẽ hỏi xác nhận trước khi gửi.
document.addEventListener('submit', function (e) {
    var msg = e.target.getAttribute('data-confirm');
    if (msg && !confirm(msg)) {
        e.preventDefault();
    }
});
