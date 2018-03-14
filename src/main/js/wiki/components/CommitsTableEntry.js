//@flow
import React from 'react';
import injectSheet from 'react-jss';
import DateFromNow from '../../DateFromNow';
import ActionLink from './ActionLink';
const styles = {
    commitTableTr: {
        backgroundColor: '#e7eff3'
    },
    commitTableTd: {
        borderTop: '1px solid #b9d1dc',
        borderBottom: '1px solid #b9d1dc',
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
    key: any
}
// <a href={commit._links.page.href}>{commit.message}</a>
class CommitsTableEntry extends React.Component<Props> {

    render() {
        const { commit, key, classes } = this.props;
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
                        : <ActionLink to={ commit._links.page.href} type="link" i18nKey={commit.message}></ActionLink>

                    </td>
                </tr>

        );
    }

}

export default injectSheet(styles)(CommitsTableEntry);
