document.addEventListener("DOMContentLoaded", function () {
    const bell = document.getElementById("notificationBell");
    const dropdown = document.getElementById("notificationDropdown");
    const badge = document.getElementById("notificationBadge");
    const latestList = document.getElementById("latestNotifications");
    const allList = document.getElementById("allNotifications");

    const script = document.querySelector('script[src*="notification.js"]');
    const contextPath = script
        ? new URL(script.src).pathname.replace(/\/js\/notification\.js$/, "")
        : "";

    // Lấy số thông báo chưa đọc.
    function loadCount() {
        fetch(contextPath + "/notifications?action=count")
            .then(response => {
                if (!response.ok) throw new Error();
                return response.json();
            })
            .then(data => {
                badge.textContent = data.count > 9 ? "9+" : data.count;
                badge.hidden = data.count === 0;
            })
            .catch(() => badge.hidden = true);
    }

    // Hiển thị danh sách thông báo.
    function loadNotifications(list, action) {
        if (!list) return;

        fetch(contextPath + "/notifications?action=" + action)
            .then(response => {
                if (!response.ok) throw new Error();
                return response.json();
            })
            .then(data => {
                if (data.length === 0) {
                    list.innerHTML = "<p>Chưa có thông báo.</p>";
                    return;
                }

                list.innerHTML = data.map(n => `
                    <article class="notification-item ${n.isRead ? "" : "is-unread"}"
                             data-id="${n.id}">
                        <strong>${n.title}</strong>
                        <p>${n.content}</p>
                    </article>
                `).join("");
            })
            .catch(() => {
                list.innerHTML = "<p>Không thể tải thông báo.</p>";
            });
    }

    // Tải lại thông báo và số lượng.
    function refresh() {
        loadCount();
        loadNotifications(latestList, "latest");
        loadNotifications(allList, "all");
    }

    // Bấm vào thông báo để đánh dấu đã đọc.
    document.addEventListener("click", function (event) {
        const item = event.target.closest(".notification-item");
        if (!item) return;

        fetch(contextPath + "/notifications?id=" + encodeURIComponent(item.dataset.id), {
            method: "POST"
        }).then(() => refresh());
    });

    // Mở hoặc đóng danh sách thông báo.
    if (bell && dropdown) {
        bell.addEventListener("click", function () {
            dropdown.classList.toggle("is-open");
            bell.setAttribute(
                "aria-expanded",
                dropdown.classList.contains("is-open")
            );
            refresh();
        });
    }

    refresh();
});
