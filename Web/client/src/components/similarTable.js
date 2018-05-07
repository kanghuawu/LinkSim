import React, {Component} from 'react';
import {connect} from 'react-redux';
import {Field, reduxForm} from 'redux-form';
import SimpleMap from './map';
import {fetchSimilarPeople} from '../actions/index';

const renderField = field => {
    const {meta: {touched, error}} = field;
    const className = `form-group ${touched && error ? 'has-danger' : ''}`;
    return (
        <div className={className}>
            <label>
                {field.label}
            </label>
            <input
                className="form-control is-valid"
                type={field.type}
                placeholder={field.placeholder}
                {...field.input}
            />
            <div className="invalid-feedback">
                {touched ? error : ''}
            </div>
        </div>
    );
};

class SimilarTable extends Component {
    constructor(props) {
        super(props);
    }

    componentDidMount() {
        this.props.fetchSimilarPeople();
    }

    renderTags(own, tags) {
        // console.log(own);
        return <div>{tags.map(tag => {
            const className = `btn btn-sm ${own.includes(tag) ? 'btn-outline-success' : 'btn-outline-warning'}`;
            return <button className={className} key={tag}>{tag}</button>
        })}</div>
    }

    renderData() {
        return this.props.data.map(data => {
            return (
                <tr key={data.idA.toString() + data.idB.toString()}>
                    <th>{data.nameA}</th>
                    <th>{this.renderTags(data.urlkeyA, data.urlkeyB)}</th>
                    <th>{data.distance}</th>
                </tr>
            );
        });
    }

    onSubmit(prop) {
        console.log(prop);
    }

    render() {
        const {handleSubmit} = this.props;
        return (
            <div className="row">
                <div className="col">
                    <div style={{height: '500px', width: '500px'}}>
                        <SimpleMap/>
                    </div>

                    <div style={{marginBottom: '20px'}}>

                        <form onSubmit={handleSubmit(this.onSubmit.bind(this))}>
                            <Field
                                className="form-control"
                                type="input"
                                placeholder="Your Name ex: Kang-Hua Wu"
                                name="name"
                                component={renderField}/>
                            <button
                                className="btn btn-dark"
                                type="submit">
                                Search
                            </button>
                        </form>
                    </div>
                </div>
                <div className="col">
                    <div className="table-responsive">
                        <table className="table table-hover">
                            <thead>
                            <tr>
                                <th>Name</th>
                                <th>Tags</th>
                                <th>Distance</th>
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

export default connect(mapStatesToProps, {fetchSimilarPeople})(reduxForm({form: 'name'})(SimilarTable));