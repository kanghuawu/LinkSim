import React, {Component} from 'react';
import {connect} from 'react-redux';
import {fetchCountryByDate} from '../../actions';


class LandingTable extends Component {
    componentWillReceiveProps(nextProps) {
        if (this.props.data != nextProps.data) {
            clearTimeout(this.timeout);

            if (!nextProps.isFetching) {
                this.startPoll();
            }
        }
    }

    componentWillUnmount() {
        clearTimeout(this.timeout);
    }

    componentDidMount() {
        this.props.fetchCountryByDate();
    }

    startPoll() {
        console.log("fetching");
        this.timeout = setTimeout(() => this.props.fetchCountryByDate(), 1000);
    }

    renderData() {
        return this.props.data.map(data => {
            return (
                <tr key={data.groupCountry + data.eventID}>
                    <th>{data.groupCountry}</th>
                    <th>{data.total}</th>
                    <th>{data.groupCity}</th>
                    <th>{data.eventName}</th>
                    <th>{data.count}</th>
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
                        <th>Group Country</th>
                        <th>Total</th>
                        <th>City</th>
                        <th>Most Popular Event</th>
                        <th>Count</th>
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
        data: state.countryByDate.data,
        isFetching: state.countryByDate.isFetching
    };
};

export default connect(mapStatesToProps, {fetchCountryByDate})(LandingTable);