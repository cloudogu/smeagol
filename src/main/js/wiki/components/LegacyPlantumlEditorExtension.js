import Editor from 'tui-editor/dist/tui-editor-Editor';

Editor.defineExtension('legacyplantuml', function(editor) {

    const startUmlRe = /@startuml/g;
    const endUmlRe = /@enduml/g;

    // setMarkdownAfter does not work for preview, but this is ok for the legacy stuff
    editor.eventManager.listen('setMarkdownAfter', markdown => {
        if (markdown.match(startUmlRe) && markdown.match(endUmlRe) ) {
            const content = markdown.replace(startUmlRe, '```uml').replace(endUmlRe, '```')
            editor.setValue(content);
        }
    });
});