import React, {Component} from 'react';
import {connect} from 'react-redux'
import {Field, reduxForm} from 'redux-form';

import {fetchIdByName, fetchSimilarPeople} from '../actions';

class InputForm extends Component {
    onSubmit({id}) {
        console.log("onSubmit");
        const obj = {...this.props.geo.center, id: id};
        // console.log(this.props.geo);
        // console.log(obj);
        this.props.fetchSimilarPeople(obj);
    }

    onSearch({name}) {
        console.log("onSearch");
        console.log(name);
        this.props.fetchIdByName(name);
    }

    redenderOptions(ids) {
        if (!ids) {
            return <option value=""></option>
        }
        return ids.map(id => {
            return <option key={id} value={id}>{id}</option>
        })
    }

    render() {
        const {handleSubmit} = this.props;
        return (
            <div>
                <form >
                    <div className="row" style={{padding: '5px'}}>
                        <div className="col">
                            <Field
                                className="form-control"
                                type="input"
                                placeholder="Your Name ex: Kang-Hua Wu"
                                name="name"
                                component="input"/>
                        </div>
                        <div className="col-3">
                            <button
                                className="btn btn-dark"
                                onClick={handleSubmit(this.onSearch.bind(this))}
                            >
                                Search
                            </button>
                        </div>

                    </div>
                    <div className="row" style={{padding: '5px'}}>
                        <div className="col">
                            <Field
                                className="form-control"
                                name="id"
                                component="select">
                                <option value=""></option>
                                {this.redenderOptions(this.props.data)}
                            </Field>
                        </div>
                        <div className="col-3">
                            <button
                                className="btn btn-dark"
                                onClick={handleSubmit(this.onSubmit.bind(this))}
                            >
                                Submit
                            </button>
                        </div>
                    </div>
                </form>
            </div>
        );
    }
}

const mapStatesToProps = state => {
    return {
        data: state.user.data,
        geo: state.geo,
    };
};

export default connect(mapStatesToProps,{fetchIdByName, fetchSimilarPeople})(reduxForm({form: 'name'})(InputForm));