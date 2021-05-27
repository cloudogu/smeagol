import React, { FC } from "react";
import { translate } from "react-i18next";
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

/**
 * The VerifiableTextArea is a react component that can be used when the content of a textarea should be processed
 * and verified.
 *
 * @param props Contains the following elements:
 * <li> t - a function to translate text</li>
 * <li> initValue - the initial value of the textarea</li>
 * <li> setParentState - the function used to update the value of the variable saved in the parents state</li>
 * <li> prefix - an identifier used for the translation of several elements</li>
 * <li> classes - css classes that should be added to the error message below the textarea</li>
 * <li> isValid - a function to validate the current text of the textarea</li>
 */
const VerifiableTextArea: FC<Props> = (props) => {
  const { t, initValue, setParentState, prefix, classes, isValid } = props;

  return (
    <div className={classNames("form-group", !isValid ? "has-error" : "")}>
      <textarea
        className={"form-control"}
        style={{ resize: "vertical" }}
        value={initValue}
        onChange={(event) => {
          setParentState(event.target.value);
        }}
      />
      {!isValid && <div className={classes.validationText}>{t(prefix + "_validationText")}</div>}
    </div>
  );
};

export default translate()(injectSheet(styles)(VerifiableTextArea));
