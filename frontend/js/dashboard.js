// Customer Dashboard Logic

document.addEventListener('DOMContentLoaded', () => {
    // 1. Secure route
    Auth.checkRouteProtection();

    // 2. Process query alerts
    processDashboardAlerts();

    // 3. Load user details
    loadUserProfile();

    // 4. Load Event bookings
    loadUserBookings();

    // 5. Load Product orders
    loadUserOrders();

    // 6. Load alerts/notifications
    loadUserNotifications();

    // 7. Bind read all alerts button
    const readAllBtn = document.getElementById('mark-all-read-btn');
    if (readAllBtn) {
        readAllBtn.addEventListener('click', handleMarkAllNotificationsRead);
    }
});

function processDashboardAlerts() {
    const urlParams = new URLSearchParams(window.location.search);
    if (urlParams.get('booking_success')) {
        showGlobalAlert('Your wedding/event booking counter request was successfully registered! Check status updates below.', 'success', 'dashboard-alert-container');
        window.history.replaceState({}, document.title, window.location.pathname);
    } else if (urlParams.get('order_success')) {
        showGlobalAlert('Your product order was placed successfully! You can track shipping status below.', 'success', 'dashboard-alert-container');
        window.history.replaceState({}, document.title, window.location.pathname);
    }
}

function loadUserProfile() {
    API.get('/auth/me')
        .then(user => {
            document.getElementById('client-name').textContent = user.name;
            document.getElementById('profile-name').value = user.name;
            document.getElementById('profile-email').value = user.email;
            document.getElementById('profile-phone').value = user.phone || '';
            document.getElementById('profile-provider').value = user.provider;
        })
        .catch(err => {
            console.error('Failed to load profile settings:', err);
        });
}

function loadUserBookings() {
    const body = document.getElementById('bookings-table-body');
    API.get('/bookings')
        .then(list => {
            if (list.length === 0) {
                body.innerHTML = `<tr><td colspan="7" class="text-secondary py-4">No event bookings placed yet.</td></tr>`;
                return;
            }

            body.innerHTML = '';
            list.forEach(b => {
                let statusBadge = `<span class="badge bg-secondary">Pending</span>`;
                if (b.status === 'APPROVED') statusBadge = `<span class="badge bg-success">Approved</span>`;
                if (b.status === 'REJECTED') statusBadge = `<span class="badge bg-danger">Rejected</span>`;
                if (b.status === 'COMPLETED') statusBadge = `<span class="badge bg-info text-dark">Completed</span>`;

                const coordinatorName = b.assignedStaff ? b.assignedStaff.name : '<span class="text-secondary small">Not Assigned</span>';
                
                const tr = document.createElement('tr');
                tr.innerHTML = `
                    <td class="font-monospace text-warning">#W-${b.id}</td>
                    <td class="text-white royal-font fw-bold">${b.eventType}</td>
                    <td>${b.eventDate}</td>
                    <td class="small text-truncate" style="max-width: 150px;">${b.location}</td>
                    <td class="font-monospace">${b.guestCount}</td>
                    <td>${coordinatorName}</td>
                    <td>${statusBadge}</td>
                `;
                body.appendChild(tr);
            });
        })
        .catch(err => {
            body.innerHTML = `<tr><td colspan="7" class="text-danger py-4">Failed to query bookings.</td></tr>`;
        });
}

function loadUserOrders() {
    const body = document.getElementById('orders-table-body');
    API.get('/orders')
        .then(list => {
            if (list.length === 0) {
                body.innerHTML = `<tr><td colspan="7" class="text-secondary py-4">No product orders placed yet.</td></tr>`;
                return;
            }

            body.innerHTML = '';
            list.forEach(order => {
                let statusBadge = `<span class="badge bg-secondary">Pending</span>`;
                if (order.status === 'SHIPPED') statusBadge = `<span class="badge bg-warning text-dark">Shipped</span>`;
                if (order.status === 'DELIVERED') statusBadge = `<span class="badge bg-success">Delivered</span>`;
                if (order.status === 'CANCELLED') statusBadge = `<span class="badge bg-danger">Cancelled</span>`;

                const tr = document.createElement('tr');
                tr.innerHTML = `
                    <td class="font-monospace text-warning">#O-${order.id}</td>
                    <td class="text-white royal-font fw-bold">${order.product.name}</td>
                    <td class="font-monospace">${order.quantity}</td>
                    <td class="text-warning font-monospace fw-bold">INR ${order.price.toFixed(2)}</td>
                    <td class="small text-truncate" style="max-width: 150px;" title="${order.shippingAddress}">${order.shippingAddress}</td>
                    <td>${new Date(order.createdAt || new Date()).toLocaleDateString()}</td>
                    <td>${statusBadge}</td>
                `;
                body.appendChild(tr);
            });
        })
        .catch(err => {
            body.innerHTML = `<tr><td colspan="7" class="text-danger py-4">Failed to query product orders.</td></tr>`;
        });
}

function loadUserNotifications() {
    const container = document.getElementById('alerts-list-container');
    const badge = document.getElementById('alerts-count-badge');

    API.get('/notifications')
        .then(list => {
            // Filter unread count
            const unread = list.filter(n => !n.isRead);
            if (unread.length > 0) {
                badge.textContent = unread.length;
                badge.style.display = 'inline-block';
            } else {
                badge.style.display = 'none';
            }

            if (list.length === 0) {
                container.innerHTML = `<div class="text-center text-secondary py-4">No alerts or notifications.</div>`;
                return;
            }

            container.innerHTML = '';
            list.forEach(n => {
                const unreadClass = n.isRead ? '' : 'border-start border-4 border-warning bg-tertiary';
                const readBtn = n.isRead ? '' : `<button class="btn btn-sm btn-gold px-2 py-0" onclick="markNotificationRead(${n.id}, this)" style="font-size:0.75rem;"><i class="bi bi-check-lg"></i> Dismiss</button>`;
                
                const item = document.createElement('div');
                item.className = `list-group-item bg-transparent text-white border-bottom border-warning py-3 ${unreadClass}`;
                item.innerHTML = `
                    <div class="d-flex justify-content-between align-items-start gap-3">
                        <div>
                            <p class="mb-1 text-light small" style="line-height: 1.4;">${n.message}</p>
                            <small class="text-secondary font-monospace" style="font-size:0.7rem;">${new Date(n.createdAt || new Date()).toLocaleString()}</small>
                        </div>
                        ${readBtn}
                    </div>
                `;
                container.appendChild(item);
            });
        })
        .catch(err => {
            container.innerHTML = `<div class="text-danger py-4 text-center">Failed to load notifications.</div>`;
        });
}

function markNotificationRead(id, buttonEl) {
    API.put(`/notifications/${id}/read`)
        .then(() => {
            // Reload alerts
            loadUserNotifications();
            // Refresh global header badge
            checkNotificationsCount();
        })
        .catch(err => console.error('Failed to dismiss alert:', err));
}

function handleMarkAllNotificationsRead() {
    API.put('/notifications/read-all')
        .then(() => {
            loadUserNotifications();
            checkNotificationsCount();
        })
        .catch(err => console.error('Failed to clear alerts:', err));
}
