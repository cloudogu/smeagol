// @flow
import {useQuery} from "react-query";
import {apiClient} from "../../apiclient";

function createHistoryUrl(repositoryId: string, branch: string, path: string) {
    return `/repositories/${repositoryId}/branches/${branch}/history/${path}`;
}

export function usePageHistory(repositoryId: string, branch: string, path: string) {
    const url = createHistoryUrl(repositoryId, branch, path)
    return useQuery( ['pagehistory',url], () =>
        apiClient.get(url).then(response => response.json()))
}