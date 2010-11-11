FirstAssistant = Class.create( FirstBase, {
	initialize: function (argFromPusher) {
		//this.beerDepot = argFromPusher.beerDepot
		this.whichPusher = argFromPusher.thisPusher
		this.beerNote = argFromPusher.beerNote
		this.beerRating = argFromPusher.beerRating
		this.beerID = argFromPusher.beerID
		var options = {
				name: "Remembeer",
				version: 1,
				replace: false // open an existing depot
		};
		//Mojo.Log.info('input parms: '+Object.toJSON(args.paramsFromURI))
		//Create a database when the scene is generated
		Mojo.Log.info('Creating Beer Depot')
		this.beerDepot = new Mojo.Depot(options, this.dbSuccess, this.dbFailure);
	},
	setup: function() {
		this.sceneSetup(1)
	},
	aboutToActivate: function(callback){
		this.sceneAboutToActivate(callback)
	},
	activate: function(parms) {
		this.sceneActivate(parms)
	},
	cleanup: function() {
		this.sceneCleanup()
	}
})