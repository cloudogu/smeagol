//@flow
import React from 'react';
import {useRepository} from '../modules/repository';
import BranchOverview from '../components/BranchOverview';
import GeneralInformation from '../components/GeneralInformation';
import Loading from '../../Loading';
import I18nAlert from '../../I18nAlert';

export default function Branches(props: any) {

    const {isLoading, isError, data} = useRepository(props.match.params.repository)

    let child = <div/>;
    if (isError) {
        child = <I18nAlert i18nKey="branches_failed_to_fetch"/>;
    } else if (isLoading) {
        child = <Loading/>;
    } else if (data) {
        child = <BranchOverview repository={data}/>
    }

    return (
        <div>
            <h1>Smeagol</h1>
            <GeneralInformation/>
            <h2>Branches</h2>
            {child}
        </div>
    );
}