//@flow
import React from 'react';
import injectSheet from 'react-jss';
import CommitsTableEntry from './CommitsTableEntry';

const styles = {};

type Props = {
    commits: any,
    classes: any,
    pagePath: string
}

class CommitsTable extends React.Component<Props> {

    render() {
        const { commits, pagePath } = this.props;
        return (
            <table className="table">
                <tbody>

                { commits.map((commit) => {
                    return (
                        <CommitsTableEntry commit={commit} key={commit.commitId} pagePath={ pagePath }/>
                    );
                }) }
                </tbody>
            </table>
        );
    }

}

export default injectSheet(styles)(CommitsTable);
