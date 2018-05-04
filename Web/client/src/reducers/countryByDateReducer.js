import {
    COUNTRY_BY_DATE
} from '../actions';

const initialState = {
    data: [],
    isFetching: false
};

export default (state = initialState, action) => {
    switch (action.type) {
        case COUNTRY_BY_DATE:
            action.payload.forEach(e => {
                e.groupCountry = e.groupCountry.toUpperCase();
            });
            // return {...state, data: action.payload, isFetching: true};
            return {...state, data: action.payload};
        default:
            return {...state};
    }
};