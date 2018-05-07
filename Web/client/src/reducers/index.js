import {combineReducers} from 'redux';
import {reducer as form} from 'redux-form';
import similar from './similarReducer';
import geo from './geoReducer';

const rootReducer = combineReducers({
    form,
    geo,
    similar
});

export default rootReducer;
