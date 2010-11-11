function TwitterLoginAssistant(argFromPusher) {
	this.url = argFromPusher.url
}

TwitterLoginAssistant.prototype = {
	setup: function() {
		Ares.setupSceneAssistant(this);
		this.controller.get('webView').mojo.openURL(this.url)
	},
	cleanup: function() {
		Ares.cleanupSceneAssistant(this);
	},
	button1Tap: function(inSender, event) {
		this.controller.stageController.popScene({"twitPin":this.twitPinModel.value})
	}
};