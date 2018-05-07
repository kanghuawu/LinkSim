import React, {Component} from 'react';
import {BrowserRouter, Route, Switch} from 'react-router-dom';

import Header from './header';
import App from './app';

export default () => {
    return (
        <BrowserRouter>
            <div>
                <Header/>
                <Switch>
                    <Route path="/" component={App}/>
                </Switch>
            </div>
        </BrowserRouter>
    );
};