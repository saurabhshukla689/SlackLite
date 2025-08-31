import React, { useState } from 'react';
import { useMutation } from '@apollo/client/react';
import { useNavigate } from 'react-router-dom';
import { LOGIN_MUTATION } from '../graphql/queries';

const Login = () => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [login, { loading, error }] = useMutation(LOGIN_MUTATION);
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const { data } = await login({
                variables: { username, password }
            });

            // Store token and user data


            localStorage.setItem('token', data.login.token);
            localStorage.setItem('user', JSON.stringify(data.login.user));

            // Redirect to dashboard
            navigate('/dashboard');
        } catch (err) {
            console.error('Login failed:', err);
        }
    };

    return (
        <div style={styles.container}>
            <form onSubmit={handleSubmit} style={styles.form}>
                <h2>SlackLite Login</h2>
                <input
                    type="text"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                    placeholder="Username"
                    style={styles.input}
                    required
                />
                <input
                    type="password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    placeholder="Password"
                    style={styles.input}
                    required
                />
                <button type="submit" disabled={loading} style={styles.button}>
                    {loading ? 'Logging in...' : 'Login'}
                </button>
                {error && <p style={styles.error}>{error.message}</p>}
            </form>
        </div>
    );
};

const styles = {
    container: {
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        minHeight: '100vh',
        backgroundColor: '#f5f5f5'
    },
    form: {
        backgroundColor: 'white',
        padding: '2rem',
        borderRadius: '8px',
        boxShadow: '0 2px 10px rgba(0,0,0,0.1)',
        width: '300px'
    },
    input: {
        width: '100%',
        padding: '0.5rem',
        margin: '0.5rem 0',
        border: '1px solid #ddd',
        borderRadius: '4px'
    },
    button: {
        width: '100%',
        padding: '0.5rem',
        backgroundColor: '#007bff',
        color: 'white',
        border: 'none',
        borderRadius: '4px',
        cursor: 'pointer'
    },
    error: {
        color: 'red',
        marginTop: '1rem'
    }
};

export default Login;