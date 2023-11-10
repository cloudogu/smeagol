import React from "react";
import dayjs from "dayjs";
import relativeTime from "dayjs/plugin/relativeTime";
import { translate } from "react-i18next";

type Props = {
  date?: string;
};

dayjs.extend(relativeTime);

class DateFromNow extends React.Component<Props> {
  static format(locale: string, date?: string) {
    let fromNow = "";
    if (date) {
      fromNow = dayjs(date).locale(locale).fromNow();
    }
    return fromNow;
  }

  render() {
    const { i18n } = this.props;

    const fromNow = DateFromNow.format(i18n.language, this.props.date);
    return <span>{fromNow}</span>;
  }
}

export default translate()(DateFromNow);
