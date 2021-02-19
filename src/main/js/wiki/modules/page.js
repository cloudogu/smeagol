//@flow
import {apiClient} from '../../apiclient';
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

export function useEditPage(url: string) {
    const queryClient = useQueryClient()
    return useMutation(["editpage", url], (data) => {
        return apiClient.post(url, {message: data.message, content: data.content})
    }, {
        onSuccess: () => {
            queryClient.invalidateQueries(["page", url])
            queryClient.invalidateQueries(["pagehistory", url])
        },
    })
}

export function useDeletePage(url: string, pushPageState: ()=>{}) {
    const queryClient = useQueryClient()
    return useMutation(["deletepage", url], (message) => {
        return apiClient.delete(url, {message}).then(() => {
            pushPageState()
        })
    }, {
        onSuccess: () => {
            queryClient.invalidateQueries(["page", url])
            queryClient.invalidateQueries(["pagehistory", url])
        },
    })
}

export function useRenamePage(url: string, path: string, pushPageState: (path: string)=>{}) {
    const queryClient = useQueryClient()
    return useMutation(["movePage", url], (data) => {
        // TODO i18n
        const message = 'Move page ' + path + ' to ' + data.target + ' (smeagol)';
        return apiClient.post(url, {message, "moveTo": data.target}).then(() => {
                pushPageState(data.target)
            }
        )
    }, {
        onSuccess: () => {
            // TODO: Invalidate pages
            queryClient.invalidateQueries(["page", url])
            queryClient.invalidateQueries(["pagehistory", url])
        },
    })
}

export function useCreatePage(url: string) {
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

type deleteParams = {
    message: string,
    commit: string
}

export function useRestorePage(url: string, pushPageState: ()=>{}) {
    const queryClient = useQueryClient()
    return useMutation(["deletepage", url], (params: deleteParams) => {
        return apiClient.post(url, {message: params.message, restore: params.commit}).then(() => {
            pushPageState()
        })
    }, {
        onSuccess: () => {
            queryClient.invalidateQueries(["page", url])
            queryClient.invalidateQueries(["pagehistory", url])
        },
    })
}