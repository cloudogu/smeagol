//@flow
import React from 'react';
import injectSheet from 'react-jss';
import DateFromNow from '../../DateFromNow';
import {Link} from 'react-router-dom';
const styles = {
    commitTableTr: {
        backgroundColor: '#e7eff3'
    },
    commitTableTd: {
        border: '1px solid #b9d1dc',
        fontSize: '1em',
        lineHeight: '1.6em',
        margin: '0',
        padding: '0.3em 0.7em',
        verticalAlign: 'middle !important'
    }
};

type Props = {
    commit: any,
    classes: any,
    key: any,
    pagePath: string
}

class CommitsTableEntry extends React.Component<Props> {

    render() {
        const { commit, key, classes, pagePath } = this.props;
        return (
                <tr className={classes.commitTableTr} key={key}>
                   <td className={classes.commitTableTd}>
                      <input type="checkbox"></input>
                   </td>
                    <td className={classes.commitTableTd}>
                        <b>{commit.author.displayName}</b>
                    </td>
                    <td className={classes.commitTableTd}>
                       <DateFromNow date={commit.date}/>
                        : <Link className="btn-link" to={ `${pagePath}?commit=${commit.commitId}` } type="link">
                            { commit.message }
                        </Link>

                    </td>
                </tr>

        );
    }

}

export default injectSheet(styles)(CommitsTableEntry);
