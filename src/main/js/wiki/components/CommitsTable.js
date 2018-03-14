//@flow
import React from 'react';
import injectSheet from 'react-jss';
import CommitsTableEntry from './CommitsTableEntry';

const styles = {};

type Props = {
    commits: any,
    classes: any
}

class CommitsTable extends React.Component<Props> {

    render() {
        const { commits } = this.props;
        return (
            <table className="table">
                <tbody>

                { commits.map((commit) => {
                    return (
                        <CommitsTableEntry commit={commit} key={commit.commitId}/>
                    );
                }) }
                </tbody>
            </table>
        );
    }

}

export default injectSheet(styles)(CommitsTable);
