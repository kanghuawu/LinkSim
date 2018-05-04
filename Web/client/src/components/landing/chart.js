import React, {Component} from 'react';
import {connect} from 'react-redux';
import {ResponsiveContainer, PieChart, Pie, Cell, Tooltip} from 'recharts';
import {scaleOrdinal, schemeCategory10} from 'd3-scale';
import {fetchCountryByDate} from '../../actions';

const colors = scaleOrdinal(schemeCategory10).range();

class Chart extends Component {
    renderLabelContent(data) {
        const {groupCountry, percent, value, x, y, midAngle} = data;

        return (
            <g transform={`translate(${x}, ${y})`} textAnchor={(midAngle < -90 || midAngle >= 90) ? 'end' : 'start'}>
                <text x={0} y={5}>{`${groupCountry}`}</text>
            </g>
        );
    };

    render() {
        if (!this.props.data) return <div/>;
        let data = [...this.props.data].sort((a, b) => b.total - a.total);
        let beyongTop10 = 0;
        for (let i = 9; i < data.length; i++) {
            beyongTop10 += data[i].total;
        }
        data = data.slice(0, 9);
        data.push({'groupCountry': 'Other', 'total': beyongTop10});
        return (
            <div style={{height: '500px'}}>
                <ResponsiveContainer>
                    <PieChart>
                        <Pie data={data} dataKey="total" nameKey="groupCountry" cx="50%" cy="50%" innerRadius="40%"
                             outerRadius="80%" label={this.renderLabelContent.bind(this)}>
                            {
                                data.map((entry, index) => (
                                    <Cell key={`slice-${index}`} fill={colors[index % 10]}/>
                                ))
                            }
                        </Pie>
                        <Tooltip />
                    </PieChart>
                </ResponsiveContainer>
            </div>
        );
    }
}

const mapStatesToProps = state => {
    return {
        data: state.countryByDate.data
    };
};

export default connect(mapStatesToProps, {fetchCountryByDate})(Chart);