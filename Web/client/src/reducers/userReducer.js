import {
    USER_ID_BY_NAME
} from '../actions';

export default (state = {}, action) => {
    switch (action.type) {
        case USER_ID_BY_NAME:
            return {...state, data: action.payload};
        default:
            return {...state};
    }
};