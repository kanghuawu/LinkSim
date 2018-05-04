import {combineReducers} from 'redux';
import {reducer as form} from 'redux-form';
import auth from './authReducer';
import countryByDate from './countryByDateReducer';
import similar from './similarReducer';

const rootReducer = combineReducers({
    form,
    auth,
    countryByDate,
    similar
});

export default rootReducer;
