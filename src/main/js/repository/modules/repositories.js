import {useQuery} from "react-query";
import {apiClient} from "../../apiclient";

export function useRepositories() {
    return useQuery('repositories', () =>
        apiClient.get("/repositories").then(response => response.json()))
}