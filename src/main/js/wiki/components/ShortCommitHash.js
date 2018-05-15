//@flow
import React from 'react';
import injectSheet from 'react-jss';
import {Link} from 'react-router-dom';

const styles = {};

type Props = {
    commit: any,
    pagePath: string
}

class ShortCommitHash extends React.Component<Props> {

    render() {
        const { commit, pagePath } = this.props;
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
