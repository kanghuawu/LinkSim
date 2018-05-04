import React, {Component} from 'react';
import {BrowserRouter, Route, Switch} from 'react-router-dom';

import Header from './header';
import RequireAuth from './auth/requireAuth';
import App from './app';
import Second from './second';

export default () => {
    return (
        <BrowserRouter>
            <div className="container" >
                <Header/>
                <div style={{padding: 10}}>
                    <Switch>
                        <Route path="/second" component={Second}/>
                        <Route path="/" component={App}/>
                    </Switch>
                </div>
            </div>
        </BrowserRouter>
    );
};