import axios from 'axios';

// const ROOT_URL = 'http://localhost:8077';
const ROOT_URL = '';

export const SIMILAR_PEOPLE = 'similar_people';
export const GEO = 'geo';

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

export function updateGeo(location) {
    return dispatch => {
        dispatch({type: GEO, payload: location});
    }
}