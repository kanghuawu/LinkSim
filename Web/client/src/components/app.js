import React, {Component} from 'react';
import SimilarTable from './similarTable';

export default class App extends Component {
    render() {
        return (
            <div style={{padding: '30px'}}>
                <div className="row" >
                    <div className="card" style={{width: '100%'}}>
                        <div className="card-body">
                            <h3>How to use:</h3>
                            <ol>
                                <li>Click on map</li>
                                <li>Enter your name</li>
                                <li>Click search</li>
                                <li>Have a look at table</li>
                            </ol>
                        </div>
                    </div>
                </div>
                <SimilarTable/>
            </div>

        );
    }
}
