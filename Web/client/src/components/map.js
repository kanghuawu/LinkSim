import React, {Component} from 'react';
import {connect} from 'react-redux';
import GoogleMapReact from 'google-map-react';
import {updateGeo} from '../actions';


class SimpleMap extends Component {
    static defaultProps = {
        center: {
            lat: 37.77,
            lng: -122.41
        },
        zoom: 4
    };

    onClick(obj){
        // console.log(obj.x, obj.y, obj.lat, obj.lng, obj.event);
        this.props.updateGeo({center: {lat: obj.lat.toFixed(2), lng: obj.lng.toFixed(2)}});
    }

    render() {
        const {lat, lng} = this.props.data.center;
        return (
            <div style={{height: '90%', width: '100%'}}>
                <GoogleMapReact
                    bootstrapURLKeys={{key: 'AIzaSyCBsZTq-Yh4VcFwDs9YsHtkMpgXOpABmoI'}}
                    defaultCenter={this.props.center}
                    defaultZoom={this.props.zoom}
                    onClick={this.onClick.bind(this)}
                >
                </GoogleMapReact>
                <div className="alert alert-primary" role="alert">
                    Current Location: {lat}, {lng}
                </div>
            </div>
        );
    }
}
const mapStatesToProps = state => {
    return {
        data: state.geo,
    };
};
export default connect(mapStatesToProps, {updateGeo})(SimpleMap);