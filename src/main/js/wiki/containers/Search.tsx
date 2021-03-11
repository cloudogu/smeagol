import React, { FC } from "react";
import * as queryString from "query-string";
import { useSearch } from "../hooks/useSearch";
import { useWiki } from "../hooks/wiki";
import SearchResults from "../components/SearchResults";
import SearchResultHeader from "../components/SearchResultHeader";
import { match } from "react-router";
import { SearchFindings } from "../types/searchFinding";
import WikiHeader from "../components/WikiHeader";
import WikiLoadingPage from "../components/WikiLoadingPage";
import WikiAlertPage from "../components/WikiAlertPage";

type Params = {
  repository: string;
  branch: string;
};

type Props = {
  history: any;
  match: match<Params>;
  location: Location;
};

const Search: FC<Props> = (props) => {
  const { repository, branch } = props.match.params;
  const query = getQuery(props);

  const searchQuery = useSearch(repository, branch, query);
  const wikiQuery = useWiki(repository, branch);

  const isLoading = searchQuery.isLoading || wikiQuery.isLoading;

  const createPageLink = (path: string) => {
    const { repository, branch } = props.match.params;
    return `/${repository}/${branch}/${path}`;
  };

  const createHomeLink = (wiki: any) => {
    const { repository, branch } = props.match.params;
    return `/${repository}/${branch}/${wiki.landingPage}`;
  };

  let results: SearchFindings;
  if (!searchQuery.data) {
    results = [];
  } else {
    results = searchQuery.data;
  }

  if (isLoading) {
    return <WikiLoadingPage />;
  }
  if (searchQuery.error || wikiQuery.error || !wikiQuery.data) {
    return <WikiAlertPage i18nKey={"search_failed_to_fetch"} />;
  }
  const homeLink = createHomeLink(wikiQuery.data);
  return (
    <div>
      <WikiHeader branch={branch} repository={repository} wiki={wikiQuery.data} />
      <hr />
      <SearchResultHeader query={query} homeLink={homeLink} />
      <hr />
      <SearchResults results={results} createPageLink={createPageLink} />
    </div>
  );
};
export default Search;

const getQuery = (props) => {
  const queryParams = queryString.parse(props.location.search);
  return queryParams["query"];
};
