import React, {Component} from 'react';
import Chart from './landing/chart';
import LandingTable from './landing/table';
import SimilarTable from './similar/similarTable';

export default class App extends Component {
    render() {
        return (
            <div>
                <div className="row">
                    <div className="col"/>
                    <div className="col-md-auto">
                        <SimilarTable/>
                        <Chart/>
                        <LandingTable/>
                    </div>
                    <div className="col"/>
                </div>
            </div>
        );
    }
}
