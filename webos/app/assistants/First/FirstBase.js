FirstBase = Class.create( {
	sceneSetup: function(BW) {
		Ares.setupSceneAssistant(this);
		this.BW = BW
		this.controller.stageController.setWindowOrientation("up");
		if( this.beerID == undefined )
			this.updateBeerItem = -1
		else
			this.updateBeerItem = this.beerID
		this.gotBeer = this.gotBeer.bind(this)
		this.gotSortBy = this.gotSortBy.bind(this)
		this.handleCommand = this.handleCommand.bind(this)
		this.dbFailure = this.dbFailure.bind(this)
		this.dbSuccess = this.dbSuccess.bind(this)
		this.beerItemDelete = this.beerItemDelete.bind(this)
		this.removeBeer = this.removeBeer.bind(this)
		this.beerSort = this.beerSort.bind(this)
		this.returnSelection = this.returnSelection.bind(this)
		this.gotImportFile = this.gotImportFile.bind(this)
		this.failure = this.failure.bind(this)
		this.importFromFile = this.importFromFile.bind(this)
		this.exportToEmail = this.exportToEmail.bind(this)
		this.gotListForEmail = this.gotListForEmail.bind(this)
		this.gotInfoForEmail = this.gotInfoForEmail.bind(this)
		this.makeUniqueID = this.makeUniqueID.bind(this)
		//this.gotPrefs = this.gotPrefs.bind(this)
		this.stackSize = this.controller.stageController.getScenes().length
		this.appMenuModel = {
			visible: true,
			items: [
				{ label: "Close this menu", command: 'palm-show-app-menu'},
				{ label: "Add a beer", command: 'add-beer'},
				{ label: "Sort...", items: [
				{ label: "Sort by beer name", command: 'do-name-sort'},
				{ label: "Sort by rating", command: 'do-rate-sort'}
				]},
				{ label: "Import/export data", command: 'do-data' },
				{ label: "Statistics", command: 'do-stats' },
				{ label: "Preferences", command: 'do-prefs' },
				{ label: "Help", command: 'do-help' },
				{ label: "About", command: 'do-myAbout' }
				]
		}
		this.scrimSpinner = this.controller.get("activity-spinner")
		this.controller.setupWidget("activity-spinner",{'spinnerSize':Mojo.Widget.spinnerLarge},this.spinnerModel={'spinning':true})
		this.scrim = Mojo.View.createScrim(this.controller.document, {scrimClass:'palm-scrim'})
		this.scrim.hide()
		this.controller.get("scrimSpinner").appendChild(this.scrim).appendChild(this.controller.get(this.scrimSpinner));

		this.controller.setupWidget(Mojo.Menu.appMenu, {omitDefaultItems: true}, this.appMenuModel);
		Mojo.Log.info('using Beer Depot')
		this.beerDepot.get('sortBy',this.gotSortBy,this.dbFailure)
	},
	sceneAboutToActivate: function(callback){
		//Mojo.Log.info('in aboutToActivate beerID:'+this.beerID)
		this.readyToGo = callback
		if( this.whichPusher == 'BeerInfo' ){
			this.beerData = [{"beerNote":this.beerNote, "beerRating":this.beerRating}]
			this.updateBeerItem = this.beerID
		}
		this.beerDepot.get('beerList',this.gotBeer,this.dbFailure)
	},
	sceneActivate: function(parms) {
		this.scrim.hide()
		this.beerItemHoldEvent = '0'
		//Mojo.Log.info('in activate: '+Object.toJSON(parms))
		if( parms != undefined ){
			this.beerData = new Array(parms)
			if( this.updateBeerItem == -1 && parms.beerID != undefined )
				this.updateBeerItem = parms.beerID
			this.beerDepot.get('beerList',this.gotBeer,this.dbFailure)
		}
		//this.beerDepot.get('preferences', this.gotPrefs, this.dbFailure)
	},
	sceneCleanup: function() {
		Ares.cleanupSceneAssistant(this);
	},
	//gotPrefs: function(results) {
	//	this.BW = results.androidScreens
	//},
	returnSelection: function(returned){
		switch(returned){
			case 0: // Export to Email
			this.exportToEmail()
			break
			case 1: // Export Locally
			break
			case 2: // Import
			this.controller.showAlertDialog({
				onChoose: this.importFromFile,
				title:'Warning!',
				message:'If you continue, this will overwrite all of your beer list and history. Do you wish to continue?',
				choices:[ {label:'Yes', value:'Yes', type:'affirmative'},
									{label:'No', value:'No', type:'negative'} ]
			});
			break
		}
	},
	exportToEmail: function(){
		this.beerDepot.get('beerInfoList', this.gotInfoForEmail, this.dbFailure)
	},
	gotInfoForEmail: function(beerInfoList){
		this.beerInfoList = beerInfoList
		this.beerDepot.get('beerList', this.gotListForEmail, this.dbFailure)
	},
	gotListForEmail: function(beerList){
		data = '------------------------Remembeer Data-----------------------<br>'
		data += '"tasting_notes","stamp","container","rating","brewery","beername","style","abv","location","about_this_beer"<br>'
		for( n = 0; n < beerList.length; n++ ){
			data += '"'+beerList[n].beerNote+'","'+beerList[n].drinkTimeDate+'","'+beerList[n].container+'","'+beerList[n].stars+'","'
			for( m = 0; m < this.beerInfoList.length; m++ ){
				if( this.beerInfoList[m].beerName == beerList[n].beerName ){
					data += this.beerInfoList[m].brewery+'","'
					data += this.beerInfoList[m].beerName+'","'
					data += this.beerInfoList[m].style+'","'
					data += this.beerInfoList[m].ABV+'","'
					data += this.beerInfoList[m].location+'","'
					data += this.beerInfoList[m].about+'"<br>'
				}
			}
		}
		this.controller.serviceRequest('palm://com.palm.applicationManager', {
			method: 'open',
			parameters: {
				id: 'com.palm.app.email',
				params: {
					summary: "Remembeer Data",
					text: data
				}
			}
		});
	},
	importFromFile: function(value){
		if( value == 'Yes' ){
			filename = "remembeer_export.csv"
			var request = new Ajax.Request('file:///media/internal/'+filename, {
				method: 'get',
				//evalJSON: 'force',
				onSuccess: this.gotImportFile,
				onFailure: this.failure
			});
		}
	},
	failure: function(results){
		Mojo.Log.info('in failure: '+Object.toJSON(results))
	},
	gotImportFile: function(results) {
		Mojo.Log.info(results.responseText)
		if( results.responseText == '' ){ // File empty or not found
			this.controller.showAlertDialog({
				onChoose: this.importFromFile,
				title:'Notice!',
				message:'Either no file was found or the file was empty. Make sure the file is name "Remembeer_export.csv" and is in the "media/internal" file path. This is the root directory when the phone is connected to a computer as a USB drive.',
				choices:[ {label:'OK', value:'Ok', type:'primary'} ]
			});
		}
		else{
			var beerList = []
			var beerInfoList = []
			var names = ["beerNote","drinkTimeDate","container","stars","brewery","beerName","style","ABV","location","about"]
			var string = results.responseText
			var newline = String.fromCharCode(10)
			string = string.slice(string.indexOf(newline)+1)
			while( string.length > 10 ){
				Mojo.Log.info(string)
				listString = ''
				infoString = ''
				thisLine = string.slice(0,string.indexOf(newline)-1)
				for( n = 0; n < names.length; n++ ){
					where = thisLine.indexOf('","')
					if( where == -1 ) where = thisLine.length
					value = thisLine.slice( 1, where )
					while( value.indexOf('"') != -1 )
						value = value.replace('"','')
					stg = "'"+names[n]+"':'"+value+"'"
					switch(n){
						case 0:
						case 1:
						case 2:
						case 3:
							if( listString != '' ) listString += ','
							listString += stg
						break
						case 5:
							if( listString != '' ) listString += ','
							listString += stg
						default:
							if( infoString != '' ) infoString += ','
							infoString += stg
						break
					}
					thisLine=thisLine.slice(where+2)
				}
				eval("object = {"+listString+"}")
				object.uniqueID = this.makeUniqueID()
				//Mojo.Log.info(Object.toJSON(object))
				beerList.push(object)
				eval("object = {"+infoString+"}")
				for( n = 0; n < beerInfoList.length; n++ ){
					if( beerInfoList[n].beerName == object.beerName ) break
					//Mojo.Log.info('beerName='+object.beerName)
				}
				if( n == beerInfoList.length )
					beerInfoList.push(object)
				//Mojo.Log.info("Info list="+Object.toJSON(beerInfoList))
				//Mojo.Log.info("Beer list="+Object.toJSON(beerList))
				if( string.indexOf(newline) == -1 )
					string = ''
				else
					string = string.slice(string.indexOf(newline)+1)
				//Mojo.Log.info(string)
			}
			Mojo.Log.info(Object.toJSON(beerList))
			this.beerListModel.items = this.beerSort( beerList, this.sortBy )
			this.controller.get('scroller1').mojo.revealTop()
			this.controller.get('beerList').mojo.setLength(beerList.length)
			this.controller.modelChanged(this.beerListModel)
			this.beerDepot.add('beerList', beerList, this.dbSuccess, this.dbFailure)
			this.beerDepot.add('beerInfoList', beerInfoList, this.dbSuccess, this.dbFailure)
		}
	},
	handleCommand: function(event) {
		if(event.type == Mojo.Event.command) {
			if(event.command == 'do-data') {
				this.controller.showDialog({
				template: 'importExportDialog/importExportDialog-scene',
				assistant: new ImportExportDialogAssistant(this, this.returnSelection.bind(this))
			});
			}
			if(event.command == 'do-help') {
				if( this.BW == 2 ) nextScreen = 'HelpBW'
				else nextScreen = 'Help'
				this.controller.stageController.pushScene({'name':nextScreen,transition: Mojo.Transition.crossFade},{'context':'History'})
			}
			if(event.command == 'do-myAbout') {
				if( this.BW == 2 ) nextScreen = 'AboutBW'
				else nextScreen = 'About'
				this.controller.stageController.pushScene({'name':nextScreen,transition: Mojo.Transition.crossFade})
			}
			if(event.command == 'do-prefs'){
				if( this.BW == 2 ) nextScreen = 'PreferencesBW'
				else nextScreen = 'Preferences'
				if( this.whichPusher != 'Preferences' && this.stackSize < 2 )
					this.controller.stageController.pushScene({'name':nextScreen,transition: Mojo.Transition.crossFade},{'beerDepot':this.beerDepot,"thisPusher":"First"})
				else
					this.controller.stageController.popScenesTo(nextScreen)
			}
			if(event.command == 'do-stats'){
				if( this.BW == 2 ) nextScreen = 'StatisticsBW'
				else nextScreen = 'Statistics'
				if( this.whichPusher != 'Statistics' && this.stackSize < 2 ){
					this.controller.stageController.pushScene({"name":nextScreen,transition: Mojo.Transition.crossFade},{"beerDepot":this.beerDepot,"thisPusher":"First"}) 
				}
				else
					this.controller.stageController.popScenesTo(nextScreen)
			}
			if(event.command == 'add-beer'){
				if( this.BW == 2 ) nextScreen = 'AddBeerBW'
				else nextScreen = 'AddBeer'
				if( this.whichPusher != 'AddBeer' && this.stackSize < 2 )
					this.addABeerTap()
				else
					this.controller.stageController.popScenesTo(nextScreen)
			}
			if(event.command == 'do-name-sort'){
				var data = this.beerSort(this.beerListModel.items, 'name')
				this.sortBy = 'name'
				this.appMenuModel.items[2].items[0].label = 'Sort by date'
				this.appMenuModel.items[2].items[0].command = 'do-date-sort'
				this.appMenuModel.items[2].items[1].label = 'Sort by rating'
				this.appMenuModel.items[2].items[1].command = 'do-rate-sort'
				this.beerListModel.items = data
				this.controller.get('beerList').mojo.setLength(data.length)
				//this.controller.modelChanged(this.beerListModel)
				this.controller.get('beerList').mojo.noticeUpdatedItems (0, this.beerListModel.items)
				this.beerDepot.add('sortBy', {"sortBy":this.sortBy}, this.dbSuccess, this.dbFailure)
			}
			if(event.command == 'do-date-sort'){
				var data = this.beerSort(this.beerListModel.items, 'date')
				this.sortBy = 'date'
				this.appMenuModel.items[2].items[0].label = 'Sort by beer name'
				this.appMenuModel.items[2].items[0].command = 'do-name-sort'
				this.appMenuModel.items[2].items[1].label = 'Sort by rating'
				this.appMenuModel.items[2].items[1].command = 'do-rate-sort'
				this.beerListModel.items = data
				this.controller.get('beerList').mojo.setLength(data.length)
				//this.controller.modelChanged(this.beerListModel)
				this.controller.get('beerList').mojo.noticeUpdatedItems (0, this.beerListModel.items)
				this.beerDepot.add('sortBy', {"sortBy":this.sortBy}, this.dbSuccess, this.dbFailure)
			}
			if(event.command == 'do-rate-sort'){
				var data = this.beerSort(this.beerListModel.items, 'rate')
				this.sortBy = 'rate'
				this.appMenuModel.items[2].items[0].label = 'Sort by date'
				this.appMenuModel.items[2].items[0].command = 'do-date-sort'
				this.appMenuModel.items[2].items[1].label = 'Sort by beer name'
				this.appMenuModel.items[2].items[1].command = 'do-name-sort'
				this.beerListModel.items = data
				this.controller.get('beerList').mojo.setLength(data.length)
				//this.controller.modelChanged(this.beerListModel)
				this.controller.get('beerList').mojo.noticeUpdatedItems (0, this.beerListModel.items)
				this.beerDepot.add('sortBy', {"sortBy":this.sortBy}, this.dbSuccess, this.dbFailure)
			}
		} 
	},
	beerSort: function( sortArray, sortBy ) {
		indexArray = new Array()
		for( n = 0; n < sortArray.length; n++ )
			indexArray.push(n) // Make array of current index
		for( var n = 0; n < sortArray.length-1; n++ ){
			var upper = new Array(sortArray[indexArray[n]])
			for( var m = n+1; m < sortArray.length; m++ ){
				var lower = new Array(sortArray[indexArray[m]])
				if( sortBy == 'name' ){
					var temp = new Array(upper[0].beerName, lower[0].beerName)
					var temp2 = new Array(upper[0].beerName, lower[0].beerName).sort()
				}
				else if( sortBy == 'date' ){
					var temp = new Array(upper[0].drinkTimeDate, lower[0].drinkTimeDate)
					var temp2 = new Array(upper[0].drinkTimeDate, lower[0].drinkTimeDate).sort()
				}
				else{ // rating
					var temp = new Array(upper[0].stars, lower[0].stars)
					var temp2 = new Array(upper[0].stars, lower[0].stars).sort()
				}
				if( temp[0] != temp.sort()[0] ){
					t = indexArray[n]
					indexArray[n] = indexArray[m]
					indexArray[m] = t
					upper = lower
				}
			}
		}
		if( sortBy != 'name' ) indexArray.reverse()
		data = new Array()
		for( n = 0; n < indexArray.length; n++ ){
			//sortArray[indexArray[n]].uniqueID = n
			data.push( sortArray[indexArray[n]] )
		}
		return( data )
	},
	addABeerTap: function(inSender, event) {
		if( this.BW == 2 ) nextScreen = 'AddBeerBW'
		else nextScreen = 'AddBeer'
		//this.scrim.show()
		if( this.whichPusher != 'AddBeer' && this.stackSize < 2 ){
			this.updateBeerItem = -1
			this.controller.stageController.pushScene(nextScreen,{"beerDepot":this.beerDepot,"thisPusher":"First"})
		}
		else{
			this.controller.stageController.popScenesTo(nextScreen)
		}
	},
	dbFailure: function(event) {},
	dbSuccess: function(event) {Mojo.Log.info('in dbSuccess')},
	gotBeer: function(results) {
		var recordSize = Object.values(results).size();
		if(recordSize !== 0) { // Adding to existing data
			//Mojo.Log.info("Adding to existing data")
			if( this.updateBeerItem == -1 ){ // Adding new beer information
				//Mojo.Log.info("Adding new beer information")
				//Mojo.Log.info('results length='+results.length)
				var data = new Array()
				for( var n = 0; n < results.length; n++ ){
					data.push(results[n])
					//Mojo.Log.info('results['+n+']='+Object.toJSON(data))
				}
			}
			else{ // Update an existing beer item in the list
				Mojo.Log.info("Updating existing beer item: "+this.updateBeerItem)
				if( this.beerData == undefined )
					Mojo.Log.info('beerData: undefined')
				else
					Mojo.Log.info("beerData: "+Object.toJSON(this.beerData))
				var data = new Array()
				for( var n = 0; n < results.length; n++ ){
					if( results[n].uniqueID == this.updateBeerItem && this.beerData != undefined ){
						Mojo.Log.info('beerData='+Object.toJSON(this.beerData))
						Mojo.Log.info('results['+n+']='+Object.toJSON(results[n]))
						Mojo.Log.info(results[n].stars+' '+this.beerData[0].beerRating)
						results[n].stars = this.beerData[0].beerRating
						results[n].beerNote = this.beerData[0].beerNote
						Mojo.Log.info('results['+n+']='+Object.toJSON(results[n]))
						Mojo.Log.info(results[n].stars+' '+this.beerData[0].beerRating)
					}
					data.push(results[n])
				}
			}
			// Save and restore scroller scroll state to make sure the list is displayed properly
			currentScrollState = this.controller.get('scroller1').mojo.getState()
			this.controller.get('scroller1').mojo.revealTop()
			this.beerListModel.items = this.beerSort( data, this.sortBy )
			this.controller.get('beerList').mojo.setLength(data.length)
			this.controller.modelChanged(this.beerListModel)
			this.controller.get('scroller1').mojo.setState(currentScrollState)
			if( data.length > 0 )
				this.beerDepot.add('beerList',data,this.dbSuccess,this.dbFailure)
		}
		else { // No existing data
			if( this.beerData != undefined ) {
				this.beerListModel.items = this.beerSort( this.beerData, this.sortBy )
				this.controller.get('beerList').mojo.setLength(this.beerData.length)
				this.controller.modelChanged(this.beerListModel)
				if( this.beerData.length > 0 )
					this.beerDepot.add('beerList',this.beerData,this.dbSuccess,this.dbFailure)
			}
		}
		this.readyToGo()
	},
	beerItemDelete: function(inSender, event) {
		this.beerListIndex = event.index
		this.beerUniqueID = event.item.uniqueID
		Mojo.Log.info(Object.toJSON(event.item))
		this.beerDepot.get('beerList',this.removeBeer,this.dbFailure)
	},
	removeBeer: function(results) {
		var recordSize = Object.values(results).size();
		if(recordSize !== 0) {
			var data = new Array()
			for( n = 0; n < results.length; n++ ) {
				if( results[n].uniqueID != this.beerUniqueID )
					data.push(results[n])
			}
			// Save and restore scroller scroll state to make sure the list is displayed properly
			currentScrollState = this.controller.get('scroller1').mojo.getState()
			this.controller.get('scroller1').mojo.revealTop()
			this.beerListModel.items = this.beerSort( data, this.sortBy )
			this.controller.get('beerList').mojo.setLength(data.length)
			this.controller.modelChanged(this.beerListModel)
			this.controller.get('scroller1').mojo.setState(currentScrollState)
			if( data.length > 0 )
				this.beerDepot.add('beerList',data,this.dbSuccess,this.dbFailure)
			else
				this.beerDepot.discard('beerList',this.dbSuccess, this.dbFailure)
			this.controller.get('beerList').mojo.setLength(data.length)
		}
	},
	beerItemHold: function(insender, event) {
		this.beerItemHoldEvent = '1'
		this.updateBeerItem = event.srcElement.up('.palm-row').id
		for( n = 0; n < this.beerListModel.items.length; n++ ){
			if( this.beerListModel.items[n].uniqueID == this.updateBeerItem ){
				this.tempBeerItem = this.beerListModel.items[n]
				break
			}
		}
		Mojo.Log.info('tempBeerItem: '+Object.toJSON(this.tempBeerItem))
		this.controller.showDialog({
			template: 'beerInfoDialog/beerInfoDialog-scene',
			assistant: new BeerInfoDialogAssistant(this, this.returnAction.bind(this))
		})
	},
	beerItemTap: function(insender, event) {
		if( this.beerItemHoldEvent == '1' ) return
		this.updateBeerItem = event.item.uniqueID
		var beerItem = event.item
		//this.beerData = undefined
		//this.scrim.show()
		if( this.BW == 2 )
			this.controller.stageController.pushScene({"name":"BeerInfoBW",transition: Mojo.Transition.crossFade},{"beerTap":true,"beerName":beerItem.beerName,"beerRating":beerItem.stars,"beerNote":beerItem.beerNote,"thisPusher":"First"})
		else
			this.controller.stageController.pushScene({"name":"BeerInfo",transition: Mojo.Transition.crossFade},{"beerTap":true,"beerName":beerItem.beerName,"beerRating":beerItem.stars,"beerNote":beerItem.beerNote,"thisPusher":"First"})
	},
	returnAction: function(action){
		this.beerItemHoldEvent = '0'
		switch (action) {
			case 0: // Drink another
			if( this.BW == 2 ) nextScreen = 'AddBeerBW'
			else nextScreen = 'AddBeer'
			this.updateBeerItem = -1
			this.controller.stageController.pushScene({'name':nextScreen,transition: Mojo.Transition.crossFade},{'beerDepot':this.beerDepot,"beerName":this.tempBeerItem.beerName,"beerNote":this.tempBeerItem.beerNote,"thisPusher":"First"})
			break
			case 1: // Edit beer info
			if( this.BW == 2 ) nextScreen = 'BeerInfoBW'
			else nextScreen = 'BeerInfo'
			this.updateBeerItem = this.tempBeerItem.uniqueID
			this.controller.stageController.pushScene({"name":nextScreen,transition: Mojo.Transition.crossFade},{"beerTap":true,"beerName":this.tempBeerItem.beerName,"beerRating":this.tempBeerItem.stars,"beerNote":this.tempBeerItem.beerNote,"thisPusher":"First"})
			break
		}
	},
	gotSortBy: function(results){
		var recordSize = Object.values(results).size();
		if(recordSize !== 0) {
			if( results.sortBy != undefined && results.sortBy != '' )
				this.sortBy = results.sortBy
			else
				this.sortBy = 'date'
		}
		else this.sortBy = 'date'
		if( this.sortBy == 'date' ){
			this.appMenuModel.items[2].items[0].label = 'Sort by beer name'
			this.appMenuModel.items[2].items[0].command = 'do-name-sort'
			this.appMenuModel.items[2].items[1].label = 'Sort by rating'
			this.appMenuModel.items[2].items[1].command = 'do-rate-sort'
		}
		else if( this.sortBy == 'name' ){
			this.appMenuModel.items[2].items[0].label = 'Sort by date'
			this.appMenuModel.items[2].items[0].command = 'do-date-sort'
			this.appMenuModel.items[2].items[1].label = 'Sort by rating'
			this.appMenuModel.items[2].items[1].command = 'do-rate-sort'
		}
		else { // 'rate'
			this.appMenuModel.items[2].items[0].label = 'Sort by date'
			this.appMenuModel.items[2].items[0].command = 'do-date-sort'
			this.appMenuModel.items[2].items[1].label = 'Sort by beer name'
			this.appMenuModel.items[2].items[1].command = 'do-name-sort'
		}
	},
	makeUniqueID: function(){
		return( Math.floor(Math.random()*1000000000))
	},
	showAppMenu: function(inSender, event) {
		Mojo.Log.info('in showAppMenu')
		Mojo.Controller.stageController.sendEventToCommanders(Mojo.Event.make(Mojo.Event.command, {command: "palm-show-app-menu"}))
	}
})