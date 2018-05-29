import Viewer from 'tui-editor/dist/tui-editor-Viewer';

Viewer.defineExtension('history', function(editor) {

    const linkHtmlRx = /<a .*(href="([^"]+)").*>/g;

    editor.eventManager.listen('convertorAfterMarkdownToHtmlConverted', html => {
        const result = html.replace(linkHtmlRx, function(match, href, url) {
            if (!(url.startsWith('/') || url.indexOf('://') > 0)) {
                return match.replace(href, href + ' data-history="true"');
            }
            return match;
        });
        // why timeout ???
        setTimeout(applyHistoryLinks.bind(), 0);
        return result;
    });

    const applyHistoryLinks = () => {
        for ( const link of document.querySelectorAll('[data-history]')) {
            link.addEventListener('click', handleDataHistoryLinkClick, false);
        }
    };

    const handleDataHistoryLinkClick = (e: Event) => {
        e.preventDefault();

        const href = e.target.getAttribute('href');
        window.appHistory.push(href, { 'some': 'thing' });
    };

});