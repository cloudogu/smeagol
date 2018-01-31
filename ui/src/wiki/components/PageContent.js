//@flow
import React from 'react';
import injectSheet from 'react-jss';
import Markdown from './Markdown';

const styles = {};

type Props = {
    page: any,
    classes: any
}

class PageContent extends React.Component<Props> {

    render() {
        const { page } = this.props;
        return (
            <Markdown content={page.content} />
        );
    }

}

export default injectSheet(styles)(PageContent);
