import {apiClient} from "../../apiclient";
import {useQuery} from "react-query";

function createSearchUrl(repositoryId: string, branch: string, query: string) {
    return `/repositories/${repositoryId}/branches/${branch}/search?query=${query}`;
}

export function useSearch(repository: string, branch: string, query: string) {
    const url = createSearchUrl(repository, branch, query)
    return useQuery(['search', {repository: repository, branch: branch, query: query}], () =>
        apiClient.get(url).then(response => response.json()))
}