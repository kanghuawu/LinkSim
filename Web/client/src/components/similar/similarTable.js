import React, {Component} from 'react';
import {connect} from 'react-redux';
import {fetchSimilarPeople} from '../../actions';


class SimilarTable extends Component {

    // componentWillUnmount() {
    //     clearTimeout(this.timeout);
    // }

    componentDidMount() {
        this.props.fetchSimilarPeople();
    }

    renderTags(tags) {
        return <div>{tags.map(tag => {
            return <button className="btn btn-xs btn-default">{tag}</button>
        })}</div>
    }

    renderData() {
        return this.props.data.map(data => {
            return (
                <tr key={data.idA + data.idB}>
                    <th><div style={{"width": "100px"}}>{data.nameA}</div></th>
                    <th><div style={{"width": "200px", "fontSize": "xx-small"}}>{this.renderTags(data.urlkeyA)}</div></th>
                    <th><div style={{"width": "100px"}}>{data.nameB}</div></th>
                    <th><div style={{"width": "200px", "fontSize": "xx-small"}}>{this.renderTags(data.urlkeyB)}</div></th>
                    <th>{data.distance}</th>
                </tr>
            );
        });
    }

    render() {
        if (!this.props.data) return <div/>;
        return (
            <div className="table-responsive-md">
                <table style={{"width": "1000px"}} className="table table-hover">
                    <thead>
                    <tr>
                        <th>Name A</th>
                        <th>Tags</th>
                        <th>Name B</th>
                        <th>Tags </th>
                        <th>Distance</th>
                    </tr>
                    </thead>
                    <tbody>
                    {this.renderData()}
                    </tbody>
                </table>
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