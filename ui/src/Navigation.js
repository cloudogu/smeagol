//@flow
import React from 'react';
import Logo from 'ces-theme/dist/images/logo/blib-white-30px.png'

type Props = {};

class Navigation extends React.Component<Props> {

    render() {
        // TODO links
        return (
            <nav className="navbar navbar-default navbar-fixed-top">
                <div className="container">
                    <div className="navbar-header">
                        <button type="button" className="navbar-toggle collapsed" data-toggle="collapse"
                                data-target="#navbar" aria-expanded="false" aria-controls="navbar">
                            <span className="sr-only">Toggle navigation</span>
                            <span className="icon-bar"></span>
                            <span className="icon-bar"></span>
                            <span className="icon-bar"></span>
                        </button>
                        <a className="navbar-brand" href="">
                            <img className="img-responsive" alt="Cloudogu"
                                 src={ Logo } />
                        </a>
                        <a className="navbar-brand" href="">
                            Smeagol
                        </a>
                    </div>
                    <div className="collapse navbar-collapse">
                        <ul className="nav navbar-nav">
                            <li>
                                <a href="">
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

    export default Navigation;
