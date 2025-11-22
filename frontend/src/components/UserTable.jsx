import React from 'react';

const UserTable = ({ users, loading, onEdit, onDelete, onApprove, onReject }) => {
    if (loading) {
        return (
            <div className="card">
                <div style={{ textAlign: 'center', padding: '2rem', color: 'var(--text-secondary)' }}>
                    Loading users...
                </div>
            </div>
        );
    }

    return (
        <div className="card">
            <table>
                <thead>
                    <tr>
                        <th>Name</th>
                        <th>Email</th>
                        <th>Role</th>
                        <th>Status</th>
                        <th style={{ textAlign: 'right' }}>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    {users.map(user => (
                        <tr key={user.id}>
                            <td>
                                <div style={{ fontWeight: 500 }}>{user.name}</div>
                            </td>
                            <td>{user.email}</td>
                            <td>
                                <span style={{
                                    padding: '0.25rem 0.5rem',
                                    borderRadius: '9999px',
                                    fontSize: '0.75rem',
                                    fontWeight: 600,
                                    backgroundColor: user.role === 'ROLE_ADMIN' ? '#f3e8ff' : '#dcfce7',
                                    color: user.role === 'ROLE_ADMIN' ? '#7e22ce' : '#15803d'
                                }}>
                                    {user.role.replace('ROLE_', '')}
                                </span>
                            </td>
                            <td>
                                <span style={{
                                    padding: '0.25rem 0.5rem',
                                    borderRadius: '9999px',
                                    fontSize: '0.75rem',
                                    fontWeight: 600,
                                    backgroundColor: user.approved ? '#dcfce7' : '#fef3c7',
                                    color: user.approved ? '#15803d' : '#a16207'
                                }}>
                                    {user.approved ? 'Approved' : 'Pending'}
                                </span>
                            </td>
                            <td style={{ textAlign: 'right' }}>
                                {!user.approved ? (
                                    <>
                                        <button
                                            className="btn"
                                            onClick={() => onApprove(user.id)}
                                            style={{ backgroundColor: '#22c55e', color: 'white' }}
                                        >
                                            Approve
                                        </button>
                                        <button
                                            className="btn btn-danger"
                                            onClick={() => onReject(user.id)}
                                        >
                                            Reject
                                        </button>
                                    </>
                                ) : (
                                    <>
                                        <button className="btn btn-edit" onClick={() => onEdit(user)}>
                                            Edit
                                        </button>
                                        <button className="btn btn-danger" onClick={() => onDelete(user.id)}>
                                            Delete
                                        </button>
                                    </>
                                )}
                            </td>
                        </tr>
                    ))}
                    {users.length === 0 && (
                        <tr>
                            <td colSpan=" 5" style={{ textAlign: 'center', color: 'var(--text-secondary)' }}>
                                No users found.
                            </td>
                        </tr>
                    )}
                </tbody>
            </table>
        </div>
    );
};

export default UserTable;
