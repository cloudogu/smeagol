//@flow
import React from 'react';
import {connect} from 'react-redux';
import I18nAlert from '../../I18nAlert';
import Loading from '../../Loading';
import {translate} from 'react-i18next';
import {createId, fetchWikiIfNeeded} from "../modules/wiki";
import {createHistoryUrl, fetchHistoryIfNeeded} from "../modules/pagehistory"

type Props = {
    loading: boolean,
    error: Error,
    directory: any,
    repository: string,
    branch: string,
    path: string,
    page: string,
    url: string,
    t: any,
    history: any,
    fetchWikiIfNeeded: (repository: string, branch: string) => void,
    fetchHistoryIfNeeded: (url: string) => void
}

class History extends React.Component<Props> {

    componentDidMount() {
        const { url, repository, branch, fetchHistoryIfNeeded, fetchWikiIfNeeded } = this.props;

        fetchHistoryIfNeeded(url);
        fetchWikiIfNeeded(repository, branch);
    }

    componentDidUpdate() {
        this.props.fetchHistoryIfNeeded(this.props.url);
    }


    render() {
        const { error, loading, t, page, history } = this.props;
        if (error) {
            return (
                <div>
                    <h1>Smeagol</h1>
                    <I18nAlert i18nKey="directory_failed_to_fetch" />
                </div>
            );
        } else if (loading) {
            return (
                <div>
                    <h1>Smeagol</h1>
                    <Loading/>
                </div>
            );
        } else if (!history) {
            return (
                <div>
                    <h1>Smeagol</h1>
                </div>
            );
        }

        return (
            <div>
                <h1>{  t('history_heading') + page }</h1>
            </div>
        );
    }

}

function findDirectoryPath(props) {
    const { pathname } = props.location;
    const parts = pathname.split('/');
    return parts[4];
}

function findPage(props) {
    const { pathname } = props.location;
    const parts = pathname.split('/');
    const pageName = parts.slice(5).join('/');
    return pageName.substr(0, pageName.indexOf('/'));
}

const mapStateToProps = (state, ownProps) => {
    const { repository, branch } = ownProps.match.params;
console.log(state);
    const path = findDirectoryPath(ownProps);
    const page = findPage(ownProps);
    const url = createHistoryUrl(repository, branch, page);
    const wikiId = createId(repository, branch);
    const stateWiki = state.wiki[wikiId] ||{};

    let baseDirectory = '';
    /*if (stateWiki.wiki && stateWiki.wiki.directory) {
        baseDirectory = stateWiki.wiki.directory;
    }*/
    return {
       ...state.pagehistory[url],
        baseDirectory,
        repository,
        branch,
        url,
        path,
        page
    }
};

const mapDispatchToProps = (dispatch) => {
    return {
        fetchHistoryIfNeeded: (url: string) => {
            dispatch(fetchHistoryIfNeeded(url))
        },
        fetchWikiIfNeeded: (repository: string, branch: string) => {
            dispatch(fetchWikiIfNeeded(repository, branch))
        },
    }
};

export default translate()(connect(mapStateToProps, mapDispatchToProps)(History));
