###
# Router
###
Router = new (Backbone.Router.extend
	routes:
		"teste": "teste"
		"sql": "sql"
		"expenses/relatory/filtered/201403": "filtered"
	teste: ->
		$("#content").html Template.render '/assets/templates/_login.html'
	sql: ->
		$("#content").html Template.render '/assets/templates/sql.html'
	filtered: ->
		period = window.prompt "Período? (YYYYMM)"
	
		$.get "/expenses/relatory/filtered/#{period}", (data) ->
			printObject = (object, content) ->
				html = $("<tr/>")
				tr = $ Template.render '/assets/templates/expense/_collectionItem.html', object
				html.append tr
				
				html.find('td')[7].remove()
				html.find('td')[6].remove()
				
				content.find('tbody').append html
			
			div = $("<div/>").html Template.render '/assets/templates/expense/_collection.html'
			
			div.find("th")[7].remove()
			div.find("th")[6].remove()
			
			printObject obj, div for obj in JSON.parse(data)
			
			div.find('h2').text('Gastos do período')
			$("#content").append div
)


App = new (Backbone.View.extend
	el: document.body
	events:
		"click .navbar_link": (e) ->
			e.preventDefault()
			Router.navigate e.target.pathname,
				trigger: true
	start: ->
		Backbone.history.start
			pushState: true
		uploadSection.init CRUD, Router, $("#content")
)


CRUD.init $('#content'), Router
CRUD.add
	model: 'category'
	collection: 'categories'
	modelField: 'name'
	template:
		form: '/assets/templates/category/_form.html'
		collection: '/assets/templates/category/_collection.html'
		collectionItem: '/assets/templates/category/_collectionItem.html'
	language:
		addText: 'Adicionar'
		editText: 'Editar'
		modelName: 'Categoria'

CRUD.add
	model: 'period'
	collection: 'periods'
	modelField: 'value'
	template:
		form: '/assets/templates/period/_form.html'
		collection: '/assets/templates/period/_collection.html'
		collectionItem: '/assets/templates/period/_collectionItem.html'
	language:
		addText: 'Adicionar'
		editText: 'Editar'
		modelName: 'Período'

CRUD.add
	model: 'earn'
	collection: 'earns'
	modelField: 'description'
	relationships: [
		{collection: 'categories', model: 'category'}
		{collection: 'periods', model: 'period'}
	]
	template:
		form: '/assets/templates/earn/_form.html'
		collection: '/assets/templates/earn/_collection.html'
		collectionItem: '/assets/templates/earn/_collectionItem.html'
	language:
		addText: 'Adicionar'
		editText: 'Editar'
		modelName: 'Ganho'

CRUD.add
	model: 'expense'
	collection: 'expenses'
	modelField: 'description'
	relationships: [
		{collection: 'categories', model: 'category'}
		{collection: 'periods', model: 'period'}
	]
	template:
		form: '/assets/templates/expense/_form.html'
		collection: '/assets/templates/expense/_collection.html'
		collectionItem: '/assets/templates/expense/_collectionItem.html'
	language:
		addText: 'Adicionar'
		editText: 'Editar'
		modelName: 'Gasto'

CRUD.add
	model: 'user'
	collection: 'users'
	modelField: 'login'
	template:
		form: '/assets/templates/user/_form.html'
		collection: '/assets/templates/user/_collection.html'
		collectionItem: '/assets/templates/user/_collectionItem.html'
	language:
		addText: 'Adicionar'
		editText: 'Editar'
		modelName: 'Usuário'

$( ->
	App.start()
)
