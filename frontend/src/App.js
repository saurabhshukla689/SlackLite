import React, { useState, useEffect } from 'react';
import Login from './components/Login';
import Dashboard from './components/Dashboard';
import authService from './services/authService';

function App() {
    const [user, setUser] = useState(null);

    useEffect(() => {

        const token = localStorage.getItem('token');
        const username = localStorage.getItem('username');

        if (token && username) {
            authService.validateToken(token)
                .then(() => {
                    setUser({ username, token });
                })
                .catch(() => {
                    localStorage.removeItem('token');
                    localStorage.removeItem('username');
                })

        }
    }, []);

    const handleLoginSuccess = (response) => {
        setUser({
            username: response.username,
            token: response.token
        });
    };


    return (
        <div className="App">
            {user ? (
                <Dashboard user={user} />
            ) : (
                <Login onLoginSuccess={handleLoginSuccess} />
            )}
        </div>
    );
}

export default App;