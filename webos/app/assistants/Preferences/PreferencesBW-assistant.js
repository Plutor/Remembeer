PreferencesBWAssistant = Class.create( PrefsBase, {
	initialize: function(argFromPusher) {
	//this.beerDepot = argFromPusher.beerDepot
	var options = {
			name: "Remembeer",
			version: 1,
			replace: false // open an existing depot
	};
	//Mojo.Log.info('input parms: '+Object.toJSON(args.paramsFromURI))
	//Create a database when the scene is generated
	this.beerDepot = new Mojo.Depot(options, this.dbSuccess, this.dbFailure);
	},
	setup: function() {
		this.sceneSetup(2)
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