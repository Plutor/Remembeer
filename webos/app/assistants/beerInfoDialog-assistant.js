var buttonTapped = false 

function BeerInfoDialogAssistant(sceneAssistant,callbackFunc) {
	this.callbackFunc = callbackFunc;
	this.sceneAssistant = sceneAssistant;
	this.controller = sceneAssistant.controller;
}

BeerInfoDialogAssistant.prototype = {
	setup: function(inDialog) {
		this.widget = inDialog
		buttonTapped = false 
		Ares.setupDialog(this, inDialog, "beerInfoDialog");
	},
	cleanup: function() {
		Ares.cleanupSceneAssistant(this);
		if( buttonTapped == false ) this.callbackFunc(2)
	},
	drinkAnotherTap: function(inSender, event) {
		buttonTapped = true 
		this.callbackFunc(0)
		this.widget.mojo.close()
	},
	editBeerInfoTap: function(inSender, event) {
		buttonTapped = true 
		this.callbackFunc(1)
		this.widget.mojo.close()
	}
};