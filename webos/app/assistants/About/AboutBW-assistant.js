AboutBWAssistant = Class.create( AboutBase, {
	initialize: function() {
		var options = {
			name: "Remembeer",
			version: 1,
			replace: false // open an existing depot
		};
		//Create a database when the scene is generated
		this.beerDepot = new Mojo.Depot(options, function(){}, function(){});
	},
	setup: function() {
		this.sceneSetup()
	},
	cleanup: function() {
		this.sceneCleanup()
	},
	aboutToActivate: function(callback) {
		this.sceneAboutToActivate(callback)
	}
})