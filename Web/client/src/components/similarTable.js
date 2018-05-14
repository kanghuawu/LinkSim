import React, {Component} from 'react';
import {connect} from 'react-redux';
import SimpleMap from './map';
import InputForm from './inputForm';
import {fetchSimilarPeople} from '../actions/index';


class SimilarTable extends Component {
    constructor(props) {
        super(props);
    }

    componentDidMount() {
        // this.props.fetchSimilarPeople();
    }

    renderOwnTags() {
        if (!this.props.data) {
            return <div/>;
        }
        const className = 'btn btn-sm btn-outline-success';
        return this.props.data[0].urlkeyA.map(tag => {
            return <button className={className} key={tag}>{tag}</button>
        })
    }

    renderTags(own, tags) {
        // console.log(own);
        return <div>{tags.map(tag => {
            const className = `btn btn-sm ${own.includes(tag) ? 'btn-outline-success' : 'btn-outline-warning'}`;
            // const className = 'btn btn-sm btn-outline-success';
            return <button className={className} key={tag}>{tag}</button>
        })}</div>
    }

    renderData() {
        if (!this.props.data) {
            return (
                <tr>
                    <th>No data yet...</th>
                </tr>
            );
        }
        return this.props.data.map(data => {
            return (
                <tr key={data.idA.toString() + data.idB.toString()}>
                    <th>{data.nameB}</th>
                    <th>{this.renderTags(data.urlkeyA, data.urlkeyB)}</th>
                    <th>{(1 - data.distance).toFixed(2)}</th>
                </tr>
            );
        });
    }

    render() {
        return (
            <div className="row" style={{height: '800px', padding: '20px'}}>
                <div className="col">
                    <div style={{height: '500px', width: '500px'}}>
                        <SimpleMap/>
                    </div>
                    <InputForm/>
                </div>
                <div className="col">
                    <div className="card">
                        <h3 className="card-title">Your Tags</h3>
                        <div className="card-body">
                            {this.renderOwnTags()}
                        </div>
                    </div>
                    <div className="table-responsive" style={{marginTop: '10px'}}>
                        <table className="table table-hover">
                            <thead>
                            <tr>
                                <th>Name</th>
                                <th>Tags</th>
                                <th>Similarity</th>
                            </tr>
                            </thead>
                            <tbody>
                            {this.renderData()}
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        );
    }
}

const mapStatesToProps = state => {
    return {
        data: state.similar.data,
    };
};

export default connect(mapStatesToProps, {fetchSimilarPeople})(SimilarTable);