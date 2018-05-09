import axios from 'axios';

// const ROOT_URL = 'http://localhost:8077';
const ROOT_URL = '';

export const SIMILAR_PEOPLE = 'similar_people';
export const GEO = 'geo';
export const USER_ID_BY_NAME = 'user_id_by_name';

export function fetchSimilarPeople({id, lat, lng}) {
    return dispatch => {
        axios.get(`${ROOT_URL}/similar/${id}/${lat}/${lng}`)
            .then(res => {
                console.log(res.data);
                dispatch({type: SIMILAR_PEOPLE, payload: res.data});
            })
            .catch(err => {
                console.log(err);
            });
    };
}

export function updateGeo(location) {
    return dispatch => {
        dispatch({type: GEO, payload: location});
    }
}

export function fetchIdByName(name) {
    return dispatch => {
        axios.get(`${ROOT_URL}/user/name/${name}`)
            .then(res => {
                console.log(res.data);
                dispatch({type: USER_ID_BY_NAME, payload: res.data});
            })
            .catch(err => {
                console.log(err);
            });
    }
}
