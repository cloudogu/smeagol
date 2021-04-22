import React, { FC, useState } from "react";
import { translate } from "react-i18next";
import ToolTip from "../components/ToolTip";
import injectSheet from "react-jss";
import classNames from "classnames";

const styles = {
  inputField: {
    marginBottom: "20px"
  }
};

type Props = {
  t: (string) => string;
  initValue: string;
  setParentState: (string) => void;
  prefix: string;
  classes: any;
};

const SettingsInputField: FC<Props> = (props) => {
  const { classes, initValue, setParentState, prefix, t } = props;
  const [inputValue, setInputValue] = useState(() => {
    setParentState(initValue);
    return initValue;
  });

  return (
    <>
      <label>{t(prefix + "_label")}</label> <ToolTip prefix={prefix} />
      <input
        type="text"
        className={classNames(classes.inputField, "form-control")}
        value={inputValue}
        onChange={(event) => {
          setParentState(event.target.value);
          setInputValue(event.target.value);
        }}
      />
    </>
  );
};

export default translate()(injectSheet(styles)(SettingsInputField));
