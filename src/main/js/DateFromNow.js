//@flow
import React from 'react';
import moment from 'moment';

type Props = {
    date?: string
};

class DateFromNow extends React.Component<Props> {

    static format(date?: string) {
        let fromNow = '';
        if (date) {
            fromNow = moment(date).fromNow();
        }
        return fromNow;
    }

    render() {
        const fromNow = DateFromNow.format(this.props.date);
        return (
            <span>{ fromNow }</span>
        );
    }

}

export default DateFromNow;
