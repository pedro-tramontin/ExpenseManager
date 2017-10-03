window.uploadSection = {}

Instances =
	crud: {}
	router: {}
	content: {}

uploadSection.init = (crud, router, content) ->
	Instances.crud = crud
	Instances.router = router
	Instances.content = content
	addUploadFormRoute()

addUploadFormRoute = ->
	Instances.router.route 'upload', ->
		showUploadFom = (collection) ->
			Instances.content.html Template.render '/assets/templates/upload/_form.html',
				periods: collection
			setUpFormUpload()
		CRUD.getCollection 'periods', 'period',
			success: showUploadFom

UploadCollectionItemView = Backbone.View.extend
	tagName: 'tr'
	template: (attributes) ->
		Template.render '/assets/templates/upload/_collectionItem.html', attributes
	render: ->
		userContent =
			helper:
				model: this.model
				periods: this.periods
				categories: this.categories
				index: this.index
		
		@$el.html @template userContent

UploadCollectionView = Backbone.View.extend
	template: (attributes) ->
		Template.render '/assets/templates/upload/_collection.html', attributes
	,
	render: ->
		@$el.html @template()
		docFragment = $ document.createDocumentFragment()

		appendItensToTable = (list, table) ->
			index = 1
			list.forEach (item) ->
				listItemView = new UploadCollectionItemView
					model: item
				listItemView.periods = @periods
				listItemView.categories = @categories
				listItemView.index = index++
				listItemView.render()
				
				docFragment.append listItemView.el
			, @
			table(@$el).append docFragment

		appendItensToTable.call this, @expenses, @expensesTable
		appendItensToTable.call this, @earns, @earnsTable

setUpFormUpload = ->
	$("#formUpload").fileupload(
		dataType: 'json'
		done: (e, data) ->
			relationships = []
		
			addRelationship = (collectionName, modelName) ->
				relationships.push
					collection: collectionName
					model: modelName
		
			addRelationship 'periods', 'period'
			addRelationship 'categories', 'category'
			
			completed = (collections) ->
				viewList = new UploadCollectionView
					collection: data.result.expenses
				
				viewList.periods = collections[0]
				viewList.categories = collections[1]
			
				viewList.expenses = data.result.expenses
				viewList.expensesTable = (el) ->
					el.find('table').first()
				
				viewList.earns = data.result.earns
				viewList.earnsTable = (el) ->
					el.find('table').last()
				
				viewList.render();
				
				Instances.content.html viewList.el
			
				$('#btPost').on 'click', (e) ->
					formData =
						expenses: new Array()
						earns : new Array()
					
					parsePostData = (formArray, sendArray) ->
						$.each formArray, (i,field) ->
							patternElementIndex = /(.+)\[(\d+)\]/
							patternIdName = /(.+)\[(.+)\]/
							
							match = patternElementIndex.exec field.name
							if match
								name = match[1]
								index = match[2] - 1;
								
								element = sendArray[index]
								if not element
									element = {}
								
								matchId = patternIdName.exec name
								if matchId
									name = matchId[1]
									idName = matchId[2]
								
									subElement = {}
									subElement[idName] = field.value
									
									element[name] = subElement
								else
									element[name] = field.value
									
								sendArray[index] = element
					
					parsePostData $('#formExpenses').serializeArray(), formData.expenses
					parsePostData $('#formEarns').serializeArray(), formData.earns
					
					$.ajax
						'type': 'POST'
						'url': '/uploadBatch'
						'contentType': 'application/json'
						'data': JSON.stringify formData
						'dataType': 'json'
						'success': (data,textStatus,jqXHR) ->
							console.log data
			
			CRUD.loadCollections completed, relationships
		progressall: (e, data) ->
			progress = parseInt data.loaded / data.total* 100, 10
			$('#progress .progress-bar').css 'width',progress + '%'
	).prop('disabled', !$.support.fileInput).parent().addClass($.support.fileInput ? undefined : 'disabled')
