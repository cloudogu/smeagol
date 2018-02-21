//@flow
import React from 'react';
import injectSheet from 'react-jss';

const styles = {};

type Props = {
    path: string,
    classes: any;
}

class Breadcrumb extends React.Component<Props> {

    render() {
        const { path } = this.props;
        return (
            <div className="breadcrumb">
                <a href="#">Start</a> / { path }
            </div>
        );
    }

}

export default injectSheet(styles)(Breadcrumb);
