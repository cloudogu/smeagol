//@flow
import React from 'react';
import {connect} from "react-redux";
import * as queryString from "query-string";
import {createSearchUrl, fetchSearchResultsIfNeeded} from "../modules/search";
import {createId, fetchWikiIfNeeded} from '../modules/wiki';
import Loading from "../../Loading";
import I18nAlert from "../../I18nAlert";
import SearchResults from "../components/SearchResults";
import SearchResultHeader from "../components/SearchResultHeader";

type Props = {
    loading: boolean,
    error: Error,
    results: any,
    url: string,
    fetchSearchResultsIfNeeded: (string) => void,
    wiki: any
};

class Search extends React.Component<Props> {

    componentDidMount() {
        const { url, repository, branch } = this.props;
        this.props.fetchSearchResultsIfNeeded(url);
        this.props.fetchWikiIfNeeded(repository, branch);
    }

    componentDidUpdate() {
        const { url } = this.props;
        this.props.fetchSearchResultsIfNeeded(url);
    }

    createPageLink = (path: string) => {
        const { repository, branch } = this.props;
        return `/${repository}/${branch}/${path}`;
    };

    search = (query: string) => {
        const { history } = this.props;
        history.push(`?query=${query}`);
    };

    createHomeLink = () => {
        const { repository, branch, wiki } = this.props;
        return `/${repository}/${branch}/${wiki.landingPage}`;
    };

    render() {
        const { loading, error, query } = this.props;
        const homeLink = this.createHomeLink();
        let results = this.props.results;

        if (!results) {
            results = [];
        }

        // TODO i18n
        if (error) {
            return (
                <div>
                    <h1>Smeagol</h1>
                    <I18nAlert i18nKey="search_failed_to_fetch" />
                </div>
            );
        } else if (loading) {
            return (
                <div>
                    <h1>Smeagol</h1>
                    <Loading/>
                </div>
            );
        } else {
            return (
                <div>
                    <SearchResultHeader query={query} search={this.search} homeLink={homeLink} />
                    <hr />
                    <SearchResults results={results} createPageLink={this.createPageLink}/>
                </div>
            );
        }
    }

}

const getQuery = (props) => {
    const queryParams = queryString.parse(props.location.search);
    return queryParams['query'];
};

const mapStateToProps = (state, ownProps) => {
    const { repository, branch } = ownProps.match.params;
    const query = getQuery(ownProps);
    const url = createSearchUrl(repository, branch, query);
    const wikiId = createId(repository, branch);
    const stateWiki = state.wiki[wikiId] || {};

    return {
        ...state.search[url],
        repository,
        branch,
        query,
        url,
        wiki: stateWiki.wiki || {},
    }
};

const mapDispatchToProps = (dispatch) => {
    return {
        fetchSearchResultsIfNeeded: (url: string) => {
            dispatch(fetchSearchResultsIfNeeded(url))
        },
        fetchWikiIfNeeded: (repository: string, branch: string) => {
            dispatch(fetchWikiIfNeeded(repository, branch))
        }
    }
};

export default (connect(mapStateToProps, mapDispatchToProps)(Search));
