import React from "react";
import injectSheet from "react-jss";
import { translate } from "react-i18next";
import DateFromNow from "../../DateFromNow";

const styles = {
  footer: {
    borderTop: "1px solid #ddd",
    paddingTop: "10px",
    paddingBottom: "10px"
  }
};

type Props = {
  page: any;
  classes: any;
};

class PageFooter extends React.Component<Props> {
  render() {
    const { page, classes, t } = this.props;
    const commit = page.commit;
    return (
      <div className={classes.footer}>
        {t("page-footer_edited_by")} {commit.author.displayName}, <DateFromNow date={commit.date} />
      </div>
    );
  }
}

export default injectSheet(styles)(translate()(PageFooter));
