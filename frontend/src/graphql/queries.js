import { gql } from '@apollo/client';

export const LOGIN_MUTATION = gql`
    mutation Login($username: String!, $password: String!) {
        login(username: $username, password: $password) {
            token
            user {
                id
                username
                email
            }
        }
    }
`;

export const GET_ME_QUERY = gql`
    query GetMe {
        me {
            id
            username
            email
        }
    }
`;

export const LOGOUT_MUTATION = gql`
    mutation Logout {
        logout
    }
`;