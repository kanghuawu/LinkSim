import axios from 'axios';

// const ROOT_URL = 'http://localhost:8080';
const ROOT_URL = '';

export const AUTH_USER = 'auth_user';
export const UNAUTH_USER = 'unauth_user';
export const AUTH_ERROR = 'auth_error';
export const CLEAR_AUTH_ERROR = 'clear_auth_error';

export const COUNTRY_BY_DATE = 'country_by_date';

export const SIMILAR_PEOPLE = 'similar_people';

export function fetchCountryByDate() {
    return dispatch => {
        axios.get(`${ROOT_URL}/world`)
            .then(res => {
                dispatch({type: COUNTRY_BY_DATE, payload: res.data});
            })
            .catch(err => {
                console.log(err);
            });
    };
}

export function fetchSimilarPeople() {
    return dispatch => {
        axios.get(`${ROOT_URL}/similar`)
            .then(res => {
                dispatch({type: SIMILAR_PEOPLE, payload: res.data});
            })
            .catch(err => {
                console.log(err);
            });
    };
}