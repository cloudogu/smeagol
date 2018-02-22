//@flow
import React from 'react';
import injectSheet from 'react-jss';
import FileBrowserEntry from './FileBrowserEntry';

const styles = {
    files: {
        listStyleType: "none"
    }
};

type Props = {
    createLink: (directory: any, file: any) => string,
    directory: any,
    classes: any
}

class FileBrowser extends React.Component<Props> {

    createFileLink = (file: any) => {
        return this.props.createLink(this.props.directory, file);
    };

    render() {
        const { directory } = this.props;
        return (
            <ul className="list-unstyled">
            { directory.children.map((file) => {
                return (
                    <li key={file.name}>
                        <FileBrowserEntry file={file} createLink={this.createFileLink} />
                    </li>
                );
            }) }
            </ul>
        );
    }

}

export default injectSheet(styles)(FileBrowser);
