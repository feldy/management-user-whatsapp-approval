import React, { useState, useEffect } from 'react';

const UserForm = ({ user, onClose, onSave }) => {
    const [formData, setFormData] = useState({
        name: '',
        email: '',
        password: '',
        phoneNumber: '',
        role: 'ROLE_USER'
    });

    useEffect(() => {
        if (user) {
            setFormData({
                name: user.name,
                email: user.email,
                password: '', // Don't show password
                phoneNumber: user.phoneNumber || '',
                role: user.role
            });
        }
    }, [user]);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));
    };

    const [isSubmitting, setIsSubmitting] = useState(false);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setIsSubmitting(true);
        try {
            await onSave(formData);
        } finally {
            setIsSubmitting(false);
        }
    };

    return (
        <div className="modal-overlay">
            <div className="modal">
                <div className="modal-header">
                    <h2 className="modal-title">{user ? 'Edit User' : 'Add New User'}</h2>
                    <button className="btn" onClick={onClose} disabled={isSubmitting} style={{ background: 'none', fontSize: '1.5rem' }}>&times;</button>
                </div>
                <form onSubmit={handleSubmit}>
                    <div className="form-group">
                        <label className="form-label">Name</label>
                        <input
                            type="text"
                            name="name"
                            className="form-input"
                            value={formData.name}
                            onChange={handleChange}
                            required
                            disabled={isSubmitting}
                        />
                    </div>
                    <div className="form-group">
                        <label className="form-label">Email</label>
                        <input
                            type="email"
                            name="email"
                            className="form-input"
                            value={formData.email}
                            onChange={handleChange}
                            required
                            disabled={isSubmitting}
                        />
                    </div>
                    <div className="form-group">
                        <label className="form-label">WhatsApp Number</label>
                        <input
                            type="tel"
                            name="phoneNumber"
                            className="form-input"
                            placeholder="628xxxxxxxxx"
                            value={formData.phoneNumber}
                            onChange={handleChange}
                            pattern="[0-9]{10,15}"
                            title="Phone number should be 10-15 digits (e.g., 628123456789)"
                            disabled={isSubmitting}
                        />
                        <small style={{ color: '#666', fontSize: '0.85rem' }}>Format: 628xxxxxxxxx (dengan kode negara)</small>
                    </div>
                    <div className="form-group">
                        <label className="form-label">Password {user && '(Leave blank to keep current)'}</label>
                        <input
                            type="password"
                            name="password"
                            className="form-input"
                            value={formData.password}
                            onChange={handleChange}
                            required={!user}
                            disabled={isSubmitting}
                        />
                    </div>
                    <div className="form-group">
                        <label className="form-label">Role</label>
                        <select
                            name="role"
                            className="form-input"
                            value={formData.role}
                            onChange={handleChange}
                            disabled={isSubmitting}
                        >
                            <option value="ROLE_USER">User</option>
                            <option value="ROLE_ADMIN">Admin</option>
                        </select>
                    </div>
                    <div className="modal-actions">
                        <button type="button" className="btn" onClick={onClose} disabled={isSubmitting}>Cancel</button>
                        <button type="submit" className="btn btn-primary" disabled={isSubmitting}>
                            {isSubmitting ? 'Saving...' : 'Save User'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default UserForm;
