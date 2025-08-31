import React from 'react';
import { useMutation, useQuery } from '@apollo/client/react';
import { useNavigate } from 'react-router-dom';
import { LOGOUT_MUTATION, GET_ME_QUERY } from '../graphql/queries';

const Dashboard = () => {
    const { data: userData, loading } = useQuery(GET_ME_QUERY);
    const [logout] = useMutation(LOGOUT_MUTATION);
    const navigate = useNavigate();

    const handleLogout = async () => {
        try {
            await logout();
            localStorage.removeItem('token');
            localStorage.removeItem('user');
            navigate('/login');
        } catch (error) {
            console.error('Logout error:', error);
        }
    };

    if (loading) return <div>Loading...</div>;

    return (
        <div style={styles.container}>
            <div style={styles.dashboard}>
                <h2>Welcome, {userData?.me?.username}</h2>
                <p>GraphQL Dashboard - Login successful!</p>
                <button onClick={handleLogout} style={styles.logoutButton}>
                    Logout
                </button>
            </div>
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
    dashboard: {
        backgroundColor: 'white',
        padding: '2rem',
        borderRadius: '8px',
        boxShadow: '0 2px 10px rgba(0,0,0,0.1)',
        textAlign: 'center'
    },
    logoutButton: {
        padding: '0.5rem 1rem',
        backgroundColor: '#dc3545',
        color: 'white',
        border: 'none',
        borderRadius: '4px',
        cursor: 'pointer'
    }
};

export default Dashboard;