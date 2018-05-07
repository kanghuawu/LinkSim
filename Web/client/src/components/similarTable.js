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

    renderTags(own, tags) {
        // console.log(own);
        return <div>{tags.map(tag => {
            const className = `btn btn-sm ${own.includes(tag) ? 'btn-outline-success' : 'btn-outline-warning'}`;
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
                    <th>{data.distance}</th>
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
                    <div className="table-responsive">
                        <table className="table table-hover">
                            <thead>
                            <tr>
                                <th>Name</th>
                                <th>Tags</th>
                                <th>Jaccard Distance</th>
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