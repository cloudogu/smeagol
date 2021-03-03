import React from "react";
import SearchResult from "./SearchResult";
import { SearchFindings } from "../types/searchFinding";

type Props = {
  results: SearchFindings;
  createPageLink: (arg0: string) => string;
};

class SearchResults extends React.Component<Props> {
  render() {
    const { results, createPageLink } = this.props;
    return (
      <div className="list-group">
        {results.map((result) => {
          return <SearchResult key={result.path} result={result} createPageLink={createPageLink} />;
        })}
      </div>
    );
  }
}

export default SearchResults;
