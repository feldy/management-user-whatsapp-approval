import { useState, useEffect } from 'react';
import UserService from '../services/UserService';

export const useUsers = (credentials) => {
    const [users, setUsers] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    useEffect(() => {
        if (credentials) {
            loadUsers();
        }
    }, [credentials]);

    const loadUsers = async () => {
        setLoading(true);
        setError(null);
        try {
            const response = await UserService.getAllUsers(credentials);
            setUsers(response.data);
        } catch (err) {
            console.error("Error loading users:", err);
            setError("Failed to load users");
        } finally {
            setLoading(false);
        }
    };

    const createUser = async (userData) => {
        try {
            await UserService.createUser(userData, credentials);
            await loadUsers();
            return { success: true };
        } catch (err) {
            console.error("Error creating user:", err);
            return { success: false, error: err.message };
        }
    };

    const updateUser = async (id, userData) => {
        try {
            await UserService.updateUser(id, userData, credentials);
            await loadUsers();
            return { success: true };
        } catch (err) {
            console.error("Error updating user:", err);
            return { success: false, error: err.message };
        }
    };

    const deleteUser = async (id) => {
        try {
            await UserService.deleteUser(id, credentials);
            await loadUsers();
            return { success: true };
        } catch (err) {
            console.error("Error deleting user:", err);
            return { success: false, error: err.message };
        }
    };

    const approveUser = async (id) => {
        try {
            await UserService.approveUser(id, credentials);
            await loadUsers();
            return { success: true };
        } catch (err) {
            console.error("Error approving user:", err);
            return { success: false, error: err.message };
        }
    };

    const rejectUser = async (id) => {
        try {
            await UserService.rejectUser(id, credentials);
            await loadUsers();
            return { success: true };
        } catch (err) {
            console.error("Error rejecting user:", err);
            return { success: false, error: err.message };
        }
    };

    return {
        users,
        loading,
        error,
        loadUsers,
        createUser,
        updateUser,
        deleteUser,
        approveUser,
        rejectUser
    };
};
