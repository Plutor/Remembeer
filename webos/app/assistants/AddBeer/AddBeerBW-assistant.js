AddBeerBWAssistant = Class.create( AddBeerBase, {
	initialize: function(argFromPusher) {
	//this.beerDepot = argFromPusher.beerDepot
	this.beerName = argFromPusher.beerName
	this.beerNote = argFromPusher.beerNote
	this.whichPusher = argFromPusher.thisPusher
	if( this.beerName != undefined ) this.noMenu = true
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
		this.sceneSetup()
	},
	aboutToActivate: function(callback){
		this.sceneAboutToActivate(callback)
	},
	cleanup: function() {
		this.sceneCleanup()
	}
})