import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080';

const api = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        'Content-Type': 'application/json',
    },
});

export const orderApi = {
    create: (order) => api.post('/orders', order),
    getWaiting: () => api.get('/orders/waiting'),
    getInProgress: () => api.get('/orders/in-progress'),
    getDone: () => api.get('/orders/done'),
    getAbandoned: () => api.get('/orders/abandoned'),
};

export const baristaApi = {
    getAll: () => api.get('/baristas'),
};

export default api;
