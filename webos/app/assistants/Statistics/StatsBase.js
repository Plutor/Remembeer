StatsBase = Class.create( {
	sceneSetup: function() {
		Ares.setupSceneAssistant(this);
		Mojo.Controller.getAppController().getStageController('First').setWindowOrientation("up");
		this.items = [{label:"Number of beers drunk",value:0},
								{label:"Number of different beers drunk",value:0},
								{label:"Favorite beer",value:"-"},
								{label:"Most drunk beer",value:"-"},
								{label:"Favorite drinking hour",value:"-"},
								{label:"Beers in the last 7 days",value:"0"},
								{label:"Beers so far this month",value:"0"},
								{label:"Beers so far this year",value:"0"}]
		this.stackSize = this.controller.stageController.getScenes().length
		this.appMenuModel = {
			visible: true,
			items: [
				{ label: "Close this menu", command: 'palm-show-app-menu'},
				{ label: "Add a beer", command: 'add-beer', disabled: false},
				{ label: "History", command: 'do-history', disabled: false},
				{ label: "Preferences", command: 'do-prefs', disabled: false},
				{ label: "Help", command: 'do-help', disbaled:false},
				{ label: "About", command: 'do-myAbout', disabled: false}]
		}
		this.controller.setupWidget(Mojo.Menu.appMenu, {omitDefaultItems: true}, this.appMenuModel);
		this.beerArray = []
		this.starArray = []
		this.nBeers = []
		this.drunk = []
		this.gotBeerInfoList = this.gotBeerInfoList.bind(this)
		this.handleCommand = this.handleCommand.bind(this)
		this.gotBeerInfo = this.gotBeerInfo.bind(this)
		this.dbFailure = this.dbFailure.bind(this)
		this.handleRelaunch = this.handleRelaunch.bind(this)
		this.gotPrefs = this.gotPrefs.bind(this)
	},
	sceneAboutToActivate: function(callback){
		if( this.beerID == undefined || this.beerID == '' )
			this.readyToGo = callback
		else
			this.readyToGo = this.handleRelaunch
			//Mojo.Log.info('calling getBeerStatistics')
		getBeerStatistics( this.beerDepot, this.gotBeerInfo, this.dbFailure )
	},
	sceneActivate: function(parms){
		this.beerDepot.get('preferences', this.gotPrefs, function(){})
	},
	handleRelaunch: function(results){
		if( results == undefined )
			this.beerDepot.get('beerList', this.handleRelaunch, function(){})
		else {
			for( n = 0; n < results.length; n++ ){
				if( results[n].uniqueID == this.beerID ){
					this.beerData = undefined
					this.controller.stageController.pushScene({"name":"BeerInfo",transition: Mojo.Transition.crossFade},{"beerDepot":this.beerDepot,"beerName":results[n].beerName,"beerRating":results[n].stars,"beerNote":results[n].beerNote,"beerID":this.beerID})
				}
			}
		}
	},
	dbFailure: function(){},
	gotPrefs: function(results) {
		this.BW = results.androidScreens
	},
	gotBeerInfoList: function(results) {
		var recordSize = Object.values(results).size();
		if(recordSize !== 0){
			this.nDiffBeers = results.length
			for( n = 0; n < results.length; n++ ){
				this.beerArray[n] = results[n].beerName
				this.drunk[n] = 0
				this.starArray[n] = 0
				this.nBeers[n] = 0
			}
		}
		this.beerDepot.get('beerList',this.gotBeerInfo,this.dbFailure)
	},
	gotBeerInfo: function(nBeers, nDiffBeers, favorite, mostDrunk, favHour, lastSeven, lastMonth, thisYear ){
		this.items[0].value = nBeers
		this.items[1].value = nDiffBeers // no. diff beers
		this.items[4].value = favHour
		this.items[5].value = lastSeven
		this.items[6].value = lastMonth
		this.items[7].value = thisYear
		this.items[2].value = favorite
		this.items[3].value = mostDrunk
		this.statsListModel.items = this.items
		this.controller.modelChanged(this.statsListModel)
		this.readyToGo()
	},
	sceneCleanup: function() {
		Ares.cleanupSceneAssistant(this);
	},
	handleCommand: function(event) {
		if(event.type == Mojo.Event.command) {
			if(event.command == 'do-help') {
				if( this.BW == 2 ) nextScreen = 'HelpBW'
				else nextScreen = 'Help'
				this.controller.stageController.pushScene({'name':nextScreen,transition: Mojo.Transition.crossFade},{context:'Statistics'})
			}
			if(event.command == 'do-prefs'){
				if( this.BW == 2 ) nextScreen = 'PreferencesBW' 
				else nextScreen = 'Preferences'
				this.controller.stageController.pushScene({'name':nextScreen,transition: Mojo.Transition.crossFade},{'beerDepot':this.beerDepot,"thisPusher":"Statistics"})
			}
			if(event.command == 'do-myAbout') {
				if( this.BW == 2 ) nextScreen = 'AboutBW'
				else nextScreen = 'About'
				//this.controller.stageController.pushAppSupportInfoScene()
				this.controller.stageController.pushScene({'name':nextScreen,transition: Mojo.Transition.crossFade})
			}
			if(event.command == 'add-beer'){
				if( this.BW == 2 ) nextScreen = 'AddBeerBW' 
				else nextScreen = 'AddBeer'
				if( this.whichPusher != 'AddBeer' && this.stackSize < 2 )
					this.controller.stageController.pushScene({"name":nextScreen,transition: Mojo.Transition.crossFade},{"beerDepot":this.beerDepot,"thisPusher":"Statistics"}) 
				else
					this.controller.stageController.popScenesTo(nextScreen)
			}
			if(event.command == 'do-history'){
				if( this.BW == 2 ) nextScreen = 'FirstBW' 
				else nextScreen = 'First'
				if( this.whichPusher != 'First' && this.stackSize < 2 )
					this.controller.stageController.pushScene({'name':nextScreen,transition: Mojo.Transition.crossFade},{'beerDepot':this.beerDepot,"thisPusher":"Statistics"})
				else
					this.controller.stageController.popScenesTo(nextScreen)
			}
		}
	},
	showAppMenu: function(inSender, event) {
		Mojo.Log.info('in showAppMenu')
		Mojo.Controller.stageController.sendEventToCommanders(Mojo.Event.make(Mojo.Event.command, {command: "palm-show-app-menu"}))
	}
})