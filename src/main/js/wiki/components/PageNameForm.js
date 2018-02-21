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

type State = {
    name: string
};

class PageNameForm extends React.Component<Props, State> {

    constructor(props) {
        super(props);
        this.state = {
            name: ''
        };
    }

    handleChange = (event) => {
        this.setState({name: event.target.value});
    };

    onOkClick = () => {
        this.props.onOk(this.state.name);
    };

    validPage = () => {
        const pageName = this.state.name;
        const containsIllegalSequence = decodeURI(pageName).includes('..');
        return pageName.length > 0 && !containsIllegalSequence;
    };

    render() {
        const { show, onAbortClick, t, labelPrefix } = this.props;
        const isButtonEnabled = this.validPage();
        return (
            <Modal show={ show } onHide={onAbortClick}>
                <Modal.Header closeButton>
                    <Modal.Title>{ t(`${labelPrefix}-form_title`) }</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <h2>{ t(`${labelPrefix}-form_name_label`)} </h2>
                    <input type="text" className="form-control" value={this.state.name} onChange={ this.handleChange } />
                    <br />
                    <p>{ t(`${labelPrefix}-form_info`)} </p>
                </Modal.Body>
                <Modal.Footer>
                    <ActionButton disabled={!isButtonEnabled} type="primary" onClick={this.onOkClick} i18nKey={labelPrefix+"-form_ok"} />
                    <ActionButton onClick={onAbortClick} i18nKey={labelPrefix+"-form_abort"} />
                </Modal.Footer>
            </Modal>
        );
    }

}

export default translate()(PageNameForm);
