# sbtl xml hints

CodeMirror.xmlHints['<'] =
[
  'sbtl'
]

CodeMirror.xmlHints['<sbtl><'] =
[
  'abstract'
  'text'
]

CodeMirror.xmlHints['<sbtl><abstract><'] =
[
  'b'
  'i'
  'c'
  'ref'
  'footnote'
]

CodeMirror.xmlHints['<sbtl><text><'] =
[
  'p'
  'ul'
  'h1'
  'h2'
  'gist'
  'img'
  'blockquote'
]

CodeMirror.xmlHints['<sbtl><text><p><'] =
[
  'b'
  'i'
  'c'
  'ref'
  'intref'
  'footnote'
]

CodeMirror.xmlHints['<sbtl><text><ul><'] =
[
  'li'
]

CodeMirror.xmlHints['<sbtl><abstract><footnote><'] =
CodeMirror.xmlHints['<sbtl><text><p><footnote><'] =
CodeMirror.xmlHints['<sbtl><text><gist><'] =
CodeMirror.xmlHints['<sbtl><text><img><'] =
[
  'b'
  'i'
  'c'
  'ref'
  'intref'
  'footnote'
]

CodeMirror.xmlHints['<sbtl><text><h1 '] =
CodeMirror.xmlHints['<sbtl><text><h2 '] =
[
  'name'
]

CodeMirror.xmlHints['<sbtl><abstract><ref '] =
CodeMirror.xmlHints['<sbtl><text><blockquote '] =
CodeMirror.xmlHints['<sbtl><text><p><ref '] =
CodeMirror.xmlHints['<sbtl><abstract><footnote><ref '] =
CodeMirror.xmlHints['<sbtl><text><p><footnote><ref '] =
CodeMirror.xmlHints['<sbtl><text><gist><ref '] =
CodeMirror.xmlHints['<sbtl><text><img><ref '] =
[
  'link'
  'author'
  'title'
]

CodeMirror.xmlHints['<sbtl><text><gist '] =
CodeMirror.xmlHints['<sbtl><text><img '] =
[
  'src'
  'name'
]

# js inspired from http://codemirror.net/demo/xmlcomplete.html

CodeMirror.commands.autocomplete =
  (cm) -> CodeMirror.showHint(cm, CodeMirror.xmlHint)
  
CollapseFunc = CodeMirror.newFoldFunction(CodeMirror.tagRangeFinder)

passAndHint = (cm) ->
  setTimeout(( -> cm.execCommand("autocomplete")), 100)
  return CodeMirror.Pass

editor = CodeMirror.fromTextArea(
  document.getElementById("Text"),
  mode: "text/xml"
  lineNumbers: true
  extraKeys: {
      "' '": passAndHint
      "'<'": passAndHint
      "Ctrl-Space": "autocomplete"
      "Ctrl-Q": (cm) -> CollapseFunc(cm, cm.getCursor().line)
  }
  autoCloseTags: true
  viewportMargin: Infinity
  lineWrapping: true
  gutter : true,
  onGutterClick: CollapseFunc
)
