//@flow
import {apiClient, PAGE_NOT_FOUND_ERROR} from '../../apiclient';
import {useMutation, useQuery, useQueryClient} from "react-query";

export function createPageUrl(repositoryId: string, branch: string, path: string) {
    return `/repositories/${repositoryId}/branches/${branch}/pages/${path}`;
}

function getHistoryUrlFromUrl(url: string) {
    return url.replace("pages", "history");
}

export function usePage(url: string) {
    return useQuery(['page', url], () =>
        apiClient.get(url).then(response => response.json()))
}

export function editPage(url: string) {
    const queryClient = useQueryClient()
    return useMutation(["editpage", url], (data) => {
        return apiClient.post(url, {message: data.message, content: data.content})
    }, {
        onSuccess: () => {
            return queryClient.invalidateQueries(["page", url])
        },
    })
}

export function renamePage(url: string, path: string) {
    const queryClient = useQueryClient()
    return useMutation(["movePage", url], (data) => {
        // TODO i18n
        const message = 'Move page ' + path + ' to ' + data.target + ' (smeagol)';
        return apiClient.post(url, {message, "moveTo": data.target})
    }, {
        onSuccess: () => {
            // TODO: Invalidate pages
            return queryClient.invalidateQueries(["page", url])
        },
    })
}

export function createPage(url: string) {
    const queryClient = useQueryClient()
    return useMutation(["createPage", url], (data) => {
        return apiClient.post(url, {message: data.message, content: data.content})
    }, {
        onSuccess: () => {
            // TODO: Invalidate pages
            return queryClient.invalidateQueries(["page", url])
        },
    })
}