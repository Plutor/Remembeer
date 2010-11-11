PrefsBase = Class.create( {
	sceneSetup: function(BW) {
		Ares.setupSceneAssistant(this);
		this.BW = BW
		this.tokenSecret = ''
		this.authToken = ''
		this.authSecret = ''
		this.firstScreen = "First"
		this.nextScreen = "First"
		this.androidScreens = 1 
		this.ratingReminders = true 
		this.vibrate = true
		this.reminderDelay = 5
		this.twitPost = false 
		this.twitToken = ''
		this.twitSecret = ''
		this.twitPostChanged = false 
		this.screens = ["First","AddBeer","Statistics"]
		this.SaveData = this.saveData.bind(this)
		this.dbSuccess = this.dbSuccess.bind(this)
		this.dbFailure = this.dbFailure.bind(this)
		this.gotPrefs = this.gotPrefs.bind(this)
		this.handleCommand = this.handleCommand.bind(this)
		this.appMenuModel = {
			visible: true,
			items: [
				{ label: "Close this menu", command: 'palm-show-app-menu'},
				{ label: "Help", command: 'do-help' },
				{ label: "About", command: 'do-myAbout' }]
		};
		this.controller.setupWidget(Mojo.Menu.appMenu, {omitDefaultItems: true}, this.appMenuModel);
		this.twitSpinner = this.controller.get("activity-spinner")
		this.controller.setupWidget("activity-spinner",{'spinnerSize':Mojo.Widget.spinnerLarge},this.spinnerModel={'spinning':true})
		this.scrim = Mojo.View.createScrim(this.controller.document, {scrimClass:'palm-scrim'})
		this.scrim.hide()
		this.controller.get("twitScrim").appendChild(this.scrim).appendChild(this.controller.get(this.twitSpinner));
	},
	sceneAboutToActivate: function(callback){
		this.readyToGo = callback
		this.beerDepot.get('preferences', this.gotPrefs, this.dbFailure)
	},
	dbSuccess: function(){},
	dbFailure: function(){},
	sceneCleanup: function() {
		Ares.cleanupSceneAssistant(this);
	},
	handleCommand: function(event) {
		if(event.type == Mojo.Event.command) {
			if(event.command == 'do-myAbout') {
				if( this.BW == 2 ) nextScreen = 'AboutBW'
				else nextScreen = 'About'
				//this.controller.stageController.pushAppSupportInfoScene()
				this.controller.stageController.pushScene({'name':nextScreen,transition: Mojo.Transition.crossFade})
			}
			if(event.command == 'do-help') {
				if( this.BW == 2 ) nextScreen = 'HelpBW'
				else nextScreen = 'Help'
				this.controller.stageController.pushScene({'name':nextScreen,transition: Mojo.Transition.crossFade},{context:'Preferences'})
			}
		}
	},
	gotPrefs: function(results){
		var recordSize = Object.values(results).size();
		if(recordSize !== 0){
			//Mojo.Log.info(Object.toJSON(results))
			this.firstScreen = results.firstScreen
			this.nextScreen = results.nextScreen
			for( n = 0; n < this.screens.length; n++ ){
				if( this.screens[n] == this.firstScreen )
					this.firstScreenModel.value = n+1
				if( this.screens[n] == this.nextScreen )
					this.nextScreenModel.value = n+1
			}
			this.androidScreens = results.androidScreens
			this.androidScreensModel.value = this.androidScreens
			this.controller.modelChanged(this.androidScreensModel)
			this.ratingReminders = results.ratingReminders
			this.remindersCheckModel.value = this.ratingReminders
			this.controller.modelChanged(this.remindersCheckModel)
			this.vibrate = results.vibrate
			this.vibrateCheckModel.value = this.vibrate
			this.controller.modelChanged(this.vibrateCheckModel)
			this.reminderDelay = results.reminderDelay
			this.reminderDelayModel.value = this.reminderDelay
			this.controller.modelChanged(this.reminderDelayModel)
			this.twitPost = results.twitPost
			this.twitCheckModel.value = this.twitPost
			this.controller.modelChanged(this.twitCheckModel)
			if( this.twitPost == true ){
				this.twitToken = results.twitToken
				this.twitSecret = results.twitSecret
			}
			this.remembeerAccountId = results.accountId
			this.remembeerCalendarId = results.calendarId
		}
		this.readyToGo()
	},
	firstScreenChange: function(inSender, event) {
		this.firstScreen = this.screens[this.firstScreenModel.value-1]
		this.saveData()
	},
	remindersCheckChange: function(inSender, event) {
		this.ratingReminders = this.remindersCheckModel.value
		this.saveData()
	},
	vibrateCheckChange: function(inSender, event) {
		this.vibrate = this.vibrateCheckModel.value
		this.saveData()
	},
	reminderDelayChange: function(inSender, event) {
		this.reminderDelay = this.reminderDelayModel.value
		this.saveData()
	},
	twitPostChange: function(inSender, event) {
		this.twitPostChanged = true 
		this.twitPost = this.twitCheckModel.value
		if( this.twitPost == true ){
			if( this.BW == 2 )
				this.controller.stageController.pushScene({name:'twitterAuthorizeBW', transition:Mojo.Transition.crossFade})
			else
				this.controller.stageController.pushScene({name:'twitterAuthorize', transition:Mojo.Transition.crossFade})
		}
		this.saveData()
	},
	sceneActivate: function(returned){
		//Mojo.Log.info(Object.toJSON(returned))
		if( returned != undefined ){
			this.twitToken = returned.twitToken
			this.twitSecret = returned.twitSecret
			if( returned.twitToken == "" ){ // Failed
				this.twitCheckModel.value = false
				this.twitPost = false
				this.controller.modelChanged(this.twitCheckModel)
			}
			else{
				this.twitPost = true
				this.twitCheckModel.value = true
				this.controller.modelChanged(this.twitCheckModel)
			}
		}
		else if( this.twitPostChanged == true ){ // Dismissed
			this.twitCheckModel.value = false
			this.twitPost = false
			this.controller.modelChanged(this.twitCheckModel)
			this.twitPostChanged = false 
		}
		this.saveData()
	},
	saveData: function() {
		var data = {"firstScreen":this.firstScreen,
								"ratingReminders":this.ratingReminders,
								"vibrate":this.vibrate,
								"reminderDelay":this.reminderDelay,
								"twitPost":this.twitPost,
								"nextScreen":this.nextScreen,
								"androidScreens":this.androidScreens,
								"twitToken":this.twitToken,
								"twitSecret":this.twitSecret,
								"calendarId":this.remembeerCalendarId,
								"accountId":this.remembeerAccountId}
		Mojo.Log.info(Object.toJSON(data))
		this.beerDepot.add('preferences',data,this.dbSuccess, this.dbFailure)
	},
	nextScreenChange: function(inSender, event) {
		this.nextScreen = this.screens[this.nextScreenModel.value-1]
		this.saveData()
	},
	showAppMenu: function(inSender, event) {
		Mojo.Log.info('in showAppMenu')
		Mojo.Controller.stageController.sendEventToCommanders(Mojo.Event.make(Mojo.Event.command, {command: "palm-show-app-menu"}))
	},
	androidScreensChange: function(inSender, event) {
		this.controller.showAlertDialog({
			onChoose: function(value) {
				if( value == 'OK' ) {
					this.androidScreens = this.androidScreensModel.value
					var data = {"firstScreen":this.firstScreen,
								"ratingReminders":this.ratingReminders,
								"vibrate":this.vibrate,
								"reminderDelay":this.reminderDelay,
								"twitPost":this.twitPost,
								"nextScreen":this.nextScreen,
								"androidScreens":this.androidScreens,
								"twitToken":this.twitToken,
								"twitSecret":this.twitSecret,
								"calendarId":this.remembeerCalendarId,
								"accountId":this.remembeerAccountId}
					this.beerDepot.add('preferences',data,function(){
						this.controller.serviceRequest('palm://com.palm.applicationManager', {
							method: 'launch',
							parameters: {
								id: 'com.gelbintergalactic.remembeer',
								params: {"action":"restart"}
							}
						})
					}.bind(this), this.dbFailure)
				}
				else {
					if( this.androidScreensModel.value == 1 )
						this.androidScreensModel.value = 2
					else this.androidScreensModel.value = 1
				}
			}.bind(this),
			title:'Notice:',
			message:'For this change to take effect, Remembeer will now be closed and restarted.',
			choices:[ {label:'OK', value:'OK', type:'affirmative'},
								{label:'Cancel', value:'cancel', type:'negative'} ]
		})
	}
})