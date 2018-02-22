//@flow
import React from 'react';
import injectSheet from 'react-jss';

const styles = {};

type Props = {
    createLink: (path: string) => string,
    path: string,
    classes: any;
};

class Breadcrumb extends React.Component<Props> {

    createEntries = () => {
        const { path } = this.props;
        const parts = path.split('/');

        let entries = [];

        let currentPath = '';
        for ( let part of parts ) {
            currentPath += part + '/';
            entries.push({
                name: part,
                link: this.props.createLink(currentPath),
            });
        }

        return entries;
    };

    render() {
        const entries = this.createEntries();

        return (
            <div className="breadcrumb">
                { entries.map((entry) => {
                    return (
                        <span>/ <a href={entry.link}>{entry.name}</a> </span>
                    );
                }) }
            </div>
        );
    }

}

export default injectSheet(styles)(Breadcrumb);
