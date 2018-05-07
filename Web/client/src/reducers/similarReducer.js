import {
    SIMILAR_PEOPLE
} from '../actions';

export default (state = {}, action) => {
    switch (action.type) {
        case SIMILAR_PEOPLE:
            return {...state, data: action.payload};
        default:
            return {...state, data: []};
    }
};