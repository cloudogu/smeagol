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
    pagehistory: any,
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
        const { error, loading, t, page, pagehistory } = this.props;
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
        } else if (!pagehistory) {
            return (
                <div>
                    <h1>Smeagol</h1>
                </div>
            );
        }

        return (
            <div>
                 <div className="page-header">
                    <h1>{  t('history_heading') + page }</h1>
                 </div>

                <table className="table table-striped">
                    <tbody>

                { pagehistory.commits.map((commit) => {
                    return (
                            <tr key={commit.commitId}>
                                <td>
                                    {commit.message}
                                </td>
                                <td>
                                    {commit.author.displayName}
                                </td>
                                <td>
                                    {commit.date}
                                </td>
                            </tr>
                    );
                }) }
                    </tbody>
                </table>

            </div>
        );
    }

}

function findDirectoryPath(props) {
    const { pathname } = props.location;
    const parts = pathname.split('/');
    return parts.slice(4).join('/');
}

function findPage(path) {
    return path.substr(0, path.length-1);
}

const mapStateToProps = (state, ownProps) => {
    const { repository, branch } = ownProps.match.params;
    const path = findDirectoryPath(ownProps);
    const page = findPage(path);
    const url = createHistoryUrl(repository, branch, page);
    const wikiId = createId(repository, branch);
    const stateWiki = state.wiki[wikiId] ||{};
    let pagehistory;
    if(state.pagehistory[url])
        pagehistory = state.pagehistory[url].pagehistory;
    return {
        pagehistory,
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