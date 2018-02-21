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
    directory: any,
    classes: any
}

class FileBrowser extends React.Component<Props> {

    render() {
        const { directory } = this.props;
        return (
            <ul className="list-unstyled">
            { directory.children.map((file) => {
                return (
                    <li key={file.name}>
                        <FileBrowserEntry file={file} />
                    </li>
                );
            }) }
            </ul>
        );
    }

}

export default injectSheet(styles)(FileBrowser);
