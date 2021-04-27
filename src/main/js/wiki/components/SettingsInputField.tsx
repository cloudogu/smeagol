import React, { FC, useState } from "react";
import { translate } from "react-i18next";
import ToolTip from "../components/ToolTip";
import injectSheet from "react-jss";
import classNames from "classnames";

const styles = {
  validationText: {
    marginTop: "5px"
  }
};

type Props = {
  t: (string) => string;
  initValue: string;
  setParentState: (string) => void;
  prefix: string;
  classes: any;
  isValid: boolean;
};

const SettingsInputField: FC<Props> = (props) => {
  const { classes, initValue, setParentState, prefix, t } = props;
  const [inputValue, setInputValue] = useState(() => {
    setParentState(initValue);
    return initValue;
  });

  return (
    <div className={classNames("form-group", !props.isValid ? "has-error" : "")}>
      <label>{t(prefix + "_label")}</label> <ToolTip prefix={prefix} />
      <input
        type="text"
        className={"form-control"}
        value={inputValue}
        onChange={(event) => {
          setParentState(event.target.value);
          setInputValue(event.target.value);
        }}
      />
      {!props.isValid && <div className={classes.validationText}>{t(prefix + "_validationText")}</div>}
    </div>
  );
};

export default translate()(injectSheet(styles)(SettingsInputField));
