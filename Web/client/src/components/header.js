import React, {Component} from 'react';
import {connect} from 'react-redux';
import {Link} from 'react-router-dom';
import {
    Collapse,
    Navbar,
    NavbarToggler,
    NavbarBrand,
    Nav,
    NavItem,
    NavLink
} from 'reactstrap';


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

    renderLinks() {
        if (this.props.authenticated) {
            return [
                <NavItem key={6}>
                    <NavLink to="/signout" tag={Link} onClick={this.toggleOff}>
                        Sign out
                    </NavLink>
                </NavItem>,
                <NavItem key={7}>
                    <NavLink to="/transaction" tag={Link} onClick={this.toggleOff}>
                        Transaction
                    </NavLink>
                </NavItem>
            ];
        } else {
            return [
                <NavItem key={8}>
                    <NavLink to="/signin" tag={Link} onClick={this.toggleOff}>
                        Sign In
                    </NavLink>
                </NavItem>,
                <NavItem key={9}>
                    <NavLink to="/signup" tag={Link} onClick={this.toggleOff}>
                        Sign Up
                    </NavLink>
                </NavItem>
            ];
        }
    }

    render() {
        return (
            <div>
                <Navbar color="light" light expand="md">
                    <NavbarToggler onClick={this.toggle}/>
                    <NavbarBrand to="/" tag={Link}>
                        Hello
                    </NavbarBrand>
                    <Collapse isOpen={this.state.isOpen} navbar>
                        <Nav className="ml-auto" navbar>
                            <NavItem key={1}>
                                <NavLink to="/search" tag={Link} onClick={this.toggleOff}>
                                    Search
                                </NavLink>
                            </NavItem>
                            <NavItem key={2}>
                                <NavLink to="/admin" tag={Link} onClick={this.toggleOff}>
                                    Admin
                                </NavLink>
                            </NavItem>
                            {this.renderLinks()}
                        </Nav>
                    </Collapse>
                </Navbar>
            </div>
        );
    }
}

const mapStateToProps = state => {
    return {
        authenticated: state.auth.authenticated
    };
};

export default connect(mapStateToProps)(Header);
