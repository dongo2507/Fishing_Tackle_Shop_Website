// Chuông thông báo trên header: lấy dữ liệu thật từ /notifications?format=json
function escapeHTML(value) {
    return String(value).replace(/[&<>"']/g, function (char) {
        return {"&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;"}[char];
    });
}

function renderNotificationItem(item) {
    return `
        <article class="notification-item ${item.unread ? "is-unread" : ""}">
            <div class="notification-icon">${escapeHTML(item.icon)}</div>
            <div class="notification-content">
                <div class="notification-item-heading">
                    <strong>${escapeHTML(item.title)}</strong>
                    ${item.unread ? '<span class="unread-dot"></span>' : ""}
                </div>
                <p>${escapeHTML(item.message)}</p>
                <span class="notification-time">${escapeHTML(item.time)}</span>
            </div>
        </article>
    `;
}

document.addEventListener("DOMContentLoaded", function () {
    const wrapper = document.querySelector(".notification-wrapper");
    const bellButton = document.getElementById("notificationBell");
    const dropdown = document.getElementById("notificationDropdown");
    const latestList = document.getElementById("latestNotifications");
    const badge = document.getElementById("notificationBadge");
    if (!wrapper || !bellButton || !dropdown) {
        return;
    }
    const url = wrapper.dataset.url;

    function load() {
        fetch(url, {headers: {"Accept": "application/json"}, credentials: "same-origin"})
            .then(function (res) {
                if (!res.ok) { throw new Error("HTTP " + res.status); }
                return res.json();
            })
            .then(function (data) {
                if (badge) {
                    badge.textContent = data.unread > 9 ? "9+" : data.unread;
                    badge.hidden = data.unread === 0;
                }
                if (latestList) {
                    latestList.innerHTML = data.items.length
                        ? data.items.map(renderNotificationItem).join("")
                        : '<p class="empty">Chưa có thông báo nào.</p>';
                }
            })
            .catch(function () {
                if (latestList) {
                    latestList.innerHTML = '<p class="empty">Không tải được thông báo.</p>';
                }
            });
    }

    load();

    bellButton.addEventListener("click", function (event) {
        event.stopPropagation();
        const isOpen = dropdown.classList.toggle("is-open");
        bellButton.setAttribute("aria-expanded", String(isOpen));
        if (isOpen) { load(); }
    });

    document.addEventListener("click", function (event) {
        if (!dropdown.contains(event.target) && !bellButton.contains(event.target)) {
            dropdown.classList.remove("is-open");
            bellButton.setAttribute("aria-expanded", "false");
        }
    });

    document.addEventListener("keydown", function (event) {
        if (event.key === "Escape") {
            dropdown.classList.remove("is-open");
            bellButton.setAttribute("aria-expanded", "false");
        }
    });
});
