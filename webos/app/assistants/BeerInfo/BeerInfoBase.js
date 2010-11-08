BeerInfoBase = Class.create( {
	sceneSetup: function(BW) {
		Ares.setupSceneAssistant(this);
		this.BW = BW
		Mojo.Controller.getAppController().getStageController('First').setWindowOrientation("up");
		this.newBeer = false
		this.dbFailure = this.dbFailure.bind(this)
		this.dbSuccess = this.dbSuccess.bind(this)
		this.gotBeerInfoList = this.gotBeerInfoList.bind(this)
		this.beerStarChange = this.beerStarChange.bind(this)
		this.dragStartHandler = this.dragStart.bindAsEventListener(this);
		this.draggingHandler = this.dragging.bindAsEventListener(this);
		this.dragEndHandler = this.dragEnd.bindAsEventListener(this);
		this.handleRelaunch = this.handleRelaunch.bind(this)
		this.aboutToActivate = this.aboutToActivate.bind(this)
		this.gotBeer = this.gotBeer.bind(this)
		this.whereToGo = this.whereToGo.bind(this)
		this.trackingArea = this.controller.get('ratingPanel')
		Element.observe(this.trackingArea, Mojo.Event.dragStart, this.dragStartHandler);
		this.infoInput = true
		this.appMenuModel = {
			visible: true,
			items: [
							{ label: "Close this menu", command: 'palm-show-app-menu'},
							{ label: "Help", command: 'do-help' },
							{ label: "About", command: 'do-myAbout' }]
		};
		this.controller.setupWidget(Mojo.Menu.appMenu, {omitDefaultItems: true}, this.appMenuModel);
		this.scrimSpinner = this.controller.get("activity-spinner")
		this.controller.setupWidget("activity-spinner",{'spinnerSize':Mojo.Widget.spinnerLarge},this.spinnerModel={'spinning':true})
		this.scrim = Mojo.View.createScrim(this.controller.document, {scrimClass:'palm-scrim'})
		this.scrim.hide()
		this.controller.get("scrimSpinner").appendChild(this.scrim).appendChild(this.controller.get(this.scrimSpinner));
		if( this.whichPusher == 'app' ){ // rating reminder
			this.beerDepot.get('beerList', this.handleRelaunch, function(){})
		}
		else{ // From item in history ("First") or add a beer
			this.beerInfoHeaderModel.label = "Editing info for "+this.beerName
			this.controller.modelChanged(this.beerInfoHeaderModel)
			this.beerNoteModel.value = this.beerNote
			this.controller.modelChanged(this.beerNoteModel)
		}
	},
	handleRelaunch: function(results){
		//getPreferences('vibrate',this.beerDepot,this.vibrate,function(){})
		Mojo.Log.info('relaunch='+Object.toJSON(results))
		for( n = 0; n < results.length; n++ ){
			//Mojo.Log.info(results[n].uniqueID+' == '+this.beerID)
			if( results[n].uniqueID == this.beerID ){
				this.beerName = results[n].beerName
				this.beerNote = results[n].beerNote
				this.beerRating = results[n].stars
				this.beerInfoHeaderModel.label = "Editing info for "+results[n].beerName
				this.controller.modelChanged(this.beerInfoHeaderModel)
				this.beerNoteModel.value = results[n].beerNote
				this.controller.modelChanged(this.beerNoteModel)
		//		this.beerDepot.add('appParams', {"beerID":""}, function(){}, function(){})
				break
			}
		}
	},
	sceneAboutToActivate: function(callback){
		this.readyToGo = callback
		this.beerDepot.get('beerInfoList', this.gotBeerInfoList, this.dbFailure)
	},
	sceneActivate: function(){
		if( this.beerRating != undefined )
			this.beerStarChange(this.beerRating)
		else
			this.beerStarChange(0)
	},
	sceneCleanup: function() {
		Ares.cleanupSceneAssistant(this);
	},
	handleCommand: function(event) {
		if(event.type == Mojo.Event.command) {
			if(event.command == 'do-help') {
				if( this.BW == 2 ) nextScreen = 'HelpBW'
					else nextScreen = 'Help'
				this.controller.stageController.pushScene({'name':nextScreen,transition: Mojo.Transition.crossFade},{context:'Beer Info'})
			}
			if(event.command == 'do-myAbout') {
				if( this.BW == 2 ) nextScreen = 'AboutBW'
				else nextScreen = 'About'
				//this.controller.stageController.pushAppSupportInfoScene()
				this.controller.stageController.pushScene({'name':nextScreen,transition: Mojo.Transition.crossFade})
			}
		}
	},
	dbFailure: function(event){
	},
	dbSuccess: function(event){
	},
	gotBeerInfoList: function(results){
		Mojo.Log.info("in gotBeerInfoList - "+Object.toJSON(results))
		var recordSize = Object.values(results).size();
		if(recordSize !== 0){
			if( this.infoInput == true ){ // Getting data
				for(var n = 0; n < results.length; n++){
					Mojo.Log.info(this.beerName+' == '+results[n].beerName)
					if( this.beerName == results[n].beerName ){
						this.breweryNameModel.value = results[n].brewery
						this.controller.modelChanged(this.breweryNameModel)
						this.locationModel.value = results[n].location
						this.controller.modelChanged(this.locationModel)
						this.styleModel.value = results[n].style
						this.controller.modelChanged(this.styleModel)
						this.ABVModel.value = results[n].ABV
						this.controller.modelChanged(this.ABVModel)
						this.aboutModel.value = results[n].about
						this.controller.modelChanged(this.aboutModel)
						break;
					}
				}
				if( n >= results.length ) this.newBeer = true
				this.readyToGo()
			}
			else{ // Putting changed/new data
				//Mojo.Log.info('output')
				var data = new Array()
				if( this.newBeer == true )
					data.push({"beerName":this.beerName,
											"brewery":this.breweryNameModel.value,
											"location":this.locationModel.value,
											"style":this.styleModel.value,
											"ABV":this.ABVModel.value,
											"about":this.aboutModel.value
											})
				for( n = 0; n < results.length; n++ ){
					if( this.beerName == results[n].beerName )
						data.push({"beerName":this.beerName,
											"brewery":this.breweryNameModel.value,
											"location":this.locationModel.value,
											"style":this.styleModel.value,
											"ABV":this.ABVModel.value,
											"about":this.aboutModel.value
											})
					else
						data.push(results[n])
				}
				this.beerDepot.add('beerInfoList', data, this.dbSuccess, this.dbFailure)
				if( this.beerTap == true ) // Return to history after beer item tap
					this.controller.stageController.popScene({"beerRating":this.beerRating,"beerNote":this.beerNoteModel.value,"beerID":this.beerID,"thisPusher":"BeerInfo"})
				else // Notification received or no notification used
					this.beerDepot.get('beerList', this.gotBeer, function(){} )
			}
		}
		else{ // First beer info
			if( this.infoInput == false ){ // Returning data
				this.beerDepot.add('beerInfoList', [{"beerName":this.beerName,
												"brewery":this.breweryNameModel.value,
												"location":this.locationModel.value,
												"style":this.styleModel.value,
												"ABV":this.ABVModel.value,
												"about":this.aboutModel.value
												}], this.dbSuccess, this.dbFailure)
				this.controller.stageController.popScene({"beerRating":this.beerRating,"beerNote":this.beerNoteModel.value,"thisPusher":"BeerInfo"})
			}
			else this.readyToGo()
		}
	},
	gotBeer: function(results){
		var recordSize = Object.values(results).size();
		if(recordSize != 0){
			for( n = 0; n < results.length; n++ ){
				if( results[n].uniqueID == this.beerID ){
					results[n].stars = this.beerRating
					results[n].beerNote = this.beerNote
					break
				}
			}
		}
		this.beerDepot.add( 'beerList', results, function(){
				this.beerDepot.get('preferences',this.whereToGo,function(){})
			}.bind(this), function(){} )
	},
	whereToGo: function(results) {
		NextScreen = results.nextScreen
		FirstScreen = results.firstScreen
		if( results.androidScreens == 2 ){
			NextScreen += 'BW'
			FirstScreen += 'BW'
		}
		if( this.whichPusher == 'app' ){ // Return from notification timeout
			var sceneStack = this.controller.stageController.getScenes()
			if( sceneStack.length >= 2 ) // Return to previous screen
				this.controller.stageController.popScene()
			else // Go to First Screen choice
				this.controller.stageController.swapScene({"name":FirstScreen,transition:Mojo.Transition.crossFade},{"thisPusher":"BeerInfo"})
		}
		else if( this.whichPusher != results.nextScreen ) // Swap to NextScreen
			this.controller.stageController.swapScene({"name":NextScreen,transition:Mojo.Transition.crossFade},{"thisPusher":this.whichPusher})
		else // Pop back to previous card
				this.controller.stageController.popScene()
	},
	saveInfoTap: function(inSender, event) {
		//this.scrim.show()
		this.infoInput = false
		this.beerDepot.get('beerInfoList', this.gotBeerInfoList, this.dbFailure)
			},
	saveRatingTap: function(inSender, event){
		this.saveInfoTap( inSender, event )
	},
	beerStar5Tap: function(inSender, event) {
		this.beerStarChange(5)
	},
	beerStar4Tap: function(inSender, event) {
		this.beerStarChange(4)
	},
	beerStar3Tap: function(inSender, event) {
		this.beerStarChange(3)
	},
	beerStar2Tap: function(inSender, event) {
		this.beerStarChange(2)
	},
	beerStar1Tap: function(inSender, event) {
		this.beerStarChange(1)
	},
	beerStarChange: function(starn) {
	Mojo.Log.info('beerStarChange: '+starn)
		this.beerStar1Model.star = "unstar"
		this.beerStar2Model.star = "unstar"
		this.beerStar3Model.star = "unstar"
		this.beerStar4Model.star = "unstar"
		this.beerStar5Model.star = "unstar"
		switch (starn-0) {
			case 5:
				this.beerStar5Model.star = 'yostar'
			case 4:
				this.beerStar4Model.star = 'yostar'
			case 3:
				this.beerStar3Model.star = 'yostar'
			case 2:
				this.beerStar2Model.star = 'yostar'
			case 1:
				this.beerStar1Model.star = 'yostar'
		}
		this.controller.modelChanged(this.beerStar5Model)
		this.controller.modelChanged(this.beerStar4Model)
		this.controller.modelChanged(this.beerStar3Model)
		this.controller.modelChanged(this.beerStar2Model)
		this.controller.modelChanged(this.beerStar1Model)
		this.beerRating = starn
	},
	beerStar0Tap: function(inSender, event) {
		this.beerRating = 0
		this.beerStarChange(0)
	},
	dragStart: function(event) {
		var trackPointX = Event.pointerX(event.down)
		var trackPointY = Event.pointerY(event.down)
		Element.observe(this.trackingArea, Mojo.Event.dragging, this.draggingHandler);
		Element.observe(this.trackingArea, Mojo.Event.dragEnd, this.dragEndHandler);
		//this.controller.get('beerNote').update('Starting: '+trackPointX+' '+trackPointY)
		starPoint = 5
		if( trackPointX < 231 ) starPoint--
		if( trackPointX < 183 ) starPoint--
		if( trackPointX < 135 ) starPoint--
		if( trackPointX < 87 ) starPoint--
		if( trackPointX < 38 ) starPoint--
		this.beerStarChange(starPoint)
		Event.stop(event);
	},
	dragging: function(event) {
		var trackingX = Event.pointerX(event.move)
		var trackingY = Event.pointerY(event.move)
		if( trackingY < 185 && trackingY > 139 ){
			//this.controller.get('beerNote').update('Tracking: '+trackingX+' '+trackingY)
			starPoint = 5
			if( trackingX < 231 ) starPoint--
			if( trackingX < 183 ) starPoint--
			if( trackingX < 135 ) starPoint--
			if( trackingX < 87 ) starPoint--
			if( trackingX < 38 ) starPoint--
			this.beerStarChange(starPoint)
		}
		Event.stop(event);
	},
	dragEnd: function(event) {
		//this.controller.get('beerNote').update('Ending')
		Element.stopObserving(this.trackingArea, Mojo.Event.dragging, this.draggingHandler);
		Element.stopObserving(this.trackingArea, Mojo.Event.dragEnd, this.dragEndHandler);
		Event.stop(event);
	},
	showAppMenu: function(inSender, event) {
		Mojo.Log.info('in showAppMenu')
		Mojo.Controller.stageController.sendEventToCommanders(Mojo.Event.make(Mojo.Event.command, {command: "palm-show-app-menu"}))
	}
})