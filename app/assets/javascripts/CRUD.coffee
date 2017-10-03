window.CRUD = {}


isFunction = (object) ->
	getType = {}
	object and (getType.toString.call(object) is '[object Function]')


class Content extends Backbone.View
	renderContent: (html) ->
		@userContent = html
		@render()
	render: ->
		@$el.html(@userContent)


Events = {}
_.extend Events, Backbone.Events


Instances =
	PageContent: {}
	Router: {}
	Models: {}
	Collections: {}
	Views: {}
	addToListIfNotContains: (listName, obj) ->
		if @[listName]?
			@[listName].add obj
	getCollection: (collection, model, options) ->
		if not @[collection]?
			@[collection] = new @Collections[model]
			@[collection].fetch
				success: options.success
		else
			options.success @[collection]
	loadCollections: (completed, relationships...) ->
		collections = []
	
		callCount = relationships.length
		
		complCallback = (collection) ->
			collections.push collection
			completed(collections) if --callCount is 0
		Events.on 'collectionCompleted', complCallback
		
		success = (collection) ->
			Events.trigger 'collectionCompleted', collection

		if callCount is 0
			completed(collections)
		else
			for relationship in relationships
				Instances.getCollection relationship.collection, relationship.model,
					success: success


CRUD.init = (el, router) ->
	Instances.PageContent = new Content
		el: el
	Instances.Router = router
	Instances.Router.route '', ->
		Instances.PageContent.renderContent ''


CRUD.add = (options) -> 
	Instances.Models[options.model] = Backbone.Model.extend
		urlRoot: "/#{options.model}"
	
	Instances.Collections[options.model] = Backbone.Collection.extend
		model: Instances.Models[options.model]
		url: "/#{options.collection}"
	
	Instances.Views["#{options.model}Form"] = Backbone.View.extend
		template: (attributes) ->
			Template.render options.template.form, attributes
		events:
			'submit form': (e) ->
				e.preventDefault()
				@model.save Backbone.Syphon.serialize(@),
					success: (model, response, options) ->
						Instances.addToListIfNotContains options.collection, model
						Instances.Router.navigate options.collection,
							trigger: true
					error: (model, xhr, options) ->
						jsonResp = JSON.parse xhr.responseText
						$('.form-group').attr 'class', 'form-group'
						$('.help-block-error').remove()
						for field, error of jsonResp
							formGroup = $("input[name='#{field}']").closest '.form-group'
							formGroup.addClass 'has-error'
							formGroup.find('.text-muted').after "<span class='help-block help-block-error'>" + error + "</span>"
					collection: options.collection
					modelField: options.modelField
		renderContent: (@userContent) ->
			@render()
		render: ->
			@userContent.helper.model = @model
			@$el.html @template @userContent
	
	Instances.Views["#{options.model}CollectionItem"] = Backbone.View.extend
		tagName: 'tr'
		template: (attributes) ->
			Template.render options.template.collectionItem, attributes
		events:
			'click .editItemLink': (e) ->
				e.preventDefault()
				
				model = @model
				
				relationships = options.relationships
				if not relationships?
					relationships = []
				
				completed = ->
					view = new Instances.Views["#{options.model}Form"]
						model: model
					helper =
						title: "#{options.language.editText} #{options.language.modelName}"
						buttonText: "#{options.language.editText}"
					for relationship in relationships
						helper[relationship.collection] = Instances[relationship.collection]
					view.renderContent
						helper: helper
					Instances.PageContent.renderContent view.el
					Instances.Router.navigate options.model + "/#{model.get('id')}",
						trigger: false
				
				Instances.loadCollections completed, relationships...
			'click .deleteItemLink': (e) ->
				e.preventDefault()
				result = confirm "Deseja mesmo apagar: #{@model.get(options.modelField)}?"
				@model.destroy() if result
		render: ->
			@$el.html @template @model.attributes
	
	Instances.Views["#{options.model}Collection"] = Backbone.View.extend
		template: (attributes) ->
			Template.render options.template.collection, attributes
		render: ->
			@$el.html @template()
			docFrag = document.createDocumentFragment()
			html = $ docFrag
			@collection.on 'remove', ->
				@render()
			, @
			@collection.forEach (item) ->
				listItemView = new Instances.Views["#{options.model}CollectionItem"]
					model: item
				listItemView.render()
				html.append listItemView.el
			, @
			@$el.find('table').append docFrag
	
	Instances.Router.route "#{options.model}/new", ->
		relationships = options.relationships
		if not relationships?
			relationships = []
		
		completed = ->
			view = new Instances.Views["#{options.model}Form"]
				model: new Instances.Models[options.model]
			helper =
				title: "#{options.language.addText} #{options.language.modelName}"
				buttonText: "#{options.language.addText}"
			for relationship in relationships
				helper[relationship.collection] = Instances[relationship.collection]
			view.renderContent
				helper: helper
			Instances.PageContent.renderContent view.el	

		Instances.loadCollections completed, relationships...
	
	Instances.Router.route options.collection, ->
		listRender = (collection) ->
			viewList = new Instances.Views["#{options.model}Collection"]
				collection: collection
			viewList.render()
			Instances.PageContent.renderContent viewList.el
		
		Instances.getCollection options.collection, options.model,
			success: listRender

CRUD.getCollection = (collection, model, options) ->
	Instances.getCollection collection, model, options

CRUD.loadCollections = (completed, relationships) ->
	Instances.loadCollections completed, relationships...
