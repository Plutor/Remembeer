AddBeerBase = Class.create( {
	sceneSetup: function() {
		Ares.setupSceneAssistant(this);
		Mojo.Controller.getAppController().getStageController('First').setWindowOrientation("up");
		//this.consumerSecret = 'iWDII0E1wTCKvDP1GqddlHzmSgQKZwAaPY3SNxdDU'
		//this.consumerKey = 'HW6MSCVzjkWM1MBugM8s6A'
		this.consumerSecret = 'VyKlwVckeFps6befs6kRHa4YGrbDHMfknq14VvwWo'
		this.consumerKey = 'D3iN6bOTkA7SF1oAl1wgrA'
		this.accessor = { consumerSecret: this.consumerSecret }
		this.message = { method: 'POST', action: 'http://api.twitter.com/1/statuses/update.json', 
							parameters: []}
		this.controller.setDefaultTransition(Mojo.Transition.crossFade)
		this.gotBeerInfo = this.gotBeerInfo.bind(this)
		this.updateFlag = false
		this.listUpdated = this.listUpdated.bind(this)
		this.popupChoose = this.popupChoose.bind(this)
		this.dbFailure = this.dbFailure.bind(this)
		this.dbSuccess = this.dbSuccess.bind(this)
		this.gotPrefs = this.gotPrefs.bind(this)
		this.gotBeer = this.gotBeer.bind(this)
		this.makeDateTimeString = this.makeDateTimeString.bind(this)
		this.holdForList = this.holdForList.bindAsEventListener(this)
		this.beerListResults = this.beerListResults.bind(this)
		this.makeUniqueID = this.makeUniqueID.bind(this)
		this.beerListUpdated = this.beerListUpdated.bind(this)
		this.gotBeersDrunkList = this.gotBeersDrunkList.bind(this)
		this.whereToGo = this.whereToGo.bind(this)
		this.twitPostSuccess = this.twitPostSuccess.bind(this)
		this.twitPostFailure = this.twitPostFailure.bind(this)
		this.scrimSpinner = this.controller.get("activity-spinner")
		this.controller.setupWidget("activity-spinner",{'spinnerSize':Mojo.Widget.spinnerLarge},this.spinnerModel={'spinning':true})
		this.scrim = Mojo.View.createScrim(this.controller.document, {scrimClass:'palm-scrim'})
		this.scrim.hide()
		this.controller.get("scrimSpinner").appendChild(this.scrim).appendChild(this.controller.get(this.scrimSpinner));
		this.controller.listen(this.controller.get('beerName'), Mojo.Event.hold, this.holdForList)
		this.beerRating = 0
		this.beerNames = []
		this.drunk = []
		this.beerNameListItems = new Array()
		this.stackSize = this.controller.stageController.getScenes().length
		if( this.noMenu == true ){
			this.appMenuModel = {
				visible: true,
				items: [
					{ label: "Close this menu", command: 'palm-show-app-menu'},
					{ label: "Preferences", command: 'do-prefs' },
					{ label: "Help", command: 'do-help' },
					{ label: "About", command: 'do-myAbout' }
				]
			}
		}
		else{
			this.appMenuModel = {
				visible: true,
				items: [
					{ label: "Close this menu", command: 'palm-show-app-menu'},
					{ label: "History", command: 'do-history' },
					{ label: "Statistics", command: 'do-stats' },
					{ label: "Preferences", command: 'do-prefs' },
					{ label: "Help", command: 'do-help' },
					{ label: "About", command: 'do-myAbout' }
				]
			}
		}
		this.controller.setupWidget(Mojo.Menu.appMenu, {omitDefaultItems: true}, this.appMenuModel);
		if( this.beerName != undefined && this.beerNote != undefined){
			this.beerNameModel.value = this.beerName
			this.beerNoteModel.value = this.beerNote
			this.controller.modelChanged(this.beerNameModel)
			this.controller.modelChanged(this.beerNodeModel)
		}
		this.gotBW = this.gotBW.bind(this)
		this.beerDepot.get('preferences',this.gotBW, this.dbFailure)
	},
	sceneAboutToActivate: function(callback){
		this.readyToGo = callback
		makeBeersDrunkList( this.beerDepot, this.gotBeersDrunkList, this.dbFailure )
	},
	gotBW: function(results) {
		this.BW = results.androidScreens
	},
	handleCommand: function(event) {
		if(event.type == Mojo.Event.command) {
			if(event.command == 'do-stats'){
				if( this.BW == 2 ) nextScreen = 'StatisticsBW'
				else nextScreen = 'Statistics'
				if( this.whichPusher != 'Statistics' && this.stackSize < 2 )
					this.controller.stageController.pushScene({"name":nextScreen,transition: Mojo.Transition.crossFade},{"beerDepot":this.beerDepot,"thisPusher":"AddBeer"}) 
				else 
					this.controller.stageController.popScenesTo(nextScreen)
			}
			if(event.command == 'do-help') {
				if( this.BW == 2 ) nextScreen = 'HelpBW'
				else nextScreen = 'Help'
				this.controller.stageController.pushScene({'name':nextScreen,transition: Mojo.Transition.crossFade},{context:'Add a Beer'})
			}
			if(event.command == 'do-prefs'){
				if( this.BW == 2 ) nextScreen = 'PreferencesBW'
				else nextScreen = 'Preferences'
				this.controller.stageController.pushScene({'name':nextScreen,transition: Mojo.Transition.crossFade},{'beerDepot':this.beerDepot,"thisPusher":"AddBeer"})
			}
			if(event.command == 'do-myAbout') {
				if( this.BW == 2 ) nextScreen = 'AboutBW'
				else nextScreen = 'About'
				//this.controller.stageController.pushAppSupportInfoScene()
				this.controller.stageController.pushScene({'name':nextScreen,transition: Mojo.Transition.crossFade})
			}
			if(event.command == 'do-history'){
				if( this.BW == 2 ) nextScreen = 'FirstBW'
				else nextScreen = 'First'
				if( this.whichPusher != 'First' && this.stackSize < 2 )
					this.controller.stageController.pushScene({'name':nextScreen,transition: Mojo.Transition.crossFade},{'beerDepot':this.beerDepot,"thisPusher":"AddBeer"})
				else
					this.controller.stageController.popScenesTo(nextScreen)
			}
		}
	},
	gotBeersDrunkList: function(results){
		Mojo.Log.info('Drunk beer list returned: '+Object.toJSON(results))
		this.beerNameListItems = results
	},
	beerListResults: function(results){
		this.beerList = results
		//Mojo.Log.info('returned beer list='+Object.toJSON(this.beerList))
		this.gotBeerInfo(this.beerList)
	},
	gotBeer: function(results){
		var recordSize = Object.values(results).size();
		if(recordSize !== 0) { // Adding to existing data
			var data = new Array(this.beerData) //{"uniqueID":0,"beerName":this.name,"container":this.containerType,"drinkTimeDate":this.drinkWhen,"stars":this.beerRating,"beerNote":this.beerNote})
			//listIndex = 1
			for( var n = 0; n < results.length; n++ ){ // push in the existing data
				//results[n].uniqueID = listIndex+n
				data.push(results[n])
				//Mojo.Log.info('results['+n+']='+Object.toJSON(data))
			}
		}
		else{ // First time data
			var data = new Array(this.beerData) //{"uniqueID":0,"beerName":this.name,"container":this.containerType,"drinkTimeDate":this.drinkWhen,"stars":this.beerRating,"beerNote":this.beerNote})
		}
		this.beerNameModel.value = "" // blank out the beer name
		this.beerNoteModel.value = "" // and the beer note
		this.drinkWhenModel.value = 1
		this.controller.modelChanged(this.beerNameModel)
		this.controller.modelChanged(this.beerNoteModel)
		this.controller.modelChanged(this.drinkWhenModel)
		this.controller.get('beerName').mojo.focus()
		//Mojo.Log.info('saving data')
		this.beerDepot.add('beerList',data,this.listUpdated,this.dbFailure)
	},
	sceneCleanup: function() {
		this.controller.stopListening(this.controller.get('beerName'), Mojo.Event.hold, this.holdForList)
		Ares.cleanupSceneAssistant(this);
	},
	doneButtonTap: function(inSender, event) {
		this.name = this.beerNameModel.value
		if( this.name == '' || this.name == undefined ) {
			this.controller.showAlertDialog({
				onChoose: function(value) {},
				title:'Entry Error',
				message:'You must type a beer name. Use the "back" gesture to cancel.',
				choices:[ {label:'OK', value:'OK', type:'color'} ]
			});
		}
		else {
			var containers = ["Bottle","Draught","Can","Growler"]
			this.containerType = containers[this.containerModel.value]
			this.drinkWhenChoice = this.drinkWhenModel.value
			if( this.drinkWhenChoice == 1 )
				this.scrim.show()
			switch( (this.drinkWhenChoice)-0 ) {
				case 1: // Now
					this.drinkWhen = this.makeDateTimeString(new Date())
					break
				case 2: // Last Night
					var today = new Date()
					today.setDate(today.getDate()-1)
					today.setHours(21)
					today.setMinutes(0)
					today.setSeconds(0)
					this.drinkWhen = this.makeDateTimeString(today)
					break
				case 3: // Ten Minutes Ago
					var today = new Date()
					today.setMinutes(today.getMinutes()-10)
					this.drinkWhen = this.makeDateTimeString(today)
					break
				case 4: // Specific Time
					break
					default:
			}
			this.beerNote = this.beerNoteModel.value
			this.beerRating = 0
			this.beerID = this.makeUniqueID()
			this.beerData = {"uniqueID":this.beerID,
											 "beerName":this.name,
											 "container":this.containerType,
											 "drinkTimeDate":this.drinkWhen,
											 "stars":this.beerRating,
											 "beerNote":this.beerNote}
			// Remove the specific time entry for the drinkwhen list, if present
			if( this.drinkWhenModel.choices.length == 5 )
				var out = this.drinkWhenModel.choices.pop()
			// Put this new beer item into the beerList DB
			putToBeerList( this.beerDepot, this.beerData, this.beerListUpdated, this.dbFailure )
			//this.beerDepot.get('beerList', this.gotBeer, this.dbFailure )
		}
	},
	beerListUpdated: function(){
		//Mojo.Log.info("in beerListUpdated")
		this.beerNameModel.value = "" // blank out the beer name
		this.controller.modelChanged(this.beerNameModel)
		this.beerNoteModel.value = "" // and the beer note
		this.controller.modelChanged(this.beerNoteModel)
		this.drinkWhenModel.value = 1 // Rest the drink when value
		this.controller.modelChanged(this.drinkWhenModel)
		this.controller.get('beerName').mojo.focus()
		// Update the beerInfoList, if necessary
		data = {"beerName":this.name,
						"brewery":'',
						"location":'',
						"style":'',
						"ABV":'',
						"about":''
					}
		putToBeerInfo( this.beerDepot, data, this.listUpdated, this.dbFailure )
	},
	listUpdated: function(){
		this.updateFlag = true
		this.beerDepot.get('preferences',this.gotPrefs,this.dbFailure)
	},
	gotBeerInfo: function(nBeers, diffBeers, favorite, mostDrunk, favHour, lastSeven, lastMonth, thisYear ){
		if( this.nextScreen != "Statistics" ) {
			array = [ diffBeers+' different beers.',
								thisYear+' beers so far this year.',
								lastMonth+' beers so far this month.',
								lastSeven+' beers in the last 7 days.']
			n = Math.floor(Math.random()*4)
			this.scrim.hide()
			this.controller.showAlertDialog({
				onChoose: function(value) {
					this.whereToGo()
				},
				title:'Beer Saved!',
				message:'You have drunk '+array[n],
				choices:[ {label:'OK'} ]
			});
		}
		else {
			this.scrim.hide()
			this.whereToGo()
		}
	},
	gotPrefs: function(results){ // Tweet this beer if selected
		//Mojo.Log.info('in gotPrefs: '+Object.toJSON(results))
		var recordSize = Object.values(results).size();
		if(recordSize !== 0){
			// If user wants to, and is drinking this beer now, tweet this beer
			this.ratingReminders = results.ratingReminders // Save this
			this.nextScreen = results.nextScreen // this too
			this.firstScreen = results.firstScreen // this too
			this.reminderDelay = results.reminderDelay // and this
			if( results.twitPost == true && this.drinkWhenChoice == 1 ){
				var twitMsg = "I'm enjoying a "+this.containerType.toLowerCase()+" of "+this.name+". (Posted automatically by Remembeer for webOS)"
				Mojo.Log.info(twitMsg)
				this.message.parameters = []
				this.message.parameters.push([ "status", twitMsg ])
				this.message.parameters.push([ "oauth_version", "1.0"])
				this.message.parameters.push([ "oauth_consumer_key", this.consumerKey])
				this.message.parameters.push([ "oauth_signature_method", "HMAC-SHA1" ])
				this.message.parameters.push([ "oauth_token", results.twitToken ])
				this.message.parameters.push([ "oauth_timestamp", this.makeTimeStamp() ])
				this.message.parameters.push([ "oauth_nonce", this.makeNonce() ])
				this.accessor.tokenSecret = results.twitSecret
				OAuth.SignatureMethod.sign(this.message, this.accessor);
				datasend = OAuth.formEncode(this.message.parameters);
				var myAjax = new Ajax.Request("http://api.twitter.com/1/statuses/update.json", {
				 	method: 'POST',
				 	postBody: datasend,
				 	onSuccess: this.twitPostSuccess,
				 	onFailure: this.twitPostFailure
				});
			}
			if( results.twitPost == false ) 
				this.setRatingReminder()
		}
	},
	setRatingReminder: function(){
		if( this.ratingReminders == true && this.drinkWhenChoice == 1 ){
			//Mojo.Log.info('Setting rating reminder: id='+this.beerData.uniqueID)
			var when = "0"+this.reminderDelay
			when = when.substr(when.length-2)
			when = "00:"+when+":00"
			//Mojo.Log.info("when="+when)
			var appController = Mojo.Controller.getAppController();
			var message = 'Setting Rating Reminder';
			var dashboardStage = appController.getStageProxy("myDashboard");
			if (!dashboardStage) {
				this.controller.serviceRequest('palm://com.palm.applicationManager', {
					method: 'launch',
					parameters: {
						id: 'com.gelbintergalactic.remembeer',
						params: {"action":"dashLaunch", "delay":when, "message":message, "beerName":this.beerData.beerName, "beerID":this.beerData.uniqueID}
					}
				})
			}
		}
		getBeerStatistics( this.beerDepot, this.gotBeerInfo, this.dbFailure )
	},
	whereToGo: function() {
		if( this.BW == 2 ) NextScreen = this.nextScreen + 'BW'
		else NextScreen = this.nextScreen
		if( this.drinkWhenChoice == 1 && this.ratingReminders == true ){ // "Drink Now"
			if( this.whichPusher == this.nextScreen )
				this.controller.stageController.popScene() // Return to calling card
			else{
				if( this.nextScreen != 'AddBeer' ){ // See where the user wants to go next
					if( this.firstScreen != 'AddBeer' ){ // Swap to "NextScreen"
						Mojo.Log.info('swap to '+NextScreen)
						this.controller.stageController.swapScene({"name":NextScreen,transition: Mojo.Transition.crossFade},{"beerDepot":this.beerDepot,"thisPusher":this.whichPusher})
					}
					else{ // Push "NextScreen"
						Mojo.Log.info('push '+NextScreen)
						this.controller.stageController.pushScene({"name":NextScreen,transition: Mojo.Transition.crossFade},{"beerDepot":this.beerDepot,"thisPusher":"AddBeer"}) 
					}
				}
			}
		}
		else { // Beer drunk earlier or no rating reminder set; prompt for a beer rating now.
			if( this.firstScreen != "AddBeer" ){ // Swap to Beer Info
				Mojo.Log.info('swap to BeerInfo')
				if( this.BW == 2 ) // Use white on black card
					this.controller.stageController.swapScene({"name":"BeerInfoBW",transition: Mojo.Transition.crossFade},{"beerDepot":this.beerDepot,"beerName":this.name,"beerNote":this.beerNote,"beerID":this.beerID,"thisPusher":this.whichPusher})
				else 
					this.controller.stageController.swapScene({"name":"BeerInfo",transition: Mojo.Transition.crossFade},{"beerDepot":this.beerDepot,"beerName":this.name,"beerNote":this.beerNote,"beerID":this.beerID,"thisPusher":this.whichPusher})
			}
			else{ // Use pushScene since AddBeer card is first card
				Mojo.Log.info('push BeerInfo')
				if( this.BW == 2 ) // Use white on black card
					this.controller.stageController.pushScene({"name":"BeerInfoBW",transition: Mojo.Transition.crossFade},{"beerDepot":this.beerDepot,"beerName":this.name,"beerNote":this.beerNote,"beerID":this.beerID,"thisPusher":"AddBeer"})
				else
					this.controller.stageController.pushScene({"name":"BeerInfo",transition: Mojo.Transition.crossFade},{"beerDepot":this.beerDepot,"beerName":this.name,"beerNote":this.beerNote,"beerID":this.beerID,"thisPusher":"AddBeer"})
			}
		}
	},
	twitPostSuccess: function(transport) {
		if (transport.status == 200) {
			//Mojo.Log.info('Successful twitter posting')
			//Mojo.Log.info(Object.toJSON(transport))
		}
		this.setRatingReminder()
	},
	twitPostFailure: function(transport) {
		this.controller.showAlertDialog( {
			title: "Twitter Post Failure",
			message: "Attempt to post this beer to twitter failed. Perhaps you do not have internet access right now. Status="+transport.status,
			onChoose: function(){},
			choices: [{label:"OK", value:"OK"}]
		})
		//Mojo.Log.info('Failed twitter posting')
		//Mojo.Log.info(Object.toJSON(transport))
		this.setRatingReminder()
	},
	makeTimeStamp: function() {
	now = new Date().getTime()
	return Math.floor(now / 1000)
	},
	makeNonce: function() {
		var result = ''
		var nonceChars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXTZabcdefghiklmnopqrstuvwxyz"
		for( n = 0; n < 43; n++ ){
			var rnum = Math.floor(Math.random() * nonceChars.length);
			result += nonceChars.substring(rnum, rnum+1);
		}
		return result
	},
	dbFailure: function(event){},
	dbSuccess: function(event){},
	makeDateTimeString: function(today){
		var hours = new String(today.getHours())
		if( hours.length == 1) hours = '0'+hours
		var mins = new String(today.getMinutes())
		if( mins.length == 1) mins = '0'+mins
		var month = new String(today.getMonth()+1)
		if( month.length == 1) month = '0'+month
		var day = new String(today.getDate())
		if( day.length == 1) day = '0'+day
		var secs = new String(today.getSeconds())
		if( secs.length == 1) secs = '0'+secs
		return(today.getFullYear()+'-'+month+'-'+day+' '+hours+':'+mins+':'+secs)
	},
	drinkWhenChange: function(inSender, event) {
		if( this.drinkWhenModel.choices.length == 5 ){
			var out = this.drinkWhenModel.choices.pop()
			//Mojo.Log.info('Popped='+out+' Left='+Object.toJSON(this.drinkWhenModel.choices))
			this.controller.modelChanged(this.drinkWhenModel)
		}
		if( this.drinkWhenModel.value == 4 ) {
			this.controller.showDialog({
				template: 'pickTimeDate/pickTimeDate-scene',
				assistant: new PickTimeDateAssistant(this, this.returnDateTime.bind(this)),
				preventCancel:true
			});
		}
	},
	returnDateTime: function(data) {
		if( new Date() < new Date(data) ){
			this.controller.showAlertDialog({
				onChoose: function(value) {
					this.drinkWhenModel.value = 1
					this.controller.modelChanged(this.drinkWhenModel)
				},
				title:'Error',
				message:'Specific "Drink When" times must be in the past.',
				choices:[ {label:'OK', value:'OK'} ]
			});
		}
		else{
			this.drinkWhen = data
			this.drinkWhenModel.choices.push({label:data,value:"5"})
			this.drinkWhenModel.value = 5
			this.controller.modelChanged(this.drinkWhenModel)
		}
	},
	holdForList: function(inSender, event) {
		Mojo.Log.info('in holdForList')
		if( this.beerNameListItems == undefined || this.beerNameListItems.length == 0)
			this.controller.showAlertDialog({
    		onChoose: function(value) {},
    		title: 'No Beer List Available',
    		message: 'No beer list has been created yet. You must first enter one or more new beers.',
    		choices:[{label:'OK'}]
			})
		else{
			var transition = this.controller.prepareTransition(Mojo.Transition.crossFade, false);
			this.controller.popupSubmenu({onChoose:this.popupChoose,
				placeNear: inSender.currentTarget,
				items: this.beerNameListItems});
			transition.run();
		}
	},
	popupChoose: function(value) {
		this.beerNameModel.value = value
		this.controller.modelChanged(this.beerNameModel)
//		this.controller.listen('beerName', Mojo.Event.tap, this.holdForList)
	},
	makeUniqueID: function(){
		return( Math.floor(Math.random()*1000000000))
	},
	showAppMenu: function(inSender, event) {
		Mojo.Log.info('in showAppMenu')
		Mojo.Controller.stageController.sendEventToCommanders(Mojo.Event.make(Mojo.Event.command, {command: "palm-show-app-menu"}))
	},
	beerNameHold: function(inSender, event) {
		Mojo.Log.info('in beerNameHold')
	}
})