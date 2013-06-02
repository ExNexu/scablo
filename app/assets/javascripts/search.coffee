#
# search field autocomplete
#

$('#searchField').autocomplete(
      source: '/blog/quicksearch'
      minLength: 2
      open: -> $(this).autocomplete('widget').css('z-index', 9999)
      select: (event, ui) ->
        item = ui.item
        location = item.url
        window.open(location, '_self', false)
).data('ui-autocomplete')._renderItem = (ul, item) ->
    if item.type == 'post'
      icon = '<i class="icon-caret-right"></i>'
    else if(item.type == "tag")
      icon = '<i class="icon-tag"></i>'
    $('<li>')
        .data('item.autocomplete', item)
        .append('<a>' + icon + ' ' + item.name + '</a>')
        .appendTo(ul);