//@flow
import React from 'react';
import injectSheet from 'react-jss';
import {Link} from 'react-router-dom';
const styles = {
    commitTableTr: {
        backgroundColor: '#e7eff3'
    },
    commitTableTd: {
        border: '1px solid #b9d1dc !important',
        fontSize: '1em',
        lineHeight: '1.6em',
        margin: '0',
        padding: '0.3em 0.7em',
        verticalAlign: 'middle !important'
    }
};

type Props = {
    commit: any,
    classes: any,
    key: any,
    pagePath: string
}

class ShortCommitHash extends React.Component<Props> {

    render() {
        const { commit, key, classes, pagePath } = this.props;
        return (
        <Link className="btn-link" to={ `${pagePath}?commit=${commit.commitId}` } type="link">
            { getShortCommitHash(commit.commitId) }
        </Link>
        );
    }

}

function getShortCommitHash(commitId){
    return commitId.substring(0,7);
}

export default injectSheet(styles)(ShortCommitHash);
