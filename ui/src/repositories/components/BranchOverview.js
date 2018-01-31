//@flow
import React from 'react';
import injectSheet from 'react-jss';
import Branch from './Branch';
import BackToRepositoriesButton from './BackToRepositoriesButton';

const styles = {};

type Props = {
    repository: any,
    classes: any
}

class BranchOverview extends React.Component<Props> {

    render() {
        const { repository } = this.props;
        if (!repository) {
            return <div />;
        }

        console.log(repository);

        let branches = repository._embedded.branches;
        if (!branches) {
            branches = [];
        }

        branches.sort((a, b) => {
            if (a.name < b.name) {
                return -1;
            } else if (a.name > b.name) {
                return 1;
            }
            return 0;
        });

        return (
            <div>
                <div className="list-group">
                    { branches.map((branch) => {
                        return (
                            <Branch key={branch.name} branch={branch}/>
                        );
                    }) }
                </div>
                <BackToRepositoriesButton />
            </div>
        );
    }

}

export default injectSheet(styles)(BranchOverview);
