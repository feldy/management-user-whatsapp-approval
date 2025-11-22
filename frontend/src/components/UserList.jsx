import React from 'react';
import { useUsers } from '../hooks/useUsers';
import { useModal } from '../hooks/useModal';
import UserForm from './UserForm';
import UserTable from './UserTable';
import DeleteConfirmModal from './DeleteConfirmModal';
import Header from './Header';

const UserList = ({ credentials, onLogout }) => {
    const { users, loading, error, createUser, updateUser, deleteUser, approveUser, rejectUser } = useUsers(credentials);
    const userFormModal = useModal();
    const deleteModal = useModal();

    const handleAddUser = () => {
        userFormModal.open(null);
    };

    const handleEditUser = (user) => {
        userFormModal.open(user);
    };

    const handleDeleteClick = (userId) => {
        deleteModal.open(userId);
    };

    const handleSaveUser = async (userData) => {
        const result = userFormModal.data
            ? await updateUser(userFormModal.data.id, userData)
            : await createUser(userData);

        if (result.success) {
            userFormModal.close();
        } else {
            alert("Failed to save user: " + result.error);
        }
    };

    const handleConfirmDelete = async () => {
        const result = await deleteUser(deleteModal.data);
        if (result.success) {
            deleteModal.close();
        } else {
            alert("Failed to delete user: " + result.error);
        }
    };

    const handleApproveUser = async (userId) => {
        const result = await approveUser(userId);
        if (!result.success) {
            alert("Failed to approve user: " + result.error);
        }
    };

    const handleRejectUser = async (userId) => {
        if (window.confirm("Are you sure you want to reject this user?")) {
            const result = await rejectUser(userId);
            if (!result.success) {
                alert("Failed to reject user: " + result.error);
            }
        }
    };

    return (
        <div className="container">
            <Header onLogout={onLogout} onAddUser={handleAddUser} />

            {error && (
                <div style={{
                    padding: '1rem',
                    marginBottom: '1rem',
                    backgroundColor: '#fee2e2',
                    color: '#991b1b',
                    borderRadius: '0.5rem'
                }}>
                    {error}
                </div>
            )}

            <UserTable
                users={users}
                loading={loading}
                onEdit={handleEditUser}
                onDelete={handleDeleteClick}
                onApprove={handleApproveUser}
                onReject={handleRejectUser}
            />

            {userFormModal.isOpen && (
                <UserForm
                    user={userFormModal.data}
                    onClose={userFormModal.close}
                    onSave={handleSaveUser}
                />
            )}

            {deleteModal.isOpen && (
                <DeleteConfirmModal
                    onConfirm={handleConfirmDelete}
                    onCancel={deleteModal.close}
                />
            )}
        </div>
    );
};

export default UserList;
