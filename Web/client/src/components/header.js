import React, {Component} from 'react';
import {Link} from 'react-router-dom';
import {Navbar, NavbarBrand, NavbarToggler} from 'reactstrap';


class Header extends Component {
    constructor(props) {
        super(props);

        this.toggle = this.toggle.bind(this);
        this.toggleOff = this.toggleOff.bind(this);
        this.state = {
            isOpen: false
        };
    }

    toggle() {
        this.setState({
            isOpen: !this.state.isOpen
        });
    }

    toggleOff() {
        this.setState({
            isOpen: false
        });
    }

    render() {
        return (
            <div>
                <Navbar color="light" light expand="md">
                    <NavbarToggler onClick={this.toggle}/>
                    <NavbarBrand to="/" tag={Link}>
                        LinkSim
                    </NavbarBrand>
                </Navbar>
            </div>
        );
    }
}

export default Header;
