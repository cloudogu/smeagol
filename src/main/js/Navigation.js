//@flow
import React from 'react';
import Logo from 'ces-theme/dist/images/logo/blib-white-30px.png'
import {Link} from 'react-router-dom';

type Props = {};

type State = {
    collapsed: boolean
};

class Navigation extends React.Component<Props, State> {

    constructor(props: Props) {
        super(props);
        this.state = {
            collapsed: true
        };
    }

    toggleCollapse = () => {
        this.setState({
            collapsed: !this.state.collapsed
        });
    };

    render() {
        const { collapsed } = this.state;

        let navBarClasses;
        if (collapsed) {
            navBarClasses = "collapse navbar-collapse";
        } else {
            navBarClasses = "navbar-collapse"
        }

        const contextPath = process.env.PUBLIC_URL || '';

        return (
            <nav className="navbar navbar-default navbar-fixed-top">
                <div className="container">
                    <div className="navbar-header">
                        <button type="button" className="navbar-toggle collapsed" data-toggle="collapse"
                                data-target="#navbar" aria-expanded="false" aria-controls="navbar"
                                onClick={this.toggleCollapse}>
                            <span className="sr-only">Toggle navigation</span>
                            <span className="icon-bar"></span>
                            <span className="icon-bar"></span>
                            <span className="icon-bar"></span>
                        </button>
                        <Link className="navbar-brand" to="/">
                            <img className="img-responsive" alt="Cloudogu" src={Logo}/>
                        </Link>
                        <Link className="navbar-brand" to="/">
                            Smeagol
                        </Link>
                    </div>
                    <div className={navBarClasses}>
                        <ul className="nav navbar-nav">
                            <li>
                                {/* TODO context path */}
                                <a href={contextPath + "/api/v1/logout" }>
                                    Logout
                                </a>
                            </li>
                        </ul>
                    </div>
                </div>
            </nav>
        );
    }

}

export function pathWithTrailingSlash(path) {
    var lastChar = path.substr(-1);
    if (lastChar !== '/') {
        path = path + '/';
    }
    return path;
}

export default Navigation;
