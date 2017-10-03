###
Executa um template no formato do Underscore.js e faz cache do mesmo localmente 
###
window.Template = {}

Template.render = (url, attributes) ->
	if not @cache?
		@cache = {}
	if not @cache[url]
		$.ajax
			url: url
			method: 'GET'
			async: false
			context: @
			success: (response) ->
				@cache[url] = _.template(response)
			error: (err) ->
				alert err
	@cache[url](attributes)

Template.printSelected = (printFunc, model, option) ->
	if (model? and (model.id is option.get 'id'))
		printFunc ' selected="selected"'

Template.getEditValueFrom = (moneyValue) ->
	if (moneyValue? and moneyValue.indexOf('R$') > -1)
		moneyValue.replace('R$', '').replace('.','').replace(',','.').trim()
