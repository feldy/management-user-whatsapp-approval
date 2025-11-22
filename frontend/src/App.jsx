import React from 'react';
import { useAuth } from './hooks/useAuth';
import UserList from './components/UserList';
import Login from './components/Login';

function App() {
    const { credentials, isAuthenticated, login, logout } = useAuth();

    if (!isAuthenticated) {
        return <Login onLogin={login} />;
    }

    return (
        <div className="App">
            <UserList credentials={credentials} onLogout={logout} />
        </div>
    );
}

export default App;
