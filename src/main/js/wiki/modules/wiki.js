//@flow
import {useQuery} from "react-query";
import {apiClient} from "../../apiclient";

export function useWiki(repositoryId: string, branch: string) {
    return useQuery(['wiki', repositoryId, branch], () =>
        apiClient.get(`/repositories/${repositoryId}/branches/${branch}.json`).then(response => response.json()))
}
