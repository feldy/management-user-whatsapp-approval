import React from 'react';

const Header = ({ onLogout, onAddUser }) => {
    return (
        <div style={{
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center',
            marginBottom: '2rem'
        }}>
            <h1>User Management</h1>
            <div style={{ display: 'flex', gap: '0.75rem' }}>
                <button className="btn btn-primary" onClick={onAddUser}>
                    + Add User
                </button>
                <button
                    className="btn"
                    onClick={onLogout}
                    style={{ backgroundColor: '#f1f5f9', color: '#475569' }}
                >
                    Logout
                </button>
            </div>
        </div>
    );
};

export default Header;
