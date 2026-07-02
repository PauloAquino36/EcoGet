const API_BASE = 'http://localhost:8080';

const Api = {
    getToken() { return localStorage.getItem('ecoget_token'); },
    getUser()  { return localStorage.getItem('ecoget_user'); },
    isAdmin()  { return localStorage.getItem('ecoget_admin') === 'true'; },

    setAuth(email, token, admin) {
        localStorage.setItem('ecoget_token', token);
        localStorage.setItem('ecoget_user', email);
        localStorage.setItem('ecoget_admin', admin ? 'true' : 'false');
    },

    logout() {
        localStorage.removeItem('ecoget_token');
        localStorage.removeItem('ecoget_user');
        localStorage.removeItem('ecoget_admin');
        const inPages = window.location.pathname.includes('/pages/');
        window.location.href = inPages ? '../index.html' : 'index.html';
    },

    requireAuth() {
        if (!this.getToken()) {
            const inPages = window.location.pathname.includes('/pages/');
            window.location.href = inPages ? '../index.html' : 'index.html';
            return;
        }
        const el = document.getElementById('user-email');
        if (el) el.textContent = this.getUser();
    },

    _headers() {
        return {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${this.getToken()}`
        };
    },

    async _fetch(url, opts = {}) {
        try {
            const res = await fetch(url, { headers: this._headers(), ...opts });
            if (res.status === 401) { this.logout(); return null; }
            return res;
        } catch (e) {
            console.error('Erro na requisição:', e);
            return null;
        }
    },

    async get(endpoint)        { return this._fetch(`${API_BASE}${endpoint}`); },
    async post(endpoint, body) { return this._fetch(`${API_BASE}${endpoint}`, { method: 'POST', body: JSON.stringify(body) }); },
    async put(endpoint, body)  { return this._fetch(`${API_BASE}${endpoint}`, { method: 'PUT',  body: JSON.stringify(body) }); },
    async del(endpoint)        { return this._fetch(`${API_BASE}${endpoint}`, { method: 'DELETE' }); },

    async postPublic(endpoint, body) {
        try {
            return await fetch(`${API_BASE}${endpoint}`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(body)
            });
        } catch (e) {
            console.error('Erro na requisição pública:', e);
            return null;
        }
    },

    async getPublic(endpoint) {
        try {
            return await fetch(`${API_BASE}${endpoint}`);
        } catch (e) {
            console.error('Erro:', e);
            return null;
        }
    }
};
