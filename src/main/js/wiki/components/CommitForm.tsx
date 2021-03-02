import React from "react";
import { Modal } from "react-bootstrap";
import { translate } from "react-i18next";
import ActionButton from "./ActionButton";

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

  handleChange = (event) => {
    this.setState({ message: event.target.value });
  };

  save = () => {
    this.props.onSave(this.state.message);
  };

  render() {
    const { show, onAbort, t } = this.props;

    return (
      <Modal show={show} onHide={onAbort}>
        <Modal.Header closeButton>
          <Modal.Title>{t("commit-form_title")}</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <textarea className="form-control" value={this.state.message} onChange={this.handleChange} />
        </Modal.Body>
        <Modal.Footer>
          <ActionButton type="primary" onClick={this.save} i18nKey="commit-form_save" />
          <ActionButton onClick={onAbort} i18nKey="commit-form_abort" />
        </Modal.Footer>
      </Modal>
    );
  }
}

export default translate()(CommitForm);
