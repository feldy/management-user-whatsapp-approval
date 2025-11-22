import { useState, useEffect } from 'react';

export const useAuth = () => {
    const [credentials, setCredentials] = useState(null);
    const [isAuthenticated, setIsAuthenticated] = useState(false);

    useEffect(() => {
        // Check if user is already logged in
        const stored = localStorage.getItem('credentials');
        if (stored) {
            setCredentials(JSON.parse(stored));
            setIsAuthenticated(true);
        }
    }, []);

    const login = (creds) => {
        localStorage.setItem('credentials', JSON.stringify(creds));
        setCredentials(creds);
        setIsAuthenticated(true);
    };

    const logout = () => {
        localStorage.removeItem('credentials');
        setCredentials(null);
        setIsAuthenticated(false);
    };

    return {
        credentials,
        isAuthenticated,
        login,
        logout
    };
};
