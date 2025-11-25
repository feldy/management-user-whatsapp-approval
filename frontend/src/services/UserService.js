import axios from 'axios';

// Use environment variable in production, fallback to proxy in development
const BASE_URL = import.meta.env.VITE_API_URL || '';
const API_URL = BASE_URL ? `${BASE_URL}/users` : '/users';

const getAuthHeader = (credentials) => {
    if (!credentials) {
        // Try to get from localStorage
        const stored = localStorage.getItem('credentials');
        if (stored) {
            credentials = JSON.parse(stored);
        }
    }

    return {
        auth: {
            username: credentials.email,
            password: credentials.password
        }
    };
};

const getAllUsers = (credentials) => {
    return axios.get(API_URL, getAuthHeader(credentials));
};

const createUser = (user, credentials) => {
    return axios.post(API_URL, user, getAuthHeader(credentials));
};

const updateUser = (id, user, credentials) => {
    return axios.put(`${API_URL}/${id}`, user, getAuthHeader(credentials));
};

const deleteUser = (id, credentials) => {
    return axios.delete(`${API_URL}/${id}`, getAuthHeader(credentials));
};

const approveUser = (id, credentials) => {
    return axios.put(`${API_URL}/${id}/approve`, {}, getAuthHeader(credentials));
};

const rejectUser = (id, credentials) => {
    return axios.put(`${API_URL}/${id}/reject`, {}, getAuthHeader(credentials));
};

const getPendingUsers = (credentials) => {
    return axios.get(`${API_URL}/pending`, getAuthHeader(credentials));
};

export default {
    getAllUsers,
    createUser,
    updateUser,
    deleteUser,
    approveUser,
    rejectUser,
    getPendingUsers
};
