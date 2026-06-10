// Authentication and Account Management

const Auth = {
    // Check if user is logged in
    isLoggedIn() {
        return !!localStorage.getItem('jwt_token');
    },

    // Get currently logged in user info from cache
    getUser() {
        return {
            email: localStorage.getItem('user_email'),
            name: localStorage.getItem('user_name'),
            role: localStorage.getItem('user_role')
        };
    },

    // Save login credentials
    saveSession(token, email, name, role) {
        localStorage.setItem('jwt_token', token);
        localStorage.setItem('user_email', email);
        localStorage.setItem('user_name', name);
        localStorage.setItem('user_role', role);
    },

    // Trigger local registration
    async register(name, email, password, phone) {
        return await API.post('/auth/register', { name, email, password, phone });
    },

    // Trigger local login
    async login(email, password) {
        const data = await API.post('/auth/login', { email, password });
        this.saveSession(data.token, data.email, data.name, data.role);
        return data;
    },

    // Terminate session
    logout() {
        localStorage.removeItem('jwt_token');
        localStorage.removeItem('user_email');
        localStorage.removeItem('user_name');
        localStorage.removeItem('user_role');
        window.location.href = '/login.html?logout=true';
    },

    // Intercept Google OAuth2 success parameters from URL
    checkOAuthRedirect() {
        const urlParams = new URLSearchParams(window.location.search);
        const token = urlParams.get('token');
        const email = urlParams.get('email');
        
        if (token && email) {
            // Store token temporarily to fetch full profile details
            localStorage.setItem('jwt_token', token);
            localStorage.setItem('user_email', email);
            
            // Call profile API to fetch user role and full details
            API.get('/auth/me')
                .then(user => {
                    this.saveSession(token, user.email, user.name, user.role.name);
                    
                    // Clear query parameters from URL bar for clean UX
                    window.history.replaceState({}, document.title, window.location.pathname);
                    
                    // Route user to appropriate dashboard
                    if (user.role.name === 'ROLE_SUPER_ADMIN' || user.role.name === 'ROLE_STAFF') {
                        window.location.href = '/admin.html';
                    } else {
                        window.location.href = '/dashboard.html';
                    }
                })
                .catch(err => {
                    console.error('Failed to resolve Google profile details:', err);
                    this.logout();
                });
        }
    },

    // Secure route check - redirect if unauthorized
    checkRouteProtection(allowedRoles = []) {
        if (!this.isLoggedIn()) {
            window.location.href = '/login.html?unauthorized=true';
            return;
        }

        const user = this.getUser();
        if (allowedRoles.length > 0 && !allowedRoles.includes(user.role)) {
            if (user.role === 'ROLE_SUPER_ADMIN' || user.role === 'ROLE_STAFF') {
                window.location.href = '/admin.html?forbidden=true';
            } else {
                window.location.href = '/dashboard.html?forbidden=true';
            }
        }
    }
};

// Auto-run checks on imports
document.addEventListener('DOMContentLoaded', () => {
    Auth.checkOAuthRedirect();
});
