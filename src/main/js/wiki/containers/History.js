//@flow
import React from 'react';
import {connect} from 'react-redux';
import FileBrowser from '../components/FileBrowser';
import Breadcrumb from '../components/Breadcrumb';
import {createDirectoryUrl, fetchDirectoryIfNeeded} from '../modules/directory';
import I18nAlert from '../../I18nAlert';
import Loading from '../../Loading';
import {translate} from 'react-i18next';
import {createId, fetchWikiIfNeeded} from "../modules/wiki";

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
    fetchWikiIfNeeded: (repository: string, branch: string) => void,
    fetchDirectoryIfNeeded: (url: string) => void
}

class History extends React.Component<Props> {

    componentDidMount() {
        const { url, repository, branch, fetchDirectoryIfNeeded, fetchWikiIfNeeded, page } = this.props;

        fetchDirectoryIfNeeded(url);
        fetchWikiIfNeeded(repository, branch);
    }

    componentDidUpdate() {
        this.props.fetchDirectoryIfNeeded(this.props.url);
    }


    render() {
        const { error, loading, directory, t, page } = this.props;
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
        } else if (!directory) {
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
    return parts.slice(5).join('/'); //TODO: Remove last /
}

const mapStateToProps = (state, ownProps) => {
    const { repository, branch } = ownProps.match.params;

    const path = findDirectoryPath(ownProps);
    const page = findPage(ownProps);
    const url = createDirectoryUrl(repository, branch, path);
    const wikiId = createId(repository, branch);
    const stateWiki = state.wiki[wikiId] ||{};

    let baseDirectory = '';
    if (stateWiki.wiki && stateWiki.wiki.directory) {
        baseDirectory = stateWiki.wiki.directory;
    }

    return {
        ...state.directory[url],
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
        fetchDirectoryIfNeeded: (url: string) => {
            dispatch(fetchDirectoryIfNeeded(url))
        },
        fetchWikiIfNeeded: (repository: string, branch: string) => {
            dispatch(fetchWikiIfNeeded(repository, branch))
        },
    }
};

export default translate()(connect(mapStateToProps, mapDispatchToProps)(History));
