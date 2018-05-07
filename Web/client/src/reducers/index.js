import {combineReducers} from 'redux';
import {reducer as form} from 'redux-form';
import similar from './similarReducer';
import geo from './geoReducer';
import user from './userReducer';

const rootReducer = combineReducers({
    form,
    geo,
    user,
    similar
});

export default rootReducer;
