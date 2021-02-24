import React, { Component } from "react";
import injectSheet from "react-jss";
import SearchBar from "./SearchBar";
import { Link } from "react-router-dom";
import { translate } from "react-i18next";

const styles = {
  searchBar: {
    marginTop: "20px"
  }
};

type Props = {
  query: string;
  homeLink: string;
  search: (arg0: string) => void;
  classes: any;
};

class SearchResultHeader extends Component<Props> {
  render() {
    const { query, classes, search, homeLink, t } = this.props;

    return (
      <div className="row">
        <div className="col-xs-8">
          <h1>
            {t("search-result-header_title")} <strong>{query}</strong>
          </h1>
        </div>
        <div className="col-xs-4">
          <div className={classes.searchBar}>
            <div className="col-xs-9">
              <SearchBar search={search} />
            </div>
            <Link to={homeLink} className="btn btn-primary col-xs-3">
              {t("page-header_home")}
            </Link>
          </div>
        </div>
      </div>
    );
  }
}

export default injectSheet(styles)(translate()(SearchResultHeader));
