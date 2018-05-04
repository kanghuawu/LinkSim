import React from 'react';
import ReactDOM from 'react-dom';
import {createStore, applyMiddleware} from 'redux';
import {Provider} from 'react-redux';
import thunk from 'redux-thunk';

import Router from './components/router';
import reducers from './reducers';
import {AUTH_USER} from './actions';
import '../style/style.css';

const store = createStore(reducers, {}, applyMiddleware(thunk));
const token = localStorage.getItem('token');

if (token) {
    store.dispatch({type: AUTH_USER});
}

ReactDOM.render(
    <Provider store={store}>
        <Router/>
    </Provider>,
    document.querySelector('#root')
);
