import {
    GEO
} from '../actions';

const defaultState = {
    center: {
        lat: 37.77,
        lng: -122.41
    }
};
export default (state = defaultState, action) => {
    switch (action.type) {
        case GEO:
            return {...state, ...action.payload};
        default:
            return {...state};
    }
};