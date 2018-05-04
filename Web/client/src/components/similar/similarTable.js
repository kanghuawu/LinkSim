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

    renderData() {
        return this.props.data.map(data => {
            return (
                <tr key={data.ida + data.idb}>
                    <th>{data.ida}</th>
                    <th>{data.namea}</th>
                    <th>{data.idb}</th>
                    <th>{data.nameb}</th>
                    <th>{data.distance}</th>
                </tr>
            );
        });
    }

    render() {
        if (!this.props.data) return <div/>;
        return (
            <div className="table-responsive-md">
                <table className="table table-hover">
                    <thead>
                    <tr>
                        <th>ID A</th>
                        <th>Name A</th>
                        <th>ID B</th>
                        <th>Name B</th>
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