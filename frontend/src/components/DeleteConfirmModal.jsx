import React from 'react';

const DeleteConfirmModal = ({ onConfirm, onCancel }) => {
    const [isDeleting, setIsDeleting] = React.useState(false);

    const handleConfirm = async () => {
        setIsDeleting(true);
        try {
            await onConfirm();
        } finally {
            setIsDeleting(false);
        }
    };

    return (
        <div className="modal-overlay">
            <div className="modal">
                <div className="modal-header">
                    <h2 className="modal-title">Confirm Delete</h2>
                </div>
                <p style={{ marginBottom: '1.5rem' }}>
                    Are you sure you want to delete this user? This action cannot be undone.
                </p>
                <div className="modal-actions">
                    <button className="btn" onClick={onCancel} disabled={isDeleting}>
                        Cancel
                    </button>
                    <button className="btn btn-danger" onClick={handleConfirm} disabled={isDeleting}>
                        {isDeleting ? 'Deleting...' : 'Delete'}
                    </button>
                </div>
            </div>
        </div>
    );
};

export default DeleteConfirmModal;
