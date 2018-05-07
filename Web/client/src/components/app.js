import React, {Component} from 'react';
import SimilarTable from './similarTable';
import SimpleMap from './map';

export default class App extends Component {
    render() {
        return (
            <div>
                <div className="row" style={{padding: '30px'}}>
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
                <div className="row" style={{height: '800px'}}>
                    <SimilarTable/>
                </div>
            </div>

        );
    }
}
