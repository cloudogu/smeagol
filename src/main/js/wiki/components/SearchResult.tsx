import React from "react";
import { Link } from "react-router-dom";
import { translate } from "react-i18next";

type Props = {
  result: any;
  createPageLink: (path: string) => string;
  t: any;
};

class SearchResult extends React.Component<Props> {
  render() {
    const { result, t } = this.props;
    const pageLink = this.props.createPageLink(result.path);
    return (
      <Link to={pageLink} className="list-group-item">
        <span className="badge" data-toggle="tooltip" title={t("search-scoring_tooltip")}>
          {result.score.toFixed(2)}
        </span>
        <h4 className="list-group-item-heading">{result.path}</h4>
        <p className="list-group-item-text" dangerouslySetInnerHTML={{ __html: result.contentFragment }} />
      </Link>
    );
  }
}

export default translate()(SearchResult);
