import React, {Component} from 'react';
import SimilarTable from './similar/similarTable';

export default class App extends Component {
    render() {
        return (
            <div>
                <div className="row">
                    <div className="col"/>
                    <div className="col-md-auto">
                        <SimilarTable/>
                    </div>
                    <div className="col"/>
                </div>
            </div>
        );
    }
}
