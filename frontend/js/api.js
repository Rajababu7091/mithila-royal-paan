// API Communication Helper

const API_BASE = '/api/v1';

const API = {
    // Helper to get authorization headers
    getHeaders(extraHeaders = {}) {
        const headers = {
            'Content-Type': 'application/json',
            ...extraHeaders
        };
        const token = localStorage.getItem('jwt_token');
        if (token) {
            headers['Authorization'] = `Bearer ${token}`;
        }
        return headers;
    },

    // Handle responses
    async handleResponse(response) {
        if (response.status === 401) {
            // Unauthorized - token might be expired, logout user
            localStorage.removeItem('jwt_token');
            localStorage.removeItem('user_email');
            localStorage.removeItem('user_role');
            localStorage.removeItem('user_name');
            if (!window.location.pathname.endsWith('login.html') && !window.location.pathname.endsWith('register.html') && !window.location.pathname.endsWith('index.html')) {
                window.location.href = '/login.html?session_expired=true';
            }
            throw new Error('Unauthorized');
        }

        const contentType = response.headers.get('content-type');
        let data;
        if (contentType && contentType.includes('application/json')) {
            data = await response.json();
        } else {
            data = await response.text();
        }

        if (!response.ok) {
            const errorMsg = (data && data.message) ? data.message : 'Something went wrong';
            throw new Error(errorMsg);
        }

        return data;
    },

    // GET Request
    async get(endpoint) {
        try {
            const response = await fetch(`${API_BASE}${endpoint}`, {
                method: 'GET',
                headers: this.getHeaders()
            });
            return await this.handleResponse(response);
        } catch (error) {
            console.error(`API GET ${endpoint} failed:`, error);
            throw error;
        }
    },

    // POST Request
    async post(endpoint, body) {
        try {
            const response = await fetch(`${API_BASE}${endpoint}`, {
                method: 'POST',
                headers: this.getHeaders(),
                body: JSON.stringify(body)
            });
            return await this.handleResponse(response);
        } catch (error) {
            console.error(`API POST ${endpoint} failed:`, error);
            throw error;
        }
    },

    // PUT Request
    async put(endpoint, body = null, params = null) {
        try {
            let url = `${API_BASE}${endpoint}`;
            if (params) {
                const searchParams = new URLSearchParams(params);
                url += `?${searchParams.toString()}`;
            }

            const config = {
                method: 'PUT',
                headers: this.getHeaders()
            };

            if (body) {
                config.body = JSON.stringify(body);
            }

            const response = await fetch(url, config);
            return await this.handleResponse(response);
        } catch (error) {
            console.error(`API PUT ${endpoint} failed:`, error);
            throw error;
        }
    },

    // DELETE Request
    async delete(endpoint) {
        try {
            const response = await fetch(`${API_BASE}${endpoint}`, {
                method: 'DELETE',
                headers: this.getHeaders()
            });
            return await this.handleResponse(response);
        } catch (error) {
            console.error(`API DELETE ${endpoint} failed:`, error);
            throw error;
        }
    }
};
