
var beerDepot

function AppAssistant(args) {
	var options = {
			name: "Remembeer",
			version: 1,
			replace: false // open an existing depot
	};
	//Mojo.Log.info('input parms: '+Object.toJSON(args.paramsFromURI))
	//Create a database when the scene is generated
	beerDepot = new Mojo.Depot(options, this.dbSuccess, this.dbFailure);
}

	AppAssistant.prototype = {
		handleLaunch: function(params) {
			this.params = params
			beerDepot.get( 'preferences', this.gotPrefs.bind(this), this.Fail.bind(this))
		},
		gotPrefs: function(results) {
			var appController = Mojo.Controller.getAppController();
			var recordSize = Object.values(results).size();
			if(recordSize == 0){ // First use of Remembeer. Set defaults.
				results = new Object()
				results.firstScreen = 'First'
				results.nextScreen = 'First'
				results.androidScreens = 1
				results.ratingReminders = false
				results.vibrate = false
				results.reminderDelay = 5
				results.twitPost = false
				results.facePost = false
				beerDepot.add('preferences', results, function(){}, function(){})
				var pushFront = function(stageController) {
					stageController.pushScene({name: "First", transition: Mojo.Transition.crossFade}, {"beerID":this.beerID, "beerDepot":beerDepot, "thisPusher":"app"} )
				}
				appController.createStageWithCallback({name: 'First'}, pushFront, 'card');
			}
			else {
				if( this.params.action == 'timeout' ){ // Relaunch by dashboard timeout
					if( results.androidScreens == 2 ) {
						var pushPopup = function(stageController) {
							stageController.pushScene('myPopupBW', {'beerDepot':beerDepot})
						}
					}
					else {
						var pushPopup = function(stageController) {
							stageController.pushScene('myPopup', {'beerDepot':beerDepot})
						}
					}
					appController.createStageWithCallback({name: "popupStage",height:211}, pushPopup, 'popupalert');
				}
				else if( this.params.action == 'dashLaunch' ){ // Start the dashboard
					var pushDashboard = function(stageController) {
						stageController.pushScene('dashboard', {"beerDepot":beerDepot, "beerID":this.params.beerID, "beerName":this.params.beerName, "delay": this.params.delay, "message":this.params.message});
					}.bind(this)
					appController.createStageWithCallback({name: "myDashboard"}, pushDashboard, 'dashboard');
				}
				else if( this.params.action == 'beerInfo' ){ // Back from dashboard to set rating
					appController.closeStage("myDashboard")
					var firstStage = appController.getStageProxy("First");
					if( !firstStage ){ // Card stage is not running
						if( results.androidScreens == 2 ) {
							var pushFront = function(stageController) {
								stageController.pushScene({name:'BeerInfoBW',transition:Mojo.Transition.crossFade}, {"thisPusher":"app","beerID":this.params.beerID, "beerDepot":beerDepot} )
							}.bind(this)
						}
						else {
							var pushFront = function(stageController) {
								stageController.pushScene({name:'BeerInfo',transition:Mojo.Transition.crossFade}, {"thisPusher":"app","beerID":this.params.beerID, "beerDepot":beerDepot} )
							}.bind(this)
						}
						appController.createStageWithCallback({name: 'First'}, pushFront, 'card');
					}
					else {
						if( !firstStage.isActiveAndHasScenes() )
							firstStage.activate() // Bring the app to the front
						if( results.androidScreens == 2 )
							firstStage.pushScene({name:'BeerInfoBW',transition:Mojo.Transition.crossFade}, {"thisPusher":"app","beerID":this.params.beerID,"beerDepot":beerDepot} )
						else
							firstStage.pushScene({name:'BeerInfo',transition:Mojo.Transition.crossFade}, {"thisPusher":"app","beerID":this.params.beerID,"beerDepot":beerDepot} )
					}
				}
				else if( this.params.action == "restart" ) { // Restart after background change.
					appController.closeStage("First")
					if( results.androidScreens == 2 )
						results.firstScreen = results.firstScreen + 'BW'
					var pushFront = function(stageController) {
						stageController.pushScene({name: results.firstScreen, transition: Mojo.Transition.crossFade}, {"beerID":this.beerID, "beerDepot":beerDepot, "thisPusher":"app"} )
					}.bind(this)
					appController.createStageWithCallback({name: 'First'}, pushFront, 'card');
				}
				else if( this.params.action == "tweeted" ){ // Tweeted beer
					if( this.params.success == "ok" )
						Mojo.Controller.getAppController().showBanner("Beer successfully tweeted!",'')
					else
						Mojo.Controller.getAppController().showBanner("Beer tweet attempt failed.",'')
				}
				else { // Remembeer launched by user
					if( results.androidScreens == 2 )
						results.firstScreen = results.firstScreen + 'BW'
					var pushFront = function(stageController) {
						stageController.pushScene({name: results.firstScreen, transition: Mojo.Transition.crossFade}, {"beerID":this.beerID, "beerDepot":beerDepot, "thisPusher":"app"} )
					}.bind(this)
					appController.createStageWithCallback({name: 'First'}, pushFront, 'card');
				}
			}
		},
		Fail: function(results) {
		// First use of Remembeer. Set defaults.
			results = new Object()
			results.firstScreen = 'First'
			results.nextScreen = 'First'
			results.androidScreens = 1
			results.ratingReminders = false
			results.vibrate = false
			results.reminderDelay = 5
			results.twitPost = false
			results.facePost = false
			beerDepot.add('preferences', results, function(){}, function(){})
			var pushFront = function(stageController) {
				stageController.pushScene({name: "First", transition: Mojo.Transition.crossFade}, {"beerID":this.beerID, "beerDepot":beerDepot, "thisPusher":"app"} )
			}
			appController.createStageWithCallback({name: 'First'}, pushFront, 'card');
		}
	}