//@flow
import React from 'react';
import {Modal} from 'react-bootstrap';
import {translate} from 'react-i18next';
import ActionButton from './ActionButton';

type Props = {
    t: any,
    labelPrefix: string,
    show: boolean,
    onOk: Function,
    onAbortClick: Function,
};

class ConfirmModal extends React.Component<Props> {

    onOkClick = () => {
        this.props.onOk();
    };

    render() {
        const { show, onAbortClick, t, labelPrefix } = this.props;
        return (
            <Modal show={ show } onHide={onAbortClick}>
                <Modal.Header closeButton>
                    <Modal.Title>{ t(`${labelPrefix}-confirm_title`) }</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <p>{ t(`${labelPrefix}-confirm_text`)} </p>
                </Modal.Body>
                <Modal.Footer>
                    <ActionButton type="primary" onClick={this.onOkClick} i18nKey={labelPrefix+"-confirm_ok"} />
                    <ActionButton onClick={onAbortClick} i18nKey={labelPrefix+"-confirm_abort"} />
                </Modal.Footer>
            </Modal>
        );
    }

}

export default translate()(ConfirmModal);
