//@flow
import React from 'react';
import {connect} from 'react-redux';
import FileBrowser from '../components/FileBrowser';
import Breadcrumb from '../components/Breadcrumb';
import {createDirectoryUrl, fetchDirectoryIfNeeded} from '../modules/directory';
import I18nAlert from '../../I18nAlert';
import Loading from '../../Loading';
import {translate} from 'react-i18next';

type Props = {
    loading: boolean,
    error: Error,
    directory: any,
    repository: string,
    branch: string,
    path: string,
    url: string,
    t: any,
    fetchDirectoryIfNeeded: (url: string) => void
}

class Directory extends React.Component<Props> {

    componentDidMount() {
        const {url, repository, branch, fetchDirectoryIfNeeded, fetchWikiIfNeeded} = this.props;

        fetchDirectoryIfNeeded(url);
        fetchWikiIfNeeded(repository, branch);
    }

    componentDidUpdate() {
        this.props.fetchDirectoryIfNeeded(this.props.url);
    }

    createDirectoryLink = (path: string) => {
        const {repository, branch} = this.props;
        return `/${repository}/${branch}/pages/${path}`;
    };

    createPageLink = (path: string) => {
        const {repository, branch} = this.props;
        return `/${repository}/${branch}/${path}`;
    };

    createLink = (directory: any, file: any) => {
        let path = this.endingSlash(directory.path) + file.name;

        if (file.type === 'directory') {
            return this.createDirectoryLink(this.endingSlash(path));
        } else if (file.type === 'page') {
            return this.createPageLink(path);
        } else {
            return '#';
        }
    };

    endingSlash = (value: string) => {
        // TODO check polyfil
        if (!value.endsWith('/')) {
            return value + '/';
        }
        return value;
    };

    render() {
        const {path, error, loading, directory, t} = this.props;

        if (error) {
            return (
                <div>
                    <h1>Smeagol</h1>
                    <I18nAlert i18nKey="directory_failed_to_fetch"/>
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
                <h1>{t('directory_heading')}</h1>
                <Breadcrumb path={path} createLink={this.createDirectoryLink}/>
                <FileBrowser directory={directory} createLink={this.createLink}/>
            </div>
        );
    }

}

function findDirectoryPath(props) {
    const {pathname} = props.location;
    const parts = pathname.split('/');
    return parts.slice(4).join('/');
}

const mapStateToProps = (state, ownProps) => {
    const {repository, branch} = ownProps.match.params;
    const path = findDirectoryPath(ownProps);
    const url = createDirectoryUrl(repository, branch, path);

    const wikiId = repository + '@' + branch;
    const stateWiki = state.wiki[wikiId] || {};

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
        path
    }
};

const mapDispatchToProps = (dispatch) => {
    return {
        fetchDirectoryIfNeeded: (url: string) => {
            dispatch(fetchDirectoryIfNeeded(url))
        },
    }
};

export default translate()(connect(mapStateToProps, mapDispatchToProps)(Directory));
