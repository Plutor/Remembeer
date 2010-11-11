HelpBWAssistant = Class.create( HelpBase, {
	initialize: function(argFromPusher) {
	this.context = argFromPusher.context
	},
	setup: function() {
		this.sceneSetup("BW")
	},
	aboutToActivate: function(callback) {
		this.sceneAboutToActivate(callback)
	},
	cleanup: function() {
		this.sceneCleanup()
	}
})