import React, {Component} from 'react';
import clock from '../../assets/clock.png';

export default class Second extends Component {
    render() {
        return (
            <div>
                <h1>Clock</h1>
                <img src={clock}/>
            </div>
        );
    }
}
