// Global Page Controller & UI Operations

document.addEventListener('DOMContentLoaded', () => {
    // 1. Initialize Light / Dark Theme Mode
    initThemeMode();

    // 2. Set up dynamic authentication states in header/navbar
    initNavbarAuth();

    // 3. Setup global alerts and notifications
    checkNotificationsCount();
});

// Setup Dark/Light mode theme toggle
function initThemeMode() {
    const currentTheme = localStorage.getItem('theme') || 'dark';
    document.documentElement.setAttribute('data-theme', currentTheme);

    const themeToggle = document.getElementById('theme-toggle-chk');
    if (themeToggle) {
        themeToggle.checked = currentTheme === 'light';
        themeToggle.addEventListener('change', (e) => {
            const theme = e.target.checked ? 'light' : 'dark';
            document.documentElement.setAttribute('data-theme', theme);
            localStorage.setItem('theme', theme);
        });
    }
}

// Adjust Navbar links based on login status
function initNavbarAuth() {
    const authLink = document.getElementById('nav-auth-link');
    const registerLink = document.getElementById('nav-register-item');
    const userDropdown = document.getElementById('nav-user-dropdown');
    
    if (Auth.isLoggedIn()) {
        const user = Auth.getUser();
        
        // Hide standard register item
        if (registerLink) {
            registerLink.style.display = 'none';
        }

        // Show logged in user controls
        if (authLink) {
            let dashboardUrl = '/dashboard.html';
            if (user.role === 'ROLE_SUPER_ADMIN' || user.role === 'ROLE_STAFF') {
                dashboardUrl = '/admin.html';
            }
            authLink.innerHTML = `<i class="bi bi-person-circle me-1"></i> Dashboard`;
            authLink.setAttribute('href', dashboardUrl);
            authLink.className = 'btn btn-outline-gold ms-2';
        }

        // Add a logout link in nav if userDropdown is present or create it
        const navContainer = document.querySelector('.navbar-nav');
        if (navContainer && !document.getElementById('nav-logout-item')) {
            const logoutLi = document.createElement('li');
            logoutLi.className = 'nav-item';
            logoutLi.id = 'nav-logout-item';
            logoutLi.innerHTML = `<a class="nav-link text-danger ms-2" href="#" onclick="Auth.logout()"><i class="bi bi-box-arrow-right me-1"></i> Logout</a>`;
            navContainer.appendChild(logoutLi);
        }
    }
}

// Check if user has unread alerts
function checkNotificationsCount() {
    if (Auth.isLoggedIn()) {
        API.get('/notifications/unread')
            .then(unreadList => {
                const badge = document.getElementById('notification-badge');
                if (badge) {
                    if (unreadList.length > 0) {
                        badge.textContent = unreadList.length;
                        badge.style.display = 'inline-block';
                    } else {
                        badge.style.display = 'none';
                    }
                }
            })
            .catch(err => console.warn('Could not fetch notifications count:', err));
    }
}

// Global alert utility
function showGlobalAlert(message, type = 'success', containerId = 'alert-container') {
    const container = document.getElementById(containerId);
    if (!container) return;

    const alertDiv = document.createElement('div');
    alertDiv.className = `alert alert-${type} alert-dismissible fade show royal-border mt-3`;
    alertDiv.setAttribute('role', 'alert');
    alertDiv.innerHTML = `
        <strong>${type === 'success' ? '✓ Success: ' : '✗ Error: '}</strong> ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    `;
    container.appendChild(alertDiv);

    // Auto dismiss after 5 seconds
    setTimeout(() => {
        const bsAlert = new bootstrap.Alert(alertDiv);
        bsAlert.close();
    }, 5000);
}
