//@flow
import React from 'react';
import RepositoryGroup from './RepositoryGroup';

type Props = {
    repositories: any
}

class RepositoryList extends React.Component<Props> {

    createRepositoryGroups(repositories) {
        let groups = {};
        for (let repository of repositories) {
            let name = repository.name;

            let groupName = 'main';
            const index = name.lastIndexOf('/');
            if (index > 0) {
                groupName = name.substring(0, index);
            }

            let group = groups[groupName];
            if (!group) {
                group = {
                    name: groupName,
                    repositories: []
                };
                groups[groupName] = group;
            }
            group.repositories.push(repository);
        }

        let groupArray = Object.values(groups);
        groupArray.sort(function(a, b){
            if (a.name === 'main' && b.name !== 'main') {
                return 10;
            } else if (a.name !== 'main' && b.name === 'main') {
                return -10;
            } else if (a.name < b.name) {
                return -1;
            } else if (a.name > b.name) {
                return 1;
            }
            return 0;
        });

        return groupArray;
    }

    render() {
        const { repositories } = this.props;

        const groups = this.createRepositoryGroups(repositories);

        console.log(groups);

        return (
            <div className="list-group">
                { groups.map((group) => {
                    return (
                        <RepositoryGroup key={ group.name } group={group} />
                    );
                }) }
            </div>
        );
    }

}


export default RepositoryList;
