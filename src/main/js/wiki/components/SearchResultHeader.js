// @flow
import React, { Component } from 'react';
import injectSheet from 'react-jss';
import SearchBar from "./SearchBar";
import {Link} from "react-router-dom";

const styles = {
    searchBar: {
        marginTop: '20px'
    }
};

type Props = {
    query: string,
    homeLink: string,
    search: (string) => void,
    classes: any
};

class SearchResultHeader extends Component<Props> {

    render() {
        // TODO i18n
        const { query, classes, search, homeLink } = this.props;
        return (
            <div className="row">
                <div className="col-xs-8">
                    <h1>Search Results for <strong>{query}</strong></h1>
                </div>
                <div className="col-xs-4">
                    <div className={classes.searchBar}>
                        <div className="col-xs-9">
                            <SearchBar search={search} />
                        </div>
                        <Link to={homeLink} className="btn btn-primary col-xs-3">
                            Home
                        </Link>
                    </div>
                </div>
            </div>
        );
    }
}

export default injectSheet(styles)(SearchResultHeader);