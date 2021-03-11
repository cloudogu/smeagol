import React from "react";
import Logo from "ces-theme/dist/images/logo/blib-white-30px.png";
import { Link } from "react-router-dom";
import SearchBar from "./wiki/components/SearchBar";

type Props = {
  history?: any;
  repository?: string;
  branch?: string;
};

type State = {
  collapsed: boolean;
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

    let searchBar: JSX.Element;
    if (this.props.repository && this.props.branch) {
      const search = (query: string) => {
        this.props.history.push(`/${this.props.repository}/${this.props.branch}/search?query=${query}`);
      };

      searchBar = (
        <li className="form-group navbar-form">
          <SearchBar search={search} />
        </li>
      );
    }

    let navBarClasses;
    if (collapsed) {
      navBarClasses = "collapse navbar-collapse";
    } else {
      navBarClasses = "navbar-collapse";
    }

    const contextPath = process.env.PUBLIC_URL || "";

    return (
      <nav className="navbar navbar-default navbar-fixed-top">
        <div className="container">
          <div className="navbar-header">
            <button
              type="button"
              className="navbar-toggle collapsed"
              data-toggle="collapse"
              data-target="#navbar"
              aria-expanded="false"
              aria-controls="navbar"
              onClick={this.toggleCollapse}
            >
              <span className="sr-only">Toggle navigation</span>
              <span className="icon-bar"></span>
              <span className="icon-bar"></span>
              <span className="icon-bar"></span>
            </button>
            <Link className="navbar-brand" to="/">
              <img className="img-responsive" alt="Cloudogu" src={Logo} />
            </Link>
            <Link className="navbar-brand" to="/">
              Smeagol
            </Link>
          </div>
          <div className={navBarClasses}>
            <ul className="nav navbar-nav navbar-right">
              {searchBar}
              <li>
                <a href={contextPath + "/api/v1/logout"}>Logout</a>
              </li>
            </ul>
          </div>
        </div>
      </nav>
    );
  }
}

export default Navigation;
