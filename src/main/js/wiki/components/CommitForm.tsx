import React from "react";
import { Modal } from "react-bootstrap";
import { translate } from "react-i18next";
import ActionButton from "./ActionButton";
import VerifiableTextArea from "./VerifiableTextArea";
import ToolTip from "./ToolTip";

type Props = {
  t: any;
  show: boolean;
  onSave: (...args: Array<any>) => any;
  onAbort: (...args: Array<any>) => any;
  defaultMessage: string;
};

type State = {
  message: string;
};

class CommitForm extends React.Component<Props, State> {
  constructor(props) {
    super(props);
    this.state = {
      message: this.props.defaultMessage
    };
  }

  save = () => {
    this.props.onSave(this.state.message);
  };

  isValidCommitMessage = (message) => {
    if (message === null || message === undefined || message.length === 0) {
      return false;
    }
    const absolutePathRegex = /(\S)+/;
    return message.match(absolutePathRegex) !== null;
  };

  handleCommitMessageUpdate = (message) => {
    this.setState({
      message: message
    });
  };

  render() {
    const { show, onAbort, t } = this.props;

    return (
      <Modal show={show} onHide={onAbort}>
        <Modal.Header closeButton>
          <Modal.Title>
            {t("commit-form_title")} <ToolTip prefix={"commit-form-message"} />
          </Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <VerifiableTextArea
            prefix={"commit-form-message"}
            initValue={this.state.message}
            setParentState={this.handleCommitMessageUpdate}
            isValid={this.isValidCommitMessage(this.state.message)}
          />
        </Modal.Body>
        <Modal.Footer>
          <ActionButton
            disabled={!this.isValidCommitMessage(this.state.message)}
            type="primary"
            onClick={this.save}
            i18nKey="commit-form_save"
          />
          <ActionButton onClick={onAbort} i18nKey="commit-form_abort" />
        </Modal.Footer>
      </Modal>
    );
  }
}

export default translate()(CommitForm);
