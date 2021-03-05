import React, { Component } from "react";
import injectSheet from "react-jss";
import { translate } from "react-i18next";

const styles = {};

type Props = {
  query: string;
  classes: any;
};

class SearchResultHeader extends Component<Props> {
  render() {
    const { t, query } = this.props;

    return (
      <div className="row">
        <div className="col-xs-8">
          <h1>
            {t("search-result-header_title")} <strong>{query}</strong>
          </h1>
        </div>
      </div>
    );
  }
}

export default injectSheet(styles)(translate()(SearchResultHeader));
